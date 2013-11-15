<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArraySlice" localMode="modern">

<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cfset arr=listToArray('aaa,bbb,ccc,ddd,eee')>
 
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,2)))#", right="bbb,ccc,ddd,eee")>
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,0)))#", right="eee")>
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,-1)))#", right="ddd,eee")>
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,-3)))#", right="bbb,ccc,ddd,eee")>

<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,2,1)))#", right="bbb")>
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,2,3)))#", right="bbb,ccc,ddd")>

<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,2,0)))#", right="bbb,ccc,ddd,eee")>
<cfset valueEquals(left="#listTrim(ArrayToList(arraySlice(arr,2,-1)))#", right="bbb,ccc,ddd")>

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