<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="beforeTests">
		<!---create a file in the ram resource --->
		<cfset variables.filePath="ram:///jira2609.txt">
		<cffile action="write" file="#variables.filePath#" output="a
b
c
d
e">
	</cffunction>
	
	<cffunction name="afterTests">
		<cffile action="delete" file="#variables.filePath#">
	</cffunction>
	
	<cffunction name="testIndex">
		<cfset counter=0>
		<cfloop file="#variables.filePath#" index="indexName" from="2" to="2">
			<cfset counter++>
			<cfset assertEquals("b",indexName)>
		</cfloop>
		<cfset assertEquals(1,counter)>
	</cffunction>
	
	<cffunction name="testItem">
		<cfset counter=0>
		<cfloop file="#variables.filePath#" item="itemName" from="2" to="2">
			<cfset counter++>
			<cfset assertEquals("b",itemName)>
		</cfloop>
		<cfset assertEquals(1,counter)>
	</cffunction>
	
	<cffunction name="testIndexAndItem">
		<cfset counter=0>
		<cfloop file="#variables.filePath#"  index="indexName" item="itemName" from="2" to="2">
			<cfset counter++>
			<cfset assertEquals(2,indexName)>
			<cfset assertEquals("b",itemName)>
		</cfloop>
		<cfset assertEquals(1,counter)>
	</cffunction>
	
</cfcomponent>