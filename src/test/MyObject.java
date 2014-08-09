/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: test.MyObject
 * Description: Remote object for testing.
 */

package test;

import java.io.Serializable;

public class MyObject implements Serializable {
	int v1;
	String v2;
	MyObject v3;
	
	public MyObject(int v1, String v2){
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = null;
	}
	
	public int getV1() {
		return v1;
	}

	public void setV1(int v1) {
		this.v1 = v1;
	}

	public String getV2() {
		return v2;
	}

	public void setV2(String v2) {
		this.v2 = v2;
	}

	public MyObject getV3() {
		return v3;
	}

	public void setV3(MyObject v3) {
		this.v3 = v3;
	}
	
	public String toString() {
		if (v3 == null) {
			return "v1:"+ v1 + " v2:" + v2 + " v3:{null}";
		}
		else {
			return "v1:"+ v1 + " v2:" + v2 + " v3:{" + v3.toString() + "}";
		}
	}
}
