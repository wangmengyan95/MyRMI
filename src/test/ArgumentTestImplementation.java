/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ArgumentTestImplementation
 * Description: Remote Implementation for testing whether our RMI could handle different
 * 				types of arguments.
 */

package test;

import java.util.List;

public class ArgumentTestImplementation implements ArgumentTest {
	public static String TAG = "ARGUMENT TEST:";
	public MyObject computeNoArgu() {
		System.out.println(TAG + "No argument");
		return new MyObject(0, "null");
	}

	public MyObject computeSingleBasicTypeArgu(int value) {
		System.out.println(TAG + "Single basic type argument");
		return new MyObject(value, "null");
	}

	public MyObject computeSingleClassTypeArgu(String str) {
		System.out.println(TAG + "Single class type argument");
		return new MyObject(0, str);
	}

	public MyObject computeSingleCustomClassArgu(MyObject object) {
		System.out.println(TAG + "Single custom class type argument");
		MyObject obj = new MyObject(0, "null");
		obj.setV3(object);
		return obj;
	}

	public MyObject computeSingleArrayArgu(MyObject[] objects) {
		System.out.println(TAG + "Single array type argument");
		MyObject obj = new MyObject(0, "null");
		obj.setV3(objects[0]);
		return obj;
	}

	public MyObject computeSinglegenericsListArgu(List<MyObject> objectList) {
		System.out.println(TAG + "Single library data type argument");
		MyObject obj = new MyObject(0, "null");
		obj.setV3(objectList.get(0));
		return obj;
	}

	public MyObject computeMultiArgus(int value, String str, MyObject object) {
		System.out.println(TAG + "Multiple arguments");
		MyObject obj = new MyObject(value, str);
		obj.setV3(object);
		return obj;
	}

}
