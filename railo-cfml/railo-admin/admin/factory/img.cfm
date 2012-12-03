<cfdirectory directory="../factory/img" action="list" name="imgs">
<cfset mimetypes={png:'image/png',gif:'image/gif', jpg:"image/jpg", swf:"application/x-shockwave-flash"}>

<cfloop query="imgs">
	<cfoutput>#imgs.name#</cfoutput>
	<cfset ext=listLast(imgs.name,'.')>
	<cfif structKeyExists(mimetypes, ext)>
		<cffile action="readbinary" file="../factory/img/#imgs.name#" variable="data">

<cfsavecontent variable="imgFileCode">{{cfset c='<cfoutput>#toBase64(data)#</cfoutput>'>{{cfif getBaseTemplatePath() EQ getCurrentTemplatePath()>{{!---
	
	--->{{cfsilent>
	{{cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	{{cfif not structKeyExists(application, "oHTTPCaching")>
		{{cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	{{/cfif>
	
	{{!--- the string to be used as an Etag - in the response header --->
	{{cfset etag = "<cfoutput>#hash(createUUID())#</cfoutput>" />
	{{cfset mimetype = "<cfoutput>#mimetypes[ext]#</cfoutput>" />
	
	{{!--- check if the content was cached on the browser, and set the ETag header. --->
	{{cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		{{cfexit method="exittemplate" />
	{{/cfif>
{{/cfsilent>

{{!--- file was not cached; send the data --->
{{cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary(c)#" />
{{cfelse>data:image/<cfoutput>#mimetypes[ext]#</cfoutput>;base64,{{cfoutput>#c#{{/cfoutput>{{/cfif>
	
</cfsavecontent>
		<cfset imgFileCode = replace(imgFileCode, '{{', '<', 'all') />
	
		<cffile 
        	action="write" 
            addnewline="no" 
            file="../resources/img/#imgs.name#.cfm" 
            output="#imgFileCode#"
            fixnewline="no">
		<cfoutput> is done<br /></cfoutput>
	<cfelse><cfoutput> Has invalid extension<br /></cfoutput></cfif>
</cfloop>