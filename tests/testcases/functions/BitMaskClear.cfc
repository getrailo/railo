<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitMaskClear" localMode="modern">
 
<!--- begin old test code --->
<cfset valueEquals(left="#BitMaskClear(3,1,1)#", right="1")>
<cfset valueEquals(left="#BitMaskClear(3,2,1)#", right="3")>
<cfset valueEquals(left="#BitMaskClear(31,2,4)#", right="3")>
<cftry>
	<cfset valueEquals(left="#BitMaskClear(31,32,4)#", right="3")>
	<cfset fail("must throw:Invalid argument for function BitMaskClear.")>
	<cfcatch></cfcatch>
</cftry>
<cftry>
	<cfset valueEquals(left="#BitMaskClear(31,-1,4)#", right="3")>
	<cfset fail("must throw:Invalid argument for function BitMaskClear.")>
	<cfcatch></cfcatch>
</cftry>
<cftry>
	<cfset valueEquals(left="#BitMaskClear(31,1,32)#", right="3")>
	<cfset fail("must throw:Invalid argument for function BitMaskClear.")>
	<cfcatch></cfcatch>
</cftry>
<cftry>
	<cfset valueEquals(left="#BitMaskClear(31,1,-1)#", right="3")>
	<cfset fail("must throw:Invalid argument for function BitMaskClear.")>
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