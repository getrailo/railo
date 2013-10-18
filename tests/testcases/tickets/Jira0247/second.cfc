<cfcomponent extends="base">


<!--- the check function must call super.check for the corruption to work --->
<cffunction name="check">      

  <cfreturn super.check() />

</cffunction>

</cfcomponent>