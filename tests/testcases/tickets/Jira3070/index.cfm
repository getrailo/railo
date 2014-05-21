<cfsetting showdebugoutput="no">
<cfprocessingdirective fullNullSupport="true">
<cfscript>
variables.test = nullValue();
echo(structKeyExists(variables, "test")); 
</cfscript>