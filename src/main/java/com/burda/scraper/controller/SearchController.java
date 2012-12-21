package com.burda.scraper.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.burda.scraper.inventory.InventoryService;
import com.burda.scraper.model.Header;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.google.common.collect.Lists;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SearchController
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	@Qualifier("inventoryService")
	InventoryService invService;
	
	@RequestMapping(value = "/startSearch", method=RequestMethod.GET, produces="application/javascript")
	@ResponseBody
	public void redirectToSearch( 
			@RequestParam Map<String, String> params, HttpServletResponse clientResponse) throws Exception
	{
		String callback = params.get("jsoncallback") 
				+ "({\"redirectURL\": \"http:\\/\\/localhost:8080\\/search?" 
				+ createRequestString(params) 
				+  "\"})";
		clientResponse.setContentType("application/json");
		clientResponse.getOutputStream().write(callback.getBytes());
	}
	
	/**
	 * Calls inventoryService and executes a search based on search params
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET,  produces = "application/json")
	@ResponseBody
	public SearchResult search(
			@RequestParam Map<String, String> params,
			HttpServletResponse clientResponse) throws Exception
	{
		SearchParams sp = new SearchParams(params);
		logger.debug("params == " + sp);
		SearchResult result = invService.getSearchResult(sp);
		
		for (Header header: result.headers)
			clientResponse.addHeader(header.name,  header.value);
		
		return result;
	}	

	private String createRequestString(Map<String, String> reqParams)
	{
		StringBuffer buf = new StringBuffer();
		List<Entry<String, String>> paramPairs = Lists.newArrayList(reqParams.entrySet()); 
		for (int i=0; i < paramPairs.size(); i++)
		{
			Entry<String, String> paramPair = paramPairs.get(i);
			buf.append(String.format("%1$s=%2$s", paramPair.getKey(), paramPair.getValue()));
			if (i < paramPairs.size() - 1)
				buf.append("&");
		}
		return buf.toString();
	}
}