<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="testCatch">
		<cftry>
		
			<cfthrow type="Database" message="test excpetion" detail="this is a test">
			<cfcatch type="database">
				<cfset assertEquals(cfcatch.detail,'this is a test')>
			</cfcatch>
		</cftry>
	</cffunction>
</cfcomponent>