/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.server.InvokeRequestListener
 * Description: This class defines server thread which listens to a certain client's
 * 				request.
 */

package myrmi.server;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import myrmi.naming.Naming;
import myrmi.utility.Message;
import myrmi.utility.MyRemote;
import myrmi.utility.MyRemoteException;
import myrmi.utility.RemoteObjectReference;

public class InvokeRequestListener implements Runnable {
	ClientInfo clientInfo;
	Map<String, MyRemote> remoteObjectMap;
	
	String regHost;
	int regPort;
	
	int serverPort;
	int downloadPort;
	
	static final int MAX_NAME_NUMBER = 10000;

	public InvokeRequestListener(String regHost, int regPort, ClientInfo clientInfo,
			Map<String, MyRemote> remoteObjectMap, int serverPort, int downloadPort) {
		this.clientInfo = clientInfo;
		this.remoteObjectMap = remoteObjectMap;
		this.regHost = regHost;
		this.regPort = regPort;
		this.serverPort = serverPort;
		this.downloadPort = downloadPort;
	}

	public void run() {
		try {
			handleRequest();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (MyRemoteException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// When the socket error happens, connection lost
			System.out.println("Client lost connection. Client:"
					+ clientInfo.toString());
			clientInfo.setAlive(false);
		}
	}
	
	private String generateName(MyRemote remoteObject) {
		String name = remoteObject.getClass().getName();
		long random = Math.round(Math.random() * MAX_NAME_NUMBER);
		while (remoteObjectMap.containsKey(name + "_" + random))
			random = Math.round(Math.random() * MAX_NAME_NUMBER);
		return name + "_" + random;
	}

	/*
	 * This function gets users' remote function call,
	 * unpackage functions' parameter and call the local
	 * function. Then send the return value or exception back to 
	 * the client.
	 */
	private void handleRequest() throws ClassNotFoundException, EOFException,
			IOException, SecurityException, IllegalAccessException, MyRemoteException {
		while (true) {
			Message request = (Message) clientInfo.getInput().readObject();
			Message reply = null;
			try {
				switch (request.getType()) {
				// Invoke a function
				case Message.MSG_INVOKE:
					if (remoteObjectMap.containsKey(request.getObjectName())) {
						// Use reflection to invoke method
						MyRemote remoteObject = remoteObjectMap.get(request
								.getObjectName());
						Class<?> remoteObjectClass = remoteObject.getClass();
						Class<?>[] argTypes = new Class<?>[request
								.getArgTypeList().size()];
						request.getArgTypeList().toArray(argTypes);
						Method method = remoteObjectClass.getMethod(
								request.getMethodName(), argTypes);
						Object result = method.invoke(remoteObject, request
								.getArgList().toArray());
						
						// Handle remote return value
						if (result instanceof MyRemote) {
							String tempName = generateName((MyRemote) result);
							Naming.bind(regHost, regPort, tempName, (MyRemote) result);
							
							// Return an ror.
							reply = new Message(Message.MSG_INVOKE_REMOTE_RETURN_SUCCESS);
							reply.setRor(new RemoteObjectReference(regHost, regPort, tempName));
						} else {
							// Generate reply message
							reply = new Message(Message.MSG_INVOKE_SUCCESS);
							reply.setReturnValue(result);
						}
					}
					break;

				default:
					break;
				}
			} catch (NoSuchMethodException e) {
				reply = new Message(
						Message.MSG_INVOKE_FAIL_REMOTE_METHOD_NOT_EXIST);
			} catch (IllegalArgumentException e) {
				reply = new Message(
						Message.MSG_INVOKE_FAIL_REMOTE_METHOD_ARGUMENTS_NOT_MATCH);
			} catch (InvocationTargetException e) {
				reply = new Message(
						Message.MSG_INVOKE_FAIL_REMOTE_METHOD_ERROR, e);
			}
			// Write reply to sender
			clientInfo.getOutput().writeObject(reply);
		}
	}
}
