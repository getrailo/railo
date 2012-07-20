<cfcomponent output="no">

	<cffunction name="handleResponseWhenCached" access="public" returntype="boolean"
	hint="I return a boolean value to indicate wheher I returned a 304 Not Modified / the asset is cached in the user's browser ">
		<cfargument name="fileETag" type="string" required="yes" />
		<cfargument name="mimetype" type="string" required="yes" />
		<cfargument name="expireDays" type="numeric" required="no" hint="Amount of days to persistently cache the asset" />
		
		<!--- persistent caching headers --->
		<cfif structKeyExists(arguments, "expiredays")>
			<cfheader name="Expires" value="#GetHTTPTimeString(now() + arguments.expireDays)#" />
			<cfheader name="Cache-Control" value="max-age=#86400*arguments.expireDays#" />
		</cfif>
		
		<!--- ETag header--->
		<cfheader name="ETag" value="""#arguments.fileETag#""" />
		
		<!--- check for existing Etag in request --->
		<cfif cgi.HTTP_IF_NONE_MATCH contains arguments.fileETag>
			<!--- nothing has changed, return nothing --->
			<cfheader statuscode="304" statustext="Not Modified" />
			<cfcontent type="#arguments.mimetype#" reset="yes" />
			<cfreturn true />
		</cfif>
		
		<cfreturn false />
	</cffunction>


	<cffunction name="getFileDateLastModified" access="public" returntype="string" output="no">
		<cfargument name="path" type="string" required="true" />
		<cfreturn createObject("java", "java.io.File").init(arguments.path).lastModified() />
	</cffunction>
	
</cfcomponent>