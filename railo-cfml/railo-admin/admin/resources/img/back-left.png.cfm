<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "B2FEC58CD3790085CA1601A7AAC592D7" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAAEAAAfnCAYAAAAarwKYAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADRJREFUeNrsxMEJACAMBLDD/efsGqdTCIXkkbRN/jYzPXkkSZIkSZIkSZIkSZIkSdrbFWAAacNYC4vpUEYAAAAASUVORK5CYII=')#" />
