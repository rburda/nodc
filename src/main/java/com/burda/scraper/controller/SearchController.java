package com.burda.scraper.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.burda.scraper.inventory.InventoryService;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
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
	
	@RequestMapping(value = "/startSearch", method={RequestMethod.GET, RequestMethod.POST}, produces="application/javascript")
	@ResponseBody
	public void redirectToSearch( 
			@RequestParam Map<String, String> params, 
			HttpServletRequest request, HttpServletResponse clientResponse) throws Exception
	{
		SearchParams sp = new SearchParams(params);
		sp.setSessionId(findSessionId(request));
		logger.debug("params == " + sp);
		invService.getSearchResult(request, sp);
		
		StringBuffer hostWithPath = HttpUtils.getRequestURL(request);
		String host = hostWithPath.substring(0, hostWithPath.indexOf("/startSearch"));
		String callback = params.get("jsoncallback") 
				+ "({\"redirectURL\": \""+host+"\\/results" +  "\"})";
		clientResponse.setContentType("application/json");
		clientResponse.getOutputStream().write(callback.getBytes());
	}
	
	/**
	 * Calls inventoryService and executes a search based on search params
	 */
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getResults(
			@RequestParam Map<String, String> params,
			@RequestParam(value="sort", defaultValue="DEFAULT") SortType sortType,
			@RequestParam(value="page", defaultValue="1") int page,
			@ModelAttribute("searchResults") ModelMap model,
			HttpServletRequest clientRequest,
			HttpServletResponse clientResponse) throws Exception
	{		
		model.put("result", invService.getUpdatedResults(findSessionId(clientRequest), sortType, page));
		
		return new ModelAndView("searchResult", model);
	}	
	
	@RequestMapping(value="/health", method=RequestMethod.GET)
	public @ResponseBody String healthCheck()
	{
		return "ok";
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
	
	private static final String findSessionId(HttpServletRequest request)
	{
		String id = "";
		for (Cookie c: request.getCookies())
		{
			logger.debug(String.format("cookie: %1$s, value: %2$s", c.getName(), c.getValue()));
			if (c.getName().equals("parent_cookie"))
			{
				int idx = c.getValue().indexOf("___");
				if (idx >=0)
					id = c.getValue().substring(0, idx);
				else
					id = c.getValue();
				break;
			}
		}		
		return id;
	}	
}