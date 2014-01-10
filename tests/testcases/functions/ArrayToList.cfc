<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayToList" localMode="modern">

<!--- begin old test code --->
<cfset arr=arrayNew(1)> 
<cfset arr[1]=111>
<cfset arr[2]=22>
<cfset arr[3]=3.5>

<cfset valueEquals(left="#arrayToList(arr)#", right="111,22,3.5")>
<cfset valueEquals(left="#arrayToList(arr,'')#", right="111223.5")>
<cfset valueEquals(left="#arrayToList(arr,',;')#", right="111,;22,;3.5")>

<cfset arr[6]="ee">
<cfset valueEquals(left="#arrayToList(arr)#", right="111,22,3.5,,,ee")>

<cfset arr[7]="e,e">
<cfset valueEquals(left="#arrayToList(arr)#", right="111,22,3.5,,,ee,e,e")>


<cfset valueEquals(left="#arrayToList(arr,";")#", right="111;22;3.5;;;ee;e,e")>

<cfset arr=arrayNew(1)>
<cfset arr[1]="a">

<cfset ArrayResize(arr, 10)>
<cfset valueEquals(left="#arrayToList(arr)#", right="a,,,,,,,,,")>

<cfset arr=arrayNew(1)>
<cfset arr[1]="a">
<cfset arr[2]="b">

<cfset valueEquals(left="#arrayToList(arr,"{}")#", right="a{}b")>

<cfset arr=arrayNew(1)>
<cfset arr[4]=111>
<cfset arr[5]=22>
<cfset arr[6]=3.5>

<cfset valueEquals(left="#arrayToList(arr)#", right=",,,111,22,3.5")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>