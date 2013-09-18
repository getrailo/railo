<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' 
				applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
			
	<cfsetting showdebugoutput="no">
	<cfparam name="url.width" default="80">
	<cfparam name="url.height" default="40">
	<cfset url.img=trim(url.img)>
	<cfset id=hash(url.img&"-"&url.width&"-"&url.height)>
	<cfset mimetypes={png:'png',gif:'gif',jpg:'jpeg'}>
	<cfset ext=listLast(url.img,'.')>
	
		
	<cfheader name='Expires' value='#getHttpTimeString( now() + 100 )#'>
	<cfheader name='Cache-Control' value='max-age=#86400 * 100#'>	
	<cfset etag=hash(id)>	
	<cfheader name='ETag' value='#etag#'>

	<!--- etag matches, return 304 !--->
	<cfif len( CGI.HTTP_IF_NONE_MATCH ) && ( CGI.HTTP_IF_NONE_MATCH == '#etag#' )>
		<cfheader statuscode='304' statustext='Not Modified'>
		<cfcontent reset='#true#' type='#mimetypes[ext]#'><cfabort>
	</cfif>


	<!--- copy and shrink to local dir --->
	<cfset tmpfile=expandPath("{temp-directory}/admin-ext-thumbnails/"&id&"."&ext)>	
	<cfif fileExists(tmpfile)>
		<cffile action="readbinary" file="#tmpfile#" variable="data">
	<cfelse>
		<cffile action="readbinary" file="#url.img#" variable="data">
		<cfimage action="read" source="#data#" name="img">
		
		<!--- shrink images if needed --->
		<cfif img.height GT url.height or img.width GT url.width>
			<cfif img.height GT url.height >
				<cfimage action="resize" source="#img#" height="#url.height#" name="img">
			</cfif>
			<cfif img.width GT url.width>
				<cfimage action="resize" source="#img#" width="#url.width#" name="img">
			</cfif>
			<cfset data=toBinary(img)>
		</cfif>
		
		<cftry>
			<cffile action="write" file="#tmpfile#" output="#data#" strict="false">
			<cfcatch></cfcatch><!--- if it fails because there is no permission --->
		</cftry>
	</cfif>
	
	<cfcontent reset="yes" type="#mimetypes[ext]#" variable="#data#">