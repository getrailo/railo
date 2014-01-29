<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfset variables.cacheName="Test"&ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
	
	<cffunction name="testCachePutEHCache" localMode="modern">
		<cfset createEHCache()>
		<cfset testCachePut()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCachePutJBossCache" localMode="modern">
		<cfset createJBossCache()>
		<cfset testCachePut()>
		<cfset deleteCache()>
	</cffunction>
	<cffunction name="testCachePutRAMCache" localMode="modern">
		<cfset createRAMCache()>
		<cfset testCachePut()>
		<cfset deleteCache()>
	</cffunction>
	
	<cffunction access="private" name="testCachePut" localMode="modern">

<!--- begin old test code --->
<cfset server.enableCache=true>

<cflock scope="server" timeout="10">
	<cfset cacheRemove(arrayToList(cacheGetAllIds()))>
	
	<cfset cachePut('abc','123',CreateTimeSpan(0,0,0,1))>
	<cfset cachePut('def','123',CreateTimeSpan(0,0,0,2),CreateTimeSpan(0,0,0,1))>
	<cfset cachePut('ghi','123',CreateTimeSpan(0,0,0,0),CreateTimeSpan(0,0,0,0))>
    
	<cfset sct={}>
    <cfset sct.a=cacheGet('abc')>
    <cfset sct.b=cacheGet('def')>
    <cfset sct.c=cacheGet('ghi')>
    
    <cfset valueEquals(left="#structKeyExists(sct,'a')#", right="true")>
    <cfset valueEquals(left="#structKeyExists(sct,'b')#", right="true")>
    <cfset valueEquals(left="#structKeyExists(sct,'c')#", right="true")>
    <cfset sleep(1200)>
    <cfset sct.d=cacheGet('abc')>
    <cfset sct.e=cacheGet('def')>
    <cfset sct.f=cacheGet('ghi')>
    <cfset valueEquals(left="#structKeyExists(sct,'d')#", right="false")>
    <cfset valueEquals(left="#structKeyExists(sct,'e')#", right="false")>
    <cfset valueEquals(left="#structKeyExists(sct,'f')#", right="true")>
    
<cfif server.ColdFusion.ProductName EQ "railo">    
	<cfset cachePut('def','123',CreateTimeSpan(0,0,0,2),CreateTimeSpan(0,0,0,1),cacheName)>
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