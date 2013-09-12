<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAActJREFUeNpcUrtKA0EUnd1MNjEkm0RCkDQWgaRSIY2NiJ+Qj7AQEYSUgoWdjfggCP5ATKl1bBRt7MROsLFxIYQNG7Kb3bw8Z9kJwYHL3Llzzpn7GK3dbguuRCIR2nw+F5qmMXQB38F+pmKTyUQMh0MhpRSSCBVUPoA7hUKhMZvNRK/Xe9B1/YNkhaGvi2jxMJ1OxXg8JuDYNE2RyWQYbzBOUy/TdB4UkQuA3VQqVec5FouJZDJZR6y2jOHOVPdg2wCVsa/F4/FaNpuVvu+Hyvl83nRd9x53n0j5FyI/8F8kgI+GYZi5XE7AF6yLiqPRKFTnq6VSqQJSxfO8sDko50sCeArgJQ466/ufEhdICx8rAOecqd44juMjnSaaIfmCIkUdXhgwHsT3kWFLUg12B7KGi+tisWgo4NJ4RLfbdbEfpNPpFsWlZVnhRRAEr6jRR/GGaj9FFRGiI/gd9oAxiUDoILCBMWQgsPhJJLC7FMBYVpFVGYJWOA52kkQAqgRHvrBt+xt8iY+wTiIzgOgmHnijsFwMVMoqAfhiFtreRPgWIito/xHqOkRdWQhvqbrlUhOe+v3+HMpXEHiPPrqN/WQwGHQQI/lZ4f8EGADkNjnBGv5i7QAAAABJRU5ErkJggg==</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''302ACCA3A9213C55CA823679DBB22A49'''>
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