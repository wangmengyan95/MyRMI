/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.compiler.StubCompiler
 * Description: This class helps to generate a .java file based on the remote
 * 				object implementation and then uses javac to compile it into
 * 				.class file.
 */

package myrmi.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import myrmi.utility.MyRemote;
import myrmi.utility.MyRemoteException;

public class StubCompiler {
	/*
	 * Create and compile the stub class. Return the name for generated stub
	 * class.
	 */
	public static String compile(String dirPath, Class<?> interfaceClass)
			throws IOException, ClassNotFoundException, MyRemoteException {
		String className = interfaceClass.getName();

		// Check whether the class implements MyRemote interface
		if (!MyRemote.class.isAssignableFrom(interfaceClass))
			throw new MyRemoteException(
					"The class to be compiled to stub should implement MyRemote interface.");

		// Create the java file.
		String stubName = parseClass(className) + "_Stub.java";
		File dir = new File(dirPath);
		// If the directory does not exist, create it.
		if (!dir.exists()) {
			dir.mkdir();
		}

		File wFile = new File(dir, stubName);
		CodeFileWriter writer = new CodeFileWriter(new FileWriter(wFile));

		// Generate the header.
		Class<?>[] interfaces = interfaceClass.getInterfaces();
		for (Class<?> i : interfaces)
			writer.writeLine("import " + i.getName() + ";");

		writer.writeLine("import java.lang.reflect.Method;");
		writer.writeLine("import java.lang.reflect.InvocationTargetException;");
		writer.writeLine("import myrmi.utility.*;");
		writer.newLine();

		// Generate the statement for class.
		StringBuilder state = new StringBuilder();
		state.append("public class " + parseClass(className) + "_Stub ");

		if (interfaces.length > 0)
			state.append("implements ");
		for (int i = 0; i < interfaces.length; i++)
			state.append(interfaces[i].getName()
					+ (i == interfaces.length - 1 ? " " : ", "));
		state.append("{");
		writer.writeLine(state.toString());

		writer.tab();
		writer.writeLine("RemoteObjectReference ref;");
		writer.newLine();

		// Generate the constructor.
		writer.tab();
		writer.writeLine("public " + parseClass(className)
				+ "_Stub(RemoteObjectReference _ref) {");
		writer.tab(2);
		writer.writeLine("try {");
		writer.tab(3);
		writer.writeLine("ref = _ref;");
		writer.tab(2);
		writer.writeLine("} catch(Exception e) {");
		writer.tab(3);
		writer.writeLine("e.printStackTrace();");
		writer.tab(2);
		writer.writeLine("}");
		writer.tab();
		writer.writeLine("}");
		writer.newLine();

		// Generate the body for methods.
		for (Class<?> intf : interfaces) {
			for (Method m : intf.getMethods()) {
				if (canOverride(m)) {
					writer.tab();
					writer.writeLine(generateMethodTitle(m));
					generateMethodBody(writer, m, intf);
					writer.newLine();
				}
			}
		}

		// Close the file.
		writer.writeLine("}");
		writer.close();

		// Compile the generated file.
		Process p = Runtime.getRuntime().exec(
				"javac -cp ./ " + dirPath + "/" + stubName);

		// Wait until the compilation is finished.
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(stubName + " is successfully compiled to "
				+ parseClass(className) + "_Stub.class");

		return parseClass(className) + "_Stub";
	}

	public static void main(String[] args) throws Exception {
		Class<?> toCompile = Class.forName(args[0]);
		compile("download", toCompile);
	}

	/*
	 * Check whether a method can be override in in the stub.
	 */
	static boolean canOverride(Method m) {
		int mod = m.getModifiers();
		return !Modifier.isFinal(mod) && !Modifier.isNative(mod)
				&& Modifier.isPublic(mod);
	}

	/*
	 * Get the class name.
	 */
	static String parseClass(String s) {
		int index = s.lastIndexOf('.');
		return s.substring(index + 1);
	}

	/*
	 * Generate the statement for a method.
	 */
	static String generateMethodTitle(Method m) {
		StringBuilder sb = new StringBuilder();
		sb.append(Modifier.toString(m.getModifiers()).replaceAll(" abstract",
				""));
		sb.append(" " + getType(m.getReturnType(), true));
		sb.append(" " + m.getName() + "(");
		// Parameter list.
		for (int i = 0; i < m.getParameterTypes().length; i++) {
			Class<?>[] types = m.getParameterTypes();
			Class<?> c = types[i];
			sb.append(getType(c, true) + " param" + i);
			if (i < types.length - 1)
				sb.append(", ");
		}
		sb.append(")");
		// Exceptions.
		if (m.getGenericExceptionTypes().length > 0) {
			sb.append(" throws");
			Class<?>[] excepts = m.getExceptionTypes();
			for (int i = 0; i < excepts.length; i++) {
				sb.append(" " + excepts[i].getName());
				if (i < excepts.length - 1)
					sb.append(",");
			}
		}

		return sb.toString();
	}

	/*
	 * Generate the body of a method.
	 */
	static void generateMethodBody(CodeFileWriter writer, Method m,
			Class<?> intf) throws IOException {
		writer.tab();
		writer.writeLine("{");
		writer.tab(2);
		writer.writeLine("Object ret = null;");
		writer.tab(2);
		writer.writeLine("try {");

		writer.tab(3);
		writer.writeLine("try {");

		writer.tab(4);
		StringBuilder sb = new StringBuilder();
		sb.append("Method " + generateMethodVariable(m) + " = "
				+ intf.getName() + ".class.getMethod(\"" + m.getName()
				+ "\", new Class[] { ");
		Class<?>[] params = m.getParameterTypes();
		for (int i = 0; i < params.length; i++)
			sb.append(getType(params[i], true) + ".class"
					+ (i == params.length - 1 ? " " : ", "));
		sb.append("});");
		writer.writeLine(sb.toString());

		// Invoke ror.invoke() method. Convert InvocationTargetException into exceptions
		// thrown by the method in its statement.
		writer.tab(4);
		writer.writeLine("ret = this.ref.invoke(" + generateMethodVariable(m)
				+ ", " + generateParamTypeList(m) + ");");

		writer.tab(3);
		writer.writeLine("} catch (InvocationTargetException e) {");
		writer.tab(4);
		writer.writeLine("throw e.getTargetException();");
		writer.tab(3);
		writer.writeLine("}");

		Class<?>[] excepts = m.getExceptionTypes();
		boolean containsThrowable = false;

		writer.tab(2);
		writer.writeLine("} catch (MyRemoteException e) {");
		writer.tab(3);
		writer.writeLine("e.printStackTrace();");

		for (Class<?> e : excepts) {
			writer.tab(2);
			writer.writeLine("} catch (" + e.getName() + " e) {");

			writer.tab(3);
			writer.writeLine("throw e;");

			if (parseClass(e.getName()).equals("Throwable"))
				containsThrowable = true;
		}

		if (!containsThrowable) {
			writer.tab(2);
			writer.writeLine("} catch (Throwable e) {");

			// Handle exceptions.
			writer.tab(3);
			writer.writeLine("e.printStackTrace();");
		}

		writer.tab(2);
		writer.writeLine("}");

		if (!m.getReturnType().toString().equals("void")) {
			writer.tab(2);
			writer.writeLine("return (" + getType(m.getReturnType(), true)
					+ ") ret;");
		}
		writer.tab();
		writer.writeLine("}");
	}

	/*
	 * Generate the method variable name for the method(in stub)
	 */
	static String generateMethodVariable(Method m) {
		StringBuilder sb = new StringBuilder();
		sb.append("method_" + m.getName());

		Class<?>[] params = m.getParameterTypes();
		for (Class<?> p : params)
			sb.append("_" + parseClass(getType(p, false)));

		return sb.toString();
	}

	/*
	 * Generate the parameter type list for the method, which is needed to 
	 * locate the method in reflection.
	 */
	static String generateParamTypeList(Method m) {
		StringBuilder sb = new StringBuilder();
		sb.append("new Object[] { ");

		Class<?>[] types = m.getParameterTypes();
		for (int i = 0; i < types.length; i++)
			sb.append("param" + i + (i == types.length - 1 ? " " : ", "));

		sb.append("}");
		return sb.toString();
	}

	/*
	 * Generate the type name, extra actions are taken if the type is an array.
	 */
	private static String getType(Class<?> c, boolean addBracket) {
		if (c.isArray()) {
			String[] types = c.getComponentType().getName().split(" ");
			if (addBracket) {
				return types[types.length - 1] + "[]";
			} else {
				return types[types.length - 1] + "Array";
			}
		} else {
			return c.getName();
		}
	}
}
