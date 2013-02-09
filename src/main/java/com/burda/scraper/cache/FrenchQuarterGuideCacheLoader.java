package com.burda.scraper.cache;

import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

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

import com.burda.scraper.dao.HotelDetailCacheKey;
import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.RoomTypeDetailDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.model.Amenity;
import com.burda.scraper.model.Photo;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.burda.scraper.model.persisted.SourceHotel;

public class FrenchQuarterGuideCacheLoader
{
	private Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideCacheLoader.class);
	
	private HotelDetailDAO hotelDetailDAO;
	private SourceHotelDAO sourceHotelDAO;
	private RoomTypeDetailDAO roomTypeDetailDAO;
	
	public void setSourceHotelDAO(SourceHotelDAO shDAO)
	{
		this.sourceHotelDAO = shDAO;
	}
	
	public void setHotelDetailDAO(HotelDetailDAO hdd)
	{
		this.hotelDetailDAO = hdd;
	}
	
	public void setRoomTypeDetailDAO(RoomTypeDetailDAO dao)
	{
		this.roomTypeDetailDAO = dao;
	}
	
	public void loadCache() throws Exception
	{

			HttpResponse hotelSummaryResponse = queryGetHotelResults(1);
			byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
			Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());

			for (Element hotelSummaryEl: xmlSummaryResults.select("hotel"))
			{
				String extHotelId = hotelSummaryEl.select("hotel_id").first().ownText();
				SourceHotel sourceHotel = sourceHotelDAO.getByHotelId(extHotelId, InventorySource.FQG);
				if (sourceHotel == null)
				{
					logger.warn(String.format("skipping hotel: %1$s; no source info found", extHotelId));
					continue;
				}
				
				Element detailsEl = null;
				int tries = 0;
				boolean hasRoomTypes = false;
				while (!hasRoomTypes && tries < 10)
				{
					HttpResponse hotelDetailResponse = queryGetHotelData(extHotelId, tries);
					byte[] xmlAsBytes = EntityUtils.toByteArray(hotelDetailResponse.getEntity());
					detailsEl = Jsoup.parse(new String(xmlAsBytes), "", Parser.xmlParser()).select("result").first();
					
					if (!detailsEl.select("rate").isEmpty())
						hasRoomTypes = true;
					else
					{
						tries++;
					}						
				}
				HotelDetailCacheKey cacheKey = new HotelDetailCacheKey(sourceHotel.getHotelName(), InventorySource.FQG);
				HotelDetail details = hotelDetailDAO.getHotelDetail(cacheKey);
				if (details == null)
				{
					details = new HotelDetail();
					details.setName(sourceHotel.getHotelName());
					details.setAddress1(detailsEl.select("address").first().ownText());
				}
				
				details.setDescription(URLDecoder.decode(detailsEl.select("description_full").first().ownText(), "UTF-8"));
				details.setAreaDescription(detailsEl.select("district").first().ownText());
				if (details.getCity() == null)
					details.setCity("");
				if (details.getState() == null)
					details.setState("");
				if (details.getZip() == null)
					details.setZip("");
				details.setLatitude(detailsEl.select("latitude").first().ownText());
				details.setLongitude(detailsEl.select("longitude").first().ownText());
				details.setRating(Float.valueOf(detailsEl.select("star_rating").first().ownText()));

				details.clearPhotos();
				for (Element photoEl: detailsEl.select("photo_data").first().select("photo"))
				{
					Photo photo = new Photo();
					photo.url = photoEl.ownText();
					details.addPhoto(photo);
				}					
				
				details.clearAmenities();
				for (Element topAmenityEl: detailsEl.select("top_amenities").first().select("amenity"))
				{
					Amenity amenity = new Amenity();
					amenity.name = topAmenityEl.select("amenity_name").first().ownText();
					details.addAmenity(amenity);
				}									

				for (Element roomTypeEl: detailsEl.select("rate"))
				{
					String roomTypeName = URLDecoder.decode(roomTypeEl.select("room_title").first().ownText(), "UTF-8");
					RoomTypeDetail rtd = null;
					for (RoomTypeDetail existingDetail: details.getRoomTypeDetails())
					{
						if (existingDetail.getName().equals(roomTypeName))
							rtd = existingDetail;
					}
					if (rtd == null)
						rtd = new RoomTypeDetail();
					rtd.setHotelName(sourceHotel.getHotelName()+"_"+InventorySource.FQG.name());
					rtd.setDescription(URLDecoder.decode(roomTypeEl.select("room_description").first().ownText(), "UTF-8"));
					rtd.setDetails(roomTypeEl.select("room_details").first().ownText());
					rtd.setFeatures(roomTypeEl.select("room_facilities").first().ownText());
					rtd.setName(roomTypeName);
					for (Element photoEl: roomTypeEl.select("room_photo_data").first().select("photo"))
					{
						Photo p = new Photo();
						p.url = photoEl.select("full").first().ownText();
						rtd.addPhoto(p);
					}
					details.addRoomTypeDetail(rtd);
				}
				
				logger.error("saving: "+ sourceHotel.getHotelName());
				hotelDetailDAO.save(details);
			}	
	}
	
	/**
	 * gets detail data on a specific hotel
	 * @return
	 */
	private HttpResponse queryGetHotelData(String hotelId, int offset)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getHotelData")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("hotel_id", hotelId)
				.addParameter("check_in", new DateTime().plusDays(60+offset).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61+offset).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
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
		/*builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getResults")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("city_id", "3000008434")
				.addParameter("check_in", new DateTime().plusDays(60).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("page", String.valueOf(page));
		*/
		builder
			.setScheme("http")
			.setHost("api.rezserver.com")
			.setPath("/api/hotel/getStaticHotels")
			.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
			.addParameter("refid", "5057")
			.addParameter("city_id", "3000008434")
			.addParameter("limit", String.valueOf(500));	
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
