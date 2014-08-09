/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.naming.Naming
 * Description: This class provides necessary methods for myrmi.
 */

package myrmi.naming;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import myrmi.server.InvokeRequestServerListener;
import myrmi.utility.Message;
import myrmi.utility.MyRemote;
import myrmi.utility.MyRemoteException;
import myrmi.utility.RemoteObjectReference;

public class Naming {
	public static final int PORT = 1357;
	private static final String stubFileDirName = ".";
	
	private static InvokeRequestServerListener serverListener;

	public static void bind(String host, int port, String name,
			MyRemote remoteObject) throws MyRemoteException {
		bind(host, port, name, stubFileDirName, remoteObject);
	}

	/*
	 * This function is used to register a remote object to registry sever. It
	 * is called by the rmi server part. The input parameter are the registry
	 * server hostname and port, the remoteobject name and reference and the
	 * path of the codebase. The stub file should be placed in the codebase for
	 * client to download.
	 */
	public static void bind(String host, int port, String name, String stubFileDirName,
			MyRemote remoteObject) throws MyRemoteException {
		try {
			if (remoteObject == null)
				throw new NullPointerException("Can not bind to null");
			
			// Set up server listener and download listener.
			if (serverListener == null) {
				serverListener = new InvokeRequestServerListener(host, port,
						stubFileDirName);
				new Thread(serverListener).start();
			}

			Socket socket = new Socket(host, port);
			ObjectOutputStream output = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(
					socket.getInputStream());

			// Send bind message to server
			RemoteObjectReference ror = new RemoteObjectReference(
					serverListener.getServerAddress(),
					serverListener.getServerPort(),
					serverListener.getDownloadPort(), name, remoteObject
							.getClass().getName());
			Message request = new Message(Message.MSG_BIND, name, ror);
			output.writeObject(request);

			// Wait result from register server
			Message reply = (Message) input.readObject();
			switch (reply.getType()) {
			case Message.MSG_BIND_FAIL_NAME_EXIST:
				throw new MyRemoteException(reply.getExceptionString());
			case Message.MSG_BIND_SUCCESS:
				serverListener.addRemoteObject(name, remoteObject);
				break;
			default:
				throw new MyRemoteException(reply.getExceptionString());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new MyRemoteException("Can not connect to registry server");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MyRemoteException("Can not connect to registry server");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return;
	}

	/*
	 * This function is used to lookup a remote object from a remote registry
	 * server. This function is called by the client-side program. The parameters are
	 * the registry server's host and port number, the name is the name of the remote
	 * object. The return value is the stub of the remote object.
	 */
	public static MyRemote lookup(String host, int port, String name)
			throws MyRemoteException {
		String errHead = "Lookup(" + host + ", " + port + ", " + name + "): ";
		try {
			// Initialize socket.
			Socket socket = new Socket(host, port);
			ObjectOutputStream output = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(
					socket.getInputStream());

			// Send lookup message.
			Message lookupMsg = new Message(Message.MSG_LOOKUP, name);
			output.writeObject(lookupMsg);

			// Receive result from register server.
			Message lookupRes = (Message) input.readObject();

			switch (lookupRes.getType()) {
			case Message.MSG_LOOKUP_SUCCESS:
				System.out.println(lookupRes.getRor().toString());
				if (lookupRes.getRor() != null) {
					// If the .class file not exist on local and can not
					// download from remote, throw exception
					if (!checkStub(lookupRes.getRor().getClassName())
							&& !downloadStub(lookupRes.getRor())) {
						throw new MyRemoteException("Can not find .class file");
					}

					Class<?> stubClass = Class.forName(parseClass(lookupRes
							.getRor().getClassName()) + "_Stub");
					return (MyRemote) stubClass.getConstructors()[0]
							.newInstance(lookupRes.getRor());

				} else {
					throw new NullPointerException( 
							"RemoteObjectReference can not be null.");
				}
			default:
				throw new MyRemoteException(lookupRes.getExceptionString());
			}
		} catch (ClassNotFoundException e) {
			throw new MyRemoteException(errHead + 
					"Can not find remote object in registry server");
		} catch (IOException e) {
			throw new MyRemoteException(errHead + "Network communication error");
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyRemoteException(errHead + e.getClass().getName());
		}
	}

	/*
	 * This function is used to check a remote objects from a remote registry
	 * server. This function is called by the client-side program. The parameters are
	 * the registry server's host and port number.
	 * The return value is the list of the remote objects' information.
	 */
	public static Map<String, RemoteObjectReference> list(String host, int port)
			throws MyRemoteException {
		Map<String, RemoteObjectReference> rorMap = null;
		try {
			Socket socket = new Socket(host, port);
			ObjectOutputStream output = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(
					socket.getInputStream());

			// Send list request message to server
			Message request = new Message(Message.MSG_LIST);
			output.writeObject(request);

			// Wait result from register server
			Message reply = (Message) input.readObject();
			socket.close();

			switch (reply.getType()) {
			case Message.MSG_LIST_SUCCESS:
				// Print rors information
				for (String name : reply.getRorMap().keySet()) {
					System.out.print(String.format("%-15s", "Name:" + name));
					System.out.print(String.format("%-45s", "ROR:"
							+ reply.getRorMap().get(name).toString()));
					System.out.println();
				}
				rorMap = reply.getRorMap();
				break;
			default:
				// Error messages
				throw new MyRemoteException(reply.getExceptionString());
			}
		} catch (UnknownHostException e) {
			throw new MyRemoteException("Can not connect to registry server");
		} catch (IOException e) {
			throw new MyRemoteException("Can not connect to registry server");
		} catch (ClassNotFoundException e) {
		}
		return rorMap;
	}

	/*
	 * This method check whether the stub.class file exists on the 
	 * client-side or not.
	 * If the .class exists, then it will return true, otherwise it will
	 * return false.
	 */
	private static boolean checkStub(String className) {
		try {
			Class.forName(parseClass(className) + "_Stub");
			System.out.println("Stub class " + parseClass(className) + "_Stub"
					+ " is found!");
			return true;
		} catch (Throwable e) {
			System.out.println("Stub class " + parseClass(className) + "_Stub"
					+ " is not found. Trying to download from server...");
			return false;
		} 
	}

	private static String parseClass(String s) {
		int index = s.lastIndexOf('.');
		return s.substring(index + 1);
	}

	/*
	 * This method is used to download the stub.class file from
	 * a remote server.
	 * The server's information contains in the ror object.
	 */
	private static boolean downloadStub(RemoteObjectReference ror) {
		try {
			Socket downloadSocket = new Socket(ror.getHost(),
					ror.getDownloadPort());
			downloadSocket.setSoTimeout(5000);
			ObjectOutputStream downloadOutput = new ObjectOutputStream(
					downloadSocket.getOutputStream());
			InputStream downloadInput = new DataInputStream(
					downloadSocket.getInputStream());

			// Send ready message and className
			Message request = new Message(Message.MSG_STUB);
			request.setClassName(ror.getClassName());
			downloadOutput.writeObject(request);

			// Start downloading, default timeout is 5s.
			// Download the file to stub dir.
			// If the directory does not exist, create it.
			File dir = new File(stubFileDirName);
			if (!dir.exists()) {
				dir.mkdir();
			}
			String filePath = "./" + parseClass(ror.getClassName())
					+ "_Stub.class"; // Ignore the package name
			File file = new File(filePath);
			file.createNewFile();
			RandomAccessFile fileOutput = new RandomAccessFile(file, "rw");

			byte[] buffer = new byte[2048];
			while (true) {
				int len = downloadInput.read(buffer);
				if (len >= 0) {
					fileOutput.write(buffer, 0, len);
					fileOutput.skipBytes(len);
				} else {
					break;
				}
			}

			// Close stream
			downloadSocket.close();
			fileOutput.close();
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
