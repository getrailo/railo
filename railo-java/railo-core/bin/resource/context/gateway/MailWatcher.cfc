<cfcomponent>
	
    
    <cfset state="stopped">
	
	<cffunction name="init" access="public" output="no" returntype="void">
		<cfargument name="id" required="false" type="string">
		<cfargument name="config" required="false" type="struct">
		<cfargument name="listener" required="false" type="component">
    	<cfset variables.id=id>
        <cfset variables.config=config>
        <cfset variables.listener=listener>
        
        <cflog text="init" type="information" file="MailWatcher">
        
	</cffunction>


	<cffunction name="start" access="public" output="no" returntype="void">
		<cfwhile state EQ "stopping">
        	<cfset sleep(10)>
        </cfwhile>
        <cfset variables.state="running">
        
        
        <cflog text="start" type="information" file="MailWatcher">
		
        
        <cfset var last=now()>
        <cfset var mail="">
        <cfwhile variables.state EQ "running">
        	<cftry>
				<cfset mails=getMailsNewerThan(config.server,config.port,config.username,config.password,config.attachmentpath,last)>
                <cfloop array="#mails#" index="el">
                	<cfset variables.listener[config.functionName](el)>
                </cfloop>
                
                <cfcatch>
                	<cflog text="#cfcatch.message#" type="Error" file="MailWatcher">
                </cfcatch>
            </cftry>
            <cfset last=now()>
            
            <cfif variables.state NEQ "running">
            	<cfbreak>
            </cfif>
            <cfset sleep(config.interval)>
    	</cfwhile>
        <cfset variables.state="stopped">
        
	</cffunction>
    
    
    
    <cffunction name="getMailsNewerThan" returntype="array" output="yes">
        <cfargument name="server" type="string" required="yes">
        <cfargument name="port" type="numeric" required="yes">
        <cfargument name="user" type="string" required="yes">
        <cfargument name="pass" type="string" required="yes">
        <cfargument name="attachmentpath" type="string" required="yes">
        <cfargument name="newerThan" type="date" required="yes">
        
        <cfset var mails="">
        <cfset var arr=[]>
        <cfset var sct="">
        
        <cfpop 
            action="getall" 
            name="mails" 
            server="#arguments.server#" 
            port="#arguments.port#" 
            username="#arguments.user#" 
            password="#arguments.pass#" 
            attachmentpath="#arguments.attachmentpath#" 
            generateuniquefilenames="yes">
         
        
        <cfloop query="mails">
            <cfif mails.date GTE newerThan>
                <cfset sct={}>
                <cfloop index="col" list="#mails.columnlist#">
                    <cfset sct[col]=mails[col]>
                </cfloop>
                <cfset ArrayAppend(arr,sct)>
            </cfif>
        </cfloop>
        <cfreturn arr>
    </cffunction>

    

	<cffunction name="stop" access="public" output="no" returntype="void">
    	<cflog text="stop" type="information" file="MailWatcher">
		<cfset variables.state="stopping">
	</cffunction>

	<cffunction name="restart" access="public" output="no" returntype="void">
		<cfif state EQ "running"><cfset stop()></cfif>
        <cfset start()>
	</cffunction>

	<cffunction name="getState" access="public" output="no" returntype="string">
		<cfreturn state>
	</cffunction>

	<cffunction name="sendMessage" access="public" output="no" returntype="string">
		<cfargument name="data" required="false" type="struct">
		<cfreturn "ERROR: sendMessage not supported">
	</cffunction>

</cfcomponent>