<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayResize" localMode="modern">

<!--- begin old test code --->
<cfset arr=arrayNew(1)>
<cfset valueEquals(left="#arrayLen(arr)#", right="0")>
<cfset ArrayResize(arr, 20)>
<cfset valueEquals(left="#arrayLen(arr)#", right="20")>
<cfset ArrayResize(arr, 10)>
<cfset valueEquals(left="#arrayLen(arr)#", right="20")>
 
<cfset arr=arrayNew(1)>
<cfset arr[2]=2>
<cfset arr[4]=4>
<cfset ArrayResize(arr, 10)>
<cfset valueEquals(left="#arrayLen(arr)#", right="10")>
<cfset valueEquals(left="#arr[2]#", right="2")>
<cfset valueEquals(left="#arr[4]#", right="4")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>