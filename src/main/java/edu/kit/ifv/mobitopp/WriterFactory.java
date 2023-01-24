package edu.kit.ifv.mobitopp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WriterFactory {
	
	public static Writer getWriter(File path) throws IOException {
		FileWriter fileWriter = new FileWriter(path);
		return new BufferedWriter(fileWriter);
	}
	
	public static void finishWriter(Writer writer) throws IOException {
		writer.write("\r\n");
		writer.flush();
		writer.close();
	}

}
