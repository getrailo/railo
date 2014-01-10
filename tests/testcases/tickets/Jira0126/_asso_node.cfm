<cfsetting enablecfoutputonly="true">
<cfif thisTag.executionMode eq "start">
	<cfset parentTag = ListGetAt(getBaseTagList(), 2)>
	<cfset thisTag.items = ArrayNew(1)>
	<cfset attributes._out = "">
<cfelseif thisTag.executionMode eq "end">
	<cfsavecontent variable="attributes._out"><cfoutput>node: {level: #attributes.level#<cfif arrayLen(thisTag.items)>,#thisTag.items[1]._out#</cfif>}</cfoutput></cfsavecontent>
	<cfassociate basetag="#parentTag#" datacollection="items">
</cfif>
<cfsetting enablecfoutputonly="false">