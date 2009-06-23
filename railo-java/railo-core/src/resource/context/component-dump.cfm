<cfsetting enablecfoutputonly="yes">
<cfif arrayLen(getTemplatePath()) EQ 2>
	<!--- dump the component --->
	<cfdump var="#component#">
<cfelse><!---
	
	write out functions of component. done for cfmx compatibility, when a cfc is included via cfinclide the functions are available for use
---><cfscript>
 	
	trace=getTemplatePath();
	abs=trace[arrayLen(trace)-1];
	real=contractPath(abs);
	real=listTrim(real,'/');
	real=mid(real,1,len(real)-4);
	cfc=createObject('component',real);
</cfscript><cfloop collection="#cfc#" item="key"><cfset variables[key]=cfc[key]></cfloop><cfscript>
 	
	StructDelete(variables,'trace',false);
	StructDelete(variables,'abs',false);
	StructDelete(variables,'real',false);
	StructDelete(variables,'cfc',false);
	
</cfscript></cfif>