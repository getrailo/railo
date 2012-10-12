<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "BF697A09DA126B524BE374202DB0EC20" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('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')#" />
