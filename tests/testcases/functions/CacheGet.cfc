<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfset variables.cacheName="Test"&ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
	
	<cffunction name="testCacheGetEHCache" localMode="modern">
		<cfset createEHCache()>
		<cfset testCacheGet()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheGetJBossCache" localMode="modern">
		<cfset createJBossCache()>
		<cfset testCacheGet()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCacheGetRAMCache" localMode="modern">
		<cfset createRAMCache()>
		<cfset testCacheGet()>
		<cfset deleteCache()>
	</cffunction>
	
	<cffunction access="private" name="testCacheGet" localMode="modern">

<!--- begin old test code --->
<cflock scope="server" timeout="1">

	<cfset cacheRemove(arrayToList(cacheGetAllIds()))>
<cfset cachePut('abc','123')>

<cfset valueEquals(left="#cacheGet('abc')#", right="123")>
<cfset cacheGetKey=cacheGet('def')>

<cfset valueEquals(left="#structKeyExists(variables,'cacheGetKey') and !isNull(variables.cacheGetKey)#", right="#false#")>

<cfif server.ColdFusion.ProductName EQ "railo">
    <cftry>
        <cfset cacheGet('def',true)>
        <cfset fail("must throw:there is no entry in cache with key [DEF]")>
        <cfcatch></cfcatch>
    </cftry>

	<cfset valueEquals(left="#cacheGet('abc',false)#", right="123")>
</cfif>
</cflock>

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