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
    	<h2 style="color: blue; text-align: center">Hotel Weights</h2>
			<form action="/admin/saveMasterHotel" method="post" commandName="hotel">
			<input type="submit" style="background-color: green" value="Save"/>
    	<div>    	
        <table class="table table-bordered table-striped">
           <thead>
	            <tr>
	            	<th width="10%">Id</th>
                <th width="60%">Name</th>
                <th width="10%">Weight</th>
                <th colspan="2" width="20%">Actions</th>
              </tr>
           </thead>
           <tbody id="mappingTable">
           	<#list model["hotelList"] as hotel>
           		<tr>
           				<input type="hidden" name="masterHotels[${hotel_index}].uuid" value="${hotel.uuid}" />
           				<input type="hidden" name="masterHotels[${hotel_index}].hotelName" value="${hotel.hotelName}" />
           				<input type="hidden" name="masterHotels[${hotel_index}].weight" value="${hotel.weight}" />		
	           			<td style="width:150px"><@chopstring val=hotel.uuid /></td>
	           			<td><input style="width:500px" 
	           			  name="masterHotels[${hotel_index}].newHotelName"  type="text" 
	           			  value="${hotel.hotelName}"/></td>
	           			<td><input style="width:50px"  
	           			  name="masterHotels[${hotel_index}].newWeight" type="text" 
	           			  value="${hotel.weight}"/></td>
	           			
									<td><a class="btn btn-primary" href="/admin/editContent?id=${hotel.uuid}">Edit Content</a></td>
	          			<td><a class="btn btn-danger" href="/admin/deleteMasterHotel?id=${hotel.uuid}">Delete</a></td>
          		</tr>
           	</#list>
          </tbody>
   			</table>
    	</div>
			</form>
    </div> <!-- /container -->

    <#include "/admin/includes/admin_footer.ftl">
  </body>
</html>
