/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.RemoteRefTestAdd
 * Description: One implementation for testing whether our RMI could handle
 * 				remote object reference in parameter and return value.
 */

package test;

public class RemoteRefTestAdd implements RemoteRefTest {
	
	int val;
	
	public RemoteRefTestAdd(int _val) {
		val = _val;
	}

	public RemoteRefTest doubleClone() {
		return new RemoteRefTestAdd(val + val);
	}

	public int calculate(RemoteRefTest r) {
		return val + r.getValue();
	}

	public int getValue() {
		return val;
	}

	public RemoteRefTest differentTypeClone() {
		return new RemoteRefTestMultiply(val);
	}
	
}
