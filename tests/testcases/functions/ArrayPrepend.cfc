<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayPrepend" localMode="modern">

<!--- begin old test code --->
<cfset arr=arrayNew(1)>
<cfset arr[1]=1>
<cfset arr[2]=2>
<cfset ArrayPrepend( arr, 'a' )>
<cfset ArrayPrepend( arr, 'b' )>
<cfset valueEquals(left="#arr[1]#", right="b")>
<cfset valueEquals(left="#arr[2]#", right="a")>
<cfset valueEquals(left="#arr[3]#", right="1")>
<cfset valueEquals(left="#arr[4]#", right="2")>
<cfset valueEquals(left="#arrayLen(arr)#", right="4")>

<cfset arr=arrayNew(1)>
<cfset arr[20]=20>
<cfset ArrayPrepend( arr, 'a' )>
<cfset ArrayPrepend( arr, 'b' )>
<cfset valueEquals(left="#arr[1]#", right="b")>
<cfset valueEquals(left="#arr[2]#", right="a")>
<cfset valueEquals(left="#arr[22]#", right="20")>
<cfset valueEquals(left="#arrayLen(arr)#", right="22")>
 
<cfset arr=arrayNew(1)>
<cfset arr[2]=2>
<cfset arr[4]=4>
<cfset ArrayPrepend( arr, 'a' )>
<cfset ArrayPrepend( arr, 'b' )>
<cfset valueEquals(left="#arr[1]#", right="b")>
<cfset valueEquals(left="#arr[2]#", right="a")>
<cfset valueEquals(left="#arr[4]#", right="2")>
<cfset valueEquals(left="#arr[6]#", right="4")>
<cftry>
<cfset valueEquals(left="#arr[5]#", right="null")>
	<cfset fail("must throw:Array at position 5 is empty")>
	<cfcatch></cfcatch>
</cftry>
<cfset valueEquals(left="#arrayLen(arr)#", right="6")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>