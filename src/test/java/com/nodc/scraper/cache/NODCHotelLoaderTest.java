package com.nodc.scraper.cache;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;
import com.nodc.scraper.dao.MasterHotelDAO;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NODCHotelLoaderTest extends BaseSpringJUnitTest
{
	@Autowired
	private NODCHotelLoader hotelLoader;
	
	@Test
	public void testLoadCache() throws Exception
	{
		hotelLoader.loadCache();
	}
	
	@Test
	@Ignore
	public void testLoadWeights() throws Exception
	{
		List<MasterHotel> allHotels = Lists.newArrayList();
		
		
		Map<String, Integer> weights = Maps.newHashMap();
		weights.put("Hotel Monteleone",1);
		weights.put("The Westin New Orleans Canal Place",2);
		weights.put("Astor Crwone Plaza",3);
		weights.put("The Cotton Exchange Hotel",4);
		weights.put("Maison Dupuy Hotel",5);
		weights.put("New Orleans Marriott",6);
		weights.put("Hyatt Regency New Orleans",7);
		weights.put("JW Marriott New Orleans",8);
		weights.put("Queen and Crescent Hotel",9);
		weights.put("Pelham Hotel",10);
		weights.put("Hyatt French Quarter",11);
		weights.put("Hilton Riverside",12);
		weights.put("Le Richelieu Hotel in the French Quarter",13);
		weights.put("Ambassador Hotel",14);
		weights.put("Bienville House",15);
		weights.put("The Ritz-Carlton New Orleans",16);
		weights.put("Holiday Inn French Quarter",17);
		weights.put("Le Pavillon Hotel",18);
		weights.put("Hotel Mazarin",19);
		weights.put("The St. James Hotel",20);
		weights.put("Royal Sonesta",21);
		weights.put("Royal St Charles",22);
		weights.put("Wyndham Garden Baronne Plaza",23);
		weights.put("Hamption Inn Downtown / French Quarter",24);
		weights.put("Intercontinental New Orleans",25);
		weights.put("Blake Hotel New Orleans",26);
		weights.put("Holiday Inn Chateau Lemoyne ",27);
		weights.put("Hotel Le Marais",28);
		weights.put("Embassy Suites New Orleans Convention",29);
		weights.put("The Chateau Dupre",30);
		weights.put("Courtyard By Marriott New Orleans Downtown",31);
		weights.put("International House Hotel",32);
		weights.put("Hilton Garden Inn New Orleans French Quarter/CBD",33);
		weights.put("Wyndham Riverfront",34);
		weights.put("Country Inn & Suites New Orleans French Quarter",35);
		weights.put("Dauphine Orleans",36);
		weights.put("Hyatt Place New Orleans Convention Center",37);
		weights.put("Creole Gardens",38);
		weights.put("Hampton Inn and Suites New Orleans Convention Center",39);
		weights.put("Staybridge Suites",40);
		weights.put("Hilton St. Charles",41);
		weights.put("O'Keefe Plaza Hotel",42);
		weights.put("Hotel Royal",43);
		weights.put("Bourbon Orleans",44);
		weights.put("Best Western Plus Landmark French Quarter Hotel",45);
		weights.put("The Hotel Modern New Orleans",46);
		weights.put("The Saint Hotel, Autograph Collection",47);
		weights.put("Place d'Armes Hotel",48);
		weights.put("Homewood Suites by Hilton New Orleans",49);
		weights.put("W New Orleans, Poydras",50);
		weights.put("Quality Inn & Suites New Orleans",51);
		weights.put("Four Points by Sheraton French Quarter",52);
		weights.put("New Orleans Downtown Marriott at the Convention Center",53);
		weights.put("Hampton Inn New Orleans / St. Charles Ave.",54);
		weights.put("Andrew Jackson Hotel",55);
		weights.put("Hotel Provincial",56);
		weights.put("Drury Inn and Suites New Orleans",57);
		weights.put("Prince Conti Hotel",58);
		weights.put("Hilton New Orleans Airport",59);
		weights.put("Windsor Court",60);
		weights.put("Hotel Indigo",61);
		weights.put("Hotel St. Helene",62);
		weights.put("Holiday Inn Express Harvey",63);
		weights.put("Best Western Plus St. Christopher Hotel",64);
		weights.put("DoubleTree Hotel New Orleans",65);
		weights.put("St. Pierre Hotel",66);
		weights.put("W New Orleans, French Quarter",67);
		weights.put("Maison St. Charles",68);
		weights.put("Hotel St. Marie",69);
		weights.put("French Market Inn",70);
		weights.put("The Roosevelt New Orleans A Waldorf Astoria Hotel",71);
		weights.put("Avenue Plaza Resort",72);
		weights.put("Best Western Bayou Inn",73);
		weights.put("Days Inn New Orleans Airport",74);
		weights.put("Federal City Inn & Suites",75);
		weights.put("Best Western Plus St. Charles Inn",76);
		weights.put("Wyndham Vr La Belle Maison",77);
		weights.put("French Quarter Suites Hotel",78);
		weights.put("New Orleans Courtyard Hotel",79);
		weights.put("Loews New Orleans Hotel",80);
		weights.put("Four Points By Sheraton",81);
		weights.put("Ramada Metairie",82);
		weights.put("Holiday Inn West Bank Tower",83);
		weights.put("The Lafayette Hotel",84);
		weights.put("Sheraton New Orleans Hotel",85);
		weights.put("Melrose Mansion",86);
		weights.put("Best Western Plus Landmark Hotel",87);
		weights.put("Clarion Grand Boutique Hotel",88);
		weights.put("Inn on St. Peter",89);
		weights.put("Olde Town Inn New Orleans",90);
		weights.put("Best Western Avalon Hotel",91);
		weights.put("Clarion Inn & Suites New Orleans",92);
		weights.put("Plaza Resort New Orleans",93);
		weights.put("Harrah's New Orleans",94);
		weights.put("Holiday Inn Superdome",95);
		weights.put("Renaissance New Orleans Arts Hotel",96);
		weights.put("The Whitney a Wyndham Historic Hotel",97);
		weights.put("Renaissance New Orleans Pere Marquette Hotel",98);
		weights.put("Omni Royal Crescent Hotel",99);
		weights.put("Chateau Hotel",100);
		weights.put("Avenue Inn Bed and Breakfast",101);
		weights.put("Omni Royal Orleans Hotel",102);
		weights.put("Hilton Garden Inn New Orleans Convention Center",103);
		weights.put("Courtyard New Orleans Convention Center",104);
		weights.put("La Quinta Inn and Suites New Orleans French Quarter",105);
		weights.put("SpringHill Suites New Orleans Convention Center",106);
		weights.put("Quality Inn & Suites New Orleans Lakefront",107);
		weights.put("Brent House Hotel",108);
		weights.put("Residence Inn New Orleans Downtown",109);
		weights.put("Sleep Inn New Orleans Airport",110);
		weights.put("The Prytania Park Hotel",111);
		weights.put("Crowne Plaza Airport",112);
		weights.put("The Burgundy Bed and Breakfast",113);
		weights.put("Lamothe House Hotel",114);
		weights.put("Inn on Saint Ann",115);
		weights.put("Inn on Ursulines",116);
		weights.put("Nine-O-Five Royal Hotel",117);
		weights.put("Frenchman Orleans 519",118);
		weights.put("La Belle Maison",119);
		weights.put("Bluegreen Club La Pension",120);
		weights.put("The Prytaina Oaks",121);
		weights.put("Hotel de L'eau Vive",122);
		weights.put("Soniat House",123);
		weights.put("The Queen Anne",124);
		weights.put("Audubon Cottages",125);
		weights.put("Loft 523",126);
		weights.put("Super 8 Metairie",127);
		weights.put("Five Continents Bed and Breakfast",128);
		weights.put("Quality Inn & Suites Westbank",129);
		weights.put("Elysian Fields Inn - Bed and Breakfast",130);
		weights.put("Best Western Plus Westbank",131);
		weights.put("Econolodge New Orleans",132);
		weights.put("Holiday Inn Express New Orleans East",133);
		weights.put("Days Hotel, Metairie",134);
		weights.put("Comfort Suites New Orleans",135);
		weights.put("La Quinta Inn New Orleans East",136);
		weights.put("Travel Best Inn",137);
		weights.put("Maison Perrier Bed & Breakfast",138);
		weights.put("Quality Inn & Suites Gretna",139);
		weights.put("Super 8 New Orleans",140);
		weights.put("Maison de Macarty",141);
		weights.put("Empress Hotel",142);
		weights.put("HH Whitney House - A Bed and Breakfast on the Historic Esplanade",143);
		weights.put("The Green House Inn",144);
		weights.put("Fairchild House",145);
		weights.put("St. Phillip French Quarter Apartments",146);
		weights.put("Motel 6 New Orleans - Near Downtown",147);
		weights.put("Flo and Joe Guest House",148);
		weights.put("The Midtown Hotel",149);
		weights.put("Budget Lodge",150);
		weights.put("AAE Bourbon House Mansion",151);
		weights.put("New Orleans Guest House",152);
		weights.put("Relax Inn and Suites New Orleans",153);
		weights.put("Annabelles House Bed and Breakfast",154);
		
		hotelLoader.updateWeights(weights);
	}
}
