<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "E8B646F5AA1FEBE1880CC8377EFAD4CA" />
	<cfset mimetype = "image/gif" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=1000)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('R0lGODlhBAAKAIABAMzMzP///yH5BAEAAAEALAAAAAAEAAoAAAILjI8GBxu9oHzOhQIAOw==')#" />
