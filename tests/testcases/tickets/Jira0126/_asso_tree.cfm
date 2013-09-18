<cfsetting enablecfoutputonly="true">

<cfif thisTag.executionMode eq "start">
	<cfset thisTag.items = ArrayNew(1)>
<cfelseif thisTag.executionMode eq "end">
	<cfoutput>
    tree: {<cfif arrayLen(thisTag.items)>#thisTag.items[1]._out#</cfif>}
	</cfoutput>
</cfif>
<cfsetting enablecfoutputonly="false">