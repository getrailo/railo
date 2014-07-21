<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfscript>
	public function setUp(){
		variables.q = QueryNew("foo,bar,biz");
		q = QueryNew("foo,bar,biz");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "One");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Two");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "First");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Second");
		querySetCell(variables.q, "biz", "Blue");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Red");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Yellow");
		queryAddRow(variables.q);
		querySetCell(variables.q, "foo", "Three");
		querySetCell(variables.q, "bar", "Third");
		querySetCell(variables.q, "biz", "Blue");
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
			<cfoutput group="bar">
				<cfset res&="{#bar#}">
				<cfoutput>
					<cfset res&="(#biz#)">
				</cfoutput>
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[One]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)[Two]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)[Three]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)",res)>
	</cffunction>

	<cffunction name="testWithMaxRow">
		<cfset local.res="">
		<cfoutput query="q" group="foo" maxrows="1">
			<cfset res&="[#foo#]">
			<cfoutput group="bar">
				<cfset res&="{#bar#}">
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[One]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)",res)>
	</cffunction>

	<cffunction name="testWith2MaxRows">
		<cfset local.res="">
		<cfoutput query="q" group="foo" maxrows="2">
			<cfset res&="[#foo#]">
			<cfoutput group="bar">
				<cfset res&="{#bar#}">
				<cfoutput>
					<cfset res&="(#biz#)">
				</cfoutput>
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[One]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)[Two]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)",res)>
	</cffunction>

	<cffunction name="testWith2MaxRowsAndStartRow">
		<cfset local.res="">
		<cfoutput query="q" group="foo" maxrows="2" startrow="10">
			<cfset res&="[#foo#]">
			<cfoutput group="bar">
				<cfset res&="{#bar#}">
				<cfoutput>
					<cfset res&="(#biz#)">
				</cfoutput>
			</cfoutput>
		</cfoutput>

		<cfset assertEquals("[Two]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)[Three]{First}(Red)(Yellow)(Blue){Second}(Red)(Yellow)(Blue){Third}(Red)(Yellow)(Blue)",res)>
	</cffunction>

</cfcomponent>
