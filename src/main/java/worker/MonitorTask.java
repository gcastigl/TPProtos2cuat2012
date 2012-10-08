package worker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import util.Config;

public class MonitorTask extends TimerTask {

	private static Logger logger = Logger.getLogger(TimerTask.class);
	
	private String taskName;
	private DataOutputStream outputStream;

	public MonitorTask(String objectName, DataOutputStream outputStream) {
		this.taskName = objectName;
		this.outputStream = outputStream;
	}

	/**
	 * When the timer executes, this code is run.
	 */
	public void run() {
		logger.debug(taskName + " is not excecuting.");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String current_time = format.format(new Date());
		try {
			outputStream.writeBytes("Statistics (" + current_time + ")\n");
			Scanner scanner = reset();
			while (scanner.hasNextLine()) {
				outputStream.writeBytes("\t" +scanner.nextLine() + "\r\n");
			}
			scanner.close();
		} catch (IOException e) {
			logger.error(taskName + " / ");
			e.printStackTrace();
		}
	}
	
	private Scanner reset() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(Config.getInstance().get("statistics_file"));
		return new Scanner(in);
	}
	
}