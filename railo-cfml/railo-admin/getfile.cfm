<!--- The Etag code was borrowed from Joe Roberts' Combine.cfc: http://combine.riaforge.org/ --->
<cfsetting showdebugoutput="no">

<cfparam name="url.file" default="" />
<cfset url.file = trim(replace(url.file, '\', '/', 'all')) />

<!--- we only allow relative paths starting at the current directory:
js/file.js
css/style.css
img/bla.png --->

<cfset allowedExtensions = {
	  css : {type:'text/css', binary:false, prependPath:""}
	, js  : {type:'application/javascript', binary:false, prependPath:""}
	, png  : {type:'image/png', binary:true, prependPath:"admin/resources/"}
	, gif  : {type:'image/gif', binary:true, prependPath:"admin/resources/"}
	, jpg  : {type:'image/jpg', binary:true, prependPath:"admin/resources/"}
} />
<cfset fileExt = listLast(url.file, '.') />
<cfif structKeyExists(allowedExtensions, fileExt)>
	<cfset cfmFile = allowedExtensions[fileExt].prependPath & url.file & ".cfm" />
</cfif>

<!--- security --->
<cfif find('..', url.file)
or find('//', url.file)
or left(url.file, 1) eq '/'
or not structKeyExists(allowedExtensions, fileExt)
or not fileExists(cfmFile)>
	<cfheader statuscode="404" statustext="Invalid request" />
	<cfoutput>Invalid request</cfoutput>
	<cfabort />
</cfif>

<!--- expire after 1 day: we want caching!
But only when we're not developing, so check for the mapping /railo-context/ --->
<cfif cgi.script_name eq '/railo-context/getfile.cfm'>
	<cfheader name="Expires" value="#GetHTTPTimeString(now() + 1)#" />
	<cfheader name="Cache-Control" value="max-age=86400" />
</cfif>

<!--- create a string to be used as an Etag - in the response header --->
<cfset lastModified = getFileDateLastModified(cfmFile) />
<cfset etag = lastModified & '-' & hash(cfmFile) />
<cfheader name="ETag" value="""#etag#""" />

<!--- check valid Etag--->
<cfif cgi.HTTP_IF_NONE_MATCH contains eTag>
	<!--- nothing has changed, return nothing --->
	<cfheader statuscode="304" statustext="Not Modified" />
	<cfcontent type="#allowedExtensions[fileExt].type#" reset="yes" /><!---
	---><cfabort />
<cfelse>
	<!--- if the request is a binary file (img for example), we expect to get a variable 'c' from the file,
	which contains a base64 encoded string of the binary data --->
	<cfif allowedExtensions[fileExt].binary>
		<cfsilent>
			<cfinclude template="#cfmFile#" />
		</cfsilent>
		<cfcontent reset="yes" type="#allowedExtensions[fileExt].type#" variable="#toBinary(c)#" />
	<!--- else just include the file --->
	<cfelse>
		<cfcontent reset="yes" type="#allowedExtensions[fileExt].type#" /><!---
		---><cfinclude template="#cfmFile#" /><!---
		---><cfabort />
	</cfif>
</cfif>

<cffunction name="getFileDateLastModified" access="private" returntype="string" output="no">
	<cfargument name="path" type="string" required="true" />
	<cfreturn createObject("java", "java.io.File").init(expandPath(arguments.path)).lastModified() />
</cffunction>