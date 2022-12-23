import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleQuery 
{
	public String searchKeyword;
	public String url;
	public String content;
	
	public GoogleQuery(String searchKeyword)
	{
		ArrayList<String> addedKeyword = new ArrayList<String>(Arrays.asList("江浙菜", "台北", "官網", "園", "樓", "館"));
		int index=addedKeyword.indexOf(searchKeyword);
		if(index!=-1) addedKeyword.remove(index);
		else addedKeyword.add(0, searchKeyword);
		this.searchKeyword = String.join("+", addedKeyword);
		try {
			this.searchKeyword = URLEncoder.encode(this.searchKeyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.url = "http://www.google.com/search?q="+this.searchKeyword+"&oe=utf8&num=20"
	}
	
	private String fetchContent() throws IOException
	{
		String retVal = "";

		URL u = new URL(url);
		URLConnection conn = u.openConnection();
		//set HTTP header
		conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
		InputStream in = conn.getInputStream();

		InputStreamReader inReader = new InputStreamReader(in, "utf-8");
		BufferedReader bufReader = new BufferedReader(inReader);
		String line = null;

		while((line = bufReader.readLine()) != null)
		{
			retVal += line;
		}
		return retVal;
	}
	
	public HashMap<String, String> query() throws IOException
	{
		if(content == null)
		{
			content = fetchContent();
		}

		HashMap<String, String> retVal = new HashMap<String, String>();
//		ArrayList<WebPage> retVal= new ArrayList<WebPage>();
//		ArrayList<WebTree> retTree= new ArrayList<WebTree>();
		
		//using Jsoup analyze html string
		Document doc = Jsoup.parse(content);
		
		//select particular element(tag) which you want 
		Elements lis = doc.select("div");
		lis = lis.select(".kCrYT");
		
		for(Element li : lis)
		{
			try 
			{
				String citeUrl = li.select("a").get(0).attr("href");
				String title = li.select("a").get(0).select(".vvjwJb").text();
				
				if(title.equals("")) 
				{
					continue;
				}
				
//				System.out.println("Title: " + title + " , url: " + citeUrl);
				
				//put title and pair into HashMap
				retVal.put(title, citeUrl);
				//create WebPage
//				retTree.add(new WebTree(new WebPage(citeUrl, title)));
//				retVal.add(new WebPage(citeUrl, title));

			} catch (IndexOutOfBoundsException e) 
			{
//				e.printStackTrace();
			}
		}		
		return retVal;
	}
}
