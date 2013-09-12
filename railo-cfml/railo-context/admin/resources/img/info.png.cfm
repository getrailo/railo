<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAjxJREFUeNpsU71rGmEcfu/19DwRIloFv9CKtiEdVCQ0Q7YMdki2DIGODaVTh1A69h8opIF0KHTpFEKnQoZCp5a2oyXichC0goo9/FYOix93eV7ryXm9F/Te39fz+3h+L6dpGjEfRVGc9Xr9AX6bNpuNV1V1kE6nr10uV1UUxTVfzgjQ6/VIs9k8aLVaxxzH7SLQsTRpAGpRSq8ikci7UCgk2e32dYB2u+0qlUpv5vP5U57nidVhvtPpVPZ6vSeZTOaC+S0ABoMBKRQK7+FzjCyrAFSgQlbwEfFdoc5ms6nH49nP5XJfKATSaDQO4PTEFEycTufXbDbr9fl8p0zWDzLb+/3+eafT2aAoWUT5z6Dk1obDcWQymURGo9E2koSYbGrnnizLh3y1Wt2CvGPuF4DfotHoZ4DsDYfD+2YADJWxledrtdomnAWjEVURv9//KR6Pny2rUcrl8rZxuKxdANyh/D+tZkYHnc8rlcorJoMym9W+AFigGM6IAVqwdhfsPFoOVLACgO4vxYYVkFG2MP5xu90/2B1s/IZPxdwm2JEonGowXpmNwWDwdSqVesnkQCBwgftjpjculSAIlzz+1HA4/FaSpCPcA/qAwPMe3kJXHxwoe2jcE/R/GYvFfi42EVQRrPER3sIHIyNsyfTeF2sLKpmMSn4lk8l9ADRXbwE7TorFYh6DO4Vui2UzZtTLZ5kTicQLUNz47zWyde12uxug8HA8HufBsw8BFD4TDOzG4XB8ROB3DHUVdCvAABZeLiizjvGPAAAAAElFTkSuQmCC</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''5362BF24483D3AB54A2A5D88F66D3C52'''>
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