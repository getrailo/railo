<cfcomponent extends="org.railo.cfml.test.RailoTestCase">


	<cffunction name="testQoQColumnType">
		
		<cfset local.q1 = queryNew( "price", "decimal"
			,[
				 [ "8.5" ]
			 	,[ "1.75" ]
			 	,[ "3.5" ]
			 	,[ "2.5" ]]
		)>

		<cfquery name="local.q2" dbtype="query">
			select * from q1
		</cfquery>

		<cfset assertEquals( getMetaData( q1 )[ 1 ].typeName, getMetaData( q2 )[ 1 ].typeName )>
	</cffunction>


</cfcomponent>