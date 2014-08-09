/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.server.InvokeRequestServerListener
 * Description: InvokeRequestServerListener listens to requests from clients,
 * 				maintains a DownloadServerListener for download service, and 
 * 				instantiates InvokeRequestListener for each client.
 */

package myrmi.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import myrmi.utility.MyRemote;
import myrmi.utility.MyRemoteException;

public class InvokeRequestServerListener implements Runnable{
	static final int MAX_PORT_NUMBER = 10000;
	static Random portGenerator = new Random();
	
	ServerSocket serverSocket;
	List<ClientInfo> clientInfoList; // Client connection cache pool
	Map<String, MyRemote> remoteObjectMap; // Rormap
	
	String regHost;
	int regPort;
	
	int serverPort;
	int downloadPort;
	DownloadServerListener downloadServer;
	
	public InvokeRequestServerListener(String _regHost, int _regPort,
			String _stubFileDirName) throws MyRemoteException, IOException {
		serverPort = portGenerator.nextInt(MAX_PORT_NUMBER);

		serverSocket = new ServerSocket(serverPort);
		clientInfoList = Collections
				.synchronizedList(new ArrayList<ClientInfo>());
		remoteObjectMap = new ConcurrentHashMap<String, MyRemote>();
		regHost = _regHost;
		regPort = _regPort;

		// Initialize and run the download server.
		downloadPort = portGenerator.nextInt(MAX_PORT_NUMBER);
		downloadServer = new DownloadServerListener(_stubFileDirName,
				downloadPort);
		new Thread(downloadServer).start();
	}
	
	public String getServerAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	public int getServerPort() {
		return serverPort;
	}

	public int getDownloadPort() {
		return downloadPort;
	}
	
	public void addRemoteObject(String name, MyRemote remoteObject) {
		remoteObjectMap.put(name, remoteObject);
	}
	
	public void run() {
		try {
			handleRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * This function generates a socket connection for a specific users.
	 * Then, it create a separate thread to handle the remote method invocation
	 * from this client.
	 */
	private void handleRequest() throws IOException{
		while (true){
			// Generate client socket, add 
			Socket socket = serverSocket.accept();
			ClientInfo clientInfo = new ClientInfo(socket);
			clientInfoList.add(clientInfo);	
			
			// Start thread to listen invoke message from specific client
			new Thread(new InvokeRequestListener(regHost, regPort, clientInfo, remoteObjectMap, serverPort, downloadPort)).start();
		}
	}
	

}
