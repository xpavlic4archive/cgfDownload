import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class Starter {
	public static void main(String[] args) throws Exception {

		ExecutorService pool = Executors.newFixedThreadPool(10);
		//
		//440981 remove duplicate
		for (int i = 948000; i < 2000000; i++) {
			pool.execute(new MyRunnable(i));
		}
	}

	public static String normalize(int i) {
		String string = "0000000" + i;
		return string.substring(string.length() - 7, string.length());
	}

	@Test
	public void testNormalize() {
		String normalize = normalize(1);
		Assert.assertEquals("0000001", normalize);
	}

	static class  MyRunnable implements Runnable {

		private int number;

		public MyRunnable(int i) {
			number = i;
		}

		@Override
		public void run() {
			if (number%100 == 0)
				System.out.println(new Date().toString() + " "+ number );
			try {
				final WebClient webClient = new WebClient();
				webClient.setJavaScriptEnabled(false);
				webClient.setCssEnabled(false);
				webClient.setActiveXNative(false);
				webClient.setAppletEnabled(true);

				try {
					final HtmlPage page = webClient
							.getPage("http://www.cgf.cz/CheckHcp.aspx?MemberNumber="
									+ normalize(number));
					HtmlElement elementById = page
							.getElementById("ctl00_divHeader");
					String name = elementById.getHtmlElementsByTagName("b")
							.get(0).asText();
					System.out.println(name);
					HtmlElement a = page
							.getElementById("ctl00_MainPlaceHolder_lbClub");
					String club = a.getTextContent();
					System.out.println(club);
					HtmlElement b = page
							.getElementById("ctl00_MainPlaceHolder_lbMemberNumber");
					String number = b.getTextContent();
					System.out.println(number);
					HtmlElement c = page
							.getElementById("ctl00_MainPlaceHolder_lbHcp");
					String hcp = c.getTextContent();
					System.out.println(hcp);
					BasicDBObject o = new BasicDBObject();
					o.put("name", name);
					o.put("club", club);
					o.put("hcp", hcp);
					o.put("number", number);

					Mongo mongo = new Mongo();
					DB db = mongo.getDB("cgf");
					DBCollection collection = db.getCollection("names2");

					collection.insert(o);
					mongo.close();
				} catch (IndexOutOfBoundsException e) {
					webClient.closeAllWindows();
				}
				webClient.closeAllWindows();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
