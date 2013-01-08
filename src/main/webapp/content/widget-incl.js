/*
 * This script is dependent upon widget-lib.js being loaded prior to this script.
 * 
 * This script interoperates with a custom Wicket response filter, JSONResponseFilter,
 * to allow wrapping of wicket components as "widgets" that communicate via JSON
 * requests, thus allowing cross-domain AJAX scripting.
 *
 * The following attributes should exist on this script tag (! denotes optional):
 * src="http://sub.domain.com/common/js/outside-wicket/widget-incl.js"
 * insertTo="[contentPlaceholderId]"
 * widget="[widgetUrl]"
 * !callbacksrc="http://sub.domain.com/someCallbacksScript.js"
 *
 * Example usage that will insert the widget returned from the "widget" url into the
 * the element with id "widgetPlaceHolder". This example will also include a callback
 * script for widget lifecycle callbacks:
 *
 * <script src="http://sub.domain.com/common/js/outside-wicket/widget-incl.js" 
 *  insertTo="widgetPlaceholder"
 *  widget="mytrip/app/HotelSearchWidget"
 *  callbacksrc="common/js/HotelSearchWidgetCallbacks.js"></script>
 *
 * @ Note that relative URLs for widget and callbacksrc will be resolved relative to the 
 *   host specified in the script src.
 *
 */

if ("undefined" == typeof Cyllenius)
	alert("Please load the widget-lib library from '/common/js/outside-wicket/widget-lib.js' prior to 'widget-incl.js'.");

// cyljq may not be loaded by widget-lib yet, so we should only use it after we know it exists
Cyllenius.Widget.onWidgetLibInitialized(function(){
	Cyllenius.Widget.loadWidget(cyljq("script[widget]:not([loadable])")[0]);
});