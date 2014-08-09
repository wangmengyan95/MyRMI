/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.utility.Message
 * Description: This class defines the message that is sent between client 
 * 				and registry server, between client and server, and between
 * 				server and registry server.
 */

package myrmi.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Message implements Serializable {
	// Message types.
	public static final int MSG_BIND = 10;
	public static final int MSG_BIND_SUCCESS = 11;
	public static final int MSG_BIND_FAIL_NAME_EXIST = 12;

	public static final int MSG_LOOKUP = 20;
	public static final int MSG_LOOKUP_REF = 21;
	public static final int MSG_LOOKUP_SUCCESS = 22;
	public static final int MSG_LOOKUP_FAIL_REMOTE_OBJECT_NOT_EXIST = 23;
	public static final int MSG_LIST = 30;
	public static final int MSG_LIST_SUCCESS = 31;

	public static final int MSG_INVOKE = 40;
	public static final int MSG_INVOKE_SUCCESS = 41;
	public static final int MSG_INVOKE_FAIL_REMOTE_OBJECT_NOT_EXIST = 42;
	public static final int MSG_INVOKE_FAIL_REMOTE_METHOD_NOT_EXIST = 43;
	public static final int MSG_INVOKE_FAIL_REMOTE_METHOD_ARGUMENTS_NOT_MATCH = 44;
	public static final int MSG_INVOKE_FAIL_REMOTE_METHOD_ERROR = 45;
	public static final int MSG_INVOKE_REMOTE_RETURN_SUCCESS = 46;

	public static final int MSG_RETURN = 50;

	public static final int MSG_STUB = 60;

	// Instance
	int type;
	String objectName;
	RemoteObjectReference ror;

	// Lookup message
	Map<String, RemoteObjectReference> rorMap;

	// Invoke message
	private String methodName;
	private ArrayList<Class<?>> argTypeList;
	private ArrayList<Object> argList;

	// Invoke return message
	private Object returnValue;
	private String returnValueType;

	// Download message
	private String className;
	
	// Exception message
	private Exception exception;

	public Message(int type) {
		this.type = type;
	}

	public Message(int type, String str) {
		this.type = type;
		this.objectName = str;
	}

	public Message(int type, RemoteObjectReference ror) {
		this.type = type;
		this.ror = ror;
	}

	public Message(int type, Exception exception) {
		this.type = type;
		this.exception = exception;
	}

	public Message(int type, Map<String, RemoteObjectReference> rorMap) {
		this.type = type;
		this.rorMap = rorMap;
	}

	public Message(int type, String objectName, RemoteObjectReference ror) {
		this.type = type;
		this.objectName = objectName;
		this.ror = ror;
	}

	public Message(int type, String objectName, String methodName,
			ArrayList<Class<?>> argTypeList, ArrayList<Object> argList) {
		this.type = type;
		this.objectName = objectName;
		this.methodName = methodName;
		this.argTypeList = argTypeList;
		this.argList = argList;
	}

	public Message(int type, String returnValueType, Object returnValue) {
		this.type = type;
		this.returnValueType = returnValueType;
		this.returnValue = returnValue;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		switch (type) {
		case MSG_BIND:
			buffer.append("TYPE:MSG_BIND|");
			buffer.append("OBJECT_NAME:" + objectName + "|");
			buffer.append("ROR:" + ror.toString() + "|");
			break;
		case MSG_LOOKUP:
			buffer.append("TYPE:MSG_LOOKUP|");
			buffer.append("OBJECT_NAME:" + objectName + "|");
			break;
		case MSG_LIST:
			buffer.append("TYPE:MSG_LIST|");
			break;
		case MSG_STUB:
			buffer.append("TYPE:MSG_STUB|");
			buffer.append("CLASSNAME:" + className);
			break;
		default:
			return "Unknown message.";
		}
		return buffer.toString();
	}

	public String getExceptionString() {
		switch (type) {
		case MSG_BIND_FAIL_NAME_EXIST:
			return "Object name has already existed in the registry server";
		case MSG_LOOKUP_FAIL_REMOTE_OBJECT_NOT_EXIST:
			return "Object name has already does not exist in the registry server";
		case MSG_INVOKE_FAIL_REMOTE_OBJECT_NOT_EXIST:
			return "Object name does not exist in the remote server";
		case MSG_INVOKE_FAIL_REMOTE_METHOD_NOT_EXIST:
			return "Remote method does not exist in the remote server";
		case MSG_INVOKE_FAIL_REMOTE_METHOD_ARGUMENTS_NOT_MATCH:
			return "Remote method arguments not match";
		case MSG_INVOKE_FAIL_REMOTE_METHOD_ERROR:
			return "Remote method invocation error" + exception.toString();
		default:
			return "Unknown eexception.";
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public RemoteObjectReference getRor() {
		return ror;
	}

	public void setRor(RemoteObjectReference ror) {
		this.ror = ror;
	}

	public Map<String, RemoteObjectReference> getRorMap() {
		return rorMap;
	}

	public void setRorMap(Map<String, RemoteObjectReference> rorMap) {
		this.rorMap = rorMap;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public ArrayList<Class<?>> getArgTypeList() {
		return argTypeList;
	}

	public void setArgTypeList(ArrayList<Class<?>> argTypeList) {
		this.argTypeList = argTypeList;
	}

	public ArrayList<Object> getArgList() {
		return argList;
	}

	public void setArgList(ArrayList<Object> argList) {
		this.argList = argList;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public String getReturnValueType() {
		return returnValueType;
	}

	public void setReturnValueType(String returnValueType) {
		this.returnValueType = returnValueType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
