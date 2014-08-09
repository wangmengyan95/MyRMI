/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.RemoteRefTest
 * Description: Remote interface for testing whether our RMI could handle
 * 				remote object reference in parameter and return value.
 */

package test;

import java.io.Serializable;

import myrmi.utility.MyRemote;

public interface RemoteRefTest extends MyRemote, Serializable {
	// Remote type as return value.
	public RemoteRefTest doubleClone();
	// Remote type as reference.
	public int calculate(RemoteRefTest r);
	// Get value.
	public int getValue();
	// Remote type as return value, and client needs to download another stub.
	public RemoteRefTest differentTypeClone();
}
