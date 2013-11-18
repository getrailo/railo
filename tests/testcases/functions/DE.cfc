<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDE" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#de(1)#", right="""1""")>
<cfset valueEquals(left="#de('hello')#", right="""hello""")>
<cfset valueEquals(left="#de('h"''ello')#", right="""h""""'ello""")>
<cfset valueEquals(left="#de('h"''ello')#", right="""h""""'ello""")>
<cfset valueEquals(left="#evaluate(de('h"''ello'))#", right="h""'ello")>

<!--- <cfset valueEquals(left="#de(true)#", right="""true""")> --->
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>