<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAkFJREFUeNp8UjtvE0EQnt2zfT774iexCRJRYruBBFERQuSEWCSRaAJOiogS+AFQ0lPzD5BogIIWKRUCwkNKESFFsuwCjO00+Ow728iPe3n3mMM4AhSYZndn59PM981HVldzMBwyEAQhnsmkH4TCkU3G+CnLtgadTvtNs648wvsh/sPvQbLZFYjFIguXF5ee4WfG4/GAKIpgWiaoagt6vW6rUv5yr9vtPqWUHgOF1GwqdjWXe2XoRmpu7hzcvXMbFhYuwdKVRWDMBkVpSNF4fKultfZs264SQkbAa2vrD+Pxyevz83Owlb/5xziZdBppDKFSqQJ32ESn3X4x7krD4fC21+uBfP4GnBQbG+sQDAYgKMtrgkeYHOfpkPGo6BfhfxEKTQAlVEYNko7zC2jblm6a1j9ByAu8Xh9gvcW503UpujwpSv5aVTXY3d09EVgqlaDf74Omqu8YGx65OQfbCslE8nMkEt2pNxrSAAump89iBy/oug6FQgEODj5BsVhEoPbYNI0PY3HI8vIKcgjdOj9/4TkB8nOHgaAEciAAA92AarXm7hI459+Uen3Hsqz3lBLcYyoFpmkWNE376DhcRNecxmX7mqo2+Fouv1XV5pOAJF0UBE9CkqQ8dt3HFdWIazk3kDg4nINP9LmSJ5BHj3Fe44yB6Pdnp6bOvMSuEc5ZG/luCzMzs6OZXaVwfjwH6N0mnt/J6O16+cgw9H1ZljdRlxilQpb+raJbOLIVOc65nEzD2GsoyrZtWYfooPs/BBgAqkkNEn8lQmwAAAAASUVORK5CYII=</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''F2D4C05FFF56ACE847C89E1F7ADE661F'''>
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