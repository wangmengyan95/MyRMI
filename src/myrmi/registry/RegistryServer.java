/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.registry.RegistryServer
 * Description: This class defines the registry server.
 */

package myrmi.registry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import myrmi.naming.Naming;
import myrmi.utility.Message;
import myrmi.utility.RemoteObjectReference;

public class RegistryServer {
	ServerSocket serverSocket;
	Map<String, RemoteObjectReference> rorMap;
	
	public RegistryServer() {
		rorMap = new ConcurrentHashMap<String, RemoteObjectReference>(); 
	}
	
	public static void main(String[] args) {
		RegistryServer server = new RegistryServer();
		server.launch();
		System.out.println("Registry server is launched.");
	}
	
	/*
	 * Launch a registry server thread.
	 */
	public void launch(){
		RegistryServerListener listener = new RegistryServerListener();
		new Thread(listener).start();
	}
	
	/*
	 * Inner class which creates the ServerListener thread. 
	 * This thread is used to listen request from client or server and send back result
	 */
	private class RegistryServerListener implements Runnable{		
		public RegistryServerListener(){
			try {
				serverSocket = new ServerSocket(Naming.PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void handleRequest() throws IOException, SocketException, ClassNotFoundException{
			while(true){
				Socket socket = serverSocket.accept();
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				// Read message from the sender
				Message request = (Message)input.readObject();
				Message reply = null;
				
				// Handle request based on message type
				switch (request.getType()) {
				case Message.MSG_BIND:
					// Name duplication
					if (rorMap.containsKey(request.getObjectName())) { 
						reply = new Message(Message.MSG_BIND_FAIL_NAME_EXIST);
					}
					else {
						rorMap.put(request.getObjectName(), request.getRor());
						reply = new Message(Message.MSG_BIND_SUCCESS);
					}
					break;
				case Message.MSG_LOOKUP:
					if (rorMap.containsKey(request.getObjectName())) {
						RemoteObjectReference ror = rorMap.get(request.getObjectName());
						reply = new Message(Message.MSG_LOOKUP_SUCCESS, ror);
					}
					// Name not found
					else {
						reply = new Message(Message.MSG_LOOKUP_FAIL_REMOTE_OBJECT_NOT_EXIST);
					}
					break;
				case Message.MSG_LIST:
					reply = new Message(Message.MSG_LIST_SUCCESS, rorMap);
					break;
				default:
					break;
				}	
				// Write reply to sender
				output.writeObject(reply);
			}
		}
		
		public void run(){
			try {
				handleRequest();
			} catch (SocketException e) {
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
