<#macro isSelected current value>
	<#if ( current?matches(value) ) >selected</#if>
</#macro>

<#macro chopstring val length=8>
	<#assign x = val?length>
	<#assign name = val>
	<#if (x > length) >
		${val?substring(0,length)}
	  <#else>
	  	${val}	
	</#if>
</#macro>
<!DOCTYPE html>
<html lang="en">
  <head>
		<#include "/admin/includes/admin_header.ftl">
  </head>

  <body>

		<#include "/admin/includes/admin_navbar.ftl">


    <div class="container">
      			
    	<h2 style="color: blue; text-align: center">Provider Mapping</h2>
    	<div>
    	
        <table class="table table-bordered table-striped">
           <thead>
	            <tr>
	            	<th width="75%">Master Hotel Name</th>
                <th width="10%">Provider</th>
                <th width="10%">External Id</th>
                <th width="5%">Actions</th>
              </tr>
           </thead>
           <tbody id="mappingTable">
           	<#list model["sourceHotelList"] as hotel>
           		<form action="/admin/updateSourceHotel" method="post" commandName="wrapper">
           		<tr>
	           			<td>
	           				<select style="width:550px" name="masterHotelName" value="${hotel.hotelName}">
	           					<#list model["masterHotelList"] as masterHotel>
	           						<option <@isSelected current=hotel.hotelName value=masterHotel.hotelName/> 
	           						 value="${masterHotel.hotelName}">
	           						 	${masterHotel.hotelName} (<@chopstring val=masterHotel.uuid /> - ${masterHotel.favoredInventorySource})</option>
	           					</#list>
	           				</select>
	           			</td>
	           			<td><input style="width:50px" readonly="readonly" name="invSource"  type="text" value="${hotel.invSource}"/></td>
	           			<td><input style="width:100px" readonly="readonly" name="sourceHotelId" type="text" value="${hotel.externalHotelId}"/></td>
	           			<td><input type="submit" value="Save"/></td>
           		</tr>
           		</form>
           	</#list>
          </tbody>
   			</table>
    	</div>
    </div> <!-- /container -->
    
    <#include "/admin/includes/admin_footer.ftl">
  </body>
</html>
