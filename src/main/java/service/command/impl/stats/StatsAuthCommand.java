package service.command.impl.stats;

import model.util.Config;
import service.AbstractSockectService;
import service.command.impl.AuthCommand;
import service.state.impl.stats.ReadState;

public class StatsAuthCommand extends AuthCommand {
	
	public StatsAuthCommand(AbstractSockectService owner) {
		super(owner);
		getBundle().put("password", Config.getInstance().getConfig("stats_conf").get("password"));
	}

	@Override
	public void onLogin() {
		owner.getStateMachine().setState(new ReadState(owner));
	}
}
