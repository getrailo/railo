<cfcomponent hint="Note" extends="railo-context.admin.plugin.Plugin">
	
	<cffunction name="init"
		hint="this function will be called to initalize">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		
		<!--- lang --->
		<cfloop collection="#arguments.lang#" item="key">
			<cfset arguments.lang[key]=replace(arguments.lang[key],'{link}','<a target="_blank" href="http://dnsexchange.ch/#session.railo_admin_lang#/login.cfm">http://dnsexchange.ch</a>','all')>
		</cfloop>
		
		<!--- app --->
		<cfset app.ddns=load()>
		<cfif not isDefined('app.ddns.id')>
			<cfset app.ddns=struct()>
		</cfif>
		
		<cfparam name="app.ddns.id" default="">
		<cfparam name="app.ddns.enabled" default="false" type="boolean">
		<cfparam name="app.ddns.proxyserver" default="">
		<cfparam name="app.ddns.proxyport" default="">
		<cfparam name="app.ddns.proxyuser" default="">
		<cfparam name="app.ddns.proxypassword" default="">
		
	</cffunction>
	
	<cfscript>
	function nullIfEmpty(str) {
		str=trim(str);
		if(len(str) GT 0) return str;
	}
	</cfscript>

	<cffunction name="overview" output="yes"
		hint="load data for a single note">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		
		<cfset req.ddns=app.ddns>
			<cfset req.ddns.enabled=structKeyExists(app.ddns,'enabled') and app.ddns.enabled>
		
	</cffunction>
	
	<cffunction name="update" output="no"
		hint="update note">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		<cfset app.ddns.id=req.id>
		<cfset app.ddns.enabled=structKeyExists(req,'enabled') and req.enabled>
		<cfset app.ddns.proxyserver=req.proxyserver>
		<cfset app.ddns.proxyport=req.proxyport>
		<cfset app.ddns.proxyuser=req.proxyuser>
		<cfset app.ddns.proxypassword=req.proxypassword>

		<cfset save(app.ddns)>
		
		
		<cfif not IsNumeric(req.proxyport)><cfset req.proxyport=80></cfif>
		<cfif app.ddns.enabled>
			<cfschedule 
				startdate="#now()#"
				starttime="#now()#"
				hidden="false"
				action="update" 
				task="plugin-dnsexchange" 
				url="http://dnsexchange.ch/int.cfm?id=#req.id#&IP=#cgi.REMOTE_ADDR#" 
				
				interval="3600" 
				resolveurl="no" 
				
				proxyserver="#nullIfEmpty(req.proxyserver)#" 
				proxyport="#req.proxyport#" 
				proxyuser="#nullIfEmpty(req.proxyuser)#" 
				proxypassword="#nullIfEmpty(req.proxypassword)#">
		<cfelse>
			<cfschedule
				action="delete" 
				task="plugin-dnsexchange">
		</cfif>
		<cfreturn "redirect:overview">
	</cffunction>
</cfcomponent>