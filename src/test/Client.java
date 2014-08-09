/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.Client
 * Description: Test framework in client side.
 */

package test;

import java.util.List;
import java.util.Scanner;

import myrmi.naming.*;
import myrmi.utility.MyRemoteException;

public class Client {
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		while (true) {
			System.out.println("Please input which type of test cases you want to run");
			System.out.println("1. Argument test");
			System.out.println("2. Return value test");
			System.out.println("3. Exception test");
			System.out.println("4. Remote object test");
			
			String command = reader.nextLine();
			if (command.equals("1")) {
				testArgument();
			}
			else if(command.equals("2")) {
				testReturn();
			}
			else if(command.equals("3")) {
				testException();
			}
			else if(command.equals("4")) {
				testRemoteRef();
			}
			else {
				System.out.println("Please input valid type");
			}
		}
	}
	
	public static void testReturn(){
		String name = "returnTest";
		ReturnTest returnTestRemote = null;
		
		try {
			returnTestRemote = (ReturnTest) Naming.lookup("localhost", 1357, name);
		} catch (MyRemoteException e) {
			e.printStackTrace();
		}
		
		returnTestRemote.computeNoReturn();
		
		int value = returnTestRemote.computeReturnBasicType();
		System.out.println("Return basic type");
		System.out.println("Result:" + value);

		
		int[] intArray = returnTestRemote.computeReturnBasicTypeArray();
		System.out.println("Return basic type array");
		System.out.print("Result:");
		for(int element : intArray){
			System.out.print(element + " ");
		}
		System.out.println();
		
		String str = returnTestRemote.computeReturnClass();
		System.out.println("Return class type");
		System.out.println("Result:" + str);
		
		String[] strArray = returnTestRemote.computeReturnClassArray();
		System.out.println("Return class type array");
		System.out.print("Result:");
		for(String element : strArray){
			System.out.print(element + " ");
		}
		System.out.println();	
		
		MyObject myObject = returnTestRemote.computeReturnCustomClass();
		System.out.println("Return customclass type");
		System.out.println("Result:" + myObject.toString());		

		MyObject[] myObjectArray = returnTestRemote.computeReturnCustomClassArray();
		System.out.println("Return custom class type array");
		System.out.print("Result:");
		for(MyObject element : myObjectArray){
			System.out.print(element.toString() + " ");
		}
		System.out.println();	
		
		
		List<MyObject> myObjectList = returnTestRemote.computeReturnGenericsList();
		System.out.println("Return basic type generic list");
		System.out.print("Result:");
		for(MyObject element : myObjectList){
			System.out.print(element.toString() + " ");
		}
		System.out.println();	
	}
	
	public static void testArgument() {
		String name = "argumentTest";
		ArgumentTest argumentTestRemote = null;
		
		try {
			argumentTestRemote = (ArgumentTest) Naming.lookup("localhost", 1357, name);
		} catch (MyRemoteException e) {
			e.printStackTrace();
		}		
		
		MyObject obj = argumentTestRemote.computeNoArgu();
		System.out.println("No argument");		
	
		obj = argumentTestRemote.computeSingleBasicTypeArgu(1);
		System.out.println("Single basic type argument");
		
		obj = argumentTestRemote.computeSingleClassTypeArgu("test");
		System.out.println("Single class type argument");

		obj = argumentTestRemote.computeSingleCustomClassArgu(new MyObject(1, "2"));
		System.out.println("Single custome class type argument");	
	
		MyObject[] objects = {new MyObject(1, "1"), new MyObject(2, "2")};
		obj = argumentTestRemote.computeSingleArrayArgu(objects);
		System.out.println("Single array argument");

		obj = argumentTestRemote.computeMultiArgus(1, "1", new MyObject(2, "2"));
		System.out.println("Multiple arguments");	
	
	}
	
	public static void testException() {
		String name = "exceptionTest";
		ExceptionTest exceptionTestRemote = null;
		
		try {
			exceptionTestRemote = (ExceptionTest) Naming.lookup("localhost", 1357, name);
		} catch (MyRemoteException e) {
			e.printStackTrace();
		}		
		
		try {
			exceptionTestRemote.computeException();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void testRemoteRef() {
		String name = "remoteRefTest";

		try {
			RemoteRefTest remoteRefTest = (RemoteRefTest) Naming.lookup(
					"localhost", 1357, name);
			System.out.println("Original remote reference value: "
					+ remoteRefTest.getValue());
			RemoteRefTest remoteRefTest2 = remoteRefTest.doubleClone();
			System.out.println("New returned remote reference value: "
					+ remoteRefTest2.getValue());
			System.out.println("Pass remote reference as parameter: "
					+ remoteRefTest.calculate(remoteRefTest2));
			RemoteRefTest remoteRefTest3 = remoteRefTest2.differentTypeClone();
			System.out.println("Different class type that client does not have stub: " + remoteRefTest3.calculate(remoteRefTest2));
		} catch (MyRemoteException e) {
			e.printStackTrace();
		}
	}
}
