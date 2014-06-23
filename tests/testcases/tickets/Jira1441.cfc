<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfscript>
	public function setUp(){
		variables.q = QueryNew("foo,bar");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "First");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Second");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "First");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Second");

	}
	</cfscript>
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->


	<cffunction name="testWithoutMaxRow">
		<cfset local.res="">
		<cfoutput query="q" group="foo">
			<cfset res&="[#foo#]">
			<cfoutput>
				<cfset res&="{#bar#}">
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[One]{First}{Second}[Two]{First}{Second}",res)>
	</cffunction>

	<cffunction name="testWithMaxRow">
		<cfset local.res="">
		<cfoutput query="q" group="foo" maxrows="1">
			<cfset res&="[#foo#]">
			<cfoutput>
				<cfset res&="{#bar#}">
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[One]{First}{Second}",res)>
	</cffunction>
</cfcomponent>
