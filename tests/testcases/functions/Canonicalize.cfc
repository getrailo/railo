<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCanonicalize" localMode="modern">

<!--- begin old test code --->
<cfscript>
valueEquals(canonicalize("&lt;",false,false),'<');
valueEquals(canonicalize("%26lt; %26lt; %2526lt%253B %2526lt%253B%2526lt%253B",false,false),'< < < <<');
valueEquals(canonicalize("&##X25;3c",false,false),'<');
valueEquals(canonicalize("&##x25;3c",false,false),'<');
</cfscript>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>