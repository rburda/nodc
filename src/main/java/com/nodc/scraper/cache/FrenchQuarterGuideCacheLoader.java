package com.nodc.scraper.cache;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;

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
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.dao.HotelDetailCacheKey;
import com.nodc.scraper.dao.HotelDetailDAO;
import com.nodc.scraper.dao.SourceHotelDAO;
import com.nodc.scraper.inventory.InventoryUtils;
import com.nodc.scraper.model.Amenity;
import com.nodc.scraper.model.Photo;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.RoomTypeDetail;
import com.nodc.scraper.model.persisted.SourceHotel;

@Component
public class FrenchQuarterGuideCacheLoader
{
	public static boolean isContentRefreshable(boolean isNew, Map<String, Boolean> refreshableMap, String col)
	{
		return (isNew || refreshableMap.get(col) == null || refreshableMap.get(col) == false);
	}
	
	private Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideCacheLoader.class);
	
	private final HotelDetailDAO hotelDetailDAO;
	private final SourceHotelDAO sourceHotelDAO;
	private final CacheStateDAO cacheStateDAO;
	
	@Autowired
	public FrenchQuarterGuideCacheLoader(
			HotelDetailDAO hdDAO, SourceHotelDAO shDAO, CacheStateDAO csDAO)
	{
		this.hotelDetailDAO = hdDAO;
		this.sourceHotelDAO = shDAO;
		this.cacheStateDAO = csDAO;
	}
	
	//1:01am every day; '1' to ensure ordering with other tasks; tasks are 
	//single threaded so if tasks overlap, they will wait for an executing one to
	//finish before starting the next one.
	@Scheduled(cron = "0 1 1 * * ?")
	public void loadCache() throws Exception
	{
			int page = 1;
			HttpResponse hotelSummaryResponse = queryGetHotelResults(1);
			byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
			Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
			int totalPages = Integer.valueOf(xmlSummaryResults.select("hotel_data").first().select("total_pages").first().ownText());
			while (page <= totalPages)
			{
				hotelSummaryResponse = queryGetHotelResults(page);
				xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
				xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
				
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
					Map<String, Boolean> overrideMap = hotelDetailDAO.loadHotelDetailOverridesAsMapFromDB(cacheKey);
					boolean isNew = false;
					if (details == null)
					{
						details = new HotelDetail();
						details.setName(sourceHotel.getHotelName());
						details.setAddress1(detailsEl.select("address").first().ownText());
						isNew = true;
					}
					
					if (isContentRefreshable(isNew, overrideMap, "desc"))
						details.setDescription(URLDecoder.decode(detailsEl.select("description_full").first().ownText(), "UTF-8"));
					if (isContentRefreshable(isNew, overrideMap, "area_desc"))
						details.setAreaDescription(detailsEl.select("district").first().ownText());
					if (isContentRefreshable(isNew, overrideMap, "city"))
						if (details.getCity() == null)	
							details.setCity("");
					if (isContentRefreshable(isNew, overrideMap, "state"))
						if (details.getState() == null)
							details.setState("");
					if (isContentRefreshable(isNew, overrideMap, "zip"))
						if (details.getZip() == null)
							details.setZip("");
					if (isContentRefreshable(isNew, overrideMap, "lat"))
						details.setLatitude(detailsEl.select("latitude").first().ownText());
					if (isContentRefreshable(isNew, overrideMap, "long"))
						details.setLongitude(detailsEl.select("longitude").first().ownText());
					if (isContentRefreshable(isNew, overrideMap, "rating"))
						details.setRating(Float.valueOf(detailsEl.select("star_rating").first().ownText()));

					if (isContentRefreshable(isNew, overrideMap, "photos_json"))
					{
						details.clearPhotos();
						for (Element photoEl: detailsEl.select("photo_data").first().select("photo"))
						{
							Photo photo = new Photo();
							photo.url = photoEl.ownText();
							details.addPhoto(photo);
						}	
					}
				
					if (isContentRefreshable(isNew, overrideMap, "amenities_json"))
					{
						details.clearAmenities();
						Elements policies = detailsEl.select("policy_data");
						if (policies != null && policies.size() > 0)
						{
							for (Element topAmenityEl: policies.first().select("policy"))
							{
								Amenity amenity = new Amenity();
								amenity.name = topAmenityEl.select("policy_name").first().ownText();
								try
								{
									amenity.description = URLDecoder.decode(topAmenityEl.select("description").first().ownText(), "UTF-8");
								}
								catch (Exception e)
								{
									amenity.description = topAmenityEl.select("description").first().ownText();
									logger.error("unabled to decode", e);
								}
								//boolean avail = (topAmenityEl.select("allowed").first() != null);
								//amenity.description = (avail ? "Yes" : "No");
								details.addAmenity(amenity);
							}						
						}						
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
						rtd.setDescription(InventoryUtils.urlDecode(roomTypeEl.select("room_description").first().ownText()));
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
				page++;
			}
			cacheStateDAO.markHotelDetailCacheUpdated();
			cacheStateDAO.markRoomTypeCacheUpdated();
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
		
		builder
		.setScheme("http")
		.setHost("api.rezserver.com")
		.setPath("/api/hotel/getResults")
		.addParameter("refid", "5057")
		.addParameter("limit", "600")
		.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
		.addParameter("filter_add_radius", "30")
		.addParameter("page", String.valueOf(page))
		.addParameter("city_id", "3000008434");
		
		/*
		builder
			.setScheme("http")
			.setHost("api.rezserver.com")
			.setPath("/api/hotel/getStaticHotels")
			.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
			.addParameter("refid", "5057")
			.addParameter("city_id", "3000008434")
			.addParameter("airport_code", "MSY")
			.addParameter("limit", String.valueOf(500));
		*/	
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