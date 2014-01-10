

<cfif thistag.EXECUTIONMODE EQ "start">
	<!--- write to callers attribute scope --->
	<cfset caller.attributes.fromLevel6="from.6">
	<cfset c="caller">
	<cfset "#c#.attributes.fromLevel6Eval"="from_6">
	
</cfif>