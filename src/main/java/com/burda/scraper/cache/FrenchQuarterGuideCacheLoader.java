package com.burda.scraper.cache;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.model.Amenity;
import com.burda.scraper.model.Photo;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.SourceHotel;

public class FrenchQuarterGuideCacheLoader
{
	private Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideCacheLoader.class);
	
	private HotelDetailDAO hotelDetailDAO;
	private SourceHotelDAO sourceHotelDAO;
	
	public void setSourceHotelDAO(SourceHotelDAO shDAO)
	{
		this.sourceHotelDAO = shDAO;
	}
	
	public void setHotelDetailDAO(HotelDetailDAO hdd)
	{
		this.hotelDetailDAO = hdd;
	}
	public void loadCache() throws Exception
	{
		boolean hasMorePages = true;
		int page = 1;
		while (hasMorePages)
		{
			HttpResponse hotelSummaryResponse = queryGetHotelResults(page);
			byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
			Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());

			int totalNumPages = Integer.valueOf(xmlSummaryResults.select("hotel_data").select("total_pages").first().ownText());
			hasMorePages = (page++ < totalNumPages);			

			for (Element hotelSummaryEl: xmlSummaryResults.select("hotel"))
			{
				String extHotelId = hotelSummaryEl.select("hotel_id").first().ownText();
				SourceHotel sourceHotel = sourceHotelDAO.getByHotelId(extHotelId, InventorySource.FQG);
				if (sourceHotel == null)
				{
					logger.warn(String.format("skipping hotel: %1$s; no source info found", extHotelId));
					continue;
				}
				
				HttpResponse hotelDetailResponse = queryGetHotelData(extHotelId);
				byte[] xmlAsBytes = EntityUtils.toByteArray(hotelDetailResponse.getEntity());
				Element detailsEl = Jsoup.parse(new String(xmlAsBytes), "", Parser.xmlParser()).select("result").first();
				
				HotelDetail details = hotelDetailDAO.getHotelDetail(sourceHotel.getHotelName());
				if (details == null)
				{
					details = new HotelDetail();
					details.setName(sourceHotel.getHotelName());
					details.setAddress1(detailsEl.select("address").first().ownText());
				}
							
				details.setDescription(detailsEl.select("description_full").first().ownText());
				details.setAreaDescription(detailsEl.select("district").first().ownText());
				if (details.getCity() == null)
					details.setCity("");
				if (details.getState() == null)
					details.setState("");
				if (details.getZip() == null)
					details.setZip("");
				if (details.getLatitude() == null)
					details.setLatitude(detailsEl.select("latitude").first().ownText());
				if (details.getLongitude() == null)
					details.setLongitude(detailsEl.select("longitude").first().ownText());
				details.setRating(Float.valueOf(detailsEl.select("star_rating").first().ownText()));

				if (details.getPhotos().isEmpty())
				{
					for (Element photoEl: detailsEl.select("photo_data").first().select("photo"))
					{
						Photo photo = new Photo();
						photo.url = photoEl.ownText();
						details.addPhoto(photo);
					}					
				}
				
				if (details.getAmenities().isEmpty())
				{
					for (Element topAmenityEl: detailsEl.select("top_amenities").first().select("amenity"))
					{
						Amenity amenity = new Amenity();
						amenity.name = topAmenityEl.select("amenity_name").first().ownText();
						details.addAmenity(amenity);
					}					
				}
				hotelDetailDAO.save(details);
			}
		}		
	}
	
	/**
	 * gets detail data on a specific hotel
	 * @return
	 */
	private HttpResponse queryGetHotelData(String hotelId)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getHotelData")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("hotel_id", hotelId)
				.addParameter("check_in", new DateTime().plusDays(60).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("rooms", "1");
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
	
	/**
	 * Returns xml that lists a summary level detail of all hotels
	 * @return
	 */
	private HttpResponse queryGetHotelResults(int page)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getResults")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("city_id", "3000008434")
				.addParameter("check_in", new DateTime().plusDays(60).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("page", String.valueOf(page));
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
