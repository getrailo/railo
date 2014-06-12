<cfsetting enablecfoutputonly="yes">
<cfif isDefined("url")>
<cfif isStruct(url)>
<cfset attributes.bValid="-">
<cfloop collection="#url#" item="itUrl">
<cfif isValid("variableName", itUrl)>
<cfset attributes.bValid="jo">
<cfelse>
<cfset attributes.bValid="no">
</cfif>
</cfloop>
</cfif>
</cfif>
<cfsetting enablecfoutputonly="no"><cfoutput>#attributes.bValid#</cfoutput>