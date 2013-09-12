<!---- load ExtensionManager ---->
<cfset manager=createObject('component','extension.ExtensionManager')>

<cfif structKeyExists(url, 'uploadExt')>
	<cfset detail = session.uploadExtDetails />
	<cfset appendURL = "&uploadExt=1" />
<cfelse>
	<cfset detail=getDetailByUid(url.uid)>
	<cfset appendURL = "" />
</cfif>

<cfset isUpdate=StructKeyExists(detail,'installed')>

<!--- create config struct --->
<cfset config=manager.createConfig()>

<cfset install=manager.loadInstallCFC(form.repPath)>


<cfset done=true>

<cftry>
	<cfif isUpdate>
		<cfset message=install.update(form.repPath,config,detail.installed.config)>
    <cfelse>
		<cfset message=install.install(form.repPath,config)>
    </cfif>
	<cfcatch>
    	<cfset done=false>
        <cfif left(cfcatch.type,7) EQ 'config.'>
        	<cfset err[mid(cfcatch.type,8,1000)]=cfcatch.message>
        <cfelse>
        	<cfset err._message=cfcatch.message>
    	</cfif>
    	<cfinclude template="extension.applications.install2.cfm">
    </cfcatch>
</cftry>

<cfif done>
    <cfadmin 
        action="updateExtension"
        type="#request.adminType#"
        password="#session["password"&request.adminType]#"
        
        config="#config#"
        provider="#detail.data.provider#"
        
        id="#detail.data.id#"
        version="#detail.data.version#"
        name="#detail.data.name#"
        label="#detail.data.label#"
        description="#detail.data.description#"	
        category="#detail.data.category#"	
        image="#detail.data.image#"	
        
        author="#detail.data.author#"	
        codename ="#detail.data.codename#"	
        video="#detail.data.video#"	
        support="#detail.data.support#"	
        documentation="#detail.data.documentation#"	
        forum="#detail.data.forum#"	
        mailinglist="#detail.data.mailinglist#"	
        network="#detail.data.network#"	
        created="#detail.data.created#"
        >
    <cfif len(message) EQ 0><cfset message=stText.ext.installDone></cfif>
    <cfset session.confirm.text=message>    
    <cfset session.confirm.success=true>    
    <cflocation url="#request.self#?action=#url.action#&action2=confirm" />
</cfif>