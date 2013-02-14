package com.nodc.scraper.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nodc.scraper.inventory.InventoryService;
import com.nodc.scraper.inventory.SessionInfo;
import com.nodc.scraper.model.SearchParams;
import com.nodc.scraper.model.SearchResult;
import com.nodc.scraper.model.SortType;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;
import com.google.common.base.Joiner;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SearchController
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	private static DateTimeFormatter CHECK_IN_OUT_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");

	
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
		sp.setSessionInfo(new SessionInfo(request));
		logger.debug("params == " + sp);
		invService.search(request, sp);
		
		StringBuffer hostWithPath = HttpUtils.getRequestURL(request);
		String host = hostWithPath.substring(0, hostWithPath.indexOf("/startSearch"));
		String callback = params.get("jsoncallback") 
				+ "({\"redirectURL\": \""+host+"\\/results" +  "\"})";
		clientResponse.setContentType("application/json");
		clientResponse.getOutputStream().write(callback.getBytes());
		createResponseCookies(request, clientResponse, sp);
	}

	@RequestMapping(value = "/search", method={RequestMethod.GET, RequestMethod.POST}, produces="application/javascript")
	@ResponseBody
	public ModelAndView search( 
			@RequestParam Map<String, String> params, 
			@ModelAttribute("searchResults") ModelMap model,
			HttpServletRequest request, HttpServletResponse clientResponse) throws Exception
	{
		SearchParams sp = new SearchParams(params);
		sp.setSessionInfo(new SessionInfo(request));
		invService.search(request, sp);
		model.put("result", invService.getAggragatedResults(sp.getSessionInfo(), null, null));
		
		createResponseCookies(request, clientResponse, sp);
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
		SessionInfo sessionInfo = new SessionInfo(clientRequest);
		SearchResult searchResult = invService.getAggragatedResults(sessionInfo, sortType, page); 
		if (searchResult == null)
			return new ModelAndView(new RedirectView("http://www.neworleans.com"));
		model.put("result", searchResult);
		
		//createResponseCookies(clientRequest, clientResponse, null);
		return new ModelAndView("searchResult", model);
	}	
	
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getHotelDetails(
			@RequestParam(value="hotelName", required=true) String hotelName,
			@ModelAttribute("details") ModelMap model,
			HttpServletRequest clientRequest,
			HttpServletResponse clientResponse) throws Exception
	{

		SessionInfo sessionInfo = new SessionInfo(clientRequest);
		
		model.put("hotelDetail",  invService.getHotelDetails(sessionInfo, hotelName));
		return new ModelAndView("hotelDetailPopup", model);
	}
	
	
	@RequestMapping(value = "/detailsJson", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public HotelDetail getHotelDetailsJson(
			@RequestParam(value="hotelName", required=true) String hotelName,
			HttpServletRequest clientRequest,
			HttpServletResponse clientResponse) throws Exception
	{
		return invService.getHotelDetails(new SessionInfo(clientRequest), hotelName);
	}
		
	@RequestMapping(value="/admin/editMaster", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView getMasterHotels(@ModelAttribute("model") ModelMap model)
	{
		model.put("hotelList",  invService.getMasterRecords());
		return new ModelAndView("/admin/masterHotelEdit");
	}
	
	@RequestMapping(value="/admin/saveMasterHotel", method=RequestMethod.POST)
	@ResponseBody
	//public ModelAndView saveMasterHotels(@ModelAttribute("wrapper") SaveMasterHotelWrapper wrapper)
	public ModelAndView saveMasterHotel(@ModelAttribute("hotel") MasterHotel mh, @RequestParam("newHotelName") String newHotelName)
	{
		invService.saveMasterRecord(mh, newHotelName);
		return new ModelAndView(new RedirectView("/admin/editMaster"));
	}
	
	@RequestMapping(value="/admin/deleteMasterHotel", method=RequestMethod.POST)
	@ResponseBody
	public ModelAndView deleteMasterHotel(@RequestParam("name") String masterHotelName)
	{
		invService.deleteMasterRecord(masterHotelName);
		return new ModelAndView(new RedirectView("/admin/editMaster"));
	}

	@RequestMapping(value="/admin/viewSourceHotels", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView viewSourceHotels(@ModelAttribute("model") ModelMap map)
	{
		List<SourceHotel> sourceHotels = invService.getSourceHotels();
		map.put("sourceHotelList",  sourceHotels);
		map.put("masterHotelList", invService.getMasterRecords());
		return new ModelAndView("/admin/viewSourceHotels", map);
	}
	
	@RequestMapping(value="/admin/editSourceHotel", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView editSourceHotel(
			@RequestParam("sourceHotelId") String sourceHotelId, 
			@RequestParam("inventorySource") InventorySource is,
			@ModelAttribute("model") ModelMap model)
	{
		SourceHotel sh = invService.getSourceHotel(sourceHotelId, is);
		model.put("sourceHotel",  sh);
		model.put("masterHotelList", invService.getMasterRecords());
		
		return new ModelAndView("/admin/editSourceHotel", model);
	}
	
	@RequestMapping(value="/admin/updateSourceHotel", method=RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateSourceHotel(HttpServletRequest req, 
			@RequestParam("sourceHotelId")String externalHotelId, 
			@RequestParam("invSource")InventorySource is, 
			@RequestParam("masterHotelName")String masterHotelName)
	{
		invService.updateSourceHotelName(externalHotelId, is, masterHotelName);
		return new ModelAndView(new RedirectView("/admin/viewSourceHotels"));
	}
	
	
	@RequestMapping(value="/health", method=RequestMethod.GET)
	public @ResponseBody String healthCheck()
	{
		return "ok";
	}
	
	private static final void createResponseCookies(
			HttpServletRequest req, HttpServletResponse resp, SearchParams params)
	{
		if (params == null)
			return;
		
		Cookie sessionIdCookie = new Cookie(SessionInfo.SESSION_ID_COOKIE_NAME, params.getSessionInfo().getSessionId());
		sessionIdCookie.setMaxAge(-1);
		sessionIdCookie.setSecure(false);
		sessionIdCookie.setDomain(".www.neworleans.com");
		resp.addCookie(sessionIdCookie);
		
		Cookie wwwSidCookie = new Cookie(SessionInfo.WWW_SID_COOKIE_NAME, params.getSessionInfo().getWWWSid());
		wwwSidCookie.setMaxAge(-1);
		wwwSidCookie.setSecure(false);
		wwwSidCookie.setDomain(".www.neworleans.com");
		resp.addCookie(wwwSidCookie);
		
		Cookie wicketParentUrlCookie = new Cookie(SessionInfo.WICKET_SEARCH_COOKIE_NAME, params.getSessionInfo().getWicketSearchPath());
		wicketParentUrlCookie.setMaxAge(-1);
		wicketParentUrlCookie.setSecure(false);
		wicketParentUrlCookie.setDomain(".www.neworleans.com");
		resp.addCookie(wicketParentUrlCookie);
		
		
		int maxCookieAge = (int)new Duration(new DateTime(), new DateTime().plusMonths(1)).getStandardSeconds();
		
		Cookie checkInDate = new Cookie("CHECKINDATE", CHECK_IN_OUT_FORMAT.print(params.getCheckInDate()));
		setStdCookieValues(checkInDate, maxCookieAge);
		resp.addCookie(checkInDate);

		Cookie checkOutDate = new Cookie("CHECKOUTDATE", CHECK_IN_OUT_FORMAT.print(params.getCheckOutDate()));
		setStdCookieValues(checkOutDate, maxCookieAge);
		resp.addCookie(checkOutDate);
		
		Cookie numRooms = new Cookie("numRooms", String.valueOf(params.getNumRooms()));
		setStdCookieValues(numRooms, maxCookieAge);
		resp.addCookie(numRooms);
		
		Cookie numAdults = new Cookie("numAdults", Joiner.on(",").join(
				params.getNumAdults1(), params.getNumAdults2(), params.getNumAdults3(), params.getNumAdults4()));
		setStdCookieValues(numAdults, maxCookieAge);
		resp.addCookie(numAdults);
		
		Cookie numChildren = new Cookie("numChildren", 
				Joiner.on(",").join(
						params.getNumChildren1(), params.getNumChildren2(), params.getNumChildren3(), params.getNumChildren4()));
		setStdCookieValues(numChildren, maxCookieAge);
		resp.addCookie(numChildren);
		
		Cookie childrenAgesRoom1 = new Cookie("ChildrenAgesRoom1", 
				Joiner.on(",").join(
						params.getRoom1ChildAge1(), params.getRoom1ChildAge2(), params.getRoom1ChildAge3()));
		setStdCookieValues(childrenAgesRoom1, maxCookieAge);
		resp.addCookie(childrenAgesRoom1);

		Cookie childrenAgesRoom2 = new Cookie("ChildrenAgesRoom2", 
				Joiner.on(",").join(
						params.getRoom2ChildAge1(), params.getRoom2ChildAge2(), params.getRoom2ChildAge3()));
		setStdCookieValues(childrenAgesRoom2, maxCookieAge);
		resp.addCookie(childrenAgesRoom2);
		
		Cookie childrenAgesRoom3 = new Cookie("ChildrenAgesRoom3", 
				Joiner.on(",").join(
						params.getRoom3ChildAge1(), params.getRoom3ChildAge2(), params.getRoom3ChildAge3()));
		setStdCookieValues(childrenAgesRoom3, maxCookieAge);
		resp.addCookie(childrenAgesRoom3);
		
		Cookie childrenAgesRoom4 = new Cookie("ChildrenAgesRoom4", 
				Joiner.on(",").join(
						params.getRoom4ChildAge1(), params.getRoom4ChildAge2(), params.getRoom4ChildAge3()));
		setStdCookieValues(childrenAgesRoom4, maxCookieAge);
		resp.addCookie(childrenAgesRoom4);
	}
	
	private static void setStdCookieValues(Cookie c, int maxCookieAge)
	{
		c.setMaxAge(maxCookieAge);
		c.setSecure(false);
		c.setDomain(".neworleans.com");
	}
}