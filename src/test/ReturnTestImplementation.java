/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ReturnTest
 * Description: Remote implementation for testing whether our RMI could handle
 * 				different types of return value.
 */


package test;

import java.util.ArrayList;
import java.util.List;

public class ReturnTestImplementation implements ReturnTest {
	public static String TAG = "RETURN VALUE TEST:";
	
	public void computeNoReturn() {
		System.out.println(TAG + "No return");
		return;
	}

	public int computeReturnBasicType() {
		System.out.println(TAG + "Return basic type");
		System.out.println("Result:" + 1);
		return 1;
	}

	public int[] computeReturnBasicTypeArray() {
		System.out.println(TAG + "Return basic type array");
		int[] array = {1, 2, 3};
		System.out.println("Result:[1, 2, 3]");
		return array;
	}

	public String computeReturnClass() {
		System.out.println(TAG + "Return class type");
		System.out.println("Result:Test");
		return "Test";
	}

	public String[] computeReturnClassArray() {
		System.out.println(TAG + "Return class type array");
		String[] array = {"1", "2", "3"};
		System.out.println("Result:[\"1\", \"2\", \"3\"]");
		return array;
	}

	public MyObject computeReturnCustomClass() {
		System.out.println(TAG + "Return custom class type");
		MyObject obj = new MyObject(1, "1");
		MyObject obj1 = new MyObject(2, "2");
		MyObject obj2 = new MyObject(3, "3");
		obj.setV3(obj1);
		obj1.setV3(obj2);
		System.out.println("Result:"+obj.toString());
		return obj;
	}

	public MyObject[] computeReturnCustomClassArray() {
		System.out.println(TAG + "Return custom class type array");
		MyObject[] objs = new MyObject[3];
		objs[0] = new MyObject(1, "1");
		objs[1] = new MyObject(2, "2");
		objs[2] = new MyObject(3, "3");
		System.out.println("Result:["+objs[0].toString()+","+objs[1].toString()+","+objs[2].toString()+"]");
		return objs;
	}

	public List<MyObject> computeReturnGenericsList() {
		System.out.println(TAG + "Return generics list");
		ArrayList<MyObject> list = new ArrayList<MyObject>();
		list.add(new MyObject(1, "1"));
		list.add(new MyObject(2, "2"));
		list.add(new MyObject(3, "3"));
		System.out.println("Result:["+list.get(0).toString()+","+list.get(1).toString()+","+list.get(2).toString()+"]");
		return list;
	}
}
