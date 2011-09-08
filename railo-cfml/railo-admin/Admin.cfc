<cfcomponent>
	<cffunction access="remote" name="invoke" output="false">
		<cfargument name="type" required="yes" type="string">
		<cfargument name="password" required="yes" type="string">
		<cfargument name="attributeCollection" required="yes" type="struct">
		<cfargument name="callerId" required="no" type="string" default="undefined">
		
		<cfset var result="">
		<cfset var id=getRailoId()[arguments.type].id>
		<cfset var sec=getRailoId()[arguments.type].securityKey>
		<cfif not listFind(arguments.callerId,id)>
			<cfadmin 
				type="#arguments.type#"
				password="#Decrypt(arguments.password,sec)#"
				attributeCollection="#arguments.attributeCollection#"
				providedCallerIds="#arguments.callerId#"
				returnVariable="result">
			<cfif isDefined('result')><cfreturn result></cfif>
		</cfif>
	</cffunction>
    
    <cffunction access="remote" name="plugin" output="false" returntype="struct">
		<cfargument name="type" required="yes" type="string">
		<cfargument name="password" required="yes" type="string">
		<cfargument name="plugin" required="yes" type="string">
		<cfargument name="action" required="yes" type="string">
		<cfargument name="arg1" required="no" type="string" default="">
		<cfargument name="arg2" required="no" type="string" default="">
		<cfargument name="arg3" required="no" type="string" default="">
		<cfargument name="arg4" required="no" type="string" default="">
		<cfargument name="arg5" required="no" type="string" default="">
		<cfargument name="arg6" required="no" type="string" default="">
        
		<cfset var sec=getRailoId()[arguments.type].securityKey>
		<cfset pw=Decrypt(arguments.password,sec,'cfmx_compat','hex')>
		
        <cfadmin 
            action="connect"
            type="#arguments.type#"
            password="#pw#">
            
        <cfadmin 
            action="getPluginDirectory"
            type="#arguments.type#"
            password="#pw#"
            returnVariable="local.pluginDir">	
		<cfset mappings['/railo_plugin_directory/']=pluginDir>
        <cfapplication action="update" mappings="#mappings#" sessionmanagement="yes" 
	clientmanagement="no" 
	setclientcookies="yes" 
	setdomaincookies="no">
    
    	<cfset url.action="plugin">
    	<cfset url.plugin=arguments.plugin>
    	<cfset url.PluginAction=arguments.action>
        
        <cfset request.arg1=arguments.arg1>
        <cfset request.arg2=arguments.arg2>
        <cfset request.arg3=arguments.arg3>
        <cfset request.arg4=arguments.arg4>
        <cfset request.arg5=arguments.arg5>
        <cfset request.arg6=arguments.arg6>
        
    
        <cfset request.disableFrame=true>
        <cfset request.setCFApplication=false>
        <cfset request.adminType=arguments.type>
        <cfset session["password"&arguments.type]=pw>
        <cfset request.return={}>
        <cfsilent>
        <cfinclude template="admin/#arguments.type#.cfm">
        </cfsilent>
        <cfreturn request.return>
	</cffunction>

</cfcomponent>