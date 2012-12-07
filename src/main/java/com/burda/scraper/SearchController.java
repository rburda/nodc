package com.burda.scraper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.burda.scraper.inventory.InventoryServiceImpl;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchResult;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SearchController
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	/**
	 * Calls inventoryService and executes a search based on search params
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public void home(Locale locale, Model model, HttpServletResponse clientResponse) throws Exception
	{
		SearchResult result = new InventoryServiceImpl().getSearchResult();
				
		JsonView.render(result,  clientResponse);
		//clientResponse.getOutputStream().write(html);
		//clientResponse.setContentLength(html.length);

	}


	
	private Connection queryNODCHotels()
	{
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("id88_hf_0", "");
		requestParams.put("productType", "HOTEL");
		requestParams.put("promoId", "");
		requestParams.put("mo", "");
		requestParams.put("pdp:physicalDestination", "982");
		requestParams.put("departureDate", "11/30/2012");
		requestParams.put("returnDate", "12/02/2012");
		requestParams.put("numRooms", "1");
		requestParams.put("r1:0:ro:na", "2");
		requestParams.put("rl:0:ro:ob:nc", "0");
		requestParams.put("rl:1:ro:na", "1");
		requestParams.put("rl:1:ro:ob:nc", "0");
		requestParams.put("rl:2:ro:na", "1");
		requestParams.put("rl:2:ro:ob:nc", "0");
		requestParams.put("rl:3:ro:na", "1");
		requestParams.put("rl:3:ro:ob:nc", "0");
		requestParams.put("a:0:b:c:0:d:e", "");
		requestParams.put("a:0:b:c:1:d:e", "");
		requestParams.put("a:0:b:c:2:d:e", "");
		requestParams.put("a:1:b:c:0:d:e", "");
		requestParams.put("a:1:b:c:2:d:e", "");
		requestParams.put("a:2:b:c:0:d:e", "");
		requestParams.put("a:2:b:c:1:d:e", "");
		requestParams.put("a:2:b:c:2:d:e", "");
		requestParams.put("a:3:b:c:0:d:e", "");
		requestParams.put("a:3:b:c:1:d:e", "");
		requestParams.put("a:3:b:c:2:d:e", "");
		requestParams.put("preferredProductId", "");
		requestParams.put("wicket:bookmarkablePage",
				":com.vegas.athena.components.browse.hotel.HotelBrowsePage");		
		
		return Jsoup.connect("http://www.neworleans.com/mytrip/app").timeout(0).userAgent("Mozilla").data(requestParams);			
	}
		

	
	private static long stream(InputStream input, OutputStream output) throws IOException
	{
		ReadableByteChannel inputChannel = null;
		WritableByteChannel outputChannel = null;

		try
		{
			inputChannel = Channels.newChannel(input);
			outputChannel = Channels.newChannel(output);
			ByteBuffer buffer = ByteBuffer.allocate(10240);
			long size = 0;

			while (inputChannel.read(buffer) != -1)
			{
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			return size;
		} finally
		{
			if (outputChannel != null)
				try
				{
					outputChannel.close();
				} catch (IOException ignore)
				{ /**/
				}
			if (inputChannel != null)
				try
				{
					inputChannel.close();
				} catch (IOException ignore)
				{ /**/
				}
		}
	}	
}
