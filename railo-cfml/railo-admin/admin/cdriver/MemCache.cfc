<cfcomponent extends="Cache">
	
    <cfset fields=array(
		field("Host","host","",true,"Host name or IP address of the memcache service"),
		field("Port","port","11211",true,"Port of the memcache service")
	)>
    
	<cffunction name="getClass" returntype="string">
    	<cfreturn "railo.extension.io.cache.memcache.MemCacheRaw">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string">
    	<cfreturn "MemCache">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "Memcache connection">
    </cffunction>
    
</cfcomponent>