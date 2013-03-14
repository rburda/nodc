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
	            	<th width="10%">Provider</th>
                <th width="70%">Name</th>
                <th width="10%">Weight</th>
                <th colspan="2" width="10%">Actions</th>
              </tr>
           </thead>
           <tbody id="mappingTable">
           	<#list model["hotelList"] as hotel>
           		<tr>
      						<input type="hidden" name="masterHotels[${hotel_index}].hotelName" value="${hotel.hotelName}" />	
      						<input type="hidden" name="masterHotels[${hotel_index}].weight" value="${hotel.weight}" />			
	           			<td><input style="width:50px" type="text" name="masterHotels[${hotel_index}].favoredInventorySource" readonly="readonly" value="${hotel.favoredInventorySource}" /></td>
	           			<td><input style="width:500px" name="masterHotels[${hotel_index}].newHotelName"  type="text" value="${hotel.hotelName}"/></td>
	           			<td><input style="width:50px"  name="masterHotels[${hotel_index}].newWeight" type="text" value="${hotel.weight}"/></td>
	           			
	           			<form action="/admin/editContent" method="post">
	           				<input type="hidden" name="masterHotelName" value="${hotel.hotelName}">
	           				<td><input type="submit" style="background-color:green;font-size:small" value="Edit Content"/></td>
	           			</form>
	           			<form action="/admin/deleteMasterHotel" method="post">
	          				<input type="hidden" name="name" value="${hotel.hotelName}" />
	          				<td><input type="submit" style="background-color:red;font-size:small" value="Delete"/></td>
	          			</form>
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
