package service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import model.StatusCodes;

import org.apache.log4j.Logger;

import service.command.impl.stats.StatsService;
import service.state.ServiceStateMachine;

public abstract class AbstractSockectService implements Runnable {

	private static final Logger logger = Logger.getLogger(AbstractSockectService.class);
	
	protected Socket socket;
	protected boolean endOfTransmission;
	protected ServiceStateMachine stateMachine;
	protected StatsService statsService = StatsService.getInstace();

	public AbstractSockectService() {
		stateMachine = new ServiceStateMachine(this);
		endOfTransmission = false;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			onConnectionEstabished();
			while (!endOfTransmission) {
				BufferedReader inFromClient = read();
				String clientSentence = inFromClient.readLine(); 
				System.out.println(getClass().getSimpleName() + " -- Command: " + clientSentence);
				if (clientSentence != null) {
					exec(clientSentence);
				} else {
					// Connection has been closed or pipe broken...
					endOfTransmission = true;
				}
			}
		} catch (Exception e) {
			logger.error("Closing Connection. Exception cought on AbstractSocketService: + " + e.getMessage());
		}
		try {
			onConnectionClosed();
		} catch (Exception e) {
			logger.error("Could not close connection " + e.getMessage());
		}
	}

	protected void onConnectionEstabished() throws Exception {
	}
	
	protected abstract void exec(String command) throws Exception;
	
	protected void onConnectionClosed() throws Exception {
		try {
			stateMachine.exit();		
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void echoLine(StatusCodes statusCode){
		if(statusCode.getCode() < 100){
			echoLine("+OK " + statusCode.getCode() + " [" + statusCode.getMessage() + "]");
		}else{
			echoLine("-ERR " + statusCode.getCode() + " [" + statusCode.getMessage() + "]");
		}
	}
	
	public void echoLine(StatusCodes statusCode, String data) {
		if(statusCode.getCode() < 100){
			echoLine("+OK " + statusCode.getCode() + " [" + statusCode.getMessage() + " (" + data + ") ]");
		}else{
			echoLine("-ERR " + statusCode.getCode() + " [" + statusCode.getMessage() + " (" + data + ") ]");
		}
	}
	
	public void echoLine(String s) {
		echo(s + "\r\n");
	}
	
	public void echo(String s) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(s);
		} catch (IOException e) {
			logger.error("Could not write to output stream!. Reason: " + e.getMessage());
		}
	}
	
	public BufferedReader read() {
		try {
			return new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			logger.error("Could not write to output stream!. Reason: " + e.getMessage());
			throw new IllegalStateException("Could not read from server!");
		}
	}
	
	public void setEndOfTransmission(boolean endOfTransmission) {
		this.endOfTransmission = endOfTransmission;
	}
	
	public ServiceStateMachine getStateMachine() {
		return stateMachine;
	}
	
	public Socket getSocket() {
		return socket;
	}


}
