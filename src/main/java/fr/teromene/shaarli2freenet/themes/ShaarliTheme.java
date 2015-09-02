package fr.teromene.shaarli2freenet.themes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public abstract class ShaarliTheme {

	abstract public String getActiveLinkPath();
	
	abstract public String getThemeCSS();
	
	abstract public String getThemeName();
	
	abstract public String getHeader(String pageName, String URI);
	
	abstract public String getFooter();
	
	String getArticle(Date date, String title, String content, String link) {
		
		return "<!-- this is an article element -->" + getArticleCode(date, title, content, link);
	}
	
	abstract public String getArticleCode(Date date, String title, String content, String link);
	
	abstract public String getPageNumber(int pageNumber, boolean hasPrevious, boolean hasNext);
	
	public void createCSS(String path) {
		
		String cssPath = getThemeCSS();
		
		if(getThemeCSS() == null) {
			return;			
		}
		URL inputUrl = getClass().getResource(cssPath);
		File dest = new File(path+"/theme.css");
		
		try {
			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

		
	
	public void createAL(String path) {
		
		String activeLinkPath = getActiveLinkPath();
		
		if(getActiveLinkPath() == null) {
			
			activeLinkPath = "/themes/freenetActivelink.png";
			
		}
		
		URL inputUrl = getClass().getResource(activeLinkPath);
		File dest = new File(path+"/activelink.png");
		
		try {
			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
