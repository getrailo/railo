<cfsetting enablecfoutputonly="yes">
<cfinclude template="extension.functions.cfm">

<cfset stText.ext.free="Free">
<cfset stText.ext.price="Price">
<cfset stText.Buttons.installTrial="Install Trial">
<cfset stText.Buttons.installFull="Install Full Version">

<cfif StructKeyExists(form,'action2')>
	<cfset url.action2="install3">
</cfif>
<cfparam name="inc" default="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfif not isDefined('session.extFilter2')>
	<cfset session.extFilter.filter="">
	<cfset session.extFilter.filter2="">
	<cfset session.extFilter.category="">
	<cfset session.extFilter.name="">
	<cfset session.extFilter.provider="">
	<cfset session.extFilter2.category="">
	<cfset session.extFilter2.name="">
	<cfset session.extFilter2.provider="">
</cfif>

<cfadmin 
	action="getExtensionProviders"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="providers">
<cfset request.providers=providers>
    

<cfadmin 
    action="getExtensions"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    returnVariable="extensions">
 
<cfparam name="err" default="#struct(message:"",detail:"")#">
<!--- <cfset data=getData(providers,err)>--->


<!--- Action --->
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cftry>

	<cfswitch expression="#form.mainAction#">
	<!--- Filter --->
		<cfcase value="#stText.Buttons.filter#">
        	<cfif StructKeyExists(form,"filter")>
				<cfset session.extFilter.filter=trim(form.filter)>
            <cfelseif StructKeyExists(form,"filter2")>
				<cfset session.extFilter.filter2=trim(form.filter2)>
            <cfelseif StructKeyExists(form,"categoryFilter")>
				<cfset session.extFilter.category=trim(form.categoryFilter)>
                <cfset session.extFilter.name=trim(form.nameFilter)>
                <cfset session.extFilter.provider=trim(form.providerFilter)>
            <cfelse>
				<cfset session.extFilter2.category=trim(form.categoryFilter2)>
                <cfset session.extFilter2.name=trim(form.nameFilter2)>
                <cfset session.extFilter2.provider=trim(form.providerFilter2)>
            </cfif>
		</cfcase>
        <cfcase value="#stText.Buttons.install#,#stText.Buttons.installFull#">
        	<cflocation url="#request.self#?action=#url.action#&action2=install1&uid=#form.uid#" addtoken="no">
		</cfcase>
        <cfcase value="#stText.Buttons.installTrial#">
        	<cflocation url="#request.self#?action=#url.action#&action2=install1&uid=#form.uid#&trial=true" addtoken="no">
		</cfcase>
        <cfcase value="#stText.Buttons.uninstall#">
        	<cflocation url="#request.self#?action=#url.action#&action2=uninstall&uid=#form.uid#" addtoken="no">
		</cfcase>
        <cfcase value="#stText.Buttons.update#">
        	<cflocation url="#request.self#?action=#url.action#&action2=install1&uid=#form.uid#" addtoken="no">
		</cfcase>
	</cfswitch>
<cfsavecontent variable="inc"><cfinclude template="#url.action#.#url.action2#.cfm"/></cfsavecontent>
	<cfcatch><cfrethrow>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>




<cfsetting enablecfoutputonly="no">
<!--- 
Error Output --->
<cfif len(err.message)>
<cfset err.message&="<br><br>(Railo still tries to load the failing Extension Providers in a background process)">
</cfif>
<cfset printError(err)>
<cfset printError(error)>

<cfoutput>#inc#</cfoutput>