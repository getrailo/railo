<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayToStruct" localMode="modern">

<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cfset arr=arrayNew(1)>
<cfset arr[1]=1>
<cfset arr[100]=100>
<cfset sct=arrayToStruct(arr)>
<cfset valueEquals(left="#IsStruct(sct)#", right="#true#")>
<cfset valueEquals(left="#ListSort(arrayToList(StructKeyArray(sct)),"numeric")#", right="1,100")>
 
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