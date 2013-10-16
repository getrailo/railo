

<cfif thistag.EXECUTIONMODE EQ "start">
	<cfset caller.fromLevel2="caller.2">
	
	<cfset caller.from.Level2="caller_2">
	
	
	
	<cfset caller.caller.fromLevel2="caller.caller.2">
	<cfset c="caller">
	<cfset "#c#.#c#.fromLevel2Eval"="caller.caller.2.eval">
	
	<cfset caller.caller.from.Level2="caller_caller_2">
	<cfset "#c#.#c#.fro.mLevel2.Eval"="caller-caller.2.eval">
	
</cfif>