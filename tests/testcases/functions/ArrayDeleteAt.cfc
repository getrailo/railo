<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayDeleteAt">
		
<cfset var arr=arrayNew(1)>
<cfset ArrayAppend( arr, 1 )>
<cfset ArrayAppend( arr, 2 )>
<cfset ArrayAppend( arr, 3 )>
<cfset ArrayDeleteAt( arr, 1 )>

<cfset valueEquals( left="#arrayLen(arr)#", right="2")>
<cfset valueEquals(left="#arr[1]#", right="2")>
<cfset valueEquals(left="#arr[2]#", right="3")>

<cfset ArrayDeleteAt( arr, 1 )>
<cfset valueEquals(left="#arrayLen(arr)#", right="1")>
<cfset valueEquals(left="#arr[1]#", right="3")>

<cftry>
	<cfset ArrayDeleteAt( arr, 10)>
	<cfset fail("must throw:Cannot insert/delete at position 10.")>
	<cfcatch></cfcatch>
</cftry>

<cfset arr=arrayNew(1)>
<cfset arr[1]=1>
<cfset arr[2]=1>
<cfset arr[3]=1>
<cfset arr[7]=7>
<cfset ArrayDeleteAt( arr, 3 )>
<cfset valueEquals(left="#arr[1]#", right="1")>
<cfset valueEquals(left="#arr[2]#", right="1")>
<cfset valueEquals(left="#arr[6]#", right="7")>
<cfset valueEquals(left="#arrayLen(arr)#", right="6")>

<cftry>
	<cfset test=arr[3]>
	<cfset fail("must throw:Element 3 is undefined in a Java object of type class coldfusion.runtime.Array referenced as ")>
	<cfcatch></cfcatch>
</cftry>
<cfset valueEquals(left="#arr[6]#", right="7")>

<cfset arr=arrayNew(1)>
<cfset arr[1]=1>
<cfset arr[2]=1>
<cfset arr[3]=1>
<cfset arr[7]=7>
<cfset ArrayDeleteAt( arr, 4 )>
<cfset valueEquals(left="#arr[1]#", right="1")>
<cfset valueEquals(left="#arr[2]#", right="1")>
<cfset valueEquals(left="#arr[3]#", right="1")>
<cfset valueEquals(left="#arr[6]#", right="7")>
<cfset valueEquals(left="#arrayLen(arr)#", right="6")>
		
		
		
		
		
	</cffunction>
	
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>