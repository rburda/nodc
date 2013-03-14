<#macro isSelected current value>
	<#if ( current?matches(value) ) >selected</#if>
</#macro>

<!DOCTYPE html>
<html lang="en">
  <head>
		<#include "/admin/includes/admin_header.ftl">
  </head>

  <body>

		<#include "/admin/includes/admin_navbar.ftl">


    <div class="container">
      <form action="/admin/updateSourceHotel" method="post" commandName="wrapper">	
      <input type="hidden" name="sourceHotel.hotelName" readonly="readonly" value="${sourceHotel.hotelName}" />		
    	<h1>Hotel Weights</h1>
				<div class="container">
					<div class="span8">
						<h2>Edit Mapping</h2>
						<fieldset>
						   <div class="control-group">
						     <label class="control-label" for="active">External Hotel Id</label>
						     <div class="controls">
									 <input class="input-medium" type="text" name="sourceHotelId" readonly="readonly" value="${sourceHotel.externalHotelId}" />
						       <p class="help-block">The id used by the provider to identify this hotel</p>	
						     </div>
						   </div>
						   <div class="control-group">
						     <label class="control-label" for="styleId">Source</label>
						     <div class="controls">
						     	<input class="input-medium" type="text" name="invSource" readonly="readonly" value="${sourceHotel.invSource}" />
						      <p class="help-block">The name of the provider</p>	
						     </div>
						   </div>
						   <div class="control-group">
						     <label class="control-label" for="styleAttributeValueId">Hotel Name</label>
						     <div class="controls">
						       <select style="width:500px" class="input-medium" id="active" name="masterHotelName" value="${sourceHotel.hotelName}">
										 <#list model["masterHotelList"] as hotel>
										 <option <@isSelected current=sourceHotel.hotelName value=hotel.hotelName/> value="${hotel.hotelName}">${hotel.hotelName}</option>
										 </#list>
									 </select>
									 <p class="help-block">The name that will be shown to the customer for this hotel</p>	
						     </div>
						   </div>	
						   	<div class="form-actions">
									<input type="submit" value="Save"/>
 									<a style="float:right" href="/admin/editMaster">Cancel</a>
								</div>
						 </fieldset>
					</div>
				</div>
			</form>
    </div> <!-- /container -->

    <#include "/admin/includes/admin_footer.ftl">
  </body>
</html>
