<cfset _stText = StructNew()>
<cfset stText = StructNew()>
<cfloop index="key" list="en,de">
	<cfinclude template="#key#/res_general.cfm">
	<cfinclude template="#key#/res_extension.cfm">
	<cfinclude template="#key#/res_search.cfm">
	<cfinclude template="#key#/res_schedule.cfm">
	<cfinclude template="#key#/res_buttons.cfm">
	<cfinclude template="#key#/res_components.cfm">
	<cfinclude template="#key#/res_debug.cfm">
	<cfinclude template="#key#/res_mail.cfm">
	<cfinclude template="#key#/res_mappings.cfm">
	<cfinclude template="#key#/res_security.cfm">
	<cfinclude template="#key#/res_service.cfm">
	<cfinclude template="#key#/res_server.cfm">
	<cfinclude template="#key#/res_ref.cfm">
	<cfinclude template="#key#/res_video.cfm">
	<cfset _stText[key]=duplicate(stText)>
</cfloop>
<cfset stText=_stText>
