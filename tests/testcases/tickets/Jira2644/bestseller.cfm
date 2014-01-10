<cfsetting showdebugoutput="no">
<cfset bestsellers = entityLoad('bestseller', {}, 'gender desc, sortorder') />

<cfoutput>#serialize(bestsellers)#</cfoutput>
