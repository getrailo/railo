<cfcomponent extends="Layout">
	
    <cfset fields=array(
		)>
    
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.commons.io.log.log4j.layout.ClassicLayout">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="false">
    	<cfreturn "Classic">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Same logging layout as with Railo 1 - 4.1">
    </cffunction>
    
</cfcomponent>