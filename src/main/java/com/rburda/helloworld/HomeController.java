package com.rburda.helloworld;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
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

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController
{
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void home(Locale locale, Model model, HttpServletResponse clientResponse) throws IOException
	{
		HttpResponse response = queryNODCHotelsViaHttpClient();
		byte[] html = EntityUtils.toByteArray(response.getEntity());

		List<Hotel> hotels = createHotelsNODC(html);
		for (Hotel h: hotels)
		{
			logger.error(String.format("hotel: %1$s", h));
		}
		for (Header header: response.getAllHeaders())
		{
			if (header.getValue().contains("JSESSIONID"))
				clientResponse.addHeader(//
						header.getName(), header.getValue() + "; domain=.neworleans.com");
			else
				clientResponse.addHeader(header.getName(), header.getValue());
			logger.error(header.getName() + " " + header.getValue());
		}
		try
		{
			clientResponse.getOutputStream().write(html);
			clientResponse.setContentLength(html.length);
		}
		catch (IOException ioe)
		{
			logger.error("unable to stream response to client", ioe);
		}
	}

	private List<Hotel> createHotelsNODC(byte[] html)
	{
		List<Hotel> hotels = new ArrayList<Hotel>();
		
		Document document = Jsoup.parse(new String(html), "http://www.neworleans.com/mytrip/app");
		Elements searchResults = document.select(".searchResult");
		for (Element searchResult: searchResults)
		{
			Element hName = searchResult.select(".productTitle a").first();
			Element hDescription = searchResult.select(".productSummary p:eq(1)").first();	
			Element hArea = searchResult.select(".productSummary p:eq(0)").first();
			Element hMapUrl = searchResult.select(".productSummary p:eq(0) a").first();
			Element hMoreInfoUrl = searchResult.select(".productSummary p:eq(2) a:eq(0)").first();
			Element hPhotosUrl = searchResult.select(".productSummary p:eq(0) a:eq(1)").first();
			
			Hotel hotel = new Hotel();
			hotel.name = hName.ownText();
			hotel.description = hDescription.ownText();
			hotel.areaDescription = calculateArea(hArea);
			hotel.mapUrl = calculateMapUrl(hMapUrl);
			hotel.moreInfoUrl = calculateMoreInfoUrl(hMoreInfoUrl);
			hotel.photosUrl = calculatePhotosUrl(hPhotosUrl);
			hotel.source="NODC";
			
			
			Elements roomTypeElements = searchResult.select("table.hotelResults");
			List<RoomType> roomTypes = new ArrayList<RoomType>(); 
			for (Element roomTypeEl: roomTypeElements.select("tbody tr.cyl-HotelRow"))
			{
				Element bookIt = roomTypeEl.select(".bookIt").first();
				Element avgNightlyRate = roomTypeEl.select("td.priceCol span:not(.originalRate)").first();
				Element roomTypeName = roomTypeEl.select("td.productCol a:eq(0)").first();
				Element promoDesc = roomTypeEl.select("td.productCol span.promo").first();
				Element totalPrice = roomTypeEl.select("td.priceCol p.totalLine span").first();
				
				RoomType roomType = new RoomType();
				roomType.bookItUrl = calculateBookItUrl(bookIt);
				roomType.avgNightlyRate = new BigDecimal(StringUtils.strip(avgNightlyRate.ownText(), "$"));
				roomType.name = roomTypeName.ownText();
				if (promoDesc != null)
					roomType.promoDesc = promoDesc.ownText();
				roomType.totalPrice = new BigDecimal(StringUtils.strip(totalPrice.ownText(), "$"));
				
				roomTypes.add(roomType);
			}
			hotel.roomTypes = roomTypes;
			hotels.add(hotel);
		}	
		
		return hotels;
	}
	
	private String calculateStandardUrl(Element el)
	{
		String onclick = el.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculatePhotosUrl(Element photoUrl)
	{
		String onclick = photoUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculateMoreInfoUrl(Element moreInfoUrl)
	{
		String onclick = moreInfoUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculateMapUrl(Element mapUrl)
	{
		String onclick = mapUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);		
	}
	
	private String calculateArea(Element area)
	{
		int beginIdx = area.ownText().indexOf("Area:");
		int endIdx = area.ownText().indexOf("(");
		
		return area.ownText().substring(beginIdx, endIdx);
	}
	
	private String calculateBookItUrl(Element bookIt)
	{
		String onclick = bookIt.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("'; }");
		
		return onclick.substring(beginIdx+1, endIdx);
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
		
	private HttpResponse queryNODCHotelsViaHttpClient()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/")
				.addParameter("id88_hf_0", "")
				.addParameter("productType", "HOTEL")
				.addParameter("promoId", "")
				.addParameter("mo", "")
				.addParameter("pdp:physicalDestination", "982")
				.addParameter("departureDate", "11/30/2012")
				.addParameter("returnDate", "12/02/2012")
				.addParameter("numRooms", "1")
				.addParameter("r1:0:ro:na", "2")
				.addParameter("rl:0:ro:ob:nc", "0")
				.addParameter("rl:1:ro:na", "1")
				.addParameter("rl:1:ro:ob:nc", "0")
				.addParameter("rl:2:ro:na", "1")
				.addParameter("rl:2:ro:ob:nc", "0")
				.addParameter("rl:3:ro:na", "1")
				.addParameter("rl:3:ro:ob:nc", "0")
				.addParameter("a:0:b:c:0:d:e", "")
				.addParameter("a:0:b:c:1:d:e", "")
				.addParameter("a:0:b:c:2:d:e", "")
				.addParameter("a:1:b:c:0:d:e", "")
				.addParameter("a:1:b:c:2:d:e", "")
				.addParameter("a:2:b:c:0:d:e", "")
				.addParameter("a:2:b:c:1:d:e", "")
				.addParameter("a:2:b:c:2:d:e", "")
				.addParameter("a:3:b:c:0:d:e", "")
				.addParameter("a:3:b:c:1:d:e", "")
				.addParameter("a:3:b:c:2:d:e", "")
				.addParameter("preferredProductId", "")
				.addParameter("wicket:bookmarkablePage",
						":com.vegas.athena.components.browse.hotel.HotelBrowsePage");					
		HttpResponse response = null;
		try
		{
			URI uri = builder.build();
			HttpGet httpget = new HttpGet(uri);
			logger.error("uri == " + httpget.getURI());

			response = new DefaultHttpClient().execute(httpget);
		} 
		catch (Exception e)
		{
			logger.error("unable to retrieve query response", e);
		}
		return response;
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
