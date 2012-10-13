package service;

import service.state.impl.monitor.AuthorityState;

public class MonitorSocketServer extends AbstractSockectService {

	public MonitorSocketServer() {
		stateMachine.setState(new AuthorityState(this));
	}
	
	@Override
	protected void onConnectionEstabished() throws Exception {
		echoLine(0, "Monitor ready");
	}
	
	@Override
	protected void exec(String command) throws Exception {
		stateMachine.exec(command.split(" "));
	}
	
	@Override
	protected void onConnectionClosed() throws Exception {
		super.onConnectionClosed();
	}

}
