package fr.teromene.shaarli2freenet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

public class ShaarliRawRss {

	private SyndFeed feed;
	private String path;
	
	public ShaarliRawRss(String filename, ShaarliProject shaarliProject) {
		
		path = filename;
		
		File configurationFile = new File(filename);
		if (configurationFile.exists()) {
			
			try {
				
				feed = new SyndFeedInput().build(new XmlReader(new URL("file://"+shaarliProject.getLocalPath()+"feed.xml")));
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		} else {
			
			feed = new SyndFeedImpl();
	        feed.setFeedType("rss_1.0");
	        feed.setTitle("MyProject Build Results");
	        feed.setLink("http://myproject.mycompany.com/continuum");
	        feed.setDescription("Continuous build results for the MyProject project");    
			
		}
	}

	public SyndFeed getRssFeed() {
	
		return this.feed;
		
	}

	
	public void addArticle(SyndEntry rssElement) {
		
		List<SyndEntry> entries = feed.getEntries();
		entries.add(rssElement);
		feed.setEntries(entries);
	}
	
	public void save() {
		
		Writer writer = null;
		try {
			writer = new FileWriter(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
        SyndFeedOutput output = new SyndFeedOutput();
        try {
			output.output(feed,writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
}
