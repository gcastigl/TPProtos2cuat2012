package service.command.impl.mail;

import java.io.IOException;

import model.User;
import model.configuration.Config;
import model.configuration.KeyValueConfiguration;
import model.validator.LoginValidationException;
import service.AbstractSockectService;
import service.MailSocketService;
import service.command.ServiceCommand;

public class UserCommand extends ServiceCommand {

	public UserCommand(AbstractSockectService owner) {
		super(owner);
	}

	@Override
	public void execute(String[] params) throws Exception {
		MailSocketService mailServer = (MailSocketService) owner;
		User user = new User(params[0], null);
		try {
			mailServer.getUserLoginvalidator().userCanLogin(user);
		} catch (LoginValidationException e) {
			mailServer.echoLine("-ERR " + e.getMessage());
			owner.setEndOfTransmission(true);
			return;
		}
		mailServer.setOriginServer(getMailServer(user));
		String resp = echoToOriginServerAndReadLine(getOriginalLine());
		owner.echoLine(resp);
		if (!"+OK".equals(resp)) {
			return;
		}
		String passwordCmd = owner.read().readLine();
		resp = echoToOriginServerAndReadLine(passwordCmd);
		owner.echoLine(resp);
		if (!resp.toUpperCase().startsWith("+OK")) {
			return;
		}
		user.setPassword(passwordCmd.split(" ")[1]);
		mailServer.userLoggedIn(user);
	}

	private String echoToOriginServerAndReadLine(String line) throws IOException {
		MailSocketService service = (MailSocketService) owner;
		service.echoLineToOriginServer(line);
		return service.readFromOriginServer().readLine();
	}
	
	private String getMailServer(User user) {
		KeyValueConfiguration originServerConfig = Config.getInstance().getKeyValueConfig("origin_server");
		String server = originServerConfig.get(user.getMail());
		return server == null ? originServerConfig.get("default") : server;
	}

}
