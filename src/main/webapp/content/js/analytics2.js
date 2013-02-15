var cmJv="1.5";

//setup variables for cmx calls
var cmxCatID = "Booking Results";

cmxCreateCookie("cmxcategory",'"' + cmxCatID + '"');
if (CMONOFF == "ON") {
	//if category cookie is set, over-ride this page ID
	if(cmxReadCookie("USECATEGORYID") == "TRUE") { cmxCatID = cmxReadCookie("CATEGORYID") };

	//build page ID from url + filename
	var cmxPageID = "/index.html"; 
	cmxPageID.toLowerCase();

	//execute cmx calls using above variables
	cmCreatePageviewTag(cmxPageID, cmxCatID);


	//execute section specific URL event attachment
	cmxExtractHrefs();
}