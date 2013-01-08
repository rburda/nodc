/*
 * This library is a prerequisite to using the widget-incl script and should be
 * included prior to any inclusions of widget-incl.js. This library dynamically
 * includes any other third-party scripts needed by the widget scripting mechanism.
 * It also offers various utility functions used by the widget-incl script.
 *
 * See http://vegaspedia.vegas.com/index.php?n=Dev.WicketJSONResponseFilter
 */

window.CylleniusSettings||(function(a){
	a.CylleniusSettings = {
		host: 'http://www.neworleans.com' //document.getElementsByTagName("script")[document.getElementsByTagName("script").length-1].src
	};
	//alert('host == ' + a.CylleniusSettings.host);
})(window);

// Cyllenius namespace
window.Cyllenius||(function(a){
	a.Cyllenius = {
		host: a.CylleniusSettings.host,
		getDomainFromUrl: function(url) {
			var domainrx = new RegExp("^.*\//[^\/]*","i");
			return url.match(domainrx);
		},
		isCrossDomain: function() {
			var libHost = Cyllenius.getDomainFromUrl(Cyllenius.host); // IE7 will return null if we are local
			return libHost != null && libHost != (a.location.protocol + "//" + a.location.host);
		},
		now: function(){
			return +new Date;
		},
		indexOf: function( elem, array ) {
			for ( var i = 0, length = array.length; i < length; i++ )
			// Use === because on IE, window == document
				if ( array[ i ] === elem )
					return i;

			return -1;
		},
		log: function(out) {
			if (typeof console != 'undefined')
				console.log(out);
			else
			{
				if (typeof Cyllenius.logs == 'undefined')
					Cyllenius.logs = [];
				Cyllenius.logs.push(out);
			}


		}
	};
})(window);

Cyllenius.Widget||(function(a)
{
	Cyllenius.Widget = {
		initialized: false,
		isDebugging: false,
		debug: function(debugHtml)
		{
			if (typeof Cyllenius.Widget.$debugWrapper == 'undefined')
			{
				Cyllenius.Widget.$debugWrapper = cyljq("<div class='ajaxDebugWrapper' style='background-color:#AAA;'><div></div>AJAX debug <span id='debugClear' onclick='cyljq(\"#debugClear ~ *\").remove();'>(clear)</span>:<div id=\"ajaxDebug\"></div></div>");
			}
			return Cyllenius.Widget.$debugWrapper.append(debugHtml);
		},
		outputDebug: function()
		{
			// Stored so we don't write to IE7 before body is loaded
			if (typeof Cyllenius.Widget.$debugWrapper != 'undefined')
				cyljq("body").append(Cyllenius.Widget.$debugWrapper);
		},
		_addedScripts: [],
		ajaxSubmitsOverridden: false,
		_widgetIds: {},
		_overrideAjaxSubmits: function(insertTo)
		{
			Cyllenius.Widget._widgetIds[insertTo] = {};

			// Since FF doesn't have a global window.event we track the clicked element this way
			cyljq("[onclick*='var wcall=wicketAjaxGet('], [onclick*='wicketAjaxPost(']", "#" + insertTo).each(function(idx, elem){
				elem.wicketonclick = elem.onclick;
				elem.onclick = function(){window.wicketTrigger=this.id;elem.wicketonclick();};
			});
			
			cyljq("[onchange*='var wcall=wicketAjaxGet('], [onchange*='wicketAjaxPost(']", "#" + insertTo).each(function(idx, elem){
				elem.wicketonchange = elem.onchange;
				elem.onchange = function(){window.wicketTrigger=this.id;elem.wicketonchange();};
			});

			if(!Cyllenius.Widget.ajaxSubmitsOverridden && "undefined" != typeof Wicket && "undefined" != typeof Wicket.Ajax)
			{
				Cyllenius.Widget.ajaxSubmitsOverridden = true;
				// Save a reference to the Wicket createTransport method to revert to it after initial JSON request
				if ("undefined" == typeof Cyllenius.Widget.wicketCreateTransport)
					Cyllenius.Widget.wicketCreateTransport = Wicket.Ajax.createTransport;

				// Override the wicket AJAX methods in order to catch the success event after all resources are loaded
				if (typeof(wicketSubmitFormById) != "undefined")
				{
					if (typeof(Cyllenius.Widget.origWicketSubmitFormById) == "undefined")
					{
						Cyllenius.Widget.origWicketSubmitFormById = wicketSubmitFormById;
					}
					if (typeof(Cyllenius.Widget.origWicketSubmitForm) == "undefined")
					{
						Cyllenius.Widget.origWicketSubmitForm = wicketSubmitForm;
					}
					if (typeof(Cyllenius.Widget.origWicketAjaxPost) == "undefined")
					{
						Cyllenius.Widget.origWicketAjaxPost = wicketAjaxPost;
					}
					if (typeof(Cyllenius.Widget.origWicketAjaxGet) == "undefined")
					{
						Cyllenius.Widget.origWicketAjaxGet = wicketAjaxGet;
					}

					var findParentWidgetId = function(childElem){
						if (typeof(childElem) == "string")
							childElem = cyljq("#" + childElem);
						for(var id in Cyllenius.Widget._widgetIds)
						{
							var $parentWidgetElem = childElem.closest("#" + id);
							if ($parentWidgetElem.length > 0)
								return id;
						}
						return "";
					};
					var onSuccess = function(successHandler, childElem){
						var widgetId = findParentWidgetId(childElem);
						if (typeof(successHandler) != "undefined" && successHandler != null)
							successHandler();

						Cyllenius.Widget._overrideAjaxSubmits(widgetId);
						if (typeof(widgetId) != 'undefined')
						{
							Cyllenius.Widget._executeCustomCallback(widgetId, 'onAfterDynamicLoad');
						}
						// Need to clear Wicket AJAX transports, otherwise Wicket will try to reuse the last transport which is likely
						// not our JSON overridden transport.
						Wicket.Ajax.transports = [];
					};

					wicketSubmitFormById = function(formId, url, submitButton, successHandler, failureHandler, precondition, channel){
						Cyllenius.Widget.origWicketSubmitFormById(formId, url, submitButton, function(){onSuccess(successHandler, formId);}, failureHandler, precondition, channel);
					};
					wicketSubmitForm = function(form, url, submitButton, successHandler, failureHandler, precondition, channel){
						Cyllenius.Widget.origWicketSubmitForm(form, url, submitButton, function(){onSuccess(successHandler, form);}, failureHandler, precondition, channel);
					};
					wicketAjaxPost = function(url, body, successHandler, failureHandler, precondition, channel){
						Cyllenius.Widget.origWicketAjaxPost(url, body, function(){onSuccess(successHandler, window.wicketTrigger);}, failureHandler, precondition, channel);
					};
					wicketAjaxGet = function(url, successHandler, failureHandler, precondition, channel){
						Cyllenius.Widget.origWicketAjaxGet(url, function(){onSuccess(successHandler, window.wicketTrigger);}, failureHandler, precondition, channel);
					};
				}
				Wicket.Ajax.createTransport = function(){
					var transport = {
							header: {},
							open: function(getOrPost, url, async){
								this.url = url;
							},
							setRequestHeader: function(attr, val){
								// JSON requests don't support setting headers, so we pass these
								// values as URL params and parse them in JSONResponseFilter
								this.header[attr] = val;
							},
							send: function(body){
								if (typeof(body) == "undefeined" || body == null)
									body = "nobody=true";
								for(attr in this.header)
								{
									body+=  (body.search("&$") != -1 ? "" : "&") + encodeURIComponent(attr) + "=" + encodeURIComponent(this.header[attr]);
								}
								cyljq.getJSON(this.url + "&" + body + "&json=true&jsoncallback=?", function(data){
									transport.responseText = data.response;
									if (a.ActiveXObject) {
										// wicket expects a doc object in IE
										var xmldoc = new ActiveXObject("Microsoft.XMLDOM");
									    xmldoc.async="false";
									    xmldoc.loadXML(data.response);
										transport.responseXML = xmldoc;
									}
									else
										transport.responseXML = data.response;
									transport.readyState = 4;
									transport.status = "";
									transport.ajaxRedirectURL = data.ajaxRedirectURL;
									transport.hasFeedback = data.hasFeedback;
									transport.onreadystatechange();

									if (typeof(data.ajaxRedirectURL) == "undefined" || data.ajaxRedirectURL == null || data.ajaxRedirectURL == "")
									{
										// Set back to wicket createTransport so that we don't request wicket resources using JSON.
										Cyllenius.Widget.ajaxSubmitsOverridden = false;
										Wicket.Ajax.createTransport = Cyllenius.Widget.wicketCreateTransport;
									}
								});
							},
							getResponseHeader: function(attr){
								if (attr == 'Ajax-Location' && typeof(transport.ajaxRedirectURL) != 'undefined')
								{
									return transport.ajaxRedirectURL;
								}
								else
									return null;
							},
							onreadystatechange: function(){},
							abort: function(){}
						};
					return transport;
				};
			}
		},
		/*
		 * Note that overriding FORM submits will work better when using a Wicket
		 * QueryStringCodingStrategy. If using a coding strategy where parameters are
		 * coded to the path, Wicket can have problems due to the form being submitted
		 * with a GET and all params appended to the URL via a query string.
		 */
		_overrideSubmits: function(insertTo)
		{
			Cyllenius.Widget._executeCustomCallback(insertTo, "onBeforeOverrideSubmits");

			var goNoGo = false;

			// jQuery serialize doesn't pick up submit button values, so we handle it here
			cyljq("#" + insertTo + " form :submit").click(function() {
				cyljq(":submit", cyljq(this).parents("form")).removeAttr("clicked");
				cyljq(this).attr("clicked", "true");
			});

			cyljq("#" + insertTo + " form").submit( function(evt) {
				Cyllenius.Widget._executeCustomCallback(insertTo, "onBeforeJSONFormSubmit", this);
				// jquery.serialize doesn't pick up submit button values, so we do it manually
				var $subFromBtn = cyljq(":submit[clicked=true]", this);
				var subBtnVal = "";
				if ($subFromBtn.attr("name"))
					subBtnVal = "&" + $subFromBtn.attr("name") + "=" + $subFromBtn.val();
				cyljq.ajax({
					url: cyljq(this).attr("action").replace(/(.*IFormSubmitListener::\/).*/, "$1"),
					type: "GET",
					async: true,
					dataType: "jsonp",
					data: cyljq(this).serialize() + subBtnVal + "&JSONFormSubmit=true",
					jsonp: "json=true&jsoncallback",
					form: this
				}).done(function(data) {
						if (data.hasFeedback)
						{
							data.form = this.form; // keep in scope
							cyljq.holdReady(true);
							Cyllenius.Widget.ajaxSubmitsOverridden = false;
							Cyllenius.Widget._loadContentScripts(data.response,
								function(content){
									var $insertTo = cyljq('#' + insertTo);
									if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: purple;'>Finding insertion point " + insertTo + "; wait: " + cyljq.readyWait + "; found: " + $insertTo.length + "</span></div>");
									$insertTo.html(content); // Need to insert form first so next line scripts execute properly
									$insertTo.append("<script>cyljq(document).ready(function(){Cyllenius.Widget._executeCustomCallback('" + insertTo + "', 'onAfterJSONFormHasFeedback', cyljq('#" + data.form.id + "')[0]);Cyllenius.Widget._overrideSubmits('" + insertTo + "');Cyllenius.Widget._overrideAjaxSubmits('" + insertTo + "');Cyllenius.Widget._executeCustomCallback('" + insertTo + "', 'onAfterDynamicLoad');});<\/script>");
									cyljq.holdReady(false);
									Cyllenius.Widget._onAllWidgetResourcesLoaded($insertTo);
							});
							goNoGo = false;
						}
						else if (data.redirectURL)
						{
							// Using setTimeout so back button works in FF.
							if (typeof this.form.target != 'undefined')
							{
								if (this.form.target == '_parent')
									setTimeout('top.location="' + data.redirectURL + '";', 0);
								else if (this.form.target == '_blank')
								{
									a.open(data.redirectURL);a.close();
								}
								else
									setTimeout('location.href="' + data.redirectURL + '"', 0);
							}
							else
								setTimeout('location.href="' + data.redirectURL + '"', 0);
						}
						else
						{
							goNoGo = true;
						}
						Cyllenius.Widget._executeCustomCallback(insertTo, "onAfterJSONFormSuccess", this.form, [data]);
				}).fail(function (XMLHttpRequest, textStatus, errorThrown) {
						Cyllenius.Widget._executeCustomCallback(insertTo, "onJSONFormError", this.form, errorThrown);
					});
				return goNoGo;
			});
		},
		_onAfterInitialLoad: function(insertTo)
		{
			Cyllenius.Widget._executeCustomCallback(insertTo, "onAfterInitialLoad");
		},
		_executeCustomCallback: function(insertTo, callbackName, ctx, args)
		{
			if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: navy;'>Executing custom callback: '" + callbackName + "' on '" + insertTo + "'</span></div>");
			// If callback script overrides
			if (insertTo in Cyllenius.Widget &&
				callbackName in Cyllenius.Widget[insertTo])
			{
				return Cyllenius.Widget[insertTo][callbackName].apply(ctx, args || []);
			}
		},
		_loadWidget: function(script)
		{
			if ("undefined" == typeof cyljq(script).attr("widgetIncluded"))
			{
				var widgetUrl =  cyljq(script).attr("widget");
				var insertTo = cyljq(script).attr("insertTo");
				var server = Cyllenius.getDomainFromUrl(script.src) || a.location.protocol + "//" + a.location.host;

				var callbacksrc = cyljq(script).attr("callbacksrc");
				if ("undefined" != typeof callbacksrc)
				{
					callbacksrc = ( (callbacksrc.indexOf("/") == 0 || callbacksrc.indexOf("http") == 0) ? callbacksrc : "/" + callbacksrc);
					Cyllenius.Includer.includeJavaScript(callbacksrc, function(){
						doAjaxLoad(insertTo, widgetUrl, server);
					});
				}
				else
					doAjaxLoad(insertTo, widgetUrl, server);

				cyljq(script).attr("widgetIncluded", "1");
			}

			function doAjaxLoad(insertTo, widgetUrl, server)
			{
				var modifiedUrl = Cyllenius.Widget._executeCustomCallback(insertTo, "onBeforeInitialLoad", null, [widgetUrl]);
				if (modifiedUrl)
					widgetUrl = modifiedUrl;

				var loadSuccess = false;

				function ajaxFail(XHR, textStatus, errorThrown) {
					if(insertTo in Cyllenius.Widget &&
						"onLoadError" in Cyllenius.Widget[insertTo])
						Cyllenius.Widget._executeCustomCallback(insertTo, "onLoadError", null, [insertTo, errorThrown]);
					else
						cyljq('#' + insertTo).html("<div title=\"" + errorThrown + "\">Unable To load widget '" + insertTo + "'...</div>");
					cyljq.holdReady(false);
				}

				var ajaxOpts = {
					url: (widgetUrl.indexOf("http") == 0 ? widgetUrl : server + (widgetUrl.indexOf("/") == 0 ? widgetUrl : "/" + widgetUrl)),
					type: "GET",
					dataType: "jsonp",
					cache: true,
					jsonp: "json=true&jsoncallback"
				};
				// jQuery has problems with https widget requests redirected to http unless they are requested as crossDomain
				if (a.location.protocol.indexOf("https") > -1)
					ajaxOpts.crossDomain=true;

				cyljq.ajax(ajaxOpts).done(function(data)
				{
					loadSuccess = true;
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: green;'>Loaded initial widget script contents and processing response: " + this.url + "</span></div>");
					Cyllenius.Widget._loadContentScripts(data.response,
						function(content){
							var insertWidget = function(preSelected){
								var $insertTo = preSelected || cyljq('#' + insertTo);
								if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: purple;'>Finding insertion point " + insertTo + "; wait: " + cyljq.readyWait + "; found: " + $insertTo.length + "</span></div>");
								$insertTo.html(content);
								$insertTo.append("<script>cyljq().ready(function(){Cyllenius.Widget._overrideSubmits('" + insertTo + "');Cyllenius.Widget._overrideAjaxSubmits('" + insertTo + "');Cyllenius.Widget._executeCustomCallback('" + insertTo + "', 'onAfterDynamicLoad');Cyllenius.Widget._onAfterInitialLoad('" + insertTo + "');});<\/script>");
								Cyllenius.Widget._onAllWidgetResourcesLoaded($insertTo);
							};
							var $insertTo = cyljq('#' + insertTo);
							if ($insertTo.length > 0)
								insertWidget($insertTo);
							else
							{
								if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: red;'>Delaying loading till ready " + insertTo + "; wait: " + cyljq.readyWait + "; found: " + $insertTo.length + "</span></div>");
								cyljq().ready(function(){insertWidget(false);});
							}
						}, insertTo);
				}).fail(ajaxFail);

				// cross domain errors with JSONP do not fire, so we need to look for success manually
				if (Cyllenius.isCrossDomain())
					setTimeout(function(){if (!loadSuccess){ajaxFail(undefined, undefined, "Failed to load.");}}, 5000);
			}
		},
		_onAllWidgetResourcesLoaded: function(div)
		{
			if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: red;'>(" + cyljq.readyWait + ") Done loading initial widget script contents: " + div.attr("id") + "</span></div>");

			// GW - 4/1/2010 since this is dynamically added HTML, need to let TL know about change
			if (div.length > 0) VDCtoTL.tlProcessNode(div[0]);

			if (Cyllenius.Widget.isDebugging)
			{
				Cyllenius.Widget.debug("<div><span style='color: navy;'>Number of widgets loading: " + cyljq.readyWait + "</span></div>");
				if (cyljq.readyWait <= 0)
				{
					Cyllenius.Widget.debug("<div><span style='color: red;'>ALL WIDGETS PROCESSED!</span></div>");
					Cyllenius.Widget.outputDebug();
				}
			}
		},
		_loadContentScripts: function ( html, callback, widgetId )
		{
			/*
				Purpose: To strip script tags from content and make sure they are injected
						 in to the current DOM and called. Note that IE will load scripts
						 immediately when they are converted to Objects if they have a
						 "src" attribute, hence we rename the attribute to srcdelay first.
			*/
			html = html.replace(/<script ([^>]*)src="(.*?)"(.*?)>/g, "<script $1srcdelay=\"$2\"$3>");
			var workingHtml = cyljq(html);
			var scripts = workingHtml.filter('script[srcdelay$=".js"]').clone();


			var scriptLoadCount = scripts.length;

			// If no scripts then just callback
			if ( scriptLoadCount == 0 ){
				callback( html );
				return this;
			}

			// Otherwise strip the scripts from the HTML
			html = workingHtml.not('script[srcdelay$=".js"]');

			var done = false;

			var __onAfterScriptProcessed = function() {
				scriptLoadCount--;
				// Call the CB if all are loaded
				if ( scriptLoadCount == 0 && !done ){
					cyljq.holdReady(false);
					callback( html );
					done = true;	//only call once
				}
			};

			var __isScriptLoaded = function(url){
				return cyljq("SCRIPT[src_='" + url + "']", "HEAD").length != 0;
			};

			// Handler to check whether all scripts have been loaded
			var __handler = function ( e ){
				var loadedSrc = this.url.replace(/\?.*/, "");

				//Wicket compatability hack
				//---------------------------------------------------------
				// This is here to make sure wicket doesn't add duplicate scripts when an AJAX response
				// is processed. See wicket-ajax.js Wicket.Head.containsElement.
				var wicketCompScr = cyljq(document.createElement("script"));
				wicketCompScr.attr({type: "text/plain", src_: loadedSrc});
				var isLoaded = __isScriptLoaded(loadedSrc);
				if (typeof Wicket != 'undefined' && typeof Wicket.Head != 'undefined' && !Wicket.Head.containsElement(wicketCompScr[0], "src") && !isLoaded)
				{
					var head = document.getElementsByTagName("head")[0] || document.documentElement;
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: blue;'>Adding fake script to head for wicket compatability: " + loadedSrc + "</span></div>");
					head.insertBefore( wicketCompScr[0], head.firstChild );
				}
				else if (!isLoaded)
				{
					var head = document.getElementsByTagName("head")[0] || document.documentElement;
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: blue;'>Adding fake script to head for multi-ajax wicket compatability: " + loadedSrc + "</span></div>");
					head.insertBefore( wicketCompScr[0], head.firstChild );
				}
				//---------------------------------------------------------
				__onAfterScriptProcessed();
			};

			/*
			 * Multiple async widgets may exist on a page that load the same script resources.
			 * A dependent script may have already been queued up to load by a prior widget so
			 * this function delays adding this widget's html content to the page until all
			 * of the dependent script resources are loaded.
			 */
			var __waitForLoad = function(url){
				if (!__isScriptLoaded(url)) // Script was added but not yet loaded
				{
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: #EEEE4C;'>'" + widgetId + "' WAITING FOR: " + url + "</span></div>");
					setTimeout(function(){__waitForLoad(url);}, 200);
				}
				else // script was added and loaded
				{
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: #EED700;'>'" + widgetId + "' AFTER LOAD: " + url + "</span></div>");
					__onAfterScriptProcessed();
				}
			};

			// Add them seperately and set the callback
			scripts.each(function(){
				var url = cyljq(this).attr("srcdelay");
				// Don't double add scripts already added, like wicket-ajax.js
				if(cyljq.inArray(url, Cyllenius.Widget._addedScripts) == -1)
				{
					Cyllenius.Widget._addedScripts.push(url);
					if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color: #EEEEBD;'>'" + widgetId + "' ADDED FOR LOADING: " + url + "</span></div>");
					cyljq.ajax({ url: url, data: undefined, cache: true, dataType: "script"}).done(__handler);
				}
				else
					__waitForLoad(url);
			});

			return this;
		},
		// This is a badly named function...the injected widget does not need to be in a popup.
		popupWidget: function(insertToId, widgetUrl, onLoadedCallback) {
			Cyllenius.Widget.onWidgetLibInitialized(function(){
				if (typeof onLoadedCallback != 'undefined')
				{
					if (typeof Cyllenius.Widget[insertToId] == 'undefined')
						Cyllenius.Widget[insertToId] = {};
					if (typeof Cyllenius.Widget[insertToId].onAfterInitialLoad != 'undefined')
					{
						var priorOAIL = Cyllenius.Widget[insertToId].onAfterInitialLoad;
						Cyllenius.Widget[insertToId].onAfterInitialLoad = function(){priorOAIL(); onLoadedCallback();};
					}
					else
						Cyllenius.Widget[insertToId].onAfterInitialLoad = onLoadedCallback;
				}

				Cyllenius.Widget.ajaxSubmitsOverridden = false; // In case wicket-ajax.js gets included with the popup
				var fakescript = {
					src: Cyllenius.getDomainFromUrl(Cyllenius.host) + "/", // only used to get domain
					widget: widgetUrl,
					insertTo: insertToId
					};
				Cyllenius.Widget.loadWidget(fakescript);
			});
		},
		//Alias for clearer naming
		insertWidget: function(insertToId, widgetUrl, onLoadedCallback){
			Cyllenius.Widget.popupWidget.apply(Cyllenius.Widget, arguments);
		},
		loadWidgetAfterDomLoaded: function(script)
		{
			Cyllenius.Widget.loadWidget(script);
		},
		loadWidget: function(script)
		{
			// widget-incl selects based on this attribute
			cyljq(script).attr("loadable", "1");
			cyljq.holdReady(true);
			Cyllenius.Widget._loadWidget(script);
		},
		onWidgetLibInitialized: function(callback) // called from widget-incl
		{
			if (Cyllenius.Widget.initialized)
				callback();
			else
				setTimeout(function(){Cyllenius.Widget.onWidgetLibInitialized(callback);}, 10);
		}
	};

	Cyllenius.Includer = {
		loading: [],
		loaded: [],
		readyCallbacks: [],
		includeJavaScript: function(jsFile, onloadCallback, cache){
			var head = document.getElementsByTagName("head")[0];
			var script = document.createElement("script");

			if (cache === false)
			{
				var ts = Cyllenius.now();
				// try replacing _= if it is there
				var ret = jsFile.replace(/(\?|&)_=.*?(&|$)/, "$1_=" + ts + "$2");
				// if nothing was replaced, add timestamp to the end
				jsFile = ret + ((ret == jsFile) ? (jsFile.match(/\?/) ? "&" : "?") + "_=" + ts : "");
			}

			var domain = Cyllenius.getDomainFromUrl(Cyllenius.host);
			if ((jsFile.indexOf("http://") == 0 || jsFile.indexOf("https://") == 0) || domain == null)
				script.src = jsFile;
			else
				script.src = domain + jsFile;

			this.loading.push(script.src);

			// Handle Script loading
			var done = false;

			// Attach handlers for all browsers
			script.onload = script.onreadystatechange = function(){
				if ( !done && (!this.readyState ||
						this.readyState == "loaded" || this.readyState == "complete") ) {
					done = true;
					if (typeof onloadCallback != 'undefined')
						onloadCallback();
					Cyllenius.Includer.loaded.push(Cyllenius.Includer.loading.splice(Cyllenius.indexOf(this.src, Cyllenius.Includer.loading)[0], 1));
					if (Cyllenius.Includer.loading.length == 0)
					{
						while(Cyllenius.Includer.readyCallbacks.length > 0)
						{
							Cyllenius.Includer.readyCallbacks.shift()();
						}
					}
					head.removeChild( script );
				}
			};

			head.appendChild(script);

			// We handle everything using the script element injection
			return undefined;
	 	},
	 	onRequestedScriptsLoaded: function(callback){
	 		if (this.loading.length == 0)
				callback();
			else
				this.readyCallbacks.push(callback);
	 	}
	};

	Cyllenius.Includer.includeJavaScript('/common/js/jquery/jquery-core.js', function(){
		if (typeof(cyljq) == 'undefined')
		{
			alert("jQuery variable was not assigned to 'cyljq' to avoid Cyllenius jQuery conflicts. Please add 'cyljq = jQuery;' to the end of the jquery-core.js.");
			return;
		}

		// Protect against jQuery conflicts on external sites
		if (Cyllenius.isCrossDomain())
		{
			if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug("<div><span style='color:orange;'>Moving cyllenius jQuery to cyljq namespace...jQuery: " + jQuery.fn.jquery + "; cyljq: " + cyljq.fn.jquery + "</span></div>");

			cyljq = cyljq.noConflict(true);
		}

		if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug().ajaxSuccess(function(evt, request, settings){
			cyljq(this).append("<div>" + cyljq.active + " Successful request for " + settings.url + " " + settings.async + "</div>");
		});

		if (Cyllenius.Widget.isDebugging)Cyllenius.Widget.debug().ajaxError(function(evt, request, settings, exception){
			cyljq(this).append("<div><span style='color: red;'>" + cyljq.active + " Errored request for " + settings.url + ": " + exception + "</span></div>");
			cyljq.holdReady(false);
		});

		Cyllenius.Widget.initialized = true;
	});
	// setting window.loaded because because the wicket-event js does a
	// document.write in IE otherwise, which doesn't work when loaded via AJAX
	a.loaded = true;
	// Including wicket-event JS so we can insure it is loaded prior to
	// wicket-ajax.js.
	Cyllenius.Includer.includeJavaScript('/mytrip/app/resources/org.apache.wicket.markup.html.WicketEventReference/wicket-event.js');
	// Cyllenius.Includer.includeJavaScript('/mytrip/app/resources/org.apache.wicket.ajax.WicketAjaxReference/wicket-ajax.js');

	//GW - adding TL facade in the cases where only the widget lib is included
	//TeaLeaf facade
	a.VDCtoTL = a.VDCtoTL || function () {

		var TLexists = function () {
			return a.TeaLeaf != null && a.TeaLeaf != 'undefined' && a.TeaLeaf != undefined;
		};

		return {
			tlQueueKey : function (e) {
				if (!e) e = a.event;
				if (TLexists() && TeaLeaf.Client && TeaLeaf.Client.tlQueueKey)
					TeaLeaf.Client.tlQueueKey(e);
			},

			tlAddEvent : function (e) {
				if (!e) e = a.event;
				if (TLexists() && TeaLeaf.Client && TeaLeaf.Client.tlAddEvent)
					TeaLeaf.Client.tlAddEvent(e);
			},

			tlProcessNode : function (el) {
				if (TLexists() && TeaLeaf.Client && TeaLeaf.Client.tlProcessNode)
					TeaLeaf.Client.tlProcessNode(el);
			}
		};
	}();
})(window);