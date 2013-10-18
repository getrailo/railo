<cfsetting showdebugoutput="no">
<cfset req=getPageContext().getRequest()>

<cfoutput>#serialize({
	map:req.getParameterMap(),
	values:req.getParameterValues('test1'),
	form:form,
	url:url})#</cfoutput>