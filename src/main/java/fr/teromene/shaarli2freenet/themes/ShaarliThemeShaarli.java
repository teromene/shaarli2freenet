package fr.teromene.shaarli2freenet.themes;

import java.util.Date;

public class ShaarliThemeShaarli extends ShaarliTheme{

	public String getThemeName() {
		return "ShaarliTheme";
	}

	public String getHeader(String pageName, String URI) {
		
		return  "<!doctype html>"
				+ "<html>"
				+ "		<head>"
				+ "			<title> Mirroir Shaarli de " + pageName +" </title>"
				+ "         <link rel='stylesheet' type='text/css' href='theme.css' />"
				+ "			<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "		</head>"
				+ "		<body>"			
				+ "<div id='pageheader'>"
				+"<div id='logo' title='Share your links !'></div>"
				+"<div id='menu'>"
				+"		<ul>"
				+"		<li><span id='shaarli_title'>"
				+"		<a href='?'>"+ pageName +"</a>"
				+"		</span>"
				+"		</li>"
				+"		<li><a href='?' >Home</a></li>"
				+"		<li><a href='feed.xml' class='nomobile'>RSS Feed</a></li>"
				+"		</ul>"
				+"		</div>"
				+"		<div class='clear'></div>"
				+"		</div>"
				+"		</div>"
				+"<div class='paging'></div>"
				+"<div id='linklist'>"
				+"<ul>";
	}

	public String getFooter() {
		return "</body>"
		+"	</html>";
	}

	public String getArticleCode(Date date, String title, String content, String link) {
		return   "<li class='publicLinkHightLight'>"
				+"  <div class='linkcontainer'>"
				+"		<span class='linktitle'><a href=\""+link+"\" >"+title+"</a></span><br />"
				+ "		<div class='linkdescription'>"+content + "</div>"
				+" 		<span class='linkdate'><a>"+date+"</a></span> - <a href='"+link+"'><span class='linkurl'>"+link+"</span></a>"				
				+"	</div>"
				+"</li>";
	}

	public String getPageNumber(int pageNumber, boolean hasPrevious, boolean hasNext) {
		
		String pageButton = "";
		if(hasPrevious) pageButton += "<a class='paging_older' href='index"+(pageNumber - 1)+".html'>◄Older</a>";
		pageButton += "<div class='paging_current'>Page "+pageNumber+"</div>";
		if(hasNext) pageButton += "<a class='paging_newer' href='index"+(pageNumber + 1)+".html'>Newer►</a>";

		
		return "<div class='paging'>"+pageButton+"</div>";
	}

	public String getActiveLinkPath() {
		return null;
	}

	public String getThemeCSS() {
		return "/themes/shaarli/shaarli.css";
	}
	
	

}
