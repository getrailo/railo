<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayReverse" localMode="modern">
 
<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cfset arr1=arrayNew(1)>
<cfset ArrayAppend( arr1, 1 )>
<cfset ArrayAppend( arr1, 2 )>
<cfset ArrayAppend( arr1, 3 )>
<cfset arr1[6]="6">

<cfset arr=arrayReverse(arr1)>
<cf_valueEquals left="#arrayLen(arr)#" startWith="6">

<cf_valueEquals left="#arr[1]#" startWith="6">
<cf_valueEquals left="#arr[4]#" startWith="3">
<cf_valueEquals left="#arr[5]#" startWith="2">
<cf_valueEquals left="#arr[6]#" startWith="1">

</cfif>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>