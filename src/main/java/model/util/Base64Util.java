package model.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

	public static String decode(String text) {
		return new String(new Base64().decode(text));
	}
	
	public static File encodeUsingOS(File file) throws IOException, InterruptedException {
		List<String> commands = new LinkedList<String>();
		commands.add("base64");
		commands.add(file.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(commands);
		File decodedContents = File.createTempFile("encoded_", ".tmp");
		pb.redirectOutput(decodedContents);
		Process process = pb.start();
		process.waitFor();
		file.delete();
		return decodedContents;
	}
	
	public static File decodeUsingOS(String text) throws IOException, InterruptedException {
		File file = createFileWithContents(text);
		List<String> commands = new LinkedList<String>();
		commands.add("base64");
		commands.add("-D");
		commands.add(file.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(commands);
		File decodedContents = File.createTempFile("decode_", ".tmp");
		pb.redirectOutput(decodedContents);
		Process process = pb.start();
		process.waitFor();
		file.delete();
		return decodedContents;
	}

	private static File createFileWithContents(String text) throws IOException {
		File file = File.createTempFile("decode_", ".tmp");
		FileWriter writer = new FileWriter(file);
		writer.append(text);
		writer.flush();
		writer.close();
		return file;
	}
	
}
