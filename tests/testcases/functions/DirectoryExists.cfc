<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDirectoryExists" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#DirectoryExists("/Users")#", right="#true#")>
<cfset valueEquals(left="#DirectoryExists("/users")#", right="#true#")>
<cfset valueEquals(left="#DirectoryExists("/Users/susi")#", right="#false#")>
<cfset valueEquals(left="#DirectoryExists("/Users/peter/temp")#", right="#false#")>

<cfset path=structNew()>
<cfset path.abs=GetDirectoryFromPath(GetCurrentTemplatePath())>
<cfset path.real="../"& ListLast(path.abs,"/\")>

<cfset valueEquals(left="#directoryExists(path.abs)#", right="true")>

<cfset valueEquals(left="#directoryExists(path.real)#", right="true")>

<cfif server.ColdFusion.ProductName EQ "Railo">
    <cfset valueEquals(left="#evaluate('directoryExists(path.real,false)')#", right="false")>
    <cfset valueEquals(left="#evaluate('directoryExists(path.real,true)')#", right="true")>
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