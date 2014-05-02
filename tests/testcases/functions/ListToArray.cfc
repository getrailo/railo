<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testListToArray" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#arrayLen(ListToArray(''))#", right="0")>
<cfset valueEquals(left="#arrayLen(ListToArray('aaa,bbb,ccc'))#", right="3")>
<cfset valueEquals(left="#arrayLen(ListToArray(',,xx,,'))#", right="1")>
<cfset valueEquals(left="#arrayLen(ListToArray('xx,xx,xx'))#", right="3")>
<cfset valueEquals(left="#arrayLen(ListToArray(',,xx,xx,xx'))#", right="3")>
<cfset valueEquals(left="#ListToArray(',,xx,,,xx,xx').size()#", right="3")>
<cfset valueEquals(left="#arrayToList(ListToArray('aayaxybbbxycxccx',"xy"))#", right="aa,a,bbb,c,cc")>
<cfset valueEquals(left="#arrayToList(ListToArray('aaaUaaa',"u"))#", right="aaaUaaa")>
<cfset valueEquals(left="#arrayToList(ListToArray('aaaUaaa',"U"))#", right="aaa,aaa")>


<cfset valueEquals(left="#arrayToList(ListToArray(',,sasa,,,asaSa,,',","))#", right="sasa,asaSa")>
<cfset valueEquals(left="#arrayToList(ListToArray(',,sasa,,,asaSa,,',",",true))#", right=",,sasa,,,asaSa,,")>
<cfset valueEquals(left="#arrayToList(ListToArray(',,sasa,,,asaSa,,',",",false))#", right="sasa,asaSa")>



<cfset valueEquals(left="#arrayToList(ListToArray('a:-b:-c',':-',false,false),'*')#", right="a*b*c")>
<cfset valueEquals(left="#arrayToList(ListToArray('a-b:c',':-',false,false),'*')#", right="a*b*c")>
<cfset valueEquals(left="#arrayToList(ListToArray('a:-b:-c',':-',false,true),'*')#", right="a*b*c")>
<cfset valueEquals(left="#arrayToList(ListToArray('a-b:c',':-',false,true),'*')#", right="a-b:c")>


<cfset valueEquals(left="#arrayToList(ListToArray(':-:-a:-b:-:-c:-:-',':-',false,true),'*')#", right="a*b*c")>
<cfset valueEquals(left="#arrayToList(ListToArray(':-:-a:-b:-:-c:-:-',':-',true,true),'*')#", right="**a*b**c**")>



<cfset valueEquals(left="#arrayToList(ListToArray(':-x:-xa:-xb:-x:-x:-x:-x',':-x',true,true),'*')#", right="**a*b****")>

<cfset valueEquals(left="#arrayLen(ListToArray('',',',false))#", right="0")>
<cfset valueEquals(left="#arrayLen(ListToArray('',',',true))#", right="1")>




<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>