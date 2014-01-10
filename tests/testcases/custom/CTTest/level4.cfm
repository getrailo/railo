<cfif thistag.EXECUTIONMODE EQ "start">
	<cfset caller.caller.recievedFromParentParent=caller.caller.parentData>
	<cfset caller.caller.recievedFromParentParentEval=evaluate("caller.caller.parentData")>
</cfif>