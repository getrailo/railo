<cfcomponent>
	
    <cfset state="stopped">
    
	
	<cffunction name="init" access="public" output="no" returntype="void">
		<cfargument name="config" required="false" type="struct">
		<cfargument name="listener" required="false" type="component">
    
	</cffunction>

	<cffunction name="start" access="public" output="no" returntype="void">
		<cfset systemOutput("start",true)>
        <cftry>
        	<cfset state="starting">
            <cfset sleep(1000)>
			...
         	<cfset state="running">
        	<cfcatch>
            	 <cfset state="failed">
                 <cfrethrow>
            </cfcatch>
        </cftry>
	</cffunction>

	<cffunction name="stop" access="public" output="no" returntype="void">
		<cfset systemOutput("stop",true)>
        <cftry>
        	<cfset state="stopping">
            <cfset sleep(1000)>
			...
         	<cfset state="stopped">
        	<cfcatch>
            	 <cfset state="failed">
                 <cfrethrow>
            </cfcatch>
        </cftry>
	</cffunction>

	<cffunction name="restart" access="public" output="no" returntype="void">
		<cfset systemOutput("restart",true)>
        <cfif state EQ "running"><cfset stop()></cfif>
		<cfset start()>
	</cffunction>

	<cffunction name="getHelper" access="public" output="no" returntype="any">
		<cfset systemOutput("getHelper",true)>
        
        <cfreturn "HelperReturnData">
	</cffunction>

	<cffunction name="getState" access="public" output="no" returntype="string">
		<cfset systemOutput("getState",true)>
        
        <cfreturn state>
	</cffunction>

	<cffunction name="sendMessage" access="public" output="no" returntype="string">
		<cfargument name="data" required="false" type="struct">
		
        <cfset systemOutput("sendMessage:",true)>
        <cfset systemOutput("- data:"&serialize(data),true)>
	</cffunction>

</cfcomponent>