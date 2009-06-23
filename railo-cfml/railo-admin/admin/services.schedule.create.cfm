<cfset more=struct()>
<cfif isDefined('form.port')>
	<cfset more.port=form.port>
</cfif>


<cfadmin 

	attributeCollection="#more#"

	action="schedule" 
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	
	scheduleAction="update" 
	operation="httprequest"
	task="#trim(form.name)#"
	url="#form.url#"
	interval="#form.interval#" 
	startdate="#nullIfNoDate('start')#" 
	starttime="#nullIfNoTime('start')#"
	remoteClients="#request.getRemoteClients()#">
    
    <cfif StructKeyExists(form,"paused") and form.paused>
    	<cfadmin 
                    action="schedule" 
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    scheduleAction="pause" 
                    task="#trim(form.name)#"
                    remoteClients="#request.getRemoteClients()#">
    </cfif>
    
		
			<!---
<cfschedule action="update" operation="httprequest"
		 	task="#trim(form.name)#"
			url="#form.url#"
			interval="#form.interval#" 
			startdate="#nullIfNoDate('start')#" 
			starttime="#nullIfNoTime('start')#" port="#form.port#">
			--->
			
<cflocation url="#request.self#?action=#url.action#&action2=edit&task=#hash(form.name)#" addtoken="no">