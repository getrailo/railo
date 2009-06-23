<cfif thistag.executionMode EQ "start">
	<cfset ctab=GetBaseTagData("CF_TABBEDPANE").attributes.ctab>
	<cfset name=GetBaseTagData("CF_TABBEDPANE").attributes.name>
	
	<!--- <cfdump var="#GetBaseTagData("CF_TABBEDPANE")#">@todo wenn dies als erstes kommt gibt es fehler !? --->
	<cfset thistag.executebody=ListFindNoCase(attributes.name,ctab)>
	<!--- <cfset thistag.executebody=ListFindNoCase(attributes.name,request._ctab)> --->
</cfif>

