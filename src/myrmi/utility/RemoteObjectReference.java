/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.utility.RemoteObjectReference
 * Description: The class describes the reference to remote object in client side.
 */


package myrmi.utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import myrmi.naming.Naming;

public class RemoteObjectReference implements Serializable {
	// Basic information for finding the remote object on server.
	String host;
	int port;
	int downloadPort;
	String name;
	String className;
	
	String regHost;
	int regPort;
	
	// Caching the socket connection.
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream input;

	public RemoteObjectReference(String _host, int _port, int _downloadPort,
			String _name, String _className) {
		host = _host;
		port = _port;
		name = _name;
		downloadPort = _downloadPort;
		className = _className;
		
		socket = null;
		output = null;
		input = null;
	}
	
	public RemoteObjectReference(String _regHost, int _regPort, String _name) {
		regHost = _regHost;
		regPort = _regPort;
		name = _name;
	}
	
	/*
	 * Get the socket connection from cache. If the cache does not contain a valid
	 * socket to server, create one.
	 */
	private void getSocketConnection() throws UnknownHostException, IOException {
		if (socket == null || input == null || output == null) {
			socket = new Socket(host, port);
			output = new ObjectOutputStream(
					socket.getOutputStream());
			input = new ObjectInputStream(
					socket.getInputStream());
		}
	}

	/*
	 * Create a socket to contact the server (The socket is cached since then), and 
	 * create a message containing the ROR, method name, parameter types and the 
	 * value of parameters, send the message to the server, and wait for the message 
	 * from server, which might contain the object returned or exceptions generated 
	 * in server.
	 */
	public Object invoke(Method method, Object[] params) throws InvocationTargetException, MyRemoteException {
		try {
			// Initialize socket.
			getSocketConnection();
			
			// Send lookup message.
			ArrayList<Class<?>> argTypeList = new ArrayList<Class<?>>();
			ArrayList<Object> argList = new ArrayList<Object>();
			for (Class<?> c : method.getParameterTypes())
				argTypeList.add(c);
			for (Object o : params)
				argList.add(o);
			Message invokeMsg = new Message(Message.MSG_INVOKE, name,
					method.getName(), argTypeList, argList);
			output.writeObject(invokeMsg);

			// Receive result from register server.
			Message invokeRes = (Message) input.readObject();

			switch (invokeRes.getType()) {
			case Message.MSG_INVOKE_SUCCESS:
				return invokeRes.getReturnValue();
			case Message.MSG_INVOKE_REMOTE_RETURN_SUCCESS:
				// Localize the return value of Remote type.
				RemoteObjectReference returnRor = invokeRes.getRor();
				return Naming.lookup(returnRor.getRegHost(), returnRor.getRegPort(), returnRor.getName());
			case Message.MSG_INVOKE_FAIL_REMOTE_METHOD_ERROR:
				throw (InvocationTargetException) (invokeRes.getException());
			default:
				throw new MyRemoteException(invokeRes.getExceptionString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			socket = null;
			output = null;
			input = null;
			// Handle the exceptions. If the exception is InvocationTargetException or
			// MyRemoteException, it should be handled by the stub.
			if (e instanceof InvocationTargetException)
				throw (InvocationTargetException) e;
			if (e instanceof MyRemoteException)
				throw (MyRemoteException) e;
			return null;
		}
	}

	

	public String toString() {
		return "{HOST:" + host + "|PORT:" + port + "|DOWNLOADPORT:"
				+ downloadPort + "|NAME:" + name + "|CLASSNAME:"+className+"}";
	}
	
	/*
	 * Override. The socket should not be serialized.
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(host);
		out.writeInt(port);
		out.writeInt(downloadPort);
		out.writeObject(name);
		out.writeObject(className);
		out.writeObject(regHost);
		out.writeInt(regPort);
	}

	/*
	 * Override. The socket should not be serialized.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		host = (String) in.readObject();
		port = in.readInt();
		downloadPort = in.readInt();
		name = (String) in.readObject();
		className = (String) in.readObject();
		regHost = (String) in.readObject();
		regPort = in.readInt();
		
		socket = null;
		input = null;
		output = null;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getDownloadPort() {
		return downloadPort;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public String getRegHost() {
		return regHost;
	}

	public int getRegPort() {
		return regPort;
	}
}
