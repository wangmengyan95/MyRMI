/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.RemoteRefTestMultiply
 * Description: Another implementation for testing whether our RMI could handle
 * 				remote object reference in parameter and return value.
 */

package test;

public class RemoteRefTestMultiply implements RemoteRefTest {
	int val;
	
	public RemoteRefTestMultiply(int _val) {
		val = _val;
	}

	public RemoteRefTest doubleClone() {
		return new RemoteRefTestMultiply(val * val);
	}

	public int calculate(RemoteRefTest r) {
		return getValue() * r.getValue();
	}

	public int getValue() {
		return val;
	}

	public RemoteRefTest differentTypeClone() {
		return new RemoteRefTestAdd(val);
	}

}
