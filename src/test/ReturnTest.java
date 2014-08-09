/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ReturnTest
 * Description: Remote interface for testing whether our RMI could handle
 * 				different types of return value.
 */

package test;

import java.util.List;

import myrmi.utility.MyRemote;

public interface ReturnTest extends MyRemote{
	// No return
	public void computeNoReturn();
	// Basic type return
	public int computeReturnBasicType();
	// Basic type array return
	public int[] computeReturnBasicTypeArray();
	// Class return
	public String computeReturnClass();
	// Class type array return
	public String[] computeReturnClassArray();
	// Custom class return
	public MyObject computeReturnCustomClass();
	// Custom class return
	public MyObject[] computeReturnCustomClassArray();
	// Generic list return
	public List<MyObject> computeReturnGenericsList();
}
