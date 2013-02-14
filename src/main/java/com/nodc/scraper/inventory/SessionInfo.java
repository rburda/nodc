package com.nodc.scraper.inventory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SessionInfo
{
	private static Logger logger = LoggerFactory.getLogger(SessionInfo.class);
	
	public static final String SESSION_ID_COOKIE_NAME = "parent_jsession_id";
	public static final String WWW_SID_COOKIE_NAME = "parent_sid";
	public static final String WICKET_SEARCH_COOKIE_NAME = "parent_url";
	
	private String jsessionId;
	private String wwwsid;
	private String wicketSessionPathForSearch;
	private HttpServletRequest request;
	
	public SessionInfo(HttpServletRequest servletRequest)
	{
		this.request = servletRequest;
		initSessionVars(servletRequest);
		if (StringUtils.isEmpty(jsessionId) || StringUtils.isEmpty(wwwsid) || StringUtils.isEmpty(wicketSessionPathForSearch))
		{
			logger.error("missing some session info...creating new");
			logger.error("invalid jsessionid == " + jsessionId);
			logger.error("invalid wwwsid == " + wwwsid);
			logger.error("invalid wicketpath == " + wicketSessionPathForSearch);			
			createNewSession();
		}
		logger.error("final jsessionid == " + jsessionId);
		logger.error("final wwwsid == " + wwwsid);
		logger.error("final wicketpath == " + wicketSessionPathForSearch);
	}
	
	public HttpServletRequest getRequest()
	{
		return request;
	}

	public final String getSessionId()
	{
		return jsessionId;
	}
	
	public final String getWWWSid()
	{
		return wwwsid;
	}
	
	public final String getWicketSearchPath()
	{
		return wicketSessionPathForSearch;
	}
	
	private void initSessionVars(Object cookieStore)
	{
		String cValue = findCookieValue(cookieStore, SESSION_ID_COOKIE_NAME);
		if (cValue.indexOf("=") >= 0)
			cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
		jsessionId = cValue;
		
		cValue = findCookieValue(cookieStore, WWW_SID_COOKIE_NAME);
		if (cValue.indexOf("=") >= 0)
			cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
		wwwsid = cValue;
		
		cValue = findCookieValue(cookieStore, WICKET_SEARCH_COOKIE_NAME);
		if (cValue.indexOf("=") >= 0)
			cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
		wicketSessionPathForSearch = cValue;	
	}
	
	private void createNewSession()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/HotelSearchWidget/")
				.addParameter("skin", "homeHotel");
		
		try
		{
			HttpGet httpget = new HttpGet(builder.build());
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 45000);
	    HttpConnectionParams.setSoTimeout(httpParams, 45000);	
	    
	    HttpResponse resp = httpClient.execute(httpget);
	    for (org.apache.http.cookie.Cookie c: httpClient.getCookieStore().getCookies())
	    {
	    	if (c.getName().equalsIgnoreCase("JSESSIONID"))
	    		jsessionId = c.getValue();
	    	else if (c.getName().equalsIgnoreCase("www_sid"))
	    		wwwsid = c.getValue();
	    }
	    Document d = Jsoup.parse(EntityUtils.toString(resp.getEntity()));
	    String rawUrl = d.select(".hotelSearchForm").first().attr("action");
	    int idx = rawUrl.indexOf("wicket:interface=");
	    if (idx >= 0)
	    	wicketSessionPathForSearch = rawUrl.substring(idx+17, rawUrl.length());
		}
		catch (Exception e)
		{
			NODCWarehouse.logger.error("Unable to create new session", e);
		}
	}	
	
	private static final String findCookieValue(Object cookieStore, String cookieName)
	{
		String value = "";
		if (cookieStore != null)
		{
			if (cookieStore instanceof HttpServletRequest)
			{
				HttpServletRequest request = (HttpServletRequest)cookieStore;
				if (request.getCookies() != null)
				{
					for (Cookie c: request.getCookies())
					{
						logger.error("cookie submitted: " + c.getName() + ": " + c.getValue());
						if (c.getName().equals(cookieName))
						{
							value = c.getValue();
						}
					}			
				}				
			}
			else 
			{
				CookieStore httpClientCookieStore = (CookieStore)cookieStore;
				if (httpClientCookieStore.getCookies() != null)
				{
					for (org.apache.http.cookie.Cookie c: httpClientCookieStore.getCookies())
					{
						logger.error("cookie submitted: " + c.getName() + ": " + c.getValue());
						if (c.getName().equals(cookieName))
						{
							value = c.getValue();
						}
					}			
				}					
			}
		}
		return value;		
	}
}