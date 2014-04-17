<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testMinMax">
		<cfset foo = 12>
		<cfparam name="foo" type="range" min="1" max="12">

		<cfset assertEquals("","")>
	</cffunction>
	<cffunction name="testMin">
		<cfset foo = 12>
		<cfparam name="foo" type="range" min="1">

		<cfset assertEquals("","")>
	</cffunction>
	<cffunction name="testMax">
		<cfset foo = 12>
		<cfparam name="foo" type="range" max="12">
		<cfparam name="foo" type="range" max="13">
	</cffunction>
</cfcomponent>