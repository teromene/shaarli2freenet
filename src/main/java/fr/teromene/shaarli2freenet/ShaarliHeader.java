package fr.teromene.shaarli2freenet;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

public class ShaarliHeader {

	public ShaarliHeader() {
	}
	
	
	public String getHeader(String title, String URI) {
		
		return  "<!doctype html>"
		+ "<html>"
		+ "		<head>"
		+ "			<title> Mirroir Shaarli de " + title +" </title>"
		+ "			<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
		+ "		</head>"
		+ "		<body><a href='/?newbookmark=USK@"+URI+"/"+title+"/-1/&desc="+title+"&hasAnActivelink=true'><img src='activelink.png'></img></a>";
		
	}
	
	public String getFooter() {
		
		return "</body>"
		+"	</html>";
		
		
	}
	
	public String getHTMLForContent(Date date, String title, String content, String link) {
		
		return "<!-- this is an article element -->"
				+ "<div>"
				+ "<h4><a href=\""+link+"\"+>"+title+"</a></h4>"
				+" <span>"+date+"</span>"
						+ "<div>"+content+"</div>"
				+"</div>";
		
		
	}

	public String getBackLink(int i) {
		return "<a href='index"+i+".html'>&lt;- Page "+i+" </a>";				
	}
	
	public String getForwardLink(int i) {
		return "<a href='index"+i+".html'> Page "+i+" -&gt; </a>";		
	}
	
	public String getCurrentPageNumber(int i) {
		return "| "+i+" |";
	}
	
	public String createAL(String path) {
		setFile(getClass().getResourceAsStream("/freenetActivelink.png"), path+"/activelink.png");
		return "";
	}
	public void setFile(InputStream io, String fileName) {
			    FileOutputStream fos;
				try {
					fos = new FileOutputStream(fileName);
				
					byte[] buf = new byte[256];
					int read = 0;
					while ((read = io.read(buf)) > 0) {
						fos.write(buf, 0, read);
					}
					fos.close();
				} catch (Exception e) {
				}
	}


}
