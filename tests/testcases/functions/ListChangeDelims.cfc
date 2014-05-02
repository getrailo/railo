<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testListChangeDelims" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#ListChangeDelims('',';','.')#", right="")>
<cfset valueEquals(left="#ListChangeDelims('a',';','.')#", right="a")>
<cfset valueEquals(left="#ListChangeDelims('a.b',';','.')#", right="a;b")>
<cfset valueEquals(left="#ListChangeDelims('a.b',';','.')#", right="a;b")>
<cfset valueEquals(left="#ListChangeDelims('..a.b',';','.')#", right="a;b")>
<cfset valueEquals(left="#ListChangeDelims('..a.b...',';','.')#", right="a;b")>
<cfset valueEquals(left="#ListChangeDelims(',,,,,a,a,,,,',';')#", right="a;a")>
<cfset valueEquals(left="#ListChangeDelims(',,,,,a,,,a,,,,',';')#", right="a;a")>
<cfset valueEquals(left="#ListChangeDelims(',,,,,a,,,a,,,,',';',',:;')#", right="a;a")>
<cfset valueEquals(left="#ListChangeDelims('a,,b',';',',:;',true)#", right="a;;b")>
<cfset valueEquals(left="#ListChangeDelims('a,,b',';',',:;',true,true)#", right="a,,b")>



<cfset valueEquals(left="#ListChangeDelims('a,,c',';')#", right="a;c")>
<cfset valueEquals(left="#ListChangeDelims('a,,c',';',',',false)#", right="a;c")>
<cfset valueEquals(left="#ListChangeDelims('a,,c',';',',',true)#", right="a;;c")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>