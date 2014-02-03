<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfset variables.cacheName="Test"&ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
	
	<cffunction name="testCacheClearEHCache" localMode="modern">
		<cfset createEHCache()>
		<cfset testCacheClear()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheClearJBossCache" localMode="modern">
		<cfset createJBossCache()>
		<cfset testCacheClear()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheClearRAMCache" localMode="modern">
		<cfset createRAMCache()>
		<cfset testCacheClear()>
		<cfset deleteCache()>
	</cffunction>
	
	<cffunction access="private" name="testCacheClear" localMode="modern">

<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cflock scope="server" timeout="1">

<cfset cacheClear()>
	<cfset cachePut('abc','123')>
    <cfset valueEquals(left="#cacheCount()#", right="1")>
    <cfset cacheClear()>
    <cfset valueEquals(left="#cacheCount()#", right="0")>
    
    <cfset cachePut('abc','123')>
    <cfset valueEquals(left="#cacheCount()#", right="1")>
    <cfset cacheClear("*")>
    <cfset valueEquals(left="#cacheCount()#", right="0")>
    
    <cfset cacheClear("",cacheName)>
    <cfset cachePut('abc','123',CreateTimeSpan(1,1,1,1),CreateTimeSpan(1,1,1,1),cacheName)>
    <cfset valueEquals(left="#cacheCount(cacheName)#", right="1")>
    <cfset cacheClear("",cacheName)>
    <cfset valueEquals(left="#cacheCount(cacheName)#", right="0")>
    
    <cfset cachePut('abc','123',CreateTimeSpan(1,1,1,1),CreateTimeSpan(1,1,1,1),cacheName)>
    <cfset cachePut('abe','456',CreateTimeSpan(1,1,1,1),CreateTimeSpan(1,1,1,1),cacheName)>
    <cfset cachePut('afg','789',CreateTimeSpan(1,1,1,1),CreateTimeSpan(1,1,1,1),cacheName)>

    <cfset valueEquals(left="#cacheCount(cacheName)#", right="3")>
    <cfset cacheClear("ab*",cacheName)>

    <cfset valueEquals(left="#cacheCount(cacheName)#", right="1")>



</cflock>

</cfif>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
<cfscript>
	private function createRAMCache(){
		admin 
				action="updateCacheConnection"
				type="web"
				password="#request.webadminpassword#"
				
				
				name="#cacheName#" 
				class="railo.runtime.cache.ram.RamCache" 
				storage="false"
				default="object" 
				custom="#{timeToLiveSeconds:86400
					,timeToIdleSeconds:86400}#";
	}
	
	private function createEHCache() {
		admin 
				action="updateCacheConnection"
				type="web"
				password="#request.webadminpassword#"
				
				
				name="#cacheName#" 
				class="railo.runtime.cache.eh.EHCache" 
				storage="false"
				default="object" 
				custom="#{timeToLiveSeconds:86400
					,maxelementsondisk:10000000
					,distributed:"off"
					,overflowtodisk:true
					,maximumChunkSizeBytes:5000000
					,timeToIdleSeconds:86400
					,maxelementsinmemory:10000
					,asynchronousReplicationIntervalMillis:1000
					,diskpersistent:true
					,memoryevictionpolicy:"LRU"}#";
	}
		
	private function createJBossCache() {
		admin 
				action="updateCacheConnection"
				type="web"
				password="#request.webadminpassword#"
				
				default="object"
				name="#cacheName#" 
				class="railo.extension.cache.jboss.JBossCache" 
				storage="false"
				custom="#{timeToLiveSeconds:86400.0
					,minTimeToLiveSeconds:0
					,minElementsInMemory:0
					,memoryEvictionPolicy:"LRU"
					,timeToIdleSeconds:86400
					,maxElementsInMemory:10000}#";
	}
				
	private function deleteCache(){
		admin 
			action="removeCacheConnection"
			type="web"
			password="#request.webadminpassword#"
			name="#cacheName#";
						
	}
</cfscript>	
</cfcomponent>