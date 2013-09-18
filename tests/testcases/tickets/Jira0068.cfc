<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="setUp"></cffunction>
	<cffunction name="test">
		<cfsavecontent variable="local.c1"><cfprocessingdirective suppresswhitespace="true">[           test1          ]</cfprocessingdirective></cfsavecontent>
		<cfsavecontent variable="local.c2"><cfprocessingdirective suppresswhitespace="false">[           test2          ]</cfprocessingdirective></cfsavecontent>
 		
		<cfset assertEquals("[ test1 ]",c1)>
		<cfset assertEquals("[           test2          ]",c2)>
	</cffunction>
</cfcomponent>