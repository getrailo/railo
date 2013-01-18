<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAACXBIWXMAAAsTAAALEwEAmpwYAAAABGdBTUEAANjr9RwUqgAAACBjSFJNAABuJwAAc68AAPd6AACFVQAAcPsAAOOkAAAyqwAAHJY/IjnCAAABgElEQVR42pTSzSvDARwG8AcbzRA28pKXtsMi89KQ0ArZJHmJjBZx2U3KJC8HxZaDXJzIvOTgMsNtXi7swE1JotDIeygvxeTl8Rf8fua5f3r69n2C4Gfa6sttk1aTK1VbVLx7dLLhFyoy1tkPNxfIDhVf+ltZUGYckPyFDIYK++j4VP/u5Tk27jTQBf/g7uriSxQZK2psj68e3ngnaDKZ2dzZS61O5wQQI4j0esOQ92GH/LSSjkjOdJupyihYBhAhiPLyS0bO7rdJXxc/JxPp7qilRqNxiaLsnMLh42sP+W6lz6HmkqWKypS0RQAKQaTNzLXte9fJ7x5+ONRcaDcwOl69CCBSEKVn6ex7p26SfeR0MufNpZQrk5YBhAsiiTSiesU1RnKQnFNxulHPkKh4l2gTEBhsqizZe3D38na2jI6mXIaExThFbwIgkcrCGuIUssy1zQNMrV7Tc3zj/Hl7sgB4Fn1yqFxuiY1L8AVIZVsAWgDI/N2vEoAeQCL+kd8BAJdvnSeNEzLrAAAAAElFTkSuQmCC</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''52250B8520B51A07EE474CC1F0D8EDF2'''>
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