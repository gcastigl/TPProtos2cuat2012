package model.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import model.configuration.ConfigUtil;
import model.configuration.SimpleListConfiguration;
import model.mail.transformerimpl.HideSenderTransformer;
import model.mail.transformerimpl.ImageTransformer2;
import model.mail.transformerimpl.LeetTransformer;
import model.mail.transformerimpl.Transformer;
import model.parser.mime.MimeHeader;

import org.apache.log4j.Logger;

public class MailTransformer {

	private static final Logger logger = Logger.getLogger(MailTransformer.class);
	private static final SimpleListConfiguration transConfig = ConfigUtil.getInstance().getSimpleListConfig("transformation");
	private static final SimpleListConfiguration externalTransConfig = ConfigUtil.getInstance().getSimpleListConfig("external_transformation");

	public boolean hasActiveTransformations() {
		return !transConfig.getValues().isEmpty() || !externalTransConfig.getValues().isEmpty();
	}

	public void transformHeader(MimeHeader header) throws IOException {
		List<HeaderTransformer> transformers = new ArrayList<HeaderTransformer>();
		for (String option : transConfig.getValues()) {
			if ("hidesender".equals(option)) {
				transformers.add(new HideSenderTransformer());
			}
		}
		for (HeaderTransformer headerTransformer : transformers) {
			headerTransformer.transform(header);
		}
	}

	public StringBuilder transformPart(MimeHeaderCollection partHeaders, StringBuilder part) throws IOException {
		StringBuilder retPart = part;
		List<Transformer> transformers = getTransformerList();
		for (Transformer transformer : transformers) {
			try {
				retPart = transformer.transform(retPart, partHeaders);
			} catch (Exception e) {
				logger.error("Could not apply transformer: " + transformer.getClass(), e);
			}
		}
		return retPart;
	}

	private List<Transformer> getTransformerList() {
		List<Transformer> transformers = new ArrayList<Transformer>();
		Collection<String> options = transConfig.getValues();
		for (String option : options) {
			if ("l33t".equals(option.toLowerCase())) {
				transformers.add(new LeetTransformer());
			} else if ("rotateimages".equals(option.toLowerCase())) {
				transformers.add(new ImageTransformer2());
			}
		}
		return transformers;
	}

	public void transformComplete(Mail mail) {
		List<String> externalTransformers = getExternalTransformerList();
		try {
			File transformedIn = mail.getContents();
			File transformedOut = File.createTempFile("externalTransformOut", ".txt");
			for (String command : externalTransformers) {
				final Process process = createProcess(command, transformedIn);
				boolean success = execute(process, command, transformedOut);
				if (success) {
					mail.setContents(transformedOut);
					// switch in <-> out
					File tmp = transformedIn;
					transformedIn = transformedOut;
					transformedOut = tmp;
				}
			}
			if (transformedIn == mail.getContents()) {
				transformedOut.delete();
			} else {
				transformedIn.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("External trasformations could not be applied!!");
		}
	}

	private List<String> getExternalTransformerList() {
		List<String> transformers = new LinkedList<String>();
		Collection<String> options = externalTransConfig.getValues();
		for (String option : options) {
			transformers.add(option);
		}
		return transformers;
	}
	
	private Process createProcess(String command, File inputFile) throws IOException {
		List<String> commands = new LinkedList<String>();
		commands.addAll(Arrays.asList(command.split(" ")));
		commands.add(inputFile.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(commands);
		return pb.start();
	}
	
	private boolean execute(Process process, String command, File out) throws IOException {
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		FileWriter writer = new FileWriter(out);
		String line;
		while ((line = reader.readLine()) != null) {
			writer.append(line + "\n");
		}
		writer.close();
		reader.close();
		int exitValue = process.exitValue();
		if (exitValue == 0) {
			return true;
		} else {
			logger.warn(command + " did not finish succesfuly. Exit code " + exitValue);
			return false;
		}
	}
}
