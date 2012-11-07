package model.mail;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import model.parser.mime.MimeHeader;

import org.joda.time.LocalDate;

public class Mail {

	private MimeHeaderCollection headers;
	private Set<String> attachmentsExtension;
	private File contents;

	public Mail(File contents) {
		headers = new MimeHeaderCollection();
		attachmentsExtension = new HashSet<String>();
		setContents(contents);
	}

	public long getSizeInBytes() {
		return contents == null ? 0 : contents.length();
	}
	
	
	public void setContents(File contents) {
		this.contents = contents;
	}
	
	public void addAttachmentsExtension(String extension) {
		attachmentsExtension.add(extension);
	}

	public Set<String> getAttachmentsExtension() {
		return attachmentsExtension;
	}

	public boolean hasAttachments() {
		return attachmentsExtension.size() > 0;
	}

	public boolean hasAttachmentWithExtension(String extension) {
		return attachmentsExtension.contains(extension);
	}

	public void addHeader(MimeHeader header) {
		headers.add(header);
	}

	public MimeHeader getHeader(String name) {
		return headers.get(name);
	}

	public String getBoundaryKey() {
		MimeHeader header = headers.get("Content-Type");
		if (header == null) {
			return null;
		}
		return header.getExtraValue("boundary");
	}

	public LocalDate getDate() {
		String headerDate = headers.get("Delivery-date").getValue();
		String[] headerDatea = headerDate.split(" "); 
		Date date;
		try {
			date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(headerDatea[2]);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse month:"+ headerDatea[2]);
		}
		Calendar.getInstance().setTime(date);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		LocalDate maxDate = new LocalDate(
				Integer.valueOf(headerDatea[3]),month+1, Integer.valueOf(headerDatea[1]));
		return maxDate;
	}

	public String getSender() {
		return headers.get("Return-path").getValue();
	}

	public boolean containsHeader(String key, String value) {
		MimeHeader header = headers.get(key);
		if (header == null) {
			return false;
		}
		return header.getValue().equals(value);
	}

	public File getContents() {
		return contents;
	}

	public boolean isMultiPart() {
	    return getBoundaryKey() != null;
	}

	public MimeHeaderCollection getHeaders() {
		return headers;
	}

	public void delete() {
		contents.delete();
	}
}
