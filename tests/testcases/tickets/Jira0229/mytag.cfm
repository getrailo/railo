<cfif thisTag.executionMode EQ "Start" >

<cfparam name="Attributes.Label" type="string" />

<cfoutput>{Start:#Attributes.Label#}</cfoutput>

  <cfset base_tags = getBaseTagList() />
  <!--- check if we have a parent context --->
	
	<cfif ListValueCountNoCase ( base_tags, "cf_mytag" ) GT 1 >
		<!--- NOTE: the instance number works if CF_MYTAG are not immediate children --->
      	<cfset pt = getBaseTagData( "CF_MYTAG", 1 ) />
		
		
		<cfif attributes.label EQ "lvl 3">
			<cfset ppt = getBaseTagData( "CF_MYTAG", 2 ) />
      		<cfoutput>{label:#attributes.label#;parent-label:#pt.Attributes.label#;parent-parent-label:#ppt.Attributes.label#}</cfoutput>
		<cfelse>
			<cfoutput>{label:#attributes.label#;parent-label:#pt.Attributes.label#}</cfoutput>
		</cfif>
		<!---
		<cfset parent_tag = getBaseTagData( "CF_MYTAG", 2 ) />
      	<cfoutput>{2=#parent_tag.Attributes.label#}</cfoutput>
		<cfabort>
		--->
    </cfif>

<cfelse>

<cfoutput>{End:#Attributes.Label#}</cfoutput>

</cfif>