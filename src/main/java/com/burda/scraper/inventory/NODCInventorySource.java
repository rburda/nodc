package com.burda.scraper.inventory;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchResult;

public class NODCInventorySource implements InventorySource
{
	private static final DateFormat STAY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static final Logger logger = LoggerFactory.getLogger(NODCInventorySource.class);
	
	@Override
	public SearchResult getResults() throws Exception
	{
		HttpResponse response = queryNODCHotelsViaHttpClient();
		byte[] html = EntityUtils.toByteArray(response.getEntity());

		SearchResult result = createHotelsNODC(html);
		for (Hotel h: result.getHotels())
		{
			logger.error(String.format("hotel: %1$s", h));
		}
		List<com.burda.scraper.model.Header> headers = new ArrayList<com.burda.scraper.model.Header>();
		for (Header header: response.getAllHeaders())
		{
			com.burda.scraper.model.Header newHeader = new com.burda.scraper.model.Header();			
			if (header.getValue().contains("JSESSIONID"))
			{
				newHeader.name = header.getName();
				newHeader.value =  header.getValue() + "; domain=.neworleans.com";
			}
			else
			{
				newHeader.name = header.getName();
				newHeader.value = header.getValue();	
			}
			result.headers.add(newHeader);
			logger.error(header.getName() + " " + header.getValue());
		}
		return result;
	}
	
	private SearchResult createHotelsNODC(byte[] html) throws Exception
	{
		SearchResult result = new SearchResult();
		
		Document document = Jsoup.parse(new String(html), "http://www.neworleans.com/mytrip/app");
		result.startDate = STAY_DATE_FORMAT.parse(document.select("[name=departureDate]").first().val());
		result.endDate = STAY_DATE_FORMAT.parse(document.select("[name=returnDate]").first().val());
		
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
			result.getHotels().add(hotel);
		}	
		
		return result;
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

}