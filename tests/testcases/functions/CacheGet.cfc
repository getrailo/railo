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
	
	<cffunction name="testCacheGet" localMode="modern">

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
</cfcomponent>