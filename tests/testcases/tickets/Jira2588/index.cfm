<cfsetting showdebugoutput="no">
<cfscript>
sct={form:form,HTTPReqData:getHTTPRequestData()};
</cfscript>


<cfoutput>#serialize(sct)#</cfoutput>