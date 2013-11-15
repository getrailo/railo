<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayLen">

<!--- begin old test code --->
<cfset arr=arrayNew(1)>
<cfset valueEquals(left="#arrayLen(arr)#", right="0")>
<cfset ArrayAppend( arr, 1 )>
<cfset valueEquals(left="#arrayLen(arr)#", right="1")>
<cfset arr[9]=9>
<cfset valueEquals(left="#arrayLen(arr)#", right="9")>
<cfset ArrayResize(arr, 20)>
<cfset valueEquals(left="#arrayLen(arr)#", right="20")>

<cfset arr=arrayNew(2)>
<cfset arr[1][1]=11>
<cfset arr[1][2]=12>
<cfset arr[1][3]=13>
<cfset arr[2][1]=21>
<cfset arr[2][2]=22>
<cfset arr[2][3]=23>

<cfset valueEquals(left="#arrayLen(arr)#", right="2")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>