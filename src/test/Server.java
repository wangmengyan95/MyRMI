/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.Server
 * Description: Test framework in server side.
 */

package test;

import myrmi.naming.*;
import myrmi.utility.MyRemote;
import myrmi.utility.MyRemoteException;

public class Server {
	public static void main(String[] args) {		
		// Test return
		MyRemote returnTest = new ReturnTestImplementation();
		MyRemote argumentTest = new ArgumentTestImplementation();
		MyRemote exceptionTest = new ExceptionTestImplementation();
		MyRemote remoteRefTest = new RemoteRefTestAdd(1);
		try {		
			Naming.bind("localhost", 1357, "returnTest", returnTest);
			System.out.println("returnTest bound");
			Naming.bind("localhost", 1357, "argumentTest", argumentTest);
			System.out.println("argumentTest bound");
			Naming.bind("localhost", 1357, "exceptionTest", exceptionTest);
			System.out.println("exceptionTest bound");
			Naming.bind("localhost", 1357, "remoteRefTest", remoteRefTest);
			System.out.println("remoteRefTest bound");
		} catch (MyRemoteException e) {
			e.printStackTrace();
		}
	}
}
