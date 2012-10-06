<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "D24B1150CC601E19DC449CC030421541" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAERJREFUeNpisbFz+M9AALCAiMMH9zPiUmBr7/ifCZsguhgTAxGABZsJMDbMGXBFMAGQAnQ3EmUdhiJsPmXB5SNkABBgANI5F5L+njplAAAAAElFTkSuQmCC')#" />
