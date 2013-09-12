<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAKoAAAAKCAYAAAAkasVsAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAIFJREFUeNrsl70JgDAUBv3DGQQHsBUElxCsBPcQXEJwCRdyizeHX8BK0vsCd3BFUn4cgeRmlkUo5SwX2cn+vQP4hSpyF8K85Mg84IXic97kTaTg+UU95M4k4PlFXYkUvIday5MpwHuo4WffMgV4D3ViBkgh1IEZIIVQG2YA7zwCDABuSQmi3wj8OgAAAABJRU5ErkJggg==</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''B35C3F49354BDBACACD1A95C01656524'''>
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