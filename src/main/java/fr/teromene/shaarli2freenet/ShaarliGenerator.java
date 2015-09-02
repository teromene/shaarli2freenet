package fr.teromene.shaarli2freenet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import fr.teromene.shaarli2freenet.themes.ShaarliTheme;
import fr.teromene.shaarli2freenet.themes.ShaarliThemeManager;
import net.pterodactylus.util.io.Closer;

public class ShaarliGenerator {

	/** The project that will be converted to html pages **/
	private ShaarliProject shaarliProject;
	
	/** The feed containing new entries **/
	private SyndFeed shaarliFeed;
	
	
	/** The raw feed written to disk **/
	private ShaarliRawRss rawShaarliFeed;
	
	/** Writer for the console. */
	private PrintWriter outputWriter = new PrintWriter(System.out, true);
		
	/** The number of elements generated per page **/
	private final int ELEMENTS_BY_PAGE = 5;
	
	
	public ShaarliGenerator(ShaarliProject shaarliProject) {
		
		rawShaarliFeed = new ShaarliRawRss(shaarliProject.getLocalPath()+"feed.xml", shaarliProject);
		
		this.shaarliProject = shaarliProject;
		File shaarliProjectPath = new File(shaarliProject.getLocalPath());
		
		if(!shaarliProjectPath.isDirectory()) {
			
			shaarliProjectPath.mkdir();
			
		}
		
	}
	
	/**
	 * Returns the project, modified by the page generation (index page, date...).
	 *
	 * @return The project.
	 */

	public ShaarliProject getModifiedProject() {
		
		return this.shaarliProject;
		
	}
	
	public void fetchRSSFeed() {

		URL feedSource = null; 
        SyndFeedInput input = new SyndFeedInput();
        
        try {
        	if(shaarliProject.getFeedURL().startsWith("http://") || shaarliProject.getFeedURL().startsWith("https://")) {
        		feedSource = new URL(shaarliProject.getFeedURL());
        	} else {
        		
        		feedSource = new URL("http://"+shaarliProject.getFeedURL());
        		outputWriter.println("The project url didn't start with http:// or https://, added http:// automatically");
        
        		
        	}
        	input = new SyndFeedInput();
			shaarliFeed = input.build(new XmlReader(feedSource));
			
			
			
        } catch (Exception e) {
        
        	outputWriter.println("Unable to fetch the RSS feed, please check the URL you supplied. The error is "+e.getMessage());
        	System.exit(0);
        }
        
	}
	
	/**
	 * Writes to disk the pages from the RSS feed
	 * @throws IOException 
	*/
	public void createPages(boolean forceRegeneration) throws IOException {
		
		fetchRSSFeed();
		
		ShaarliTheme theme = ShaarliThemeManager.getTheme(shaarliProject.getTheme());
		theme.createAL(shaarliProject.getLocalPath());
		theme.createCSS(shaarliProject.getLocalPath());
		
		/* Getting the last page number */
		int lastPageNumber = 0;
		if(shaarliProject.getIndexFile() == "") {
			lastPageNumber = 0;
		} else {
			lastPageNumber = Integer.parseInt(shaarliProject.getIndexFile().split("index")[1].split(".html")[0]);
		}
		
		/* Getting the number of elements in the last page */
		File lastOpenedFile = new File(shaarliProject.getLocalPath()+"index"+lastPageNumber+".html");
		
		int lastPageElements = 0;
		
		if(lastOpenedFile.exists()) {
			try {
				
				lastPageElements = StringUtils.countMatches(FileUtils.readFileToString(lastOpenedFile), "<!-- this is an article element -->");
			
			} catch(Exception e) {
				
				outputWriter.println("Unable to count the number of elements in the last page, error is : "+e.getMessage()+"\nIs this the first time you're running s2f ?");
				
			}
		}
		
		
		/* Starting writing elements */
		List<SyndEntry> feedList = shaarliFeed.getEntries();
		Collections.reverse(feedList);
		
		File pageFile = lastOpenedFile;
		
		String contentToWrite = "";
		
		if(pageFile.exists()) {
			contentToWrite = FileUtils.readFileToString(pageFile);
		}
		
		BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pageFile), "UTF-8"));
		
		if(forceRegeneration) {
			System.out.println("Regenerating the whole project");
			for(int i = 0; i <= lastPageNumber; i++) {
				Files.delete(Paths.get(shaarliProject.getLocalPath()+"index"+i+".html"));
				System.out.println("Deleted page "+i);
			}
			shaarliProject.setLastInsertionTime(0);
			shaarliProject.setIndexFile("index0.html");
			lastPageNumber = 0;
			lastPageElements = 0;
			try {
				shaarliFeed = (SyndFeed) rawShaarliFeed.getRssFeed().clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			fileWriter.close();
			pageFile = new File(shaarliProject.getLocalPath()+"index"+lastPageNumber+".html");			
			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pageFile), "UTF-8"));
			contentToWrite = "";
			
		}
		
		// Scrapping the content of the last inserted page
		contentToWrite = contentToWrite.replaceAll(theme.getFooter(), "");
		contentToWrite = contentToWrite.replaceAll(theme.getPageNumber(lastPageNumber, true, false), "");
		
		for(Object objectRssElement : shaarliFeed.getEntries()) {
			
			
			SyndEntry rssElement = (SyndEntry) objectRssElement;
			if(rssElement.getPublishedDate().after(new Date(shaarliProject.getLastInsertionTime()))) {
				
				rawShaarliFeed.addArticle(rssElement);
				
				
				shaarliProject.setLastInsertionTime(rssElement.getPublishedDate().getTime());
				
				if(lastPageElements == ELEMENTS_BY_PAGE) {
					
					if(!contentToWrite.contains("<head>")) contentToWrite = theme.getHeader(shaarliProject.getName(), shaarliProject.getRequestURI()) + contentToWrite;
					
					
					contentToWrite += theme.getPageNumber(lastPageNumber, lastPageNumber != 0 ? true : false, true);
					
					
					
					fileWriter.write(contentToWrite);
					fileWriter.close();
					
					Closer.close(pageFile);
					
					contentToWrite = "";
					lastPageNumber += 1;
					lastPageElements = 0;
					
					pageFile = new File(shaarliProject.getLocalPath()+"index"+lastPageNumber+".html");			
					fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pageFile), "UTF-8"));
					

					
					
				}
				contentToWrite = theme.getArticleCode(rssElement.getPublishedDate(), rssElement.getTitle(), rssElement.getDescription().getValue().toString(), rssElement.getLink()) + contentToWrite;
				
				lastPageElements += 1;
				
			}
			
		}
		if(contentToWrite != null) {
			if(!contentToWrite.contains("<head>")) contentToWrite = theme.getHeader(shaarliProject.getName(), shaarliProject.getRequestURI()) + contentToWrite;
			if(!contentToWrite.contains("</body>"))  {
				contentToWrite = contentToWrite + theme.getPageNumber(lastPageNumber, true, false);
				contentToWrite = contentToWrite + theme.getFooter();
			}
			fileWriter.write(contentToWrite);
			
		}
		
		fileWriter.close();
		shaarliProject.setIndexFile("index"+lastPageNumber+".html");
		rawShaarliFeed.save();
	}

	
}

