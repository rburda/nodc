<#macro chopstring val length=17>
	<#assign x = val?length>
	<#assign name = val>
	<#if (x > length) >
		${val?substring(0,length) + "..."}
	  <#else>
	  	${val}	
	</#if>
</#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
    <title>NewOrleans.com - Hotel Search Results</title>
    <link type="text/css" rel="stylesheet" href="http://www.neworleans.com/style/global.css" />
    <link type="text/css" rel="stylesheet" href="http://www.neworleans.com/mytrip/style/checkout.css" />
    <link type="text/css" rel="stylesheet" href="http://www.neworleans.com/style/globaless.css" />
    <link type="text/css" rel="stylesheet" href="style/searchstyle.css" />
	
	<script src="http://www.neworleans.com/common/js/jquery/jquery-core.js" type="text/javascript"></script>
    <!-- Following Script tags are added because this page is expected to have JQModalWindow -->
    <script src="http://neworleans.com/common/js/jquery/jquery-core.js" type="text/javascript"></script>
    <!--<script src="http://neworleans.com/common/js/jquery/jquery-modal.js" type="text/javascript"></script>-->
    <script type="text/javascript" src="http://neworleans.com/common/js/jquery/jquery-ui.js"></script>
    <script type="text/javascript" src="http://neworleans.com/common/js/jquery/jquery-scrollTo.js"></script> 
    <!--<script type="text/javascript" src="http://neworleans.com/common/js/jquery/jquery-modal.js"></script>-->    
    <script type="text/javascript" src="http://neworleans.com/common/js/jquery/jquery.galleriffic.js"></script>
    <script type="text/javascript" src="http://neworleans.com/common/js/jquery/jquery.opacityrollover.js"></script>
    <script type="text/javascript" src="http://neworleans.com/common/js/outside-wicket/widget-lib.js"></script>    
    <script src="http://platform.twitter.com/widgets.js"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
    <script src="http://maps.google.com/maps/api/js?key=AIzaSyATPZS76QxgLdfyWoC2f_v9yZYed1cTLZc&sensor=false" type="text/javascript"></script>
    <script type="text/javascript" src="http://www.neworleans.com/javascript/global.js"></script>
    <script type="text/javascript" src="http://neworleans.com/javascript/tealeaf.js"></script>
    <script src="http://neworleans.com/common/js/jquery/cyl-tooltip.js" type="text/javascript"></script>
    <script type="text/javascript" src="http://neworleans.com/video/player-helper.js"></script>
    <script type="text/javascript" src="http://neworleans.com/video/swfobject.js"></script>
    <script src="http://maps.google.com/maps/api/js?sensor=false&amp;v=3.5" type="text/javascript"></script>
    <script src="http://neworleans.com/common/js/mini_cart.js" type="text/javascript"></script>
    <script type="text/javascript" src="http://neworleans.com/javascript/coremetrics/v40/eluminate.js"></script>
    <script type="text/javascript" src="http://neworleans.com/javascript/coremetrics/cmcustom.js"></script>
    <script type="text/javascript" src="http://neworleans.com/javascript/coremetrics/cmxCustomGlobal.js"></script>
    <script type="text/javascript" src="js/search.js"></script>    
	
		<script type="text/javascript">
	 		cyljq(document).ready(function() 
	 		{
	 			cyljq(".scheck").click(function() {
	   			document.location.href="results?page=1&sort="+cyljq(this).val();
	  		});
	 			
				var ajaxOpts = {
						url: 'http://www.neworleans.com/mytrip/app/SearchWidget?skin=homeHotel',
						type: "GET",
						dataType: "jsonp",
						cache: true,
						jsonp: "json=true&jsoncallback"
				};
				
				insertTo = 'sidebarWidget';
				cyljq.ajax(ajaxOpts).done(function(data)
				{
					Cyllenius.Widget._loadContentScripts(data.response,
							function(content){
								
								var insertWidget = function(preSelected){
									var $insertTo = preSelected || cyljq('#' + insertTo);
									if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: purple;'>Finding insertion point " + insertTo + "; wait: " + cyljq.readyWait + "; found: " + $insertTo.length + "</span></div>");
									$insertTo.html(content);
									$insertTo.append("<script>cyljq().ready(function(){Cyllenius.Widget._overrideSubmits('" + insertTo + "');Cyllenius.Widget._overrideAjaxSubmits('" + insertTo + "');Cyllenius.Widget._executeCustomCallback('" + insertTo + "', 'onAfterDynamicLoad');Cyllenius.Widget._onAfterInitialLoad('" + insertTo + "');});<\/script>");
									Cyllenius.Widget._onAllWidgetResourcesLoaded($insertTo);
								};
								var $insertTo = cyljq('#' + insertTo);
								if ($insertTo.length > 0)
									insertWidget($insertTo);
								else
								{
									if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: red;'>Delaying loading till ready " + insertTo + "; wait: " + cyljq.readyWait + "; found: " + $insertTo.length + "</span></div>");
									cyljq().ready(function(){insertWidget(false);});
								}
							}, insertTo);
				}).fail(function(XHR, textStatus, errorThrown){alert(textStatus);alert(errorThrown);});
				setTimeout("modifyUrl();", 1000);
	 		});
	 		
	</script>
	
	<script type="text/javascript" id="com-vegas-athena-components-browse-hotel-HotelBrowseRoomRatePanel-0">
/* */
$(window).load(
function(event)
{
if (!$.jqm)
return;
if (!window.RoomRatePanel)
{
// initialize
window.mwCSS =
{
position : 'absolute',
offsetTop : 130,
offsetLeft : 111
};
var scope = $('#devSearchContent');
var totalPriceLinks = $("a.pricetrigger",$("#devSearchContent"));
totalPriceLinks.live("click",function(e){
try
{
VDCtoTL.tlAddEvent(e);
}
catch (err)
{
}
var lx = e.clientX, ly = this.offsetTop;
if (mwCSS)
{
var cssObj =
{
position : mwCSS.position,
top : ly
- mwCSS.offsetTop,
left : lx
- mwCSS.offsetLeft
};
$(
'div.CSScontainer',
$('#mwplaceholder'))
.css(cssObj);
}
window.RoomRatePanel =
{
onShow : function(
callback)
{
var placeholder = $('#mwplaceholder');
var contentid = e.target.name;
//alert(contentid);
if (contentid)
{
var newEl = $(
'#'
+ contentid
+ ' .child')
.clone(
true);
$(
'.CSScontainer div.content',
placeholder[0])
.empty()
.html(
newEl);
}
$('.CSScontainer',
placeholder)
.css(mwCSS)
.show();
// for IE, modify the overlay such that it will be a white background. This will remove the flash effect.
if ($.browser.msie)
{
$(
'div.jqmOverlay')
.css(
{
'background-color' : '#FFF'
});
}
},
onClose : function(e)
{
try
{
VDCtoTL
.tlAddEvent(e);
}
catch (e)
{
}
$target = $(e.target);
if ($target
.hasClass('pricejqmClose'))
$(
'div.CSScontainer',
$('#mwplaceholder')[0])
.jqmHide();
}
};
$(
'div.CSScontainer',
$('#mwplaceholder')[0])
.jqm(
{
overlay : 1,
modal : false,
onShow : window.RoomRatePanel.onShow
})
.jqmShow();
$("#devSearchContent")
.click(
RoomRatePanel.onClose);
$('a.moreInfoLink',
scope[0])
.click(
function(e)
{
var url = (window.location.href
.indexOf("vegas.com") == -1 ? 'http://www.neworleans.com/includes/pop_servicefees_popup.html'
: 'http://www.neworleans.com/incl/q12003/pop_servicefees_popup.html');
window
.open(
url,
'termsandconditions',
'width=630,height=270,scrollbars=yes');
});
});
}
});
/* */
</script>
	
    <script type="text/javascript" src="http://neworleans.com/javascript/tealeaf.js"></script>       
    <!--<link type="text/css" rel="stylesheet" href="http://neworleans.com/common/css/jquery/jqModal.css" />-->
    <script src="http://neworleans.com/mytrip/common/js/adtag_docwrite_util.js" type="text/javascript"></script>
    <script type="text/javascript" charset="UTF-8" src="http://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/8/12/%7Bcommon,util%7D.js"></script>
    <script type="text/javascript" charset="UTF-8" src="http://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/8/12/%7Bstats%7D.js"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
  <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
      <script src="jqModal.js" type="text/javascript"></script>
    <link type="text/css" rel="stylesheet" href="jqModal.css" />	
</head>
<body class="globaless" id="transaction">
    <div class="header-wrapper">
        <div class="headercontain" id="header">
            <div>
                <div class="topmenu" id="topmenu">
                    <span class="topmenutitle">QUICK LINKS:</span>
                    <ul class="quicklinks">
                        <li><a href="http://www.neworleans.com/new-orleans-hotels/french-quarter-hotels/">French
                            Quarter Hotels</a></li>
                        <li><a href="http://www.neworleans.com/cruises/">Cruises</a></li>
                        <li><a href="http://www.neworleans.com/new-orleans-tours/new-orleans-city-tours/">New
                            Orleans City Tours</a></li>
                    </ul>
                </div>
                <div class="logo midheader">
                    <a href="http://www.neworleans.com">
                        <img width="358" height="82" border="0" alt="New Orleans, The Official New Orleans Travel Site"
                            src="http://www.neworleans.com/images/NO-web-logo.png"/>
                    </a>
                    <div class="searchBox">
                        <form name="gs" method="get" action="http://find.neworleans.com/search">
                        	<input type="hidden" name="entqr" value="1"/>
                        	<input type="hidden" name="output" value="xml_no_dtd"/>
                        	<input type="hidden" name="sort" value="date:D:L:d1"/>
                        	<input type="hidden" name="entsp" value="0"/>
                        	<input type="hidden" name="client" value="neworleans"/>
                        	<input type="hidden" name="ud" value="1"/>
                        	<input type="hidden" name="oe" value="UTF-8"/>
                        	<input type="hidden" name="ie" value="UTF-8"/>
                        	<input type="hidden" name="proxystylesheet" value="neworleans"/>
                        	<input type="hidden" name="site" value="neworleans"/>
                        	<input type="hidden" name="filter" value="p"/>
                        	<input type="text" onfocus="this.value='';this.className=this.className+' active';"
                            onblur="this.className=this.className.replace(' active','');" value="Search..."
                            name="q" maxlength="250" class="customsearchInput"/>
                        	<input type="image" class="customsearchButton" name="btnG" src="http://www.neworleans.com/images/arrowbutton-copy.png"
                            value=""/>
                        </form>
                    </div>
                </div>
                <div class="links_cart">
                    <!-- /top_links -->
                    <div class="miniCart">
                        <script id="csjs" src="http://www.neworleans.com/mytrip/app/?wicket:bookmarkablePage=:com.vegas.athena.pages.CartStatusContainerJS"
                            type="text/javascript"></script>
                        <!-- mini cart wrap -->
                    </div>
                    <!-- /miniCart -->
                    <div id="facebook-like">
						<iframe scrolling="no" frameborder="0" allowtransparency="true" style="border: medium none; overflow: hidden; width: 90px; height: 21px;" src="http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Ffacebook.com%2Fmyneworleans&amp;send=false&amp;layout=button_count&amp;width=100&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21"></iframe>
						<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=location.protocol+"//www.facebook.com/plugins/like.php?href=http%3A%2F%2Ffacebook.com%2Fmyneworleans&amp;send=false&amp;layout=button_count&amp;width=100&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21";js.style.display="";}}(document,"iframe","facebook-like");</script>
					</div>

                    <div class="twitter-link-container">
                        <iframe scrolling="no" frameborder="0" allowtransparency="true" src="http://platform.twitter.com/widgets/follow_button.1355514129.html#_=1356566343585&amp;id=twitter-widget-0&amp;lang=en&amp;screen_name=NODotCom&amp;show_count=false&amp;show_screen_name=true&amp;size=m"
                            class="twitter-follow-button" style="width: 133px; height: 20px;" title="Twitter Follow Button"
                            data-twttr-rendered="true"></iframe>
                        <script>
                        	!function (d, s, id) { 
                        		var js, fjs = d.getElementsByTagName(s)[0]; 
                        		if (!d.getElementById(id)) { 
                        			js = d.createElement(s); js.id = id; 
                        			js.src = location.protocol + "//platform.twitter.com/widgets.js"; 
                        			fjs.parentNode.insertBefore(js, fjs); 
                        		} 
                        	} (document, "script", "twitter-wjs");
                        </script>
                    </div>
                </div>
                <!-- /links_cart -->
                <div id="navigation">
                    <ul class="top_nav">
                        <li title="home"><a href="/" title="New Orleans Home">Home</a> </li>
                        <li class="selected" title="hotels"><a href="http://www.neworleans.com/new-orleans-hotels/"
                            title="New Orleans Hotels">Hotels</a> </li>
                        <li title="airhotel"><a href="http://www.neworleans.com/new-orleans-vacation-packages/"
                            title="New Orleans Air + Hotel">Air + Hotel</a> </li>
                        <li title="deals"><a href="http://www.neworleans.com/new-orleans-deals/" title="New Orleans Travel Deals">
                            Deals</a> </li>
                        <li class="selected" title="tours-activities"><a href="http://www.neworleans.com/new-orleans-tours/"
                            title="New Orleans Tours and Activities">Tours / Activities</a> </li>
                        <li title="festivals-events"><a href="http://www.neworleans.com/new-orleans-events/"
                            title="New Orleans Festivals &amp; Events">Festivals / Events</a> </li>
                        <li title="dining"><a href="http://www.neworleans.com/la/restaurants/" title="New Orleans Dining">
                            Dining</a> </li>
                        <li title="nightlife"><a href="http://www.neworleans.com/la/nightlife/" title="New Orleans Nightlife">
                            Nightlife</a> </li>
                        <li title="frenchquarter"><a href="http://www.neworleans.com/french-quarter/" title="New Orleans French Quarter">
                            French Quarter</a> </li>
                        <li class="last" title="more"><a href="http://www.neworleans.com/new-orleans-tips/"
                            title="Things To Do">More<div>
                            </div>
                        </a>
                            <ul class="sub_nav">
                                <li class="first"><a href="http://www.neworleans.com/new-orleans-tips/group-travel/"
                                    title="Group Travel">Group Travel</a></li>
                                <li><a href="http://www.neworleans.com/things-to-do-in-new-orleans/" title="New Orleans Attractions">
                                    Things to Do &amp; Attractions</a></li>
                                <li><a href="http://www.neworleans.com/things-to-do-in-new-orleans/kids/" title="Things to Do with Kids in New Orleans ">
                                    Things to Do with Kids</a></li>
                                <li><a href="http://tickets.neworleans.com/index.html" title="Event Tickets">Event Tickets</a></li>
                                <li><a href="http://www.neworleans.com/new-orleans-tips/maps/" title="New Orleans Maps">
                                    Maps</a></li>
                                <li><a href="http://www.neworleans.com/blog/" title="Blog">NewOrleans.com Blogs</a></li>
                                <li><a href="http://www.neworleans.com/new-orleans-sports/" title="New Orleans Sports">
                                    Sports</a></li>
                                <li><a href="http://www.neworleans.com/new-orleans-tips/transportation/" title="New Orleans Transportation">
                                    Transportation</a></li>
                                <li><a href="http://www.neworleans.com/new-orleans-tips/new-orleans-weather/" title="New Orleans Weather">
                                    Weather</a></li>
                                <li><a href="http://pages.reply.shop.neworleans.com/page.aspx?QS=773ed3059447707d7e52647881115fcaf053f0855bc772e6781e5a23db264caf"
                                    title="Free New Orleans Travel Guide">Free Travel Guide</a></li>
                                <li><a href="http://www.neworleans.com/new-orleans-tips/new-orleans-directory/" title="All Sections">
                                    All Sections</a></li>
                            </ul>
                        </li>
                    </ul>
                    <div class="clear">
                    </div>
                </div>
            </div>
        </div>
        <!-- /header -->
    </div>
    <!-- /header-wrapper -->

    <div id="wrapper">
        <div id="main-content">
            <!-- page content -->
            <a name="summaryTop"></a>
            <div class="wait_img hotel_wait_img"></div>
            <div class="summaryCol">
                <div class="summaryBox">
				
					<div class="searchSummary">
						<h3>Search Summary</h3>
						<p>
							<strong>Check-In:</strong>
							${searchResults["result"].startDate?string("MM/dd/yy")}
							
						</p>
						<p>
							<strong>Check-Out:</strong>
							${searchResults["result"].endDate?string("MM/dd/yy")}
						</p>
						<p>
							<strong>Rooms:</strong>
							${searchResults["result"].numRooms}
						</p>
						<p>
							<strong>Adults:</strong>
							${searchResults["result"].numAdults}
							<strong>Children:</strong>
							${searchResults["result"].numChildren}
						</p>

					</div>
					
					<div class="browseSideBarPanel hotel">
						<div class="hotelOnly">
						<!-- change search list -->
							<div class="changeSearch">
								<h3>Change Search</h3>								
								<div id="sidebarWidget"></div>
							</div>
							<div class="allAvailableItems">
								<h3>All Available Hotels</h3>
								<p>For your convenience, we've listed all of the available hotels within your search.</p>
								<ul>
									<li class="leftTitle">Hotel</li>
									<li class="rightTitle">Avg</li>
									<#list searchResults["result"].allHotels as hotel>
										<li class="productName" style="position: static !important;">
											<a onclick="popup('rooms', '${hotel.name}');" href="#rooms" class="tooltip-hover" id="id6c0"><@chopstring val=hotel.name /></a>
										</li>
										<li class="productPrice">$${hotel.lowestAvgRate?round!0.00}</li>									
									</#list>
									<li class="last"></li>
								</ul>
							</div>
							
							<div class="tcenter">
								<div class="verisignWrap">
								<img width="113" height="59" border="0" alt="COMODO Secured" src="http://neworleans.com/common/images/img_comodo_secure.gif"> <span class="labelDrop">NewOrleans.com GUARDS YOUR PRIVACY &amp; SECURITY</span> 
								<a onclick="window.open('http://neworleans.com/includes/info_safetobuy.html','secure','width=450,height=450,scrollbars=yes,resizable=yes');return false;" href="javascript:void(0);">Learn how we protect<br>your online purchases</a>
								</div>
							<!-- /verisign -->
							<!-- / customer support -->
							</div>
						</div>
					</div>
					<div class="csWrap">
						<img width="97" height="56" style="border: 0px;" alt="" src="http://neworleans.com/images/placeholder_cs.jpg">
						<p>
						<strong> We're here for you
						</strong> <br>
						Need help with your order?<br>
						<strong>USA/Canada toll free<br>
						1-855-639-6756 </strong>
						</p>
					</div>
					
                </div>
                <div style="width: 200px; height: 265px; margin-top: -10px;">
                    <script src="http://ad.doubleclick.net/adj/neworleans.com/hotelsearch;sz=200x265;sidebar=100;ord=5360199?"
                        type="text/javascript"></script>
                </div>
            </div>
            <!-- /leftColumn -->
            <!-- starting the right content column -->
            <div class="searchContent" id="devSearchContent">
                <!-- removed id="airContent" -->
                <div>
                </div>
                <div id="id139">
                    <h2>
                        Select a New Orleans Hotel</h2>
                    <h3 class="bookTitle">
                        <span style="color: red;">Get your Room Now!</span> Rooms sell out quickly.
                    </h3>
                    <div class="promoSpot" id="promotionPanel">
                        <script src="http://ad.doubleclick.net/adj/neworleans.com/hotelsearch;sz=720x90;ad_slot=1;ord=5360199?"
                            type="text/javascript"></script>
                        <script></script>
                        <!--http://www.neworleans.com/promotions/bigeasyupgrade/popup.html-->
                        <script>
                            var newwindow;
                            function poptastic(url) {
                                newwindow = window.open(url, 'name', 'height=450,width=754');
                                if (window.focus) { newwindow.focus() }
                            }
                        </script>
                        
                    </div>
                    <!-- sort bar -->
                    <div class="sortBar">
	                    <ul>
	                     <li class="first"><label>Sort by:</label></li>
	                     <li>
	                         <input id="sortWeight" type="radio" value="DEFAULT" class="scheck checkBox" <#if (searchResults["result"].currentSort.name() == "DEFAULT") > checked="checked" </#if> />
	                         <label for="sortWeight">NewOrleans.com Picks</label>&nbsp; 
	                     </li>
	                     <li>
	                         <input id="sortPrice" type="radio" value="PRICE" class="scheck checkBox" <#if (searchResults["result"].currentSort.name() == "PRICE") > checked="checked" </#if> />
	                         <label for="sortPrice">Price</label> &nbsp; 
	                     </li>
	                     <li>
	                         <input id="sortName" type="radio" value="HOTEL_NAME" class="scheck checkBox" <#if (searchResults["result"].currentSort.name() == "HOTEL_NAME") > checked="checked" </#if> />
	                         <label for="sortName">Hotel Name</label>&nbsp; 
	                     </li>
	                     <li>
	                         <input id="sortRating" type="radio" value="RATING" class="scheck checkBox" <#if (searchResults["result"].currentSort.name() == "RATING") > checked="checked" </#if> />
	                         <label for="sortRating">Rating</label>
	                     </li>
	                    </ul>
                     	<span class="clear"></span>
                    </div>
                    <div class="clear"></div>
                    <!-- / sort bar -->
                    <!-- pagination -->
											<#if (searchResults["result"].filteredHotels?size > 0) >
												<div style="font-size: 14px;" class="pagination">
													<p class="left">
														<span>${searchResults["result"].startHotel}-${searchResults["result"].endHotel} of ${searchResults["result"].numTotalHotels} results</span>
													</p>
													<p class="right">
														<#if (searchResults["result"].currentPage != 1) >
															<a href='results?page=${searchResults["result"].currentPage-1}'>&lt; Previous</a>
														</#if>
														<#list 1..searchResults["result"].numPages as i>
															<#if (searchResults["result"].currentPage == i) >
																<span style="font-weight: bold;">${i}</span> 
																<#else>
																	<span> <a href="results?page=${i}" title="Go to page ${i}">${i}</a></span>
															</#if>
	
														</#list>
														<#if (searchResults["result"].currentPage != searchResults["result"].numPages) >
															<a href='results?page=${searchResults["result"].currentPage+1}'>Next &gt;</a>
														</#if>
													</p>
													<div class="clear"></div>
												</div>
											</#if>
                    <!-- / pagination -->
                    <div>
					<#assign hotelId=0>
                        <#list searchResults["result"].filteredHotels as hotel>
                        <div class="searchResult">
                            <!-- search results box header with gradient background, hotel name, price -->
                            <div class="searchResultsHead">
                                <h2 class="averageRate">
                                    Lowest Average Price: <span style="" class="averageRate">$${hotel.lowestAvgRate!0.00}</span>
                                </h2>
                                <h2 class="productTitle">
                                    <a href="#rooms" onclick="popup('rooms','${hotel.name}');"> ${hotel.name!''}</a>
                                </h2>
                                <div class="clear">
                                </div>
                            </div>
                            <!-- / search results box header with gradient background, hotel name, price -->
                            <!-- the product information summary section, including the thumbnail, star rating, and text. -->
                            <div class="resultsDescription">
                                <#if (hotel.hotelDetails.photos?size > 0)> <a onclick="popup('rooms','${hotel.name}');" href="#rooms">
                                    <img src="${hotel.hotelDetails.photos[0].url!''}" width="100" height="74" alt=""
                                        class="productThumb" />
                                </a></#if>
                                <div class="productSummary">
                                    <p>
                                        <img width="71" height="14" alt="Star Rating" src="http://www.neworleans.com/common/images/star_2_0.gif" />${hotel.hotelDetails.areaDescription!''}
                                        (<a onclick="popup('map', '${hotel.name}');" href="#map">Map</a>)&nbsp;<a onclick="popup('photos', '${hotel.name}');" href="#photos">Photos</a>&nbsp;
                                    </p>
                                    <p>
                                        ${hotel.hotelDetails.description!''}
                                    </p>
                                    <p>
                                        <a onclick="popup('rooms', '${hotel.name}');" href="#rooms">more hotel info</a>
                                    </p>
                                    <div>
                                        <img width="82" height="17" border="0" alt="" src="http://www.neworleans.com/images/BEU-ticket-2.png"
                                            style="vertical-align: middle;" />&nbsp; <strong>Food, Drinks and more with the <a
                                                onclick="window.open(this.href, 'promos', 'width=760,height=620,scrollbars=yes,resizable=yes'); return false;"
                                                href="http://www.neworleans.com/promotions/bigeasyupgrade/popup.html">Big Easy Upgrade</a>!</strong>
                                    </div>
                                    <a style="display: none;" href="http://www.neworleans.com?cm_re=20121220-_-${hotel.name!''}-_-Slot+1||2">
                                    </a>
                                    <p>
                                    </p>
                                </div>
                            </div>
                            <!-- / the product information summary section, including the thumbnail, star rating, and text. -->
                            <!-- room types table -->
                            <div>
                                <table cellspacing="0" cellpadding="0" border="0" class="hotelResults">
                                    <thead>
                                        <tr>
                                            <th class="productCol">
                                                Room Type
                                            </th>
                                            <#list hotel.roomTypes[0].dailyRates as dailyRate>
                                            <th class="dayCol">
                                                ${dailyRate.date?string("EEE")}
                                            </th>
                                            </#list>
                                            <th class="priceCol">
                                                <strong>Avg Nightly Rate</strong><br />
                                                per night, per room
                                            </th>
                                            <th class="bookItCol">
                                                &nbsp;
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
										<#assign roomId = 200>
										<#assign roomId1 = 100>
										
                                        <#list hotel.roomTypes as roomType>
										
										<tr class="cyl-HotelRow <#if roomType.isPromoRate()> hasPromos  hasSale</#if>">
																		
                                            <td rowspan="1" class="productCol">
                                                <a class="showRoomDetail" onclick="popup('rooms', '${hotel.name}');" href="#rooms">${roomType.name!''}</a>
                                                <br />
                                                <span class="promo">${roomType.promoDesc!''}</span> (<a href="#" class="showPromoDetail">details</a>)
                                            </td>
                                            <#list roomType.dailyRates as dailyRate>
                                            <td class="dayCol">
                                                <span <#if roomType.isPromoRate()>class="originalRate"</#if>>$${dailyRate.originalPrice!dailyRate.price}</span>
                                                <#if roomType.isPromoRate()>
												<br />
                                                <span class="promo">$${dailyRate.price!0.00}</span>
												</#if>
                                            </td>
                                            </#list>
                                            <td rowspan="1" class="priceCol">
                                                <span <#if roomType.isPromoRate()> class="originalRate" </#if>>$${roomType.avgNightlyOriginalRate!roomType.avgNightlyRate}</span>
                                                <br />
												<#if roomType.isPromoRate()>
                                                <span class="promo">$${roomType.avgNightlyRate!''}</span>
                                                <br />
												</#if>
                                                <a href="javascript:void(0);" class="pricetrigger" id="${hotelId}${roomId1}" name="${hotelId}${roomId}" style="position:relative;" contentref="${hotelId}${roomId}">view total price
												</a>
												<div style="display:none;" id="${hotelId}${roomId}">
												<div class="child hotelDetailsPop">
												<p>
												Room Charges:
												<span>$95.20</span><br />
												Taxes &amp; Fees:
												<span>$16.23</span><br />
												</p>
												(<a href="javascript:void(0);" class="moreInfoLink">more info</a>)
												<p class="totalLine">
												Total Price:
												<span>${roomType.totalPrice!''}</span>
												</p>
												<a href="javascript:void(0);" class="pricejqmClose">Close Window
												</a><br />
												</div>
												</div>
                                            </td>
                                            <td rowspan="1" class="bookItCol">
                                                <input type="button" border="0" value="" onclick="parent.location='${roomType.bookItUrl}'"
                                                    class="bookIt" alt="" />
                                            </td>
                                        </tr>
                                        <tr style="display: none">
                                        </tr>
                                        <tr style="display: none">
                                        </tr>
										<#assign roomId = roomId+1>
										<#assign roomId1 = roomId1+1>
                                        </#list>
                                    </tbody>
                                </table>
                            </div>
                            <!-- new promo group fragment 1 week -->
                            <!-- new promo group fragment -->
                            <!-- / room types table -->
                            <div class="clear">
                            </div>
                            <!-- clearing div to clear the parent in case of floats -->
                            <div class="clear">
                                <!-- / clearing -->
                            </div>
                        </div>
						<#assign hotelId=hotelId+1>
                        </#list>
                    </div>
                    <div>
                        <!-- / clearing -->
                    </div>
                    <!-- bottom pagination -->
										<#if (searchResults["result"].filteredHotels?size > 0) >
											<div style="font-size: 14px;" class="pagination">
												<p class="left">
													<span>${searchResults["result"].startHotel}-${searchResults["result"].endHotel} of ${searchResults["result"].numTotalHotels} results</span>
												</p>
												<p class="right">
													<#if (searchResults["result"].currentPage != 1) >
														<a href='results?page=${searchResults["result"].currentPage-1}'>&lt; Previous</a>
													</#if>
													<#list 1..searchResults["result"].numPages as i>
														<#if (searchResults["result"].currentPage == i) >
															<span style="font-weight: bold;">${i}</span> 
															<#else>
																<span> <a href="results?page=${i}" title="Go to page ${i}">${i}</a></span>
														</#if>

													</#list>
													<#if (searchResults["result"].currentPage != searchResults["result"].numPages) >
														<a href='results?page=${searchResults["result"].currentPage+1}'>Next &gt;</a>
													</#if>
												</p>												
												<div class="clear">
												</div>
											</div>
										</#if>
					
						
                    <!-- / pagination -->
                    <div class="clear">
                        <br />
                    </div>
                </div>
                <div id="mwplaceholder">
                    <div style="display: none" class="CSScontainer" id="CSScontainer">
                        <div class="container_top">
                            <div class="round1">
                            </div>
                            <div class="round2 round_all">
                            </div>
                            <div class="round3 round_all">
                            </div>
                            <div class="round4 round_all">
                            </div>
                        </div>
                        <div id="mwcontent" class="content">
                            some content here</div>
                        <div class="container_top">
                            <div class="round4 round_all">
                            </div>
                            <div class="round3 round_all">
                            </div>
                            <div class="round2 round_all">
                            </div>
                        </div>
                        <div class="bottom_round">
                            <div class="point_round_left">
                            </div>
                            <div class="point_round_center">
                            </div>
                            <div class="point_round_right">
                            </div>
                        </div>
                        <div class="point">
                            <div class="point1 point_all">
                            </div>
                            <div class="point2 point_all">
                            </div>
                            <div class="point3 point_all">
                            </div>
                            <div class="point4 point_all">
                            </div>
                            <div class="point5 point_all">
                            </div>
                            <div class="point6 point_all">
                            </div>
                            <div class="point7 point_all">
                            </div>
                            <div class="point8 point_all">
                            </div>
                            <div class="point9 point_all">
                            </div>
                            <div class="point10 point_all">
                            </div>
                            <div class="point11 point_all">
                            </div>
                            <div class="point12 point_all">
                            </div>
                            <div class="point13 point_all">
                            </div>
                            <div class="point14 point_all">
                            </div>
                            <div class="point15 point_all">
                            </div>
                            <div class="point16 point_all">
                            </div>
                            <div class="point17 point_all">
                            </div>
                            <div class="point18 point_all">
                            </div>
                            <div class="point19 point_all">
                            </div>
                            <div class="point20 point_all">
                            </div>
                            <div class="point21 point_all">
                            </div>
                            <div class="point22 point_all">
                            </div>
                            <div class="point23 point_all">
                            </div>
                            <div class="point24 point_all">
                            </div>
                            <div class="point25">
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /widget and ad tag wrapper -->
            </div>
            <!-- / devSearchContent -->
            <!-- /show content -->
            <div class="clear">
            </div>
            <!-- /page content -->
            <span class="clear"></span>
        </div>
    </div>
    <!-- clearing div to clear the parent in case of floats -->
    <div class="clear">
    </div>
    <!-- / clearing -->
    <!-- footer: contains footer elements -->
    <div>
        <div>
            <div typeof="v:Organization" xmlns:v="http://rdf.data-vocabulary.org/#" id="footer">
                <div class="footer-top">
                    <div class="footer-top-content">
                        <img class="year-seal" src="http://www.neworleans.com/images/18yearslarge.png" alt="18 years New Orleans experts" />
                        We are located in downtown New Orleans, Louisiana (NOLA) on historic St. Charles
                        Avenue - at the center of what's happening in the Crescent City. Southern hospitality
                        is in our DNA, and our team is genuinely committed to showing visitors the best
                        of the Big Easy. With every hotel, tour, cruise, event ticket, or activity you research
                        or purchase on our site, you'll get honest recommendations from our in-house crew
                        of local experts. We love this city and we can't wait to show you a good time!
                    </div>
                </div>
                <div class="footer-container">
                    <div class="col1">
                        <div class="footer-heading">
                            Payment Information</div>
                        We accept the following payment types
                        <img class="accepted-cards" src="http://www.neworleans.com/images/cards.png" alt="We accept all major credit cards" />
                        <div class="footer-heading">
                            Security Guarantee</div>
                        <p class="security-guarantee">
                            <img style="float: left; padding: 5px;" src="http://www.neworleans.com/images/hackerpro.png"
                                alt="We protect your personal information" />To protect your personal information,
                            we use a wide array of electronic and physical safety measures. <a onclick="window.open(this.href, 'safe_to_buy', 'width=450,height=450,scrollbars=yes,resizable=yes'); return false;"
                                href="http://www.neworleans.com/includes/pop_info_safetobuy.html" rel="nofollow">
                                Learn More</a>
                        </p>
                    </div>
                    <div class="col2">
                        <div class="footer-heading">
                            Customer Service</div>
                        <p>
                            For customer service please call us at:<br />
                            Toll-Free Customer Service (within the U.S.): <span property="v:tel">(855)-639-6756</span><br />
                            Customer Service (international): <span property="v:tel">(504) 309-1648</span>
                        </p>
                        <div class="footer-heading">
                            Policies and Copyright Information</div>
                        <p>
                            Use of this website constitutes acceptance of the NewOrleans.com <a href="http://www.neworleans.com/about-us/termsofuse.html">
                                Terms of Use</a> and <a href="http://www.neworleans.com/about-us/privacy.html">Privacy
                                    Policy</a>. We guard your privacy and security.
                        </p>
                        <div class="company-info">
                            All contents &copy;
                            <div itemtype="http://schema.org/LocalBusiness" itemscope="">
                                <span itemprop="name">NewOrleans.Com</span> - <span itemprop="description">The Official
                                    New Orleans Travel Site</span>
                                <div itemtype="http://schema.org/PostalAddress" itemscope="" itemprop="address">
                                    <span itemprop="streetAddress">839 St. Charles Ave. Suite 305</span> <span itemprop="addressLocality">
                                        New Orleans</span>, <span itemprop="addressRegion">LA</span>
                                </div>
                                Phone: <span itemprop="telephone">504-309-1004</span> | <a href="http://www.neworleans.com"
                                    itemprop="url">www.neworleans.com</a>
                            </div>
                        </div>
                        <p class="cyllenius">
                            Powered By:</p>
                        <span typeof="v:Geo" style="display: none;"><span content="29.945136" property="v:latitude">
                        </span><span content="-90.072055" property="v:longitude"></span></span>
                    </div>
                    <div class="col3">
                        <span class="footer-heading"><a href="/sitemap/">Site Map</a></span><br />
                        <ul class="firstList">
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-hotels/">Hotels</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-tips/group-travel/">
                                Group Travel</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-vacation-packages/">
                                Air + Hotel</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-tours/">Tours /
                                Activities</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/la/restaurants/">Dining</a></li>
                        </ul>
                        <ul class="secondList">
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-deals/">Deals</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-events/">Festivals
                                / Events</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/blog/">NewOrleans.com Blogs</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/about-us/">About Us</a></li>
                            <li><a class="bulleted" href="http://www.neworleans.com/new-orleans-tips/maps/">Maps</a></li>
                        </ul>
                        <div class="travel-guide">
                            <img src="http://www.neworleans.com/images/guide.png" alt="Free Travel Guide" />
                            <div class="footer-heading">
                                Free Travel Guide</div>
                            <a class="bulleted" href="http://pages.reply.shop.neworleans.com/page.aspx?QS=773ed3059447707d7e52647881115fcaf053f0855bc772e6781e5a23db264caf">
                                Get the Best Travel Deals</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- / footer -->
    <div class="tooltip" style="background: transparent url(/common/images/tooltip_left.png) no-repeat top left;
        padding-bottom: 6px; color: #f8f8f8; display: none; position: absolute;">
        <div style="background: transparent url(/common/images/tooltip_right.png) no-repeat top right;
            padding-right: 5px; padding-left: 5px;">
            <div style="font-size: 11px; font-family: Arial, Helvetica, sans-serif; letter-spacing: .05em;
                padding: 4px 0px; background-color: #444444; zindex: 40000">
            </div>
        </div>
    </div>
    <div class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"
        id="ui-datepicker-div">
    </div>
<div id="Details" class="jqmWindow"></div>
</body>

 <script>
 var varHeight = function(hash){
        windowHeight   = $(window).height();
		hash.w.css('height', windowHeight-35).show();
}

function popup(link,hotelName){
hotelName = hotelName.replace(/\s/g,"%20");
var url="/details"+"?hotelName="+hotelName+"#"+link;
//alert(url);

$('#Details').jqm({ ajax: url,overlay:1, onShow:varHeight });
$('#Details').jqmShow();
}

    
  </script>
</html>
