/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.server.DownloadServerListener
 * Description: This class defines the server for downloading stub files.
 */

package myrmi.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import myrmi.compiler.StubCompiler;
import myrmi.utility.Message;
import myrmi.utility.MyRemoteException;
public class DownloadServerListener implements Runnable {
	String stubFileDirName; // directory name for store the stub.class file
	ServerSocket downloadServerSocket; // download server socket
	int port; // download port number

	public DownloadServerListener(String stubFileDirName, int port) {
		this.stubFileDirName = stubFileDirName;
		this.port = port;
	}

	public void run() {
		try {
			handleRequest();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/*
	 * This function handles the download stub.class request and create the download thread for 
	 * every request.
	 * Basically, it first compiles the stub.class based on the remote interface implementation class.
	 * Then it creates a seperate thread to communicate with the cleint to send the stub.class
	 * file to it.
	 */
	private void handleRequest() throws IOException, ClassNotFoundException {
		downloadServerSocket = new ServerSocket(port);
		while (true) {
			try {
				Socket socket = downloadServerSocket.accept();
				OutputStream networkOutput = new DataOutputStream(
						socket.getOutputStream());
				ObjectInputStream networkInput = new ObjectInputStream(
						socket.getInputStream());

				// Get the .class file name based on given objectName from
				// client request
				Message request = (Message) networkInput.readObject();
				
				StubCompiler.compile(stubFileDirName, Class.forName(request.getClassName()));
				String fileName = parseClass(request.getClassName()) + "_Stub.class";

				// Start download service
				File dir = new File(stubFileDirName);
				FileInputStream fileInput = new FileInputStream(new File(
						dir, fileName));
				new Thread(new Download(networkOutput, fileInput, networkInput))
						.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MyRemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String parseClass(String s) {
		int index = s.lastIndexOf('.');
		return s.substring(index + 1);
	}

	/*
	 * Seperate download thread for send a stub.class file to a client
	 */
	private class Download implements Runnable {
		OutputStream networkOutput;
		InputStream fileInput;
		ObjectInputStream networkInput;

		public Download(OutputStream networkOutput, InputStream fileInput,
				ObjectInputStream networkInput) {
			this.networkOutput = networkOutput;
			this.fileInput = fileInput;
			this.networkInput = networkInput;
		}

		public void run() {
			try {
				byte[] buffer = new byte[2048];
				while (true) {
					int len = fileInput.read(buffer);
					if (len == -1) {
						break;
					} else {
						networkOutput.write(buffer, 0, len);
						networkOutput.flush();
					}
				}
				fileInput.close();
				networkOutput.close();
				networkInput.close();
				System.out.println("Download finished");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
