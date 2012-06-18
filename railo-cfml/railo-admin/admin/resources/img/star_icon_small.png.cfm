<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "796BDC75C47D5379497BD340FC7B586F" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAAsAAAALCAYAAACprHcmAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAXxJREFUeNo8kc1OFEEUhb/qqmboyTgwkkggKATckeDW+BKsCBujxLDmAdj4LpIYd24NGxckbpREN6AkY0z8CWSGTEM30A5dXZ4elUoqla773XNPnzLhxSxEBhpDnSVYs0Jit7kon+OrL4QYijEwgYj/KzgQT5RsMn1/HZc8w0sEd4P8hU0FvobNMo3OU1ozkHQ2dLdEJWWqEeYwrBK5Bzg7R2PqIe2ZNpmHidk7nBcvCf4DZfilhn0TXi10ub2wSHtS30ETtCspWQ21tT1ZOU8h+74fqbjJIP3GQGq5gAsVC1nKa0j0mZrz/IjSb6ka3tI7fUL3Z/cGvNT1b52ZlL/+OKTfeyzuneNg5H8Pm3+mwxLu39/XQRQF9IpPRPY9/UhpZLVCdJd44h5hHK7ldyjoWuNtS03NeU59m75XGg0F7v0czVvzZGlgcLxDcbVL0lplcnoNM7Y42tZ+dMTKMY5T8rM3DE52CeWOXnRImr4mu9pT7RGtZl77+iPAAKrmjpujSKwVAAAAAElFTkSuQmCC')#" />
