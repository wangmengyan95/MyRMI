/* 
 * 15-640 Project 2: Remote Method Invocation
 * 
 * Andrew ID: bz1 		(Bo Zhang)
 * 			  mengyanw 	(Mengyan Wang)
 * 
 * Class: myrmi.compiler.CodeFileWriter
 * Description: This class helps to write line and tabs in to a file.
 */

package myrmi.compiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class CodeFileWriter extends BufferedWriter {

	public CodeFileWriter(Writer out) {
		super(out);
	}

	public void writeLine(String line) throws IOException {
		this.write(line);
		this.newLine();
	}
	
	public void tab() throws IOException {
		this.write("\t");
	}
	
	/*
	 * Write n tabs into the file.
	 */
	public void tab(int n) throws IOException {
		if (n > 0) {
			for (int i = 0; i < n; i++)
				this.write("\t");
		}
	}

}
