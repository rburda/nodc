package com.burda.scraper;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.burda.scraper.inventory.InventoryServiceImpl;
import com.burda.scraper.model.Header;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SearchController
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@RequestMapping(value = "/startSearch", method=RequestMethod.GET, produces="application/javascript")
	@ResponseBody
	public void redirectToSearch(
			@RequestParam("jsoncallback") String jsonCallback, HttpServletResponse clientResponse) throws Exception
	{
		String callback = jsonCallback + "({\"redirectURL\": \"http:\\/\\/localhost:8080\\/search\"})";
		clientResponse.setContentType("application/json");
		clientResponse.getOutputStream().write(callback.getBytes());
	}
	
	/**
	 * Calls inventoryService and executes a search based on search params
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET,  produces = "application/json")
	@ResponseBody
	public SearchResult search(
			@ModelAttribute("searchParams") SearchParams params, 
			HttpServletResponse clientResponse) throws Exception
	{
		SearchResult result = new InventoryServiceImpl().getSearchResult();
		
		for (Header header: result.headers)
			clientResponse.addHeader(header.name,  header.value);
		
		return result;
	}	
}