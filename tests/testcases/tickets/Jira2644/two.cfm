<cfsetting showdebugoutput="no">
<cfset stateListing = entityLoad("State") />

<cfoutput>#serialize(stateListing[1])#</cfoutput>
