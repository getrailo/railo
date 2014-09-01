<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' 
				applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
	<cfscript>
		function useDefault(){
			data=toBinary("R0lGODlhMQApAIAAAGZmZgAAACH5BAEAAAAALAAAAAAxACkAAAIshI+py+0Po5y02ouz3rz7D4biSJbmiabqyrbuC8fyTNf2jef6zvf+DwwKeQUAOw==");
			content reset="yes" type="png" variable="#imageread(data)#";
			abort;
		}
	</cfscript>


	<cfsetting showdebugoutput="no">
	<cfparam name="url.width" default="80">
	<cfparam name="url.height" default="40">
	<cfset url.img=trim(url.img)>
	<cfset id=hash(url.img&"-"&url.width&"-"&url.height&"-"&getRailoId().web.securityKey)>
	<cfset mimetypes={png:'png',gif:'gif',jpg:'jpeg'}>
	
	<cfif len(url.img) ==0>
		<cfset ext="gif"><!--- using tp.gif in that case --->
	<cfelse>
	    <cfset ext=listLast(url.img,'.')>
	</cfif>

	<cfif !structkeyExists(mimetypes,ext)>
		<cfset useDefault()>
	</cfif>

	<cfheader name='Expires' value='#getHttpTimeString( now() + 100 )#'>
	<cfheader name='Cache-Control' value='max-age=#86400 * 100#'>	
	<cfset etag=hash(id)>	
	<cfheader name='ETag' value='#etag#'>

	<!--- etag matches, return 304 --->
	<cfif len( CGI.HTTP_IF_NONE_MATCH ) && ( CGI.HTTP_IF_NONE_MATCH == '#etag#' )>
		<cfheader statuscode='304' statustext='Not Modified'>
		<cfcontent reset='#true#' type='#mimetypes[ext]#'><cfabort>
	</cfif>

	<!--- copy and shrink to local dir --->
	<cfset tmpfile=expandPath("{temp-directory}/admin-ext-thumbnails/"&id&"."&ext)>

	<cfif fileExists(tmpfile)>
		<cffile action="readbinary" file="#tmpfile#" variable="data">
	<cfelseif len(url.img) ==0>
		<cfset useDefault()>
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
		<!-- resize image always for security reason, this way we avoid code injected that looks like a image -->
		<cfelse>
			<cfimage action="resize" source="#img#" height="#img.height#" width="#img.width#" name="img">
		</cfif>
		
		<cftry>
			<cffile action="write" file="#tmpfile#" output="#data#" createPath="true">
			<cfcatch><cfrethrow></cfcatch><!--- if it fails because there is no permission --->
		</cftry>
	</cfif>
	<cfcontent reset="yes" type="#mimetypes[ext]#" variable="#data#">