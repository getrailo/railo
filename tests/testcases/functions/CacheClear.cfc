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
	
	<!---
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCacheClear" localMode="modern">

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
</cfcomponent>