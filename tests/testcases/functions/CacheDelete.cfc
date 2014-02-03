<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfset variables.cacheName="Test"&ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
	
	<cffunction name="testCacheDeleteEHCache" localMode="modern">
		<cfset createEHCache()>
		<cfset testCacheDelete()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheDeleteJBossCache" localMode="modern">
		<cfset createJBossCache()>
		<cfset testCacheDelete()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheDeleteRAMCache" localMode="modern">
		<cfset createRAMCache()>
		<cfset testCacheDelete()>
		<cfset deleteCache()>
	</cffunction>
	
	<cffunction access="private" name="testCacheDelete" localMode="modern">

<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cflock scope="server" timeout="1">
	<cfset cacheClear()>
	
	<cfset cachePut('abc','123')>
    <cfset valueEquals(left="#cacheCount()#", right="1")>
    <cfset cacheDelete('abc')>
    <cfset valueEquals(left="#cacheCount()#", right="0")>
    <cfset cacheDelete('feg')>
    
    <cftry>
        <cfset cacheDelete('def',true)>
        <cfset fail("must throw:there is no entry in cache with key [DEF]")>
        <cfcatch></cfcatch>
    </cftry>
    
    <cfset cachePut('abc','123')>
    <cfset valueEquals(left="#cacheCount()#", right="1")>
    <cfset cacheDelete('abc',false)>
    <cfset valueEquals(left="#cacheCount()#", right="0")>
    <cfset cacheDelete('feg')>
    
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