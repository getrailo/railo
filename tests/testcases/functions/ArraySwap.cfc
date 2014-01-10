<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArraySwap" localMode="modern">

<!--- begin old test code --->
<cfset arr=arrayNew(1)>
<cfset arr[1]=111>
<cfset arr[2]=22>
<cfset arr[3]=3.5>
 
<cfset ArraySwap(arr, 1,3)>
<cfset valueEquals(left="#arr[1]#", right="3.5")>
<cfset valueEquals(left="#arr[2]#", right="22")>
<cfset valueEquals(left="#arr[3]#", right="111")>


<cftry>
	<cfset ArraySwap(arr, 1,4)>
	<cfset fail("must throw:4 is an invalid swap index of the array. ")>
	<cfcatch></cfcatch>
</cftry>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>