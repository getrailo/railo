<cfcomponent extends="Cache">
	
    <cfset fields=array(
		
	)>
    
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.runtime.cache.ram.RamCache">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string">
    	<cfreturn "RamCache">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Create a Ram Cache (in Memory Cache)">
    </cffunction>
    
</cfcomponent>