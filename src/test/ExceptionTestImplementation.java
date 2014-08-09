/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ExceptionTestImplementation
 * Description: Remote implementation for testing whether our RMI could handle
 * 				exceptions.
 */

package test;

public class ExceptionTestImplementation implements ExceptionTest {

	public void computeException() throws Exception {
		throw new RuntimeException("Exception message is successfully transferred back to client.");
	}

}
