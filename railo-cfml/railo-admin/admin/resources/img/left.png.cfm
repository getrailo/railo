<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "3EC72C74146C81AF245B65E50C3DF31D" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAKoAAAAKCAYAAAAkasVsAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAIFJREFUeNrsl70JgDAUBv3DGQQHsBUElxCsBPcQXEJwCRdyizeHX8BK0vsCd3BFUn4cgeRmlkUo5SwX2cn+vQP4hSpyF8K85Mg84IXic97kTaTg+UU95M4k4PlFXYkUvIday5MpwHuo4WffMgV4D3ViBkgh1IEZIIVQG2YA7zwCDABuSQmi3wj8OgAAAABJRU5ErkJggg==')#" />
