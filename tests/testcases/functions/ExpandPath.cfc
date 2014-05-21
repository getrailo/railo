<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testExpandPath" localMode="modern">

<!--- begin old test code --->
<cfset dir=getDirectoryFromPath(GetCurrentTemplatePath())>
<cfset dir=mid(dir,1,len(dir)-1)>

<cfset parent=getDirectoryFromPath(dir)>
<cfset parent=mid(parent,1,len(parent)-1)>

<cfset valueEquals(left="#ExpandPath(".")#" ,right="#dir#")>

<cfset valueEquals(
	left="#ExpandPath("..")#" ,
	right="#parent#")>
<cfset valueEquals(
	left="#ExpandPath("../")#" ,
	right="#parent#/")>
	
<cfset valueEquals(
	left="#ExpandPath("../tags")#" ,
	right="#parent#/tags")>
		
		
<cfset valueEquals(
	left="#ExpandPath("../tagx/")#" ,
	right="#parent#/tagx/")>
<cfset valueEquals(
	left="#ExpandPath("/jm")#" ,
	right="#server.coldfusion.rootdir#/jm")>
 
<cfset valueEquals(
	left="#ExpandPath("\jm")#" ,
	right="#server.coldfusion.rootdir#/jm")>

<cfset valueEquals(
	left="#ExpandPath("\railo-context\")#" ,
		right="#server.coldfusion.rootdir#/WEB-INF/railo/context/")>
<cfset valueEquals(
	left="#ExpandPath("\railo-context")#" ,
		right="#server.coldfusion.rootdir#/WEB-INF/railo/context")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>