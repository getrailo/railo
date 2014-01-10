<cfif thistag.EXECUTIONMODE EQ "start">
	<cfmodule template="level2.cfm">
	
	<cfset c="caller">
	
	<cfset caller.fromLevel1="caller.1">
	<cfset caller.recievedfromLevel2=variables.fromLevel2>
	<cfset caller.recieved.from.Level2=variables.from.Level2>
	<cfset "#c#.rec.ieved.from.Level2"=variables.from.Level2>
	
	<cfset "#c#.fromLevel1Eval"="caller.1.eval">
	
</cfif>