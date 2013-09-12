<cfset detail=getDetailbyUid(url.uid)>




<cfadmin 
	action="getExtensionInfo"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="info">

<cfset detail.directory=info.directory>
   
<cfset session.confirm.text="">
<cfset session.confirm.success=true>

<!--- get REP --->
<cfset dest=detail.directory>
<cfif not DirectoryExists(dest)>
	<cfset session.confirm.text=stText.ext.uninstallMissingCFC>
    <cfset session.confirm.success=false>
</cfif>
<cfset dest=dest&"/"&url.uid>
<cfif not DirectoryExists(dest)>
	<cfset session.confirm.text=stText.ext.uninstallMissingCFC>
    <cfset session.confirm.success=false>
</cfif>
<cfset destFile="#dest#/#detail.installed.version#.rep">
<cfif not FileExists(destFile)>
	<cfset session.confirm.text=stText.ext.uninstallMissingCFC>
    <cfset session.confirm.success=false>
</cfif>

<!--- ALLOW to unisntall from old structure --->
<cfif not session.confirm.success>
	<cfset tmp=session.confirm>
	<cfset session.confirm.text="">
    <cfset session.confirm.success=true>
	
    <cfset dest=detail.directory>
    <cfif not DirectoryExists(dest)>
        <cfset session.confirm.text=stText.ext.uninstallMissingCFC>
        <cfset session.confirm.success=false>
    </cfif>
    <cfset dest=dest&"/"&hash(trim(detail.installed.provider))>
    <cfif not DirectoryExists(dest)>
        <cfset session.confirm.text=stText.ext.uninstallMissingCFC>
        <cfset session.confirm.success=false>
    </cfif>
    <cfset dest=dest&"/"&detail.installed.name>
    <cfif not DirectoryExists(dest)>
        <cfset session.confirm.text=stText.ext.uninstallMissingCFC>
        <cfset session.confirm.success=false>
    </cfif>
    <cfset destFile="#dest#/#detail.installed.version#.rep">
    <cfif not FileExists(destFile)>
        <cfset session.confirm.text=stText.ext.uninstallMissingCFC>
        <cfset session.confirm.success=false>
    </cfif>
    <cfif not session.confirm.success>
    	<cfset session.confirm=tmp>
    </cfif>
</cfif>


<cfif session.confirm.success>
	<cfset zip="zip://"&destFile&"!/">
    <cfset virtual='/install'>
    <cfset mappings[virtual]=zip>
    <cfapplication action="update" mappings="#mappings#">
    <cfset install=createObject('component',virtual&"/Install")>
    <cftry>
        <cfset session.confirm.text=install.uninstall(zip,detail.installed.config)>
        
        
        
        <cfif len(session.confirm.text) EQ 0><cfset session.confirm.text=stText.ext.uninstallDone></cfif>
        <cfset session.confirm.success=true> 
        <cfcatch>
            <cfset session.confirm.text=replace(stText.ext.uninstallMethodException,'{message}',cfcatch.message)>
            <cfset session.confirm.success=false>
        </cfcatch>
    </cftry>
</cfif>

<cfadmin 
    action="removeExtension"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    
    provider="#detail.installed.provider#"
    id="#detail.installed.id#">
<cftry>
	<cffile action="delete" file="#destFile#">
	<cfcatch></cfcatch>
</cftry>

<cflocation url="#request.self#?action=#url.action#&action2=confirm">