/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.ClientRefTestSubstract
 * Description: One client implementation for testing whether our RMI could handle
 * 				remote object reference in parameter.
 */

package test;

public class ClientRefTestSubstract implements RemoteRefTest {
	
	int val;
	
	public ClientRefTestSubstract(int _val) {
		val = _val;
	}

	public RemoteRefTest doubleClone() {
		return new ClientRefTestSubstract(val - val);
	}

	public int calculate(RemoteRefTest r) {
		return val - r.getValue();
	}

	public int getValue() {
		return val;
	}

	public RemoteRefTest differentTypeClone() {
		return new ClientRefTestSubstract(val * val);
	}
	
}
