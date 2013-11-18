<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayMid">

<!--- begin old test code --->
<cfset var text="abcdef">
<cfset var arr=['a','b','c','d','e','f']>






<cfset valueEquals(left="#mid(text,1)#", right="abcdef")>
<cfset valueEquals(left="#mid(text,2)#", right="bcdef")>
<cfset valueEquals(left="#mid(text,1,3)#", right="abc")>
<cfset valueEquals(left="#mid(text,2,3)#", right="bcd")>
<cfset valueEquals(left="#mid(text,2,100)#", right="bcdef")>
<cfset valueEquals(left="#mid(text,200,100)#", right="")>



<cfset valueEquals(left="#arrayToList(arrayMid(arr,1))#", right="a,b,c,d,e,f")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,2))#", right="b,c,d,e,f")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,1,3))#", right="a,b,c")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,2,3))#", right="b,c,d")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,2,100))#", right="b,c,d,e,f")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,200,100))#", right="")>

<cfset arr=['a','b']>
<cfset arr[4]='d'>
<cfset arr[5]='e'>
<cfset arr[6]='f'>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,1,3))#", right="a,b,")>
<cfset valueEquals(left="#arrayToList(arrayMid(arr,2,3))#", right="b,,d")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>