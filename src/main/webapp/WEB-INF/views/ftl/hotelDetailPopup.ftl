
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
	<title>Royal Sonesta Hotel New Orleans French Quarter | NewOrleans.com</title>
	<meta content="Percussion CM System" name="generator"/>
	<meta name="perc_linkback" id="perc_linkback" content="FmRjZm1mZ3gHbWZ4AWRkZWV4BmNmZXgTZGNmbWVn"/>	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="google-site-verification" content="VuYBMs5Uo7jdZmib_THsIaXMPXKABWvtdqqIHTzOtl0" />
	<meta name="keywords" content="Royal Sonesta Hotel New Orleans, Royal Sonesta New Orleans hotel " />
	<meta name="description" content="Book the Royal Sonesta Hotel located on Bourbon Street in New Orleans. Get great deals, see video and photos, and discounts at this French Quarter hotel." />
	<meta name="robots" content="noydir" />
	<meta name="robots" content="noodp" />
	<meta name="language" content="English" />
	<meta http-equiv="P3P" content="CP='OTI DSP COR IND CUR ADMa DEVa TAIa PSAa PSDa IVAa IVDa HISa OUR UNI COM NAV INT CNT STA'" />
	<link rel="shortcut icon" href="http://www.neworleans.com/favicon.ico" />
	<link href="http://www.neworleans.com/style/global.css" type="text/css" rel="stylesheet" />
	<link href="http://www.neworleans.com/style/globaless.css" type="text/css" rel="stylesheet" />
	<script src="http://www.neworleans.com/common/js/jquery/jquery-core.js" type="text/javascript"></script>	
	<script src="http://www.neworleans.com/javascript/global.js" type="text/javascript"></script>

</head>

<body id="hotels">
<!--<div id="wrapper" style="width:620px">
<div id="main-content" style="width:620px">-->
	
	    <div class="hotelModal">
			
		<script src="http://www.neworleans.com/common/js/jquery/jquery-ui.js" type="text/javascript"></script>
		<script src="http://www.neworleans.com/common/js/jquery/jquery-tabs.js" type="text/javascript"></script>
		<script src="http://www.neworleans.com/common/js/jquery/jquery.galleriffic.js" type="text/javascript"></script>
		<script src="http://www.neworleans.com/common/js/jquery/jquery.opacityrollover.js" type="text/javascript"></script>
		<script src="jquery/jqModal.js" type="text/javascript"></script>
		<script type="text/javascript" src="http://www.neworleans.com/video/player-helper.js"></script>
		<script type="text/javascript" src="http://www.neworleans.com/video/swfobject.js"></script>
				<script type="text/javascript">
			/* <![CDATA[ */
			//Google Maps loading
			var lat1 = "299560";
			var long1 = "-900679";
			var lat = lat1.replace(/^(.*)(.{4})$/g, '$1.$2');
            var lng = long1.replace(/^(.*)(.{4})$/g, '$1.$2');
			var map;
			
			function initMaps() {
				$("head").append('<script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAAYa-iGWeVFh8NtPW8eVhdoRSjh285OL2u7H9w5QiyNL0Y3ue7ABSZe8mqofy9Pq36RNMSRdiOJ2O2Ug&amp;async=2&amp;callback=loadMaps"><\/script>')
			}
			function loadMaps() {
				google.load("maps", "2", {"callback": load});
			}
			function load() {
				map = new google.maps.Map2(document.getElementById("map_container"));
				map.addControl(new google.maps.LargeMapControl());
        		map.addControl(new google.maps.MapTypeControl());
        		map.addControl(new google.maps.ScaleControl());
				map.setCenter(new google.maps.LatLng(lat, lng), 15);
				var mapMarker = new google.maps.Marker(new google.maps.LatLng(lat, lng));
          		map.addOverlay(mapMarker);
			}
			function resizeMap() {
				map.checkResize();
				map.setCenter(new google.maps.LatLng(lat, lng), 15);
			}
			
			var $tabs;
			jQuery().ready(function() {
				if (typeof selectedHotelDetailTab != 'undefined')
{
if(selectedHotelDetailTab.indexOf("#map") > -1) {
selectedHotelDetailTab = "#map-163372"
}
else if(selectedHotelDetailTab.indexOf("#photos") > -1) {
selectedHotelDetailTab = "#photo-163372"
}
else if(selectedHotelDetailTab.indexOf("#video") > -1) {
selectedHotelDetailTab = "#video-163372"
}
else if(selectedHotelDetailTab.indexOf("#rooms") > -1) {
selectedHotelDetailTab = "#room-163372"
}
else if(selectedHotelDetailTab.indexOf("#rateCalendar") > -1) {
selectedHotelDetailTab = "#calendar-163372"
}
$tabs.tabs("select", selectedHotelDetailTab);
selectedHotelDetailTab = "";
}
			
				$tabs = jQuery("#tabs").tabs({show:function(event, ui) {
    				if (ui.panel.id == "map") {
						if(typeof(map) != 'undefined')
        					resizeMap();
    				}
					else if(ui.panel.id=='deals-promos'){
						Cyllenius.Widget.popupWidget('deals-promos', '/mytrip/app/PromotionWidget?productId=2802&fromPricingQueryId=16&fromPricingQueryId=11');
					}
									}});
				
				Cyllenius.Widget.popupWidget('deals-promos', '/mytrip/app/PromotionWidget?productId=2802&fromPricingQueryId=16&fromPricingQueryId=11');
				Cyllenius.Widget['deals-promos'] = {onAfterDynamicLoad : function(){if(!$('.no-promos').length)$tabs.tabs('select','#deals-promos');}};
				
				jQuery("#tabs, #tabs ul, #tabs li, #tabs a, #tabs div").each(function(index, doElem) {
					jQuery(doElem).removeClass('ui-tabs ui-widget ui-widget-content ui-corner-all ui-state-default ui-corner-top ui-tabs-panel ui-corner-bottom ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header');
				});
								// We only want these styles applied when javascript is enabled
				$('div.navigation').css({'width' : '210px', 'float' : 'left', 'padding-left' : '10px'});
				$('div.photo_content').css('display', 'block');

				// Initially set opacity on thumbs and add
				// additional styling for hover effect on thumbs
				var onMouseOutOpacity = 0.67;
				$('#thumbs ul.thumbs li').opacityrollover({
					mouseOutOpacity:   onMouseOutOpacity,
					mouseOverOpacity:  1.0,
					fadeSpeed:         'fast',
					exemptionSelector: '.selected'
				});
				
				// Initialize Advanced Galleriffic Gallery
				var gallery = $('#thumbs').galleriffic({
					delay:                     2500,
					numThumbs:                 6,
					preloadAhead:              10,
					enableTopPager:            true,
					enableBottomPager:         false,
					maxPagesToShow:            7,
					imageContainerSel:         '#slideshow',
					controlsContainerSel:      '#controls',
					loadingContainerSel:       '#loading',
					renderSSControls:          false,
					renderNavControls:         true,
					playLinkText:              '',
					pauseLinkText:             '',
					prevLinkText:              '&lsaquo; Previous',
					nextLinkText:              'Next &rsaquo;',
					nextPageLinkText:          'Next &rsaquo;',
					prevPageLinkText:          '&lsaquo; Previous',
					enableHistory:             false,
					autoStart:                 false,
					syncTransitions:           true,
					defaultTransitionDuration: 0,
					onSlideChange:             function(prevIndex, nextIndex) {
						// 'this' refers to the gallery, which is an extension of $('#thumbs')
						this.find('ul.thumbs').children()
							.eq(prevIndex).fadeTo('fast', onMouseOutOpacity).end()
							.eq(nextIndex).fadeTo('fast', 1.0);
					}
				});
												$('#videoModal').jqm({overlay: 20, toTop: true});
												initMaps();
							});
			
			

			/* ]]> */
		</script>
		<div class="resultsDescription">
        <a href="#" id="id592" class="closeWindow jqmClose"> 
		<img width="80" height="25" border="0" alt="" src="http://www.neworleans.com/mytrip/images/btn_pop_close.gif"> 
		</a>

		<div class="description">
			<div class="productThumb">
				<img src="http://www.neworleans.com/new-orleans-hotels/le-pavillon/med_main.jpg" alt="Royal Sonesta Hotel " class="productThumb" />
			</div>
			<div class="productSummary">
    			<h1 class="fn org hotel-title">Royal Sonesta Hotel</h1>
				<div class="rating productStars"><img src="http://www.neworleans.com/images/star-rating-4_5.png" alt="4.50" /></div>
				<div class="adr">
					<div class="address1 street-address">300 Bourbon St.</div>
					<div class="address2 extended-address"></div>
					<div class="cityStatePC"><span class="locality">New Orleans</span>, <span class="region">LA</span> <span class="postal-code">70130</span></div>
                    <div class="tel">NewOrleans.com Reservation Hotline: 1-855-639-6756</div>
				</div>
                <p>The only 4.5-star hotel on Bourbon Street, the Royal Sonesta Hotel in the French Quarter delivers an unforgettable New Orleans experience. </p>
			</div>
			<div class="clear"></div>
		</div>
		<!---des-->
		</div>
		<div class="productWrap">
		<div class="adBlock"></div>
        <div class="clear"></div>
		<div id="destinations">

    		<div id="tabs" class="tab-row" style="width:668px;border-left-width: 0;border-right-width: 0;">
    			<ul class="tabList">
                	<li><a href="#rooms"><span>Rooms</span></a></li>
					<li><a href="#description"><span>Description</span></a></li>
    				<li><a href="#hotelDetails"><span>Hotel Details</span></a></li>
					<li><a href="#map"><span>Map</span></a></li>
   					<li><a href="#photos"><span>Photos</span></a></li>
					<li><a href="#video"><span>Video</span></a></li>
					<li><a href="#deals-promos"><span>Deals</span></a></li>    				
                </ul>
    			<div class="clear"></div>
				<div id="rooms" class="roomsTab tabbed-item">
    				<h2>Room Descriptions</h2>
    				
		    <div class="tabRoom" id="11658">
    		<div class="tabText">
        		<h4>Deluxe Double</h4>
				        		
		<img class="photo previewThumb productThumb" src="/room-details/images/RS_deluxe-double.jpg" alt="Royal Sonesta Deluxe Double" height='188' width='250' />
									<p>This spacious <b>Royal Sonesta</b> room has <b>two double beds</b> that come with pillow-top mattresses. The d꤯r is elegant and features antique-reproduction art and furnishings. There is a view of either the street or inner courtyard. There is a satellite TV as well as a table and two chairs. Coffeemakers can be requested and are subject to availability.</p><p>The marble bathrooms have <b>a combined shower/bathtub</b> and designer toiletries.</p>				<strong>Amenities Include:</strong> 
													Air conditioning,									Iron / Ironing board,									In-room safe,									Mini bar,									Alarm clock,									Climate control,									Complimentary newspaper,									Rollaway bed (On request),									Crib (On request),									Daily maid service,									Turndown service,									High-Speed Internet access (Complimentary),									Wireless Internet access (Complimentary),									Hair dryer,									Combined shower and bathtub,									Complimentary toiletries,									Television,									Cable / Satellite television channels,									Radio,									CD player,									Video game console and games,									Multi-line telephone,									Voice Mail,									Wake-up Calls,									Pillowtop mattresses,									Premium bedding				    		</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="tabRoom" id="11657">
    		<div class="tabText">
        		<h4>Deluxe King</h4>
				        		
		<img class="photo previewThumb productThumb" src="/room-details/images/RS_deluxe.jpg" alt="Royal Sonesta Deluxe" height='188' width='250' />
									<p>This spacious <b>Royal Sonesta</b> room has <b>one king bed</b> that comes with a pillow-top mattress. The d꤯r is elegant and features antique-reproduction art and furnishings. There is a view of either the street or inner courtyard. There is a satellite TV as well as a table and two chairs. Coffeemakers can be requested and are subject to availability.</p><p>The marble bathrooms have <b>a combined shower/bathtub</b> and designer toiletries.</p>				<strong>Amenities Include:</strong> 
													Air conditioning,									Iron / Ironing board,									In-room safe,									Mini bar,									Alarm clock,									Climate control,									Complimentary newspaper,									Rollaway bed (On request),									Crib (On request),									Daily maid service,									Turndown service,									High-Speed Internet access (Complimentary),									Wireless Internet access (Complimentary),									Hair dryer,									Combined shower and bathtub,									Complimentary toiletries,									Television,									Cable / Satellite television channels,									Radio,									CD player,									Video game console and games,									Multi-line telephone,									Voice Mail,									Wake-up Calls,									Pillowtop mattresses,									Premium bedding				    		</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="tabRoom" id="11659">
    		<div class="tabText">
        		<h4>Standard</h4>
				        		
		<img class="photo previewThumb productThumb" src="/room-details/images/RS_standard.jpg" alt="Royal Sonesta Standard " height='188' width='250' />
									<p>This <b>Royal Sonesta</b> room has one king, one queen or two double beds. The d꤯r is elegant and features antique-reproduction art and furnishings. There is a view from the windows of either the street or inner courtyard. Guests will enjoy a satellite TV. Coffeemakers can be requested and are subject to availability.</p><p>The marble bathrooms have <b>a combined shower/bathtub</b>.</p>				<strong>Amenities Include:</strong> 
													Air conditioning,									Iron / Ironing board,									In-room safe,									Alarm clock,									Climate control,									Complimentary newspaper,									Daily maid service,									Turndown service,									Window opens,									High-Speed Internet access (Complimentary),									Wireless Internet access (Complimentary),									Hair dryer,									Combined shower and bathtub,									Complimentary toiletries,									Television,									Cable / Satellite television channels,									Radio,									CD player,									Video game console and games,									Multi-line telephone,									Voice Mail,									Wake-up Calls,									Premium bedding				    		</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="tabRoom" id="12528">
    		<div class="tabText">
        		<h4>R-Club Concierge Accommodations</h4>
				        		
		<img class="photo previewThumb productThumb" src="/room-details/images/RS_deluxe.jpg" alt="Royal Sonesta Deluxe" height='188' width='250' />
									<p>The decor in this <b>Royal Sonesta</b> room reflects traditional French Quarter design. Featuring <b>one king bed or two double beds</b> with pillowtop mattresses and a seating area with two chairs and a small table. Along with R-Club Concierge Accommodations, this room has daily maid service, turndown service and wake-up calls.</p><p>The bathroom has a <b>combined shower and tub</b>.</p><p>R-Club guests enjoy:</p><p>In the Private Club Lounge:</p><p>ՠ24-hour concierge service<span class="Apple-converted-space"> <br />ՠBreakfast beginning at 6:00 a.m. <br />ՠAfternoon coffee and tea service accompanied by fresh baked goods <br />ՠCocktail and hors d'oeuvres offered from 5:00 p.m. to 8:00 p.m. <br />ՠAssortment of magazines and daily newspapers for your reading pleasure <br />ՠWireless Internet <br />ՠiMac guest computer <br />ՠ52-inch flat-screen television </span></p><p><span class="Apple-converted-space">In the room</span>:<span class="Apple-converted-space"> </span></p><p>ՠFresh flowers in-room daily<span class="Apple-converted-space"> <br />ՠLuxurious bed and bath linens <br />ՠDeluxe robes and bath amenities <br />ՠPersonal in-room safe <br />ՠComplimentary newspaper daily <br />ՠMorning and evening housekeeping service <br />ՠComplimentary bottled water </span></p>				<strong>Amenities Include:</strong> 
													Air conditioning,									Complimentary breakfast,									Iron / Ironing board,									In-room safe,									Alarm clock,									Complimentary newspaper,									Complimentary bottled water,									Robes,									Slippers,									Daily maid service,									Turndown service,									Wireless Internet access (Complimentary),									Hair dryer,									Combined shower and bathtub,									Complimentary toiletries,									Flatscreen television,									Wake-up Calls,									Premium bedding				    		</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="tabRoom" id="12602">
    		<div class="tabText">
        		<h4>Conti/Bienville Street Balcony</h4>
				        		
		<img class="photo previewThumb productThumb" src="/room-details/images/royalsonestabalcony.jpg" alt="Royal Sonesta Conti / Bienville Balcony Room" height='188' width='250' />
									<p>The decor in this <b>Royal Sonesta</b> room reflects a traditional French Quarter design and features <b>one king bed</b> with pillowtop mattresses and king-size pillows. Guests will enjoy a wrought iron balcony overlooking either Conti or Bienville Street. Please note: This room does NOT overlook Bourbon Street. There is a desk and chair plus a seating area with two chairs and a small table.</p><p>The marble bathroom has a <b>combined shower and tub</b>.</p>				<strong>Amenities Include:</strong> 
													Air conditioning,									Iron / Ironing board,									In-room safe,									Alarm clock,									Climate control,									Complimentary newspaper,									Daily maid service,									Turndown service,									Window opens,									High-Speed Internet access (Complimentary),									Wireless Internet access (Complimentary),									Hair dryer,									Combined shower and bathtub,									Complimentary toiletries,									Television,									Pay-Per-View movies,									Radio,									CD player,									Video game console and games,									Voice Mail,									Wake-up Calls,									Pillowtop mattresses,									Premium bedding				    		</div>
			
			<div class="clear"></div>
		</div>
	    			</div> <!-- /roomsTab -->
				<div id="description" class="expertTab tabbed-item">
    				<h2>Description</h2>
    				<p>The <b>Royal Sonesta Hotel</b> is the premiere French Quarter hotel on Bourbon Street ֠a premium hotel with 4.5-star accommodations and a staff well versed in the art of Southern hospitality. At the epicenter of Bourbon Street, the Royal Sonesta Hotel in the French Quarter puts you smack in the middle of what's happening in New Orleans, from the madness of New Years' Eve and Mardi Gras to the everyday revelry and culture that makes the French Quarter one of the most unique neighborhoods in the world.</p><p>There's a reason New Orleans locals make the <b>Royal Sonesta</b> their home away from home during big events like Mardi Gras and New Year's Eve: the Old World elegance and charm of this quintessentially New Orleans hotel, topped by the Royal Sonesta's unquestionably hip vibe. See: Irvin Mayfield's Jazz Playhouse, the swanky lounge inside the Royal Sonesta. You'll hear live music (at no charge) seven nights a week here (including sets with the Grammy award-winning Mr. Mayfield himself); the Saturday brass band set followed by the Burlesque Ballroom midnight show is one of the favorite weekly gigs in New Orleans.</p><p>And did we mention the Royal Sonesta's famous wrought iron balconies? The balconies at the Royal Sonesta Hotel offer a birds-eye view of the world-class spectacle on Bourbon Street. But make no mistake ֠the Royal Sonesta is a top French Quarter hotel, offering travelers both all-access to the nightlife in New Orleans as well as a quiet oasis to enjoy and recover from it. The Royal Sonesta's rooftop pool is a destination in itself, offering stunning views of the French Quarter.</p><p>No visit to New Orleans is complete without trying jambalaya, red beans and rice, or a roast beef po-boy, and the Royal Sonesta's Desire Bistro &amp; Oyster Bar serves New Orleans classics more for breakfast, lunch and dinner. Our favorite seat in the house? At the curved raw bar, tossing back a raw dozen or two oysters.</p><p>Opened in June 2012, Restaurant R'evolution inside the Royal Sonesta hotel is one of the most ambitious new restaurants New Orleans has seen in years. The multi-faceted restaurant is the work of Louisiana uber-chef John Folse and former Chicago restaurateur/chef Rick Tramonto, and offers diners imaginative and historic interpretations of classic Creole and Cajun cuisine.</p><p> </p><p><i>Hotel tags: New Orleans hotel on Bourbon Street, hotels in the French Quarter, Restaurant R'Evolution, Irvin Mayfield's Jazz Playhouse, best New Orleans hotels, rooftop pool</i></p>    			</div> <!-- /description -->
    			<div id="hotelDetails" class="hotelDetailsTab tabbed-item">
					<h2>Hotel Details</h2>
																																																												
							
							
							
									
							
									
									
									
									
									
									
									
									
									
									
									
									
									
									
									
									
									
									
									
							
									
							
									
									
									
									
									
									
									
									
							
									
							
							
									
									
									
									
									
									
				    		    						
				<div style="width: 293px; float: left;">
				<div class="feature-container"><strong class="feature-name">Number of Rooms:</strong> <p>483.</p></div>
					<div class="feature-container"><strong class="feature-name">Casino:</strong> <p>No.</p></div>
					<div class="feature-container"><strong class="feature-name">Restaurants:</strong> <p>Desire Bistro &amp; Oyster Bar is a casual street bistro and one of the most famous restaurants in New Orleans, featuring fresh Louisiana seafood. Open daily for breakfast, lunch and dinner.  Restaurant Rӥvolution, set to open in March 2012, created by world-renowned chefs John Folse and Rick Tramonto.</p></div>
					<div class="feature-container"><strong class="feature-name">Entertainment:</strong> <p>Irvin Mayfield's Jazz Playhouse, a world-class music destination with live Jazz 7 nights a week. Nightly performances begin at 8:00 p.m. and on Sundays at 7:00 p.m.</p></div>
					<div class="feature-container"><strong class="feature-name">Nightlife:</strong> <p>Irvin Mayfield's Jazz Playhouse, Le Booze, overlooks Bourbon St, is one of the best spots that locals like to meet and greet on Bourbon Street. Open Monday - Thursday, 2:00 p.m. - until morning, and Friday - Sunday, 12:00 p.m. - until morning. Daiquiri Delight Shop.</p></div>
					<div class="feature-container"><strong class="feature-name">Attractions:</strong> <p>French Quarter, Jackson Square, French Market, Jackson Brewery, Riverwalk, Aquarium of the Americas, Audubon Zoo, Insectarium, Harrahӳ Casino, Louisiana State Museum, Mardi Gras World, Preservation Hall, National WWII Museum, New Orleans Museum of Art, Garden District and many more. Plus the many bistros, boutiques, antique shops, art galleries, historic sites, live entertainment that are all within walking distance.</p></div>
					<div class="feature-container"><strong class="feature-name">Pool:</strong> <p>Heated outdoor pool located on the third-floor with a deck that overlooks the tropical courtyard.</p></div>
					<div class="feature-container"><strong class="feature-name">Health Club or Fitness Equipment:</strong> <p>Cybex training equipment is offered on the second-floor fitness room.</p></div>
					<div class="feature-container"><strong class="feature-name">Tennis Courts:</strong> <p>No.</p></div>
					<div class="feature-container"><strong class="feature-name">Spa:</strong> <p>Spa services are available poolside seasonally and in-room by request.</p></div>
					<div class="feature-container"><strong class="feature-name">Shops:</strong> <p><b>:</b>  Located in PJӳ Coffee Caf鬠the gift shop has souvenirs, cookbooks and logo wear.</p></div>
					<div class="feature-container"><strong class="feature-name">Wedding Services:</strong> <p>Can design weddings and receptions from small, intimate occasions to the most lavish affair.  Wedding planners can create uniquely personal events, from bridal showers and rehearsal dinners to bachelor or bachelorette parties.</p></div>
					<div class="feature-container"><strong class="feature-name">Business Center:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Convention Rooms:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Concierge:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Child-care Facilities:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Room Service:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Maid service:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Luggage service:</strong> <p>Yes.</p></div>
		</div><div style="width: 293px; float: right;">			<div class="feature-container"><strong class="feature-name">Pets Allowed:</strong> Yes. Domesticated Pets under 30 lb, one time fee of $ 75.00</div>
					<div class="feature-container"><strong class="feature-name">In-Room Internet Access:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Complimentary Breakfast:</strong> <p>No.</p></div>
					<div class="feature-container"><strong class="feature-name">24-hour Front Desk:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Doorman:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Security:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Currency Exchange:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Elevators:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Safe-deposit boxes:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Parking Garage:</strong> <p>Yes. Parking rates at $36.00 per night.</p></div>
					<div class="feature-container"><strong class="feature-name">Free  Parking:</strong> <p>No.</p></div>
					<div class="feature-container"><strong class="feature-name">Shuttle Service to the airport:</strong> <p>No.</p></div>
					<div class="feature-container"><strong class="feature-name">Wheelchair Accessible Rooms:</strong> <p>Yes.</p></div>
					<div class="feature-container"><strong class="feature-name">Check-in Time:</strong> <p>3 p.m.</p></div>
					<div class="feature-container"><strong class="feature-name">Check-out Time:</strong> <p>Noon.</p></div>
					<div class="feature-container"><strong class="feature-name">Minimum Age to Reserve a Room:</strong> <p>21.</p></div>
					<div class="feature-container"><strong class="feature-name">Policies:</strong> <p>The maximum total number of guests (adults and children) in a room is four. The maximum number of guests in a multi-room suite varies.</p></div>
					<div class="feature-container"><strong class="feature-name">Incidental Charges:</strong> <p>The hotel requires a credit card for incidental charges.   </p></div>
					<div class="feature-container resort-fee"><strong class="feature-name">Resort Fee:</strong> Royal Sonesta Hotel has a $30.00 per room, per night resort fee. Tax may or may not be included. This fee is not included in your reservation and will be collected by the hotel at check-in.<br />The resort fee includes:<p>An additional $30 per room, per night, will be charged to all guests who stay during Mardi Gras (2/6/13 - 2/12/13).</p></div>
			
    </div>	
					<div class="clear"></div>
				</div> <!-- /hotelDetails -->
    			
    			
				<div id="map" class="mapTab tabbed-item">
					<h2>Map</h2>
					<div id="map_container" style="height: 350px; width: 600px;"></div>
				</div> <!-- /mapTab -->
    			    			<div id="photos" class="photosTab tabbed-item">
					
		<!-- module 164201 -->
		<div id="gallery" class="photo_content">
			<div id="controls" class="controls"></div>
			<div class="slideshow-container">
				<div id="loading" class="loader"></div>
				<div id="slideshow" class="slideshow"></div>
			</div>
			<div id="caption" class="caption-container"></div>
		</div>
		<div id="thumbs" class="navigation">
			<ul class='thumbs noscript'><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta1.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta1.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta2.JPG" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta2.JPG" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta3.JPG" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta3.JPG" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta4.JPG" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta4.JPG" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta5.JPG" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta5.JPG" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/RoyalSenesta6.JPG" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/RoyalSenesta6.JPG" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/lobby.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/lobby.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/dining.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/dining.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/sittingarea.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/sittingarea.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/double.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/double.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/room.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/room.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/room2.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/room2.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/room3.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/room3.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/room4.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/room4.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/suite.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/suite.jpg" alt="Royal Sonesta" />
</a></li><li><a class="thumb" href="/slideshows/hotels/royal-sonesta/suite2.jpg" title="Royal Sonesta">
	<img src="/slideshows/hotels/royal-sonesta/suite2.jpg" alt="Royal Sonesta" />
</a></li></ul>		</div>
		<div class="clear"></div>
		<!-- end module 164201 -->
					</div> <!-- /photosTab -->
				    							<div id="video" class="videoTab tabbed-item">
					<h2>Video</h2>
					<script type="text/javascript">
						var playlist = new Object();
						var playerParams = {
                        	version: "9.0.115",
                        	url: "/video/player.swf",
                        	placeholder: 'videoBox',
                        	popup: true,
        					modalWindow: '#videoModal'
            			};
					</script>
					
		<!-- start video royal_sonesta_2_v4_1Mbps -->
		<script type="text/javascript">
			playlist['royal_sonesta_2_v4_1Mbps'] = {
				streamer: 'rtmp://fms.6EF8.edgecastcdn.net/006EF8/neworleans/hotels/',
				file: 'royal_sonesta_2_v4_1Mbps',
				type: 'rtmp',
				image: '',
				autostart: true,
				title: 'Royal Sonesta',
				height: '360',
				width: '640',
				ga: false
			}
		</script>
		<a href="javascript:void('royal_sonesta_2_v4_1Mbps')" onclick="videoPlayer('PLAY',playlist['royal_sonesta_2_v4_1Mbps']); return false;">
    		<div style="padding: 10px;">
    			
		
		<div class="playImage" style="background-image: url(/video/hotels/royal-sonesta/royal-sonesta.jpg); height: ">
			<div class="playImageThumb"></div>
		</div>
	    			<div class="videoLink">Royal Sonesta</div>
    		</div>
		</a> <!-- end video royal_sonesta_2_v4_1Mbps -->
						<div class="jqmWindow" id="videoModal">
                    	<div class="topRoundCorner">
                      	<div class="roundCorner1 roundCorner"></div>
                        <div class="roundCorner2 roundCorner"></div>
                        <div class="roundCorner3 roundCorner"></div>
                        <div class="roundCorner4 roundCorner"></div>
                        <div class="roundCorner5 roundCorner"></div>
                      </div>
                      <div class="videoModalContent" style="">
                      	<a href="#" style="color: #FFFFFF; text-decoration: none; float: right;" class="jqmClose">Close Window X</a>
                      	<div class="clear"></div>
                      	<div id="videoBox"></div>
                      </div>
                      <div class="bottomRoundCorner">
                      	<div class="roundCorner5 roundCorner"></div>
                        <div class="roundCorner4 roundCorner"></div>
                        <div class="roundCorner3 roundCorner"></div>
                        <div class="roundCorner2 roundCorner"></div>
                        <div class="roundCorner1 roundCorner"></div>
                      </div>
                    </div>
				</div> <!-- /videoTab -->
												<div id="deals-promos" class="tabbed-item"></div>
			</div>
		</div>
		
		<div class="clear"></div>  
		<p class="close jqmClose">
<a onclick="var wcall=wicketAjaxGet('?wicket:interface=:18:browseItems:browseableProducts:1:itemContainer:hotelDetailPopupWin:content:closeWindow::IBehaviorListener:0:-1',function() { }.bind(this),function() { }.bind(this), function() {return Wicket.$('id592') != null;}.bind(this));return !wcall;" href="#" id="id592">Close Window
</a>
</p>
		    	<script language="javascript" src="/javascript/coremetrics/v40/eluminate.js" type="text/javascript"></script>
    	<script language="javascript" src="/javascript/coremetrics/cmcustom.js" type="text/javascript"></script>
    	<script language="javascript" src="/javascript/coremetrics/cmxCustomGlobal.js" type="text/javascript"></script>
		<script type="text/javascript">
        	var cmJv="1.5";
        
        	//setup variables for cmx calls
        	var cmxCatID = "Royal Sonesta";
        	
			cmxCreateCookie("cmxcategory",'"' + cmxCatID + '"');
        	if (CMONOFF == "ON") {
        		//if category cookie is set, over-ride this page ID
        		if(cmxReadCookie("USECATEGORYID") == "FALSE") { cmxCatID = cmxReadCookie("CATEGORYID") };
        
        		var  cmxPropertyCode = "2802",
        			 cmxPropertyName = "Royal Sonesta Hotel",
        			 cmxHotelBrand = "Royal Sonesta",
        			 cmxPageType = "Hotel View",
        			 cmxHotelZip = "70130",
					 cmxHotelCity = "New Orleans",
        			 cmxHotelCityLocation = "New Orleans | French Quarter",
					 cmxHotelState = "LA",
					 cmxHotelCountry = "US",
        			 cmxProductType = "Hotel",
        			 cmxHotelRating = "4.50",
					 cmxSiteCountry = "en-us",
					 cmxLanguage = "en-us";
        		//Create attribute list
				var  attributes = cmxPropertyName + "-_-" 
							  + cmxHotelCityLocation + "-_-" 
							  + cmxHotelRating + "-_-" 
							  + cmxHotelBrand + "-_-"
							  + cmxPropertyCode + "-_-"
							  + cmxProductType + "-_-"
							  + cmxPageType
        		//execute cmx calls using above variables
        		cmCreateHotelViewTag(stripAccents(cmxPropertyCode),
        								stripAccents(cmxPropertyName),
        								stripAccents(cmxCatID),
										stripAccents(cmxHotelBrand),
										"",
										stripAccents(cmxSiteCountry),
										stripAccents(cmxLanguage),
										stripAccents(cmxHotelZip),
										stripAccents(cmxHotelCity),
										stripAccents(cmxHotelState),
										stripAccents(cmxHotelCountry),
										stripAccents(cmxHotelRating),
        								stripAccents(attributes));
        
        		//execute section specific URL event attachment
        		cmxExtractHrefs();
        	}
			
			function stripAccents(r) {
                r = r.replace(new RegExp("[ᢢ坢, 'g'),"a");
				r = r.replace(new RegExp("[AÄ]", 'g'),"A");
                r = r.replace(new RegExp("[骪읢, 'g'),"e");
				r = r.replace(new RegExp("[ɉˋ]", 'g'),"E");
                r = r.replace(new RegExp("[�ݢ, 'g'),"i");
				r = r.replace(new RegExp("[͍Ϗ]", 'g'),"I");
                r = r.replace(new RegExp("񢬠'g'),"n");
				r = r.replace(new RegExp("Ѣ, 'g'),"N");
                r = r.replace(new RegExp("[򳴶]", 'g'),"o");
				r = r.replace(new RegExp("[ӓՖ]", 'g'),"O");
                r = r.replace(new RegExp("[񺻼]", 'g'),"u");
				r = r.replace(new RegExp("[ښܜ]", 'g'),"u");
                r = r.replace(new RegExp("[��, 'g'),"y");
				r = r.replace(new RegExp("[ޝ", 'g'),"Y");
				return r;
			}
    	</script>
	    </div>
    <div class="clear"></div>  
 <!-- </div> <!-- /main-content -->
<!--</div>-->
  
        




		
	
        
	
	
				

<!-- /Matchflow tags -->
</body>
</html>
