<cfsetting showdebugoutput="no">
<cfsetting enablecfoutputonly="yes">
<cfif isDefined("url")>
<cfif isStruct(url)>
<cfloop collection="#url#" item="itUrl">
<cfif isValid("variableName", itUrl)>
<cfset attributes.bValid="jo">
<cfelse>
<cfset attributes.bValid="no">
</cfif>
</cfloop>
</cfif>
</cfif>
<cfsetting enablecfoutputonly="no">
<cf_ct>---<cfoutput>#attributes.bValid#</cfoutput>