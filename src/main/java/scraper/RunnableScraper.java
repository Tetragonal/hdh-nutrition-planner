package scraper;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RunnableScraper implements Runnable {

	HtmlPage page;
	int day;
	public RunnableScraper(HtmlPage page, int day) {
		this.page = page;
		this.day = day;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
