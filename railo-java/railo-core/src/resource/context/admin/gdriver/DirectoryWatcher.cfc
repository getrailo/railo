<cfcomponent extends="Gateway">

	
    <cfset fields=array(
		field("Directory","directory","",true,"The directory you want to watch","text")
		,field("Watch subdirectory","recurse","true",true,"Should we watch the directory and all subdirectories too","checkbox")
		,field("Interval (ms)","interval","60000",true,"The interval between checks, in miliseconds","text")
		,field("Extensions","extensions","*",true,"The comma separated list of extensions to match (* = all files).","text")
		
		,group("CFC Listener Function Defintion","Definitation for the CFC Listener Functions, when empty no listener is called",3)
		
		,field("Change","changeFunction","onChange",true,"called when a file change","text")
		,field("Add","addFunction","onAdd",true,"called when a file is added","text")
		,field("Delete","deleteFunction","onDelete",true,"called when a file is removed","text")
		
		
	)>

	<cffunction name="getClass" returntype="string">
    	<cfreturn "">
    </cffunction>
	<cffunction name="getCFCPath" returntype="string">
    	<cfreturn "railo.extension.gateway.DummyGateway">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="no">
    	<cfreturn "Directory Watcher">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Watch a certain directory for changes">
    </cffunction>
    
	<cffunction name="onBeforeUpdate" returntype="void" output="false">
		<cfargument name="cfcPath" required="true" type="string">
		<cfargument name="startupMode" required="true" type="string">
		<cfargument name="custom" required="true" type="struct">
        
        <cfif not DirectoryExists(custom.directory)>
        	<cfthrow message="directory [#custom.directory#] does not exist">
        </cfif>
        <cfif not IsNumeric(custom.interval)>
        	<cfthrow message="interval [#custom.interval#] is not a numeric value">
        </cfif>
        
	</cffunction>
    
    
	<cffunction name="getListenerCfcMode" returntype="string" output="no">
		<cfreturn "none">
	</cffunction>
</cfcomponent>

