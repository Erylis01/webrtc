package cz.cvut.fel.webrtc.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Line {
	private String name;
	private String username;
	private String secret;
	private String callerid;
	private String extension;

	/**
	 * 
	 * @return the name of the user
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param username
	 *            change the name of the user
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param secret
	 *            change the password
	 */
	public void setPassword(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the id of the one who invit you in the room
	 */
	public String getCallerid() {
		return this.callerid;
	}

	/**
	 * @param callerid
	 *            the name of the user who call
	 */
	public void setCallerid(String callerid) {
		this.callerid = callerid;
	}

	/**
	 * @return the name of the message
	 */
	public String getName() {
		if (name == null)
			setProperties();
		return name;
	}

	/**
	 * @return put a message in the line
	 */
	public String getExtension() {
		if (extension == null)
			setProperties();
		return extension;
	}

	/**
	 * find the name of the caller with his id and add it to the line and set
	 * the message in extension
	 */
	private void setProperties() {
		if (extension == null || name == null) {
			Pattern p = Pattern.compile("\"(.*)\" <([0-9]+)>");
			Matcher m = p.matcher(callerid);

			try {
				if (m.matches()) {
					name = m.group(1);
					extension = m.group(m.groupCount());
				}
			} catch (Exception e) {
			}
		}
	}
}