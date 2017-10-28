package scraper;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RunnableScraper implements Runnable {

	HtmlPage page;
	public RunnableScraper(HtmlPage page) {
		this.page = page;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
