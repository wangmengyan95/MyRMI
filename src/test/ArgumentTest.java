/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ArgumentTest
 * Description: Remote Interface for testing whether our RMI could handle different
 * 				types of arguments.
 */

package test;

import java.util.List;

import myrmi.utility.MyRemote;

public interface ArgumentTest extends MyRemote {
	// No argu
	public MyObject computeNoArgu();
	// Single basic type argu
	public MyObject computeSingleBasicTypeArgu(int value);
	// Single class type argu
	public MyObject computeSingleClassTypeArgu(String str);
	// Single custom class type argu
	public MyObject computeSingleCustomClassArgu(MyObject object);
	// Single array argu
	public MyObject computeSingleArrayArgu(MyObject[] objects);
	// Single generic list argu
	public MyObject computeSinglegenericsListArgu(List<MyObject> objectList);
	// Multiple argus
	public MyObject computeMultiArgus(int value, String str, MyObject object);
}
