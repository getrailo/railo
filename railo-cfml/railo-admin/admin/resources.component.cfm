<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">

<cfadmin 
	action="getComponent"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="component">
    
    
<cfadmin 
	action="getComponentMappings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="mappings">


<cfset flushName="#stText.Buttons.flush# (#structCount(componentCacheList())#)">

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cftry>
	<cfswitch expression="#form.mainAction#">
	
	
	
	
    
    
    
    
    
    
    
		<cfcase value="#flushName#">
           <cfset componentCacheClear()>
            
            
        </cfcase>
        <cfcase value="#stText.Buttons.Update#">
        
        
        	              
                
            <!--- create Archive --->
			<cfset doDownload=form.subAction EQ stText.Buttons.downloadArchive>
			<cfif doDownload or form.subAction EQ stText.Buttons.addArchive>
            	
				<cfsetting requesttimeout="3000">		
				<cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.secure=toArrayFromForm("secure")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfset data.trusteds[idx]=isDefined("data.trusteds[#idx#]") and data.trusteds[idx]>
						<cfset data.secure[idx]=isDefined("data.secure[#idx#]") and data.secure[idx]>
					
					<cfif data.secure[idx]>
						<cfset ext='ras'>
					<cfelse>
						<cfset ext='ra'>
					</cfif>
					<cfset target=getTempDirectory() & Rand() & "."&ext>
					<cfset filename=data.virtuals[idx]>
					<cfset filename=mid(filename,2,len(filename))>
					<cfif len(filename)>
						<cfset filename="cfc-archive-"&filename&"."&ext>
					<cfelse>
						<cfset filename="cfc-archive-root."&ext>
					</cfif>
					<cfset filename=Replace(filename,"/","-","all")>
					
					
					<cfif not doDownload>
						<cfset target=expandPath("#cgi.context_path#/railo-context/archives/"&filename)>
						<cfset count=0>
						<cfwhile fileExists(target)>
							<cfset count=count+1>
							<cfset target="#cgi.context_path#/railo-context/archives/"&filename>
							<cfset target=replace(target,'.'&ext,count&'.'&ext)>
							<cfset target=expandPath(target)>
						</cfwhile>
					</cfif>
					<cfadmin 
						action="createComponentArchive"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						file="#target#"
						virtual="#data.virtuals[idx]#"
						secure="#data.secure[idx]#"
						append="#not doDownload#"
						remoteClients="#request.getRemoteClients()#">
						<cfif doDownload><CFHEADER NAME="Content-Disposition" VALUE="inline; filename=#filename#"><!--- 
						 ---><cfcontent file="#target#" deletefile="yes" type="application/unknow"></cfif>
					</cfif>
				</cfloop>
			
        
        
        
            <!--- compile mapping --->
			<cfelseif form.subAction EQ "#stText.Buttons.compileAll#">
            
				<cfsetting requesttimeout="3000">		
				<cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.stoponerrors=toArrayFromForm("stoponerror")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfset data.trusteds[idx]=isDefined("data.trusteds[#idx#]") and data.trusteds[idx]>
						<cfset data.toplevels[idx]=isDefined("data.toplevels[#idx#]") and data.toplevels[idx]>
						<cfset data.stoponerrors[idx]=isDefined("data.stoponerrors[#idx#]") and data.stoponerrors[idx]>
					
					<cfadmin 
						action="compileComponentMapping"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						virtual="#data.virtuals[idx]#"
						stoponerror="#data.stoponerrors[idx]#"
			remoteClients="#request.getRemoteClients()#">
						
					</cfif>
				</cfloop>
  
                
                
                
			
            
            <!--- delete mapping --->
            <cfelseif form.subaction EQ stText.Buttons.Delete>
            
            	<cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
					
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfadmin 
							action="removeComponentMapping"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							
							virtual="#data.virtuals[idx]#"
			remoteClients="#request.getRemoteClients()#">
					
						<!--- <cfset admin.removeCustomTag(data.virtuals[idx])> --->
					</cfif>
				</cfloop>
           <cfelseif form.subaction EQ stText.Buttons.Update>
            	
                <cfset data.virtuals=toArrayFromForm("virtual")>
				<cfset data.physicals=toArrayFromForm("physical")>
				<cfset data.archives=toArrayFromForm("archive")>
				<cfset data.primaries=toArrayFromForm("primary")>
				<cfset data.trusteds=toArrayFromForm("trusted")>
				<cfset data.rows=toArrayFromForm("row")>
				
                <cfloop index="idx" from="1" to="#arrayLen(data.physicals)#">
					<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">
						<cfset data.trusteds[idx]=isDefined("data.trusteds[#idx#]") and data.trusteds[idx]>
						
						<cfdump var="#data.trusteds[idx]#">
					<cfadmin 
						action="updateComponentMapping"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						virtual="#data.virtuals[idx]#"
						physical="#data.physicals[idx]#"
						archive="#data.archives[idx]#"
						primary="#data.primaries[idx]#"
						trusted="#data.trusteds[idx]#"
						remoteClients="#request.getRemoteClients()#">
                	</cfif>
				</cfloop><cfdump var="#form.subaction#">
            <cfelse>
                <cfadmin 
                    action="updateComponent"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    baseComponentTemplate="#form.baseComponentTemplate#"
                    componentDumpTemplate="#form.componentDumpTemplate#"
                    componentDataMemberDefaultAccess="#form.componentDataMemberDefaultAccess#"
                    triggerDataMember="#isDefined('form.triggerDataMember')#"
                    useShadow="#isDefined('form.useShadow')#"
                    componentDefaultImport="#form.componentDefaultImport#"
                    componentLocalSearch="#isDefined('form.componentLocalSearch')#"
                    componentPathCache="#isDefined('form.componentPathCache')#"
                    deepSearch="#isDefined('form.componentDeepSearchDesc') and form.componentDeepSearchDesc EQ true#"
						
                    
                    remoteClients="#request.getRemoteClients()#"
                    >
            </cfif>
            
            
            
            
            
			
		
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updateComponent"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				baseComponentTemplate=""
				componentDumpTemplate=""
				componentDataMemberDefaultAccess=""
				triggerDataMember=""
				useShadow=""
                componentPathCache=""
                componentDefaultImport=""
                componentLocalSearch=""
                deepSearch=""
				remoteClients="#request.getRemoteClients()#">
		
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<!--- list all mappings and display necessary edit fields --->


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>
<!--- Error Output --->
<cfset printError(error)>


<cfif url.action2 EQ "create">
	<cfinclude template="resources.component.edit.cfm">
<cfelse>
	<cfinclude template="resources.component.list.cfm">
</cfif>