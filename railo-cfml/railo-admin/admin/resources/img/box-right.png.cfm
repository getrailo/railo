<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "8A90A1E8AD336F684F47BA5F73F0B5DE" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAAQAAAAfCAIAAABPgvtxAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAINJREFUeNrEkDsKBCEMhqOoWAiewML738bGzhtoI+ID0Q0zU0wxs8U2+xchjz8JfOCcCyGMMfbe1FrLOffet9ZIrRUAUkqlFLoPaa1zzgwznBBCMFK46b24dh6Kd9svB/7y9IvtVO9dCMHWWggVcUopWYxxzokthM6Qv1LKGIO2jwADAIhxYhIjefe6AAAAAElFTkSuQmCC')#" />
