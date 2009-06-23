<!---- load ExtensionManager ---->
<cfset manager=createObject('component','extension.ExtensionManager')>

<cfset detail=getDetail(url.provider,url.app)>
<cfset isUpdate=StructKeyExists(detail,'installed')>

<!--- create config struct --->
<cfset config=manager.createConfig()>

<!--- loadcfc --->
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
        provider="#detail.url#"
        
        id="#detail.app.id#"
        version="#detail.app.version#"
        name="#detail.app.name#"
        label="#detail.app.label#"
        description="#detail.app.description#"	
        category="#detail.app.category#"	
        image="#detail.app.image#"	
        
        author="#detail.app.author#"	
        codename ="#detail.app.codename#"	
        video="#detail.app.video#"	
        support="#detail.app.support#"	
        documentation="#detail.app.documentation#"	
        forum="#detail.app.forum#"	
        mailinglist="#detail.app.mailinglist#"	
        network="#detail.app.network#"	
        created="#detail.app.created#"
        >
    <cfif len(message) EQ 0><cfset message=stText.ext.installDone></cfif>
    <cfset session.confirm.text=message>    
    <cfset session.confirm.success=true>    
    <cflocation url="#request.self#?action=#url.action#&action2=confirm">
	
</cfif>







