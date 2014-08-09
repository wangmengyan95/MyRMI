/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ExceptionTest
 * Description: Remote Interface for testing whether our RMI could handle
 * 				exceptions.
 */

package test;

import myrmi.utility.MyRemote;

public interface ExceptionTest extends MyRemote {
	public void computeException() throws Exception;
}
