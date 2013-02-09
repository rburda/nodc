
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Bootstrap, from Twitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="/style/bootstrap.css" rel="stylesheet">
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="/style/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
      <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
                    <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">
                                   <link rel="shortcut icon" href="../assets/ico/favicon.png">
  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="/admin/editMaster">NODC Admin</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li class="active"><a href="/admin/editMaster">Weights</a></li>
              <li class="active"><a href="/admin/viewSourceHotels">Provider Mapping</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>


    <div class="container">
      <form action="/admin/saveMasterHotel" method="post" commandName="wrapper">			
    	<h2 style="color: blue; text-align: center">Hotel Weights</h2>
			<input type="submit" value="Save"/>
    	<div>
    	
        <table class="table table-bordered table-striped">
           <thead>
	            <tr>
	            	<th width="10%">Source</th>
                <th width="70%">Name</th>
                <th width="10%">Weight</th>
                <th width="10%">Actions</th>
              </tr>
           </thead>
           <tbody id="mappingTable">
           	<#list model["hotelList"] as hotel>
           		<tr>
	           			<td><input style="width:50px" type="text" name="masterHotels[${hotel_index}].favoredInventorySource" disabled="disabled" value="${hotel.favoredInventorySource}" /></td>
	           			<td><input style="width:500px" name="masterHotels[${hotel_index}].hotelName"  type="text" value="${hotel.hotelName}"/></td>
	           			<td><input style="width:50px"  name="masterHotels[${hotel_index}].weight" type="text" value="${hotel.weight}"/></td>
          				<td><a href="/admin/deleteMasterHotel?name=${hotel.hotelName}">delete</a></td>
          		</tr>
           	</#list>
          </tbody>
   			</table>
    	</div>
			</form>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/js/jquery-1.9.1.min.js"></script>
    <script src="/js/bootstrap.js"></script>
  </body>
</html>
