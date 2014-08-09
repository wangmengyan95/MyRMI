/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.utility.MyRemoteException
 * Description: MyRemoteException is the exception related to RMI process.
 */


package myrmi.utility;

public class MyRemoteException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public MyRemoteException(String message){
		super(message);
	}
}
