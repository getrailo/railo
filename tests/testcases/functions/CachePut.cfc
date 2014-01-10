<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.cacheName="Test"&ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfadmin 
				action="updateCacheConnection"
				type="web"
				password="#request.webadminpassword#"
				
				
				name="#cacheName#" 
				class="railo.runtime.cache.eh.EHCacheLite" 
				storage="false"
				default="object" 
				custom="#{'overflowtodisk':'true','maxelementsinmemory':'10000','maxelementsondisk':'10000000','memoryevictionpolicy':'LRU','timeToIdleSeconds':86400,'diskpersistent':'true','timeToLiveSeconds':86400}#"
				>
	</cffunction>
	
	<cffunction name="afterTests">
		<cfadmin 
			action="removeCacheConnection"
			type="web"
			password="#request.webadminpassword#"
			name="#cacheName#">
						
	</cffunction>
	<cffunction name="testCachePut" localMode="modern">

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
</cfcomponent>