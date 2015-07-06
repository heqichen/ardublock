package com.ardublock.core;

import java.io.Serializable;

public class Example implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6464848306994358956L;
	
	private String name;
	private String filename;
	private String tutorialLink;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getTutorialLink() {
		return tutorialLink;
	}
	public void setTutorialLink(String tutorialLink) {
		this.tutorialLink = tutorialLink;
	}
	
	
}
