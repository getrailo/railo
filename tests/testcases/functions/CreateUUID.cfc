<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCreateUUID" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")> 
<cfset valueEquals(left="#len(CreateUUID()) GT 34#", right="true")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>