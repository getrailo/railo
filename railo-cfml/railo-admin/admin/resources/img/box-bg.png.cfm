<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAABcAAAAfCAIAAACDG8GaAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAH5JREFUeNrsk0EKACEMA636/5/5Bp/Ro7tQCKEiLKsHEXuQgCGOrUopJUyXqOp8SgwrKrfWdklZc6OdUs7r7nks573d++o+s4jIApYfdPcfDVJSStxXDIh1X9g1ETEUB8URzsBOs0Uod3JvhYF5zZZrrQBjTnNDcLTzvOsjwABwaWsihJUTlwAAAABJRU5ErkJggg==</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''EC38625ED2A5EC67F76E059F294DBBE4'''>
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