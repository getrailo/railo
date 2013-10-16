<cfif thistag.EXECUTIONMODE EQ "start">
	<cfset caller.recievedFromParent=caller.parentData>
	<cfset caller.recievedFromParentEval=evaluate("caller.parentData")>
	
	<cfmodule template="level4.cfm">
</cfif>