package service.command.impl.stats;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import model.util.CollectionUtil;

public class StatsService {

	private static StatsService statsService = new StatsService(); 

	public static StatsService getInstace() {
		return statsService;
	}

	private AtomicInteger transferedBytes;
	private AtomicInteger numberOfAccesses;
	private AtomicInteger numberOfReadMail;
	private AtomicInteger numberOfDeletedMail;
	private ConcurrentMap<String, UserHistogram> statsByUserMap;

	public StatsService() {
		reset();
	}

	private void reset() {
		transferedBytes = new AtomicInteger();
		numberOfAccesses = new AtomicInteger();
		numberOfReadMail = new AtomicInteger();
		numberOfDeletedMail = new AtomicInteger();
		statsByUserMap = new ConcurrentHashMap<String, UserHistogram>();
	}

	public void incrementNumberOfAccesses(String userMail) {
		numberOfAccesses.incrementAndGet();
		createUserInStatsMap(userMail);
		incrementUserAccesses(userMail);
	}

	private void createUserInStatsMap(String userMail) {
		if (!statsByUserMap.containsKey(userMail)) {
			statsByUserMap.put(userMail, new UserHistogram(userMail));
		}		
	}

	public void incrementTransferedBytes(long bytes, String userMail) {
		transferedBytes.addAndGet((int) bytes);
		incrementUserTransferedBytes(userMail, bytes);
	}

	public int getTransferedBytes() {
		return transferedBytes.get();
	}

	public UserHistogram getOrCreateStatsByUser(String user) {
		createUserInStatsMap(user);
		return statsByUserMap.get(user);
	}
	
	public UserHistogram getStatsByUser(String user) {
		return statsByUserMap.get(user);
	}

	private void incrementUserTransferedBytes(String user, long bytes) {
		UserHistogram uh = statsByUserMap.get(user);
		if (uh != null) {
			uh.incrementTransferedBytes(bytes);
		}
	}

	private void incrementUserReadMail(String user) {
		UserHistogram uh = statsByUserMap.get(user);
		if (uh != null) {
			uh.incrementNumberOfReadMail();
		}
	}

	public void incrementNumberOfReadMail(String userMail) {
		numberOfReadMail.incrementAndGet();
		incrementUserReadMail(userMail);
	}

	public int getNumberOfAccesses() {
		return numberOfAccesses.get();
	}
	

	private void incrementUserAccesses(String user) {
		UserHistogram uh = statsByUserMap.get(user);
		if (uh != null) {
			uh.incrementNumberOfAccesses();
		}
	}

	private void incrementUserDeletedMail(String user) {
		UserHistogram uh = statsByUserMap.get(user);
		if (uh != null) {
			uh.incrementNumberOfDeletedMail();
		}
	}

	public int getNumberOfDeletedMail() {
		return numberOfDeletedMail.get();
	}
	
	public void incrementNumberOfDeletedMail(String userMail) {
		numberOfDeletedMail.incrementAndGet();
		incrementUserDeletedMail(userMail);
	}

	public int getNumberOfReadMail() {
		return numberOfReadMail.get();
	}

	public String getPrettyFormat() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total accesses: " + getNumberOfAccesses() + "\r\n");
		sb.append("Total tranfered bytes: " + getTransferedBytes() + "\r\n");
		sb.append("Total read mails: " + getNumberOfReadMail() + "\r\n");
		sb.append("Total deleted mails: " + getNumberOfDeletedMail() + "\r\n");
		return sb.toString();
	}

	public String getAllUserStats() {
		Collection<UserHistogram> userHistograms = statsByUserMap.values();
		Collection<String> prettyFormats = new LinkedList<String>();
		for (UserHistogram uh : userHistograms) {
			prettyFormats.add(uh.getPrettyFormat());
		}
		return CollectionUtil.join(prettyFormats, "\r\n");
	}

}
