(function(){
	cyljq(document).ready(
		function() 
		{
			cyljq("body").on("click", "#hotelSearchWidgetSearchButton", function(){
		        c = document.cookie.split('; ');
		        for(i=c.length-1; i>=0; i--)
		        {
		           C = c[i].split('=');
		           if (C[0] == 'JSESSIONID')
		           {
		        	   var jsession = C[1];  
		           }	   
		           else if (C[0]=='www_sid')
		           {
		        	   var wwwsid= C[1];
		           }
		        }
		        if (typeof window.nodcActionUpdated === 'undefined')
		        {
			        origAction = cyljq('.hotelSearchForm').attr("action");
			        if (origAction !== 'undefined' && origAction && origAction !== '')
			        {
				        //quote this cookie becuase it contains ':'
				        document.cookie = 
				        	'parent_url="'+origAction.substring(origAction.indexOf("?")+1, origAction.length)+'";domain=.www.neworleans.com; path=/';
			            if (typeof jsession !== 'undefined')
			            {
			            	document.cookie = 'parent_jsession_id='+jsession+";domain=.www.neworleans.com; path=/";
			            }
			            if (typeof wwwsid !== 'undefined')
			            {
			            	document.cookie = 'parent_sid='+wwwsid+";domain=.www.neworleans.com; path=/";
			            }
			            cyljq('.hotelSearchForm').attr("action", "http://book.www.neworleans.com/startSearch");        	
			        }
		        }
	            window.nodcActionUpdated=true;
	            
	            cyljq('.magLink').attr('href', 'http://book.www.neworleans.com/results');
			});
		});
})();