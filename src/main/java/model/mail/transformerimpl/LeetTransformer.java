package model.mail.transformerimpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import model.parser.mime.MimeHeader;
import model.util.Base64Util;

import org.apache.commons.codec.net.QuotedPrintableCodec;

public class LeetTransformer implements Transformer {

	@Override
	public StringBuilder transform(StringBuilder text,
			Map<String, MimeHeader> partheaders) throws IOException {
		MimeHeader contentType = partheaders.get("Content-Type");
		MimeHeader contentTransferEncoding = partheaders
				.get("Content-Transfer-Encoding");
		if (contentType == null) {
			return text;
		}
		if (contentTransferEncoding == null
				&& contentType.getValue().startsWith("text/plain")) {
			return new StringBuilder(toLeet(text.toString()));
		} else if ("base64".equals(contentTransferEncoding.getValue())) {
			File decodedFile;
			File convertedText = File.createTempFile("encode_", ".tmp");
			FileWriter writer = new FileWriter(convertedText);
			StringBuilder builder = new StringBuilder();
			try {
				decodedFile = Base64Util.decodeUsingOS(text.toString());
				Scanner decodedContents = new Scanner(decodedFile);
				while (decodedContents.hasNextLine()) {
					writer.append(toLeet(decodedContents.nextLine() + "\r\n"));
				}
				decodedContents.close();
				writer.close();
				File encodedText = Base64Util.encodeToFile(convertedText);
				Scanner encodedTextScaner = new Scanner(encodedText);
				while (encodedTextScaner.hasNextLine()) {
					String line = encodedTextScaner.nextLine();
					builder.append(line);
				}
				encodedTextScaner.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new IOException();
			}
			return builder;
		} else { // quoted-printable
			String decodeString = decode(text.toString());
			String transformed = toLeet(decodeString);
			return new StringBuilder(transformed);
		}
	}

	private String toLeet(String text) {
		String textString = text;
		textString = textString.replace("a", "4");
		textString = textString.replace("e", "3");
		textString = textString.replace("i", "1");
		textString = textString.replace("a", "4");
		return textString;
	}

	private String decode(String quotedPrintable) {
		try {
			QuotedPrintableCodec codec = new QuotedPrintableCodec("ISO-8859-1");
			return codec.decode(quotedPrintable);
		} catch (Exception e) {
			return quotedPrintable;
		}
	}

}