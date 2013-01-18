<cfparam name="Attributes.srcDir" default="#expandPath( "../factory/" )#">
<cfparam name="Attributes.dstDir" default="#expandPath( "./" )#">

<cfparam name="Attributes.filename">


<cfset srcDir = Attributes.srcDir>
<cfset dstDir = Attributes.dstDir>
<cfset filename = Attributes.filename>

<cfset ext = listLast( filename, '.' )>


<cfswitch expression="#ext#">
	
	<cfcase value="jpg">
		
		<cfset mimetype = "image/jpeg">
	</cfcase>
	<cfcase value="gif,png" delimiters=",">

		<cfset mimetype = "image/#ext#">
	</cfcase>
	<cfcase value="css,js" delimiters=",">

		<cfset mimetype = "text/#ext#">
	</cfcase>
	<cfdefaultcase>
		
		<cfthrow type="UnsupportedType" message="files of type [#ext#] are not supported. supported types are: css, js, gif, jpg, png">
	</cfdefaultcase>
</cfswitch>


<cfset isText = listFirst( mimeType, '/' ) == "text">


<cfset src = srcDir & ( isText ? ext : 'img' ) & "/#filename#">
<cfset filepath = dstDir & ( isText ? ext : 'img' ) & "/#filename#.cfm">


<cfset template = "<cfsavecontent variable='content'>{static-content}</cfsavecontent>

	<cfsetting showdebugoutput='##false##'>
	{inline-part-1}	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='##false##' clientmanagement='##false##' applicationtimeout='##createtimespan( 1, 0, 0, 0 )##'>
				
		<cfset etag 	= '''{etag}'''>
		<cfset mimetype = '{mime-type}'>		

		<cfheader name='Expires' value='##getHttpTimeString( now() + 100 )##'>
		<cfheader name='Cache-Control' value='max-age=##86400 * 100##'>		
		<cfheader name='ETag' value='##etag##'>

		<cfif len( CGI.HTTP_IF_NONE_MATCH ) && ( CGI.HTTP_IF_NONE_MATCH == '##etag##' )>

			<!--- etag matches, return 304 !--->
			<cfheader statuscode='304' statustext='Not Modified'>
			<cfcontent reset='##true##' type='##mimetype##'><cfabort>
		</cfif>

		<!--- file was not cached; send the content !--->
		{send-content}<cfabort>
	{inline-part-2}">


<cfif isText>
	
	<cfset staticContent = fileRead( src )>

	<cfset sendContent = "<cfcontent reset='##true##' type='##mimetype##'><cfoutput>##content##</cfoutput>">

	<cfset inlinePart1 = "">
	<cfset inlinePart2 = "">

<cfelse>

	<cfset staticContent = toBase64( fileReadBinary( src ) )>

	<cfset sendContent = "<cfcontent reset='##true##' type='##mimetype##' variable='##toBinary( content )##'>">

	<cfset inlinePart1 = "<cfif getBaseTemplatePath() == getCurrentTemplatePath()>">
	<cfset inlinePart2 = "<cfelse>

		<cfcontent reset='##true##'><cfoutput>content:image/#mimeType#;base64,##content##</cfoutput><cfabort>
	</cfif>">

</cfif>


<cfset etag = hash( staticContent )>

<cfset content = replace( template, "{static-content}", staticContent )>

<cfset content = replace( content, "{etag}", etag, 'all' )>
<cfset content = replace( content, "{mime-type}", mimeType, 'all' )>

<cfset content = replace( content, "{send-content}", sendContent, 'all' )>

<cfset content = replace( content, "{inline-part-1}", inlinePart1 )>
<cfset content = replace( content, "{inline-part-2}", inlinePart2 )>


<cfset fileWrite( filepath, trim( content ) )>


<cfoutput>

	<p>Generated file #filepath# from #src#
</cfoutput>