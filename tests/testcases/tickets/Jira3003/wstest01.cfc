<!---
==================================================================
WSTEST01.cfc 
==================================================================
--->
<cfcomponent displayname="wstest01"              
             namespace = "http://beans.webservices.cfc.netmover"
             serviceportname = "wstest01Service" 
             porttypename = "wstest01" 
             bindingname = "wstest01Binding">
<!---
==================================================================
function: run()
==================================================================
--->
<cffunction name="run" returnType="string" access="remote">
<cfargument name="reqParams" type="wstest01Request" required="true">
  <cfset var resp="RUN OK - " & now()>
  <!--- test --->
  <cfreturn resp>
</cffunction>

</cfcomponent>