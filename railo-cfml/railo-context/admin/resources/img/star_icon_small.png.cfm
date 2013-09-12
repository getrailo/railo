<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAAsAAAALCAYAAACprHcmAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAXxJREFUeNo8kc1OFEEUhb/qqmboyTgwkkggKATckeDW+BKsCBujxLDmAdj4LpIYd24NGxckbpREN6AkY0z8CWSGTEM30A5dXZ4elUoqla773XNPnzLhxSxEBhpDnSVYs0Jit7kon+OrL4QYijEwgYj/KzgQT5RsMn1/HZc8w0sEd4P8hU0FvobNMo3OU1ozkHQ2dLdEJWWqEeYwrBK5Bzg7R2PqIe2ZNpmHidk7nBcvCf4DZfilhn0TXi10ub2wSHtS30ETtCspWQ21tT1ZOU8h+74fqbjJIP3GQGq5gAsVC1nKa0j0mZrz/IjSb6ka3tI7fUL3Z/cGvNT1b52ZlL/+OKTfeyzuneNg5H8Pm3+mwxLu39/XQRQF9IpPRPY9/UhpZLVCdJd44h5hHK7ldyjoWuNtS03NeU59m75XGg0F7v0czVvzZGlgcLxDcbVL0lplcnoNM7Y42tZ+dMTKMY5T8rM3DE52CeWOXnRImr4mu9pT7RGtZl77+iPAAKrmjpujSKwVAAAAAElFTkSuQmCC</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''8D52ECA7B35B3CB2F501F98BFAA3D832'''>
		<cfset mimetype = 'image/png'>		

		<cfheader name='Expires' value='#getHttpTimeString( now() + 100 )#'>
		<cfheader name='Cache-Control' value='max-age=#86400 * 100#'>		
		<cfheader name='ETag' value='#etag#'>

		<cfif len( CGI.HTTP_IF_NONE_MATCH ) && ( CGI.HTTP_IF_NONE_MATCH == '#etag#' )>

			<!--- etag matches, return 304 !--->
			<cfheader statuscode='304' statustext='Not Modified'>
			<cfcontent reset='#true#' type='#mimetype#'><cfabort>
		</cfif>

		<!--- file was not cached; send the content !--->
		<cfcontent reset='#true#' type='#mimetype#' variable='#toBinary( content )#'><cfabort>
	<cfelse>

		<cfcontent reset='#true#'><cfoutput>content:image/image/png;base64,#content#</cfoutput><cfabort>
	</cfif>