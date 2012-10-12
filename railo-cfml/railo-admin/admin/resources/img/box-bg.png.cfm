<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "9F64458890DFB3D9C89AB3D912F97B5A" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAABcAAAAfCAIAAACDG8GaAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAH5JREFUeNrsk0EKACEMA636/5/5Bp/Ro7tQCKEiLKsHEXuQgCGOrUopJUyXqOp8SgwrKrfWdklZc6OdUs7r7nks573d++o+s4jIApYfdPcfDVJSStxXDIh1X9g1ETEUB8URzsBOs0Uod3JvhYF5zZZrrQBjTnNDcLTzvOsjwABwaWsihJUTlwAAAABJRU5ErkJggg==')#" />
