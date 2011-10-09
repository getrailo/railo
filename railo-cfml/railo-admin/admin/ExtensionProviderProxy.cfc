<cfcomponent>

	
	<cffunction name="init" output="no">
    	<cfargument name="cfcName" type="string">
        <cfset this.cfcName=arguments.cfcName>
    	<cfreturn this>
    </cffunction>
    
    
	<cffunction name="_getData" access="private" output="no">
    	<!--- session --->
        <cfif StructKeyExists(session,"cfcs") and StructKeyExists(session.cfcs,this.cfcName) and DateAdd("n",10,session.cfcs[this.cfcName].getInfo.lastModified) GT now()>
        	<cfset var info=session.cfcs[this.cfcName].getInfo>
            <cfif not StructKeyExists(info,'mode') or (info.mode NEQ "develop" and info.mode NEQ "development")>
        		<cfreturn session.cfcs[this.cfcName]>
            </cfif>
        </cfif>
        <!--- request --->
        <cfif StructKeyExists(request,"cfcs") and StructKeyExists(request.cfcs,this.cfcName)>
        	<cfreturn request.cfcs[this.cfcName]>
        </cfif>
        <cfset cfc= createObject('webservice',this.cfcName&"?wsdl")>
        <cfset var data.getInfo=cfc.getInfo()>
        <cfset data.getInfo.lastModified=now()>
        <cfset data.listApplications=cfc.listApplications()>
        <cfset session.cfcs[this.cfcName]=data>
        <cfset request.cfcs[this.cfcName]=data>
        
        <cfreturn data>
    </cffunction>

	<cffunction name="getInfo" access="remote" returntype="struct" output="no">
    	<cfreturn _getData().getInfo>
    </cffunction>
    
	<cffunction name="listApplications" access="remote" returntype="query" output="no">
    	<cfreturn _getData().listApplications>
    </cffunction>
    
	<cffunction name="getDownloadDetails" access="remote" output="no">
    	<cfargument name="type" required="yes" type="string">
        <cfargument name="serverId" required="yes" type="string">
        <cfargument name="webId" required="yes" type="string">
        <cfargument name="appId" required="yes" type="string">
    	<cfargument name="serialNumber" required="no" type="string">
    
    	<cfset cfc = createObject('webservice',this.cfcName&"?wsdl")>
        <cftry>
        <cfreturn cfc.getDownloadDetails(type,serverId,webId,appId,serialNumber)>
        	<cfcatch>
            <cfreturn cfc.getDownloadDetails(type,serverId,webId,appId)>
            </cfcatch>
        </cftry>
    </cffunction>
</cfcomponent>