<#macro isSelected current value>
	<#if ( current?matches(value) ) >selected</#if>
</#macro>

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
              <li class="active"><a href="/j_spring_security_logout">Logout</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>


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

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/js/jquery-1.9.1.min.js"></script>
    <script src="/js/bootstrap.js"></script>
  </body>
</html>
