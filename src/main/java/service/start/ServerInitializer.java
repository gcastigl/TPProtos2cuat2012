package service.start;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.util.StringUtil;

import org.apache.log4j.Logger;

import service.AbstractSockectService;
import service.WelcomeSocketService;

public class ServerInitializer {

	private static Logger logger = Logger.getLogger(ProxyInitializer.class);
	
	public void initialize(File serviceConfiguration) throws FileNotFoundException {
		Scanner scanner = new Scanner(serviceConfiguration);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (StringUtil.empty(line) || line.startsWith("#")) {
				continue;
			}
			String[] parts = line.split(",");
			if (parts.length == 2) {
				String className = parts[0].trim();
				int port = Integer.parseInt(parts[1].trim());
				boolean initialized = initialize(port, className);
				if (!initialized) {
					printStatus("Ignoring invalid line: " + line, true);
				} else {
					printStatus("server: " + className + ". Port: " + port, false);
				}
			} else {
				printStatus("Ignoring invalid line: " + line, true);
			}
		}
		scanner.close();
	}
	
	@SuppressWarnings("unchecked")
	private boolean initialize(int port, String className) {
		try {
			Class<? extends AbstractSockectService> clazz;
			if (AbstractSockectService.class.isAssignableFrom(Class.forName(className))) {				
				clazz = (Class<? extends AbstractSockectService>) Class.forName(className);
				new Thread(new WelcomeSocketService(port, clazz)).start();
				return true;
			} else {
				logger.error(className + " does not extend " + AbstractSockectService.class);
				return false;
			}
		} catch (ClassNotFoundException e) {
			logger.error("Could not find class " + className);
			return false;
		}
	}
	
	private void printStatus(String status, boolean isError) {
		if (isError) {
			logger.error("[ERROR]\t " + status);
		} else {
			logger.info("[OK]\t " + status);
		}
	}
}
