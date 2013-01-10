package com.burda.scraper.controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
import com.burda.scraper.model.SortType;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SearchController
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	private static final AtomicLong SESSION_SALT = new AtomicLong();
	
	@Autowired
	@Qualifier("inventoryService")
	InventoryService invService;
	
	/**
	 * Calls inventoryService and executes a search based on search params. 
	 * Returns jsonp response with redirectUrl that can be used to view results
	 */
	@RequestMapping(value = "/startSearch", method={RequestMethod.GET, RequestMethod.POST}, produces="application/javascript")
	@ResponseBody
	public void redirectToSearch( 
			@RequestParam Map<String, String> params, 
			HttpServletRequest request, HttpServletResponse clientResponse) throws Exception
	{
		SearchParams sp = new SearchParams(params);
		String sessionId = findSessionId(request);
		sp.setSessionId(sessionId);
		logger.debug("params == " + sp);
		invService.search(request, sp);
		
		StringBuffer hostWithPath = HttpUtils.getRequestURL(request);
		String host = hostWithPath.substring(0, hostWithPath.indexOf("/startSearch"));
		String callback = params.get("jsoncallback") 
				+ "({\"redirectURL\": \""+host+"\\/results" +  "\"})";
		clientResponse.setContentType("application/json");
		clientResponse.getOutputStream().write(callback.getBytes());
		clientResponse.addCookie(createSessionIdCookie(sessionId));
	}

	@RequestMapping(value = "/search", method={RequestMethod.GET, RequestMethod.POST}, produces="application/javascript")
	@ResponseBody
	public ModelAndView search( 
			@RequestParam Map<String, String> params, 
			@ModelAttribute("searchResults") ModelMap model,
			HttpServletRequest request, HttpServletResponse clientResponse) throws Exception
	{
		SearchParams sp = new SearchParams(params);
		String sessionId = findSessionId(request);
		sp.setSessionId(sessionId);
		invService.search(request, sp);
		model.put("result", invService.getUpdatedResults(sessionId, null, null));
		
		clientResponse.addCookie(createSessionIdCookie(sessionId));
		return new ModelAndView("searchResult", model);
	}	
	
	/**
	 * 
	 * @param params
	 * @param sortType
	 * @param page
	 * @param model
	 * @param clientRequest
	 * @param clientResponse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getResults(
			@RequestParam(value="sort", required=false) SortType sortType,
			@RequestParam(value="page", required=false) Integer page,
			@ModelAttribute("searchResults") ModelMap model,
			HttpServletRequest clientRequest,
			HttpServletResponse clientResponse) throws Exception
	{		
		String sessionId = findSessionId(clientRequest);
		model.put("result", invService.getUpdatedResults(sessionId, sortType, page));
		
		clientResponse.addCookie(createSessionIdCookie(sessionId));
		return new ModelAndView("searchResult", model);
	}	
	
	@RequestMapping(value="/health", method=RequestMethod.GET)
	public @ResponseBody String healthCheck()
	{
		return "ok";
	}
	
	private static final String findSessionId(HttpServletRequest request)
	{
		String id = findCookieValue(request, "parent_cookie");
		if (id != null)
		{
			int idx = id.indexOf("___");
			if (idx >=0)
				id = id.substring(0, idx);
		}
		else
		{
			id = findCookieValue(request, "sessionid");
			if (id == null)
				id = String.valueOf(System.currentTimeMillis() + SESSION_SALT.getAndIncrement());
		}
		logger.error("sessionid == " + id);
		return id;
	}	
	private static final String findCookieValue(HttpServletRequest request, String cookieName)
	{
		if (request != null && request.getCookies() != null)
		{
			for (Cookie c: request.getCookies())
			{
				logger.debug(String.format("cookie: %1$s, value: %2$s", c.getName(), c.getValue()	));
				if (c.getName().equals(cookieName))
				{
					return c.getValue();
				}
			}			
		}
		return null;
	}
	
	private static final Cookie createSessionIdCookie(String sessionId)
	{
		Cookie sessionIdCookie = new Cookie("sessionid", sessionId);
		//sessionIdCookie.setDomain(".neworleans.com");
		sessionIdCookie.setMaxAge(-1);
		sessionIdCookie.setSecure(false);
		
		return sessionIdCookie;
	}
}