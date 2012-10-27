package service.command.impl.mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import model.mail.Mail;

import org.apache.log4j.Logger;

import service.AbstractSockectService;
import service.MailSocketService;
import service.command.ServiceCommand;

public class RetrCommand extends ServiceCommand {

	protected static final Logger logger = Logger.getLogger(DeleCommand.class);

	public RetrCommand(AbstractSockectService owner) {
		super(owner);
	}

	@Override
	public void execute(String[] params) throws Exception {
		MailSocketService mailSocketService = (MailSocketService) owner;
		boolean showToClient = (params.length == 3) ? Boolean.valueOf(params[3]) : true;
		mailSocketService.echoLineToOriginServer(getOriginalLine());
		BufferedReader mailInStream = mailSocketService.readFromOriginServer();
		String firstLine = mailInStream.readLine();
		if (showToClient) {
			mailSocketService.echoLine(firstLine);
		}
		if (!firstLine.toUpperCase().startsWith("+OK")) {
			return;
		}
		File mailContent = mailSocketService.getMailRetriever().retrieve(params[0], mailInStream);
		int sizeInBytes = Integer.valueOf(firstLine.split(" ")[1]);
		Mail mail = mailSocketService.getMailMimeParser().parse(mailContent, sizeInBytes, mailSocketService.getMailTranformer());
		if (showToClient) {
			echoMailToClient(mail);
		} else {
			getBundle().put("DELE_" + params[0], mail);
		}
	}

	private void echoMailToClient(Mail mail) throws IOException {
		MailSocketService mailSocketService = (MailSocketService) owner;
		DataOutputStream os = mailSocketService.getClientOutputStream();
		Scanner s = new Scanner(mail.getContents());
		logger.info("echoing mail to client...");
		while (s.hasNextLine()) {
			owner.echoLine(s.nextLine());
		}
		os.close();
		s.close();
	}
}
