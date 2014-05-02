<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testListContains" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#ListContains('a,b,abba,bcb','bb')#", right="3")>
<cfset valueEquals(left="#ListContains('abba,bb,AABBCC','BB')#", right="3")>
<cfset valueEquals(left="#ListContains('abba,bb,AABBCC','ZZ')#", right="0")>
<cfset valueEquals(left="#ListContains(',,,,,abba,bb,AABBCC,,,,','ZZ')#", right="0")>
<cfset valueEquals(left="#ListContains('abba,,,,,bb,AABBCC','BB')#", right="3")>
<cfset valueEquals(left="#ListContains('abba,,,,,bb,AABBCC','BB',';,.')#", right="3")>



<cfset valueEquals(left="#listcontains("evaluate,expression","")#", right="0")>
<cfset valueEquals(left="#listcontains("evaluate,,expression","")#", right="0")>



<cfset valueEquals(left="#listcontains("evaluate,,expression","expression")#", right="2")>
<cfset valueEquals(left="#listcontains("evaluate, ,expression","expression")#", right="3")>
<cfset valueEquals(left="#listcontains("evaluate, ,expression","expression",',',true)#", right="3")>
<cfset valueEquals(left="#listcontains("evaluate,,expression","expression",',',true)#", right="3")>
<cfset valueEquals(left="#listcontains("evaluate, ,expression","expression",',',false)#", right="3")>
<cfset valueEquals(left="#listcontains("evaluate,,expression","expression",',',false)#", right="2")>

<cfset valueEquals(left="#listcontains("evaluate,,expression","expression",';,',false,false)#", right="2")>
<cfset valueEquals(left="#listcontains("evaluate,,expression","expression",';,',false,true)#", right="1")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>