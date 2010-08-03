<cfcomponent extends="Cache">
	
    <cfset fields=array(
		field("Servers","servers","",true,"please define here a list of all Servers you wanna connect, please follow this pattern:<br> Host:Port&lt;new line><br> Host:Port&lt;new line><br>Host:Port","textarea")
		
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