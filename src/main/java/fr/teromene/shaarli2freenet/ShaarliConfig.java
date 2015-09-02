package fr.teromene.shaarli2freenet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.pterodactylus.util.io.Closer;

public class ShaarliConfig {

	private String path = "";
	private String URL = "";
	private Date lastDate = null;
	private int lastPage = 0;
	
	private static DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);				

	
	public ShaarliConfig(String path) throws IOException, ParseException {
	
		this.path = path;
		
		File configFolder = new File(path);
		if(!configFolder.exists()) configFolder.mkdir();
		Closer.close(configFolder);
		
		File configFile = new File(path+".config");
		
		if (configFile.exists()) {
			
			BufferedReader reader = null;
			FileReader fileReader = null;
			
					
			fileReader = new FileReader(configFile);
			reader = new BufferedReader(fileReader);
				
			URL = reader.readLine();
			lastDate = df.parse(reader.readLine());
			lastPage = Integer.parseInt(reader.readLine());
				
			Closer.close(fileReader);
			Closer.close(reader);
			
		} else {
			
			configSaver(URL);
			
		}

		
	}
	
	public boolean shaarliProjectExists() {
		
		File confFile = new File(path + ".config");
		boolean fileExists = confFile.exists();
		Closer.close(confFile);
		
		return fileExists;
		
		
		
		
	}
	
	public void configSaver(String url) throws IOException, ParseException {
		
		configSaver(url, df.parse("Mon Jan 01 00:00:01 BST 1970"), 0);
		
		
	}
	public void configSaver(String URL, Date date, int page) throws IOException, ParseException {
		
		this.URL = URL;
		this.lastDate = date;
		this.lastPage = page;
		
		File descDir = new File(path);
		if(!descDir.exists()) descDir.mkdir();
		Closer.close(descDir);
		
		File descFile = new File(path+".config");

	
		FileWriter fw = null;
		BufferedWriter bw = null;
				
		descFile.createNewFile();
		fw = new FileWriter(descFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		
		bw.write(URL+"\n");
		bw.write(date.toString()+"\n");
		bw.write(page+"");
		
		
		bw.close();
		Closer.close(fw);
		Closer.close(bw);
		
	}
	
	public String getURL() {
		
		return this.URL;
		
	}
	
	public Date getDate() {
		
		return this.lastDate;
		
	}
	
	public int getLastPage() {
		
		return this.lastPage;
		
	}
	
	public void setURL(String URL) {
		
		this.URL = URL;
		
	}
}
