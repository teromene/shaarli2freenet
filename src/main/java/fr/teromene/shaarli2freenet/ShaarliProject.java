package fr.teromene.shaarli2freenet;

import java.util.Scanner;

import de.todesbaum.jsite.application.Freenet7Interface;
import de.todesbaum.jsite.application.Project;

public class ShaarliProject extends Project {
	
	/** The URL of the RSS stream  **/
	private String feedURL;

	/** The name of the Theme to use  **/
	private String theme;

	
	public ShaarliProject() {
		
	}
	
	public ShaarliProject(String name, String feedURL, String theme, Freenet7Interface freenetInterface) {
		
		this.name = name;
		this.feedURL = feedURL;
		this.theme = theme;
		this.path = name;
		this.indexFile = "index0.html";
		
		this.localPath = System.getProperty("user.home") + "/.s2f/"+this.name+"/";

		String[] keys = null;
		
		try {
			
			keys = freenetInterface.generateKeyPair();
			
		} catch(Exception e) {
			
			System.out.println("Unable to create project : " + e.getMessage());
			System.exit(0);
			
		}

		this.insertURI = keys[0];
		this.requestURI = keys[1];
	}

	

	/**
	 * Returns the URL of the project's feed.
	 *
	 * @return The URL of the project's feed
	 */
	public String getFeedURL() {
		return feedURL;
	}

	/**
	 * Sets the URL of the project's feed.
	 *
	 * @param feedURL
	 *            The URL of the project's feed
	 */
	public void setFeedURL(String feedURL) {
		this.feedURL = feedURL;
	}
	
	
	
	public static ShaarliProject createInteractiveShaarliProject(Freenet7Interface freenetInterface) {
		
		Scanner interactiveMode = new Scanner(System.in);
    	String title = null;
    	String url = null;
    	    	
    	System.out.println("Creating project in interactive mode.");        	
    	System.out.println("Enter the name of your project :");
    	
    	do {
    		
    		title = interactiveMode.nextLine();
    		
    	} while(title == "");
    	
    	System.out.println("Enter the URL of your rss feed :");
    	
    	do {
    		
    		url = interactiveMode.nextLine();
    		
    	} while(url == "");

    	interactiveMode.close();
    	
    	ShaarliProject project = new ShaarliProject();
    	
    	project.setName(title);
    	project.setPath(title);
    	project.setFeedURL(url);
		project.setIndexFile("index0.html");
		
		project.setLocalPath(System.getProperty("user.home") + "/.s2f/"+project.getName()+"/");

		String[] keys = null;
		
		try {
			
			keys = freenetInterface.generateKeyPair();
			
		} catch(Exception e) {
			
			System.out.println("Unable to create project : " + e.getMessage());
			System.exit(0);
			
		}

		project.setInsertURI(keys[0]);
		project.setRequestURI(keys[1]);
    	
		return project;
		
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
	
}
