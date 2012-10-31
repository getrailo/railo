<cfset c='iVBORw0KGgoAAAANSUhEUgAAAAQAAAAfCAIAAABPgvtxAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAINJREFUeNrEkDsKBCEMhqOoWAiewML738bGzhtoI+ID0Q0zU0wxs8U2+xchjz8JfOCcCyGMMfbe1FrLOffet9ZIrRUAUkqlFLoPaa1zzgwznBBCMFK46b24dh6Kd9svB/7y9IvtVO9dCMHWWggVcUopWYxxzokthM6Qv1LKGIO2jwADAIhxYhIjefe6AAAAAElFTkSuQmCC'><cfif getBaseTemplatePath() EQ getCurrentTemplatePath()><!---
	
	---><cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "AB3362CDDED41699D8338F82F57FFEE8" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary(c)#" />
<cfelse>data:image/image/png;base64,<cfoutput>#c#</cfoutput></cfif>
	
