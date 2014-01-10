<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testExp" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#left(exp(1),12)#" ,right="2.7182818284")>
<cfset valueEquals(left="#left(exp(2),12)#" ,right="7.3890560989")>
<cfset valueEquals(left="#left(exp(3),12)#" ,right="20.085536923")>
<cfset valueEquals(left="#left(exp(1.2),12)#" ,right="3.3201169227")>
<!--- end old test code --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>