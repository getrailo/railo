<!--- The Etag code was borrowed from Joe Roberts' Combine.cfc: http://combine.riaforge.org/ --->

<cfparam name="url.file" default="" />
<cfset url.file = trim(replace(url.file, '\', '/', 'all')) />

<!--- we only allow relative paths starting at the current directory:
js/file.js
css/style.css
img/bla.png --->

<cfset allowedExtensions = {
	  css : {type:'text/css'}
	, js  : {type:'application/javascript'}
	, png  : {type:'image/png'}
	, gif  : {type:'image/gif'}
	, jpg  : {type:'image/jpg'}
} />
<cfset fileExt = listLast(url.file, '.') />


<!--- security --->
<cfif find('..', url.file)
	or find('//', url.file)
	or left(url.file, 1) eq '/'
	or not structKeyExists(allowedExtensions, fileExt)
	or not fileExists(url.file)>
	<cfheader statuscode="404" statustext="Invalid request" />
	<cfoutput>Invalid request</cfoutput>
	<cfabort />
</cfif>

<!--- expire after 1 day: we want caching! --->
<cfif cgi.script_name eq '/railo-context/getfile.cfm'>
	<cfheader name="Expires" value="#GetHTTPTimeString(now() + 1)#" />
	<cfheader name="Cache-Control" value="max-age=86400" />
</cfif>

<!--- create a string to be used as an Etag - in the response header --->
<cfset lastModified = getFileDateLastModified(url.file) />
<cfset etag = lastModified & '-' & hash(url.file) />
<cfheader name="ETag" value="""#etag#""" />

<!--- check valid Etag--->
<cfif cgi.HTTP_IF_NONE_MATCH contains eTag>
	<!--- nothing has changed, return nothing --->
	<cfheader statuscode="304" statustext="Not Modified" />
	<cfcontent type="#allowedExtensions[fileExt].type#" reset="yes" /><!---
	---><cfabort />
<cfelse>
	<cfcontent reset="yes" type="#allowedExtensions[fileExt].type#" file="#url.file#" />
</cfif>

<cffunction name="getFileDateLastModified" access="private" returntype="string" output="no">
	<cfargument name="path" type="string" required="true" />
	<cfreturn createObject("java", "java.io.File").init(expandPath(arguments.path)).lastModified() />
</cffunction>