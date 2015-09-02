package fr.teromene.shaarli2freenet.themes;

import java.util.Date;

public class ShaarliThemeBasic extends ShaarliTheme{

	public String getThemeName() {
		return "BasicTheme";
	}

	public String getHeader(String pageName, String URI) {
		return  "<!doctype html>"
				+ "<html>"
				+ "		<head>"
				+ "			<title> Mirroir Shaarli de " + pageName +" </title>"
				+ "			<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "		</head>"
				+ "		<body><a href='/?newbookmark=USK@"+URI+"/"+pageName+"/-1/&desc="+pageName+"&hasAnActivelink=true'><img src='activelink.png'></img></a>";
	}

	public String getFooter() {
		return "</body>"
		+"	</html>";
	}

	public String getArticleCode(Date date, String title, String content, String link) {
		return "<div>"
				+ "<h4><a href=\""+link+"\"+>"+title+"</a></h4>"
				+" <span>"+date+"</span>"
						+ "<div>"+content+"</div>"
				+"</div>";
	}

	public String getBackLink(int pageNumber) {
		return "<a href='index"+pageNumber+".html'>&lt;- Page "+pageNumber+" </a>";
	}

	public String getForwardLink(int pageNumber) {
		return "<a href='index"+pageNumber+".html'> Page "+pageNumber+" -&gt; </a>";
	}

	public String getPageNumber(int pageNumber, boolean hasPrevious, boolean hasNext) {
		return "| "+pageNumber+" |";
	}

	public String getActiveLinkPath() {
		return null;
	}

	public String getThemeCSS() {
		return null;
	}
	
	

}
