(function(){
	$(document).ready(
		function() 
		{
	        c = document.cookie.split('; ');
        	jsession = '';
        	wwwsid = '';
	        for(i=c.length-1; i>=0; i--)
	        {
	           C = c[i].split('=');
	           if (C[0] == 'JSESSIONID')
	           {
	        	   jsession = C[0]+"="+C[1];  
	           }	   
	           else if (C[0]=='www_sid')
	           {
	        	   wwwsid= C[0]+"="+C[1];
	           }
	        }
            document.cookie = 'parent_cookie='+jsession+"___"+wwwsid+';domain=.www.neworleans.com';
			$('.hotelSearchForm').attr("action", "http://qa.www.neworleans.com/startSearch");
		});
})();