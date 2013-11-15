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
	<cffunction name="testCacheGetAllIds" localMode="modern">

<!--- begin old test code --->
<cfset server.enableCache=true>
<cflock scope="server" timeout="1">


	<cfset cacheRemove(arrayToList(cacheGetAllIds()))>
	
	<cfset cachePut('abc','123')>
	<cfset cachePut('def','123')>
    <cfset valueEquals(left="#ListSort(arrayToList(cacheGetAllIds()),'textnocase')#", right="ABC,DEF")>
    
<cfif server.ColdFusion.ProductName EQ "railo"> 
	<cfset cacheClear()>   
	<cfset cachePut('abc','123')>
	<cfset cachePut('abd','123')>
	<cfset cachePut('def','123')>
    <cfset valueEquals(left="#ListSort(arrayToList(cacheGetAllIds("ab*")),'textnocase')#", right="ABC,ABD")>
    <cfset valueEquals(left="#ListSort(arrayToList(cacheGetAllIds("ab*")),'textnocase')#", right="ABC,ABD")>
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