<div class="filterWrap">
	<form name="searchWidgetForm" class="hotelSearchForm" action="results?page=1&filter=location" method="post">

		<!-- Total Matching Products Display -->
		<div class="totalMatches">
			<div class="numberOfHotels">${searchResults["result"].numTotalHotels}</div>
			<div class="matchingHotels">
				<span>Matching Hotels</span><br> <a href="#">Show All</a>
			</div>
			<div class="clear"></div>
		</div>
		
		<!-- LocationsDropDownChoice -->
		<!-- location -->
		<div class="filterBoxWrapper">
			<div class="topHeader"></div>
			<div class="locationPreference">
				<strong>Location</strong> 
				<select class="hotelLocation" name="locationValue" style="width: 160px;" >
					<option selected="selected" value="">All Locations</option>
					<#list searchResults["result"].locations as location>
						<option value="${location.name}">{location.name}</option>
					</#list>
				</select> 
				<span class="clear"></span>
			</div>
			<div class="bottomRound"></div>
		</div>
		
	</form>
</div>