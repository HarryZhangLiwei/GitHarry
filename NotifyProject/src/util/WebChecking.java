package util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/*Monitor helper, which used send requset to the web page and analysis the time*/
public class WebChecking {
	
	private String url = "https://turing.cs.hbg.psu.edu/comp512/";
	
	public void setUrl(String new_url){
		url = new_url;
	}
   

   
   @Test
   public Date  AnlysisHTMLByURL() throws IOException
   {
       int  timeout=3000;
      Document doc=  Jsoup.connect(url).get();
      URL u = new URL(url); 
      HttpURLConnection http = (HttpURLConnection) u.openConnection();
      http.setRequestMethod("HEAD");
      Date lastModify =new Date(http.getLastModified()); 
      System.out.println(u+"lastupdatetime："+lastModify);  
      return lastModify;
   }
	
}
