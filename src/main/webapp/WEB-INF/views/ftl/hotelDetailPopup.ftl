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
			var lat="";
			var lng="";
			var empty="empty";
			lat = ${hotelDetail.latitude!'empty'};
			lng = ${hotelDetail.longitude!'empty'};
			
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
				
				
				var infowindow = new google.maps.InfoWindow({
				content: ""
				});

				latLng = new google.maps.LatLng(lat, lng)
				var myOptions = {
				zoom: 14,
				center: latLng,
				mapTypeId: google.maps.MapTypeId.ROADMAP
				}
				
				var marker = new google.maps.Marker({
					position: latLng,
					map: map,
					title:""
				});
				infoWindow.open(map, marker);

				google.maps.event.addListener(marker, 'click', function() {
				infowindow.open(map,marker);
				});
				
			}
			function resizeMap() {
				map.checkResize();
				map.setCenter(new google.maps.LatLng(lat, lng), 15);
			}
			
			var $tabs;
			jQuery().ready(function() {
			if(lat==empty || lng==empty){

			}else{
						initMaps();
			}
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
				cyljq.getScript('http://www.neworleans.com/common/js/jquery/jquery.opacityrollover.js',function(){

				$('#thumbs ul.thumbs li').opacityrollover({
					mouseOutOpacity:   onMouseOutOpacity,
					mouseOverOpacity:  1.0,
					fadeSpeed:         'fast',
					exemptionSelector: '.selected'
				});
				});
				cyljq.getScript('http://www.neworleans.com/common/js/jquery/jquery.galleriffic.js',function(){
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
				});
												$('#videoModal').jqm({overlay: 20, toTop: true});
												//initMaps();
							});
			
			

			/* ]]> */
		</script>
		<div class="resultsDescription">
        <a href="#" id="id592" class="closeWindow jqmClose"> 
		<img width="80" height="25" border="0" alt="" src="http://www.neworleans.com/mytrip/images/btn_pop_close.gif"> 
		</a>

		<div class="description">
			<div class="productThumb">
				<img src= '<#if (hotelDetail.photos?size > 0)>${hotelDetail.photos[0].url!''}</#if>' alt="${hotelDetail.name!''}" class="productThumb" />
			</div>
			<div class="productSummary">
    			<h1 class="fn org hotel-title">${hotelDetail.name!''}</h1>
				<#assign rating=hotelDetail.rating>
								<#assign ratingSrc=''>
                                    <p><#if rating == 1.0>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_1_0.gif'>
									   <#elseif rating == 1.5>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_1_5.gif'>
										<#elseif rating == 2.0>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_2_0.gif'>
										<#elseif rating == 2.5>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_2_5.gif'>
										<#elseif rating == 3.0>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_3_0.gif'>
										<#elseif rating == 3.5>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_3_5.gif'>
										<#elseif rating == 4.0>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_4_0.gif'>
										<#elseif rating == 4.5>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_4_5.gif'>
										<#elseif rating == 5.0>
									   <#assign ratingSrc='http://www.neworleans.com/common/images/star_5_0.gif'>

									   </#if>
				<div class="rating productStars"><img src="${ratingSrc}" alt="" /></div>
				<div class="adr">
					<div class="address1 street-address">${hotelDetail.address1!''}</div>
					<div class="address2 extended-address"></div>
					<div class="cityStatePC"><span class="locality">${hotelDetail.city!''}</span> <span class="region">${hotelDetail.state!''}</span> <span class="postal-code">${hotelDetail.zip!''}</span></div>
                    <div class="tel">NewOrleans.com Reservation Hotline: 1-855-639-6756</div>
				</div>
                <#if hotelDetail.areaDescription?has_content><p>Location: ${hotelDetail.areaDescription!''}</p></#if>
			</div>
			<div class="clear"></div>
		</div>
		<!---des-->
		</div>
		<div class="productWrap">
		<div class="adBlock"></div>
        <div class="clear"></div>
		<div id="destinations">

    		<div id="tabs" class="tab-row" style="width:668px">
    			<ul class="tabList">
                	<li><a href="#rooms"><span>Rooms</span></a></li>
					<li><a href="#description"><span>Description</span></a></li>
					<#if (hotelDetail.hotelAmenitiesSupported) >
    				<li><a href="#hotelDetails"><span>Hotel Details</span></a></li>
    			</#if>
					<li><a href="#map"><span>Map</span></a></li>
   					<li><a href="#photos"><span>Photos</span></a></li>
					  				
                </ul>
    			<div class="clear"></div>
				<div id="rooms" class="roomsTab">
				
    		
<script type="text/javascript">
if (!window.GRTP)
{
window.GRTP =
{
togglePromoDetails : function(clicked)
{
$(clicked).parents('div.jqRoomRates').find(
'div.jqPromo').toggle();
return false;
}
}
}
</script>
<#list hotelDetail.roomTypeDetails as roomType>
<div class="room_details">
<div class="tabText">


<div id="id8a1" class="jqRoomRates">
<div id="id8a2" class="">

</div>
<div id="id8a3">
<table cellspacing="0" cellpadding="0" border="0" class="hotelResults">
<thead>
		<tr>
		<th class="productCol"><span>${roomType.name}</span>
		</th>
		<#list roomType.dailyRates as dailyRate>
		<th class="dayCol"> ${dailyRate.date?string("EEE")}
        </th>
		</#list>
<th class="priceCol">
<#if (roomType.dailyRates?size>0)>
<strong>Avg Nightly Rate
</strong><br> per night, per room
</#if>
</th>
<th class="bookItCol">&nbsp;</th>
</tr>
</thead>

<tbody>


<#if (roomType.dailyRates?size>0)>

<tr class="cyl-HotelRow <#if roomType.isPromoRate()> hasPromos  hasSale</#if>">

<td rowspan="1" class="productCol">
<span>${roomType.name}</span>
  </td>

<#list roomType.dailyRates as dailyRate>
<td class="dayCol">

<span <#if roomType.isPromoRate()>class="originalRate"</#if>>$${dailyRate.originalPrice!dailyRate.price}</span>
                                                <#if roomType.isPromoRate()>
												<br />
                                                <span class="promo">${dailyRate.price!''}</span>
												</#if></td>
</#list>
<td rowspan="1" class="priceCol">
<span <#if roomType.isPromoRate()> class="originalRate" </#if>>
												<#if roomType.avgNightlyOriginalRate?has_content>
													${roomType.avgNightlyOriginalRate?string.currency}
												<#elseif roomType.avgNightlyRate?has_content>
													${roomType.avgNightlyRate?string.currency}
												</#if>
												</span>
                                                <br />
												<#if roomType.isPromoRate()>
                                                <span class="promo">${roomType.avgNightlyRate?string.currency!'0.0'}</span>
                                                <br />
												</#if>
</td>

<td rowspan="1" class="bookItCol">
<input type="button" border="0" value="" onclick="parent.location='${roomType.bookItUrl!''}'" class="bookIt" alt="" />
</td>
</tr>
</#if>

<tr class="jqRoomDetails" style="display:none">
<td class="productCol" colspan="5">
<div class="jqPromo">


</div>
<div class="clear"></div></td>
</tr>



</tbody>
</table>
</div>



<!-- new promo group fragment 1 week -->





</div>

<div class="room_details">
<div class="room_image">
<#if (roomType.photos?size > 0)>
<img src="${roomType.photos[0].url!''}">
</#if>
</div>
<p>
${roomType.details!''}
</p>
<#if roomType.features?has_content>
<p class="amenities">
<strong>Amenities Include:
</strong>
${roomType.features!''}
</p>
</#if>
<div class="clear"></div>
</div>
<div class="clear"></div>
</div>
</div>
</#list>


</div> <!-- /roomsTab -->
				<div id="description" class="expertTab tabbed-item">
    				<h2>Description</h2>
    				<p>
					${hotelDetail.description!''}
					</p>    			</div> <!-- /description -->
					
				<#if (hotelDetail.hotelAmenitiesSupported) >
					
	    			<div id="hotelDetails" class="hotelDetailsTab tabbed-item">
						<h2>Hotel Details</h2>
					
					<#if (hotelDetail.amenities?size > 0)>
					
						<#assign size=(hotelDetail.amenities?size)>
						<#assign rem=size%2>
						<#assign col1=0>
						<#if rem==1>
							<#assign col1=(size+1)/2>
							<#else>
								<#assign col1=size/2>
						</#if>	
					</#if>
					<div style="width: 293px; float: left;">
						<#if (hotelDetail.amenities?size > 0)>
							<#list 0..(col1-1) as i>
							<div class="feature-container"><strong class="feature-name">${hotelDetail.amenities[i].name!''}</strong> <p>${hotelDetail.amenities[i].description!''}</p></div>
							</#list>
						</#if>
					</div>
						
					<div style="width: 293px; float: right;">
						<#if (hotelDetail.amenities?size > 1)>
							<#list col1..(size-1) as i>
								<div class="feature-container"><strong class="feature-name">${hotelDetail.amenities[i].name!''}</strong> <p>${hotelDetail.amenities[i].description!''}</p></div>
							</#list>
						</#if>
					</div>	
					<div class="clear"></div>
					</div> <!-- /hotelDetails -->
				</#if>
    			
    			
				<div id="map" class="mapTab tabbed-item">
					<h2>Map</h2>
					<div id="map_container" style="height: 350px; width: 600px;"></div>
				</div> <!-- /mapTab -->
    			    			<div id="photos" class="photosTab tabbed-item">
					
		<!-- module 164201 -->
		
		<div id="gallery" class="photo_content">
		<style type="text/css">
    	#slideshow img
    	{
    		width: 370px;
			height: auto;
    	}
		</style>
			<div id="controls" class="controls"></div>
			<div class="slideshow-container">
				<div id="loading" class="loader"></div>
				<div id="slideshow" class="slideshow"></div>
			</div>
			<div id="caption" class="caption-container"></div>
		</div>
		<#if (hotelDetail.photos?size > 0)>
		<div id="thumbs" class="navigation">
			<ul class='thumbs noscript'>
			 
			<#list hotelDetail.photos as photo>
			<li><a class="thumb" href='${photo.url!''}' title='${hotelDetail.name!''}'>
			<img src='${photo.url!''}' alt='${hotelDetail.name!''}' />
			</a></li>
			</#list>
			</ul>		
		</div>
					</#if>

		<div class="clear"></div>
		<!-- end module 164201 -->
					</div> <!-- /photosTab -->
				   
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
		<!--<script type="text/javascript">
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
                r = r.replace(new RegExp("[á¢¢å¢, 'g'),"a");
				r = r.replace(new RegExp("[AÃ„]", 'g'),"A");
                r = r.replace(new RegExp("[éªªì¢, 'g'),"e");
				r = r.replace(new RegExp("[É‰Ë‹]", 'g'),"E");
                r = r.replace(new RegExp("[ï¿½Ý¢, 'g'),"i");
				r = r.replace(new RegExp("[ÍÏ]", 'g'),"I");
                r = r.replace(new RegExp("ñ¢¬ 'g'),"n");
				r = r.replace(new RegExp("Ñ¢, 'g'),"N");
                r = r.replace(new RegExp("[ò³´¶]", 'g'),"o");
				r = r.replace(new RegExp("[Ó“Õ–]", 'g'),"O");
                r = r.replace(new RegExp("[ñº»¼]", 'g'),"u");
				r = r.replace(new RegExp("[ÚšÜœ]", 'g'),"u");
                r = r.replace(new RegExp("[ï¿½ï¿½, 'g'),"y");
				r = r.replace(new RegExp("[Þ", 'g'),"Y");
				return r;
			}
    	</script>-->
	    </div>
    <div class="clear"></div>  
 <!-- </div> <!-- /main-content -->
<!--</div>-->
  
        




		
	
        
	
	
				

<!-- /Matchflow tags -->
</body>
</html>
