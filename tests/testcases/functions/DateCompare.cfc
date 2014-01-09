<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDateCompare" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 23:59:59'}","d")#", right="1")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 23:59:59'}","h")#", right="1")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 23:59:59'}","n")#", right="1")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 23:59:59'}","m")#", right="0")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 23:59:59'}","s")#", right="1")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-10 00:00:00'}","{ts '2007-10-09 00:00:00'}","d")#", right="1")>
	
<cfset valueEquals(left="#DateCompare("{ts '2007-10-09 00:00:00'}","{ts '2007-10-10 00:00:00'}","d")#", right="-1")>
<cfset valueEquals(left="-1", right="#dateCompare(1,2)#")>



<cfset d1=CreateDateTime(2001, 11, 1, 4, 10, 1)> 
<cfset d2=CreateDateTime(2001, 11, 1, 4, 10, 4)> 
<cfset valueEquals(left="#DateCompare(d1, d2)#", right="-1")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"s")#", right="-1")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"n")#", right="0")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"h")#", right="0")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"yyyy")#", right="0")> 
<cfset d2=CreateDateTime(2001, 11, 1, 5, 10, 4)> 
<cfset valueEquals(left="#DateCompare(d1, d2,"m")#", right="0")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"d")#", right="0")> 
<cfset valueEquals(left="#DateCompare(d1, d2,"d")#", right="0")> 
<cftry> 
        <cfset valueEquals(left="#DateCompare(d1, d2,"w")#", right="0")> 
        <cfset fail("must throw:DateCompare w")> 
        <cfcatch></cfcatch> 
</cftry> 
<cftry> 
        <cfset valueEquals(left="#DateCompare(d1, d2,"ww")#", right="0")> 
        <cfset fail("must throw:DateCompare ww")> 
        <cfcatch></cfcatch> 
</cftry> 
<cftry> 
        <cfset valueEquals(left="#DateCompare(d1, d2,"q")#", right="0")> 
        <cfset fail("must throw:DateCompare q")> 
        <cfcatch></cfcatch> 
</cftry> 
<cftry> 
        <cfset valueEquals(left="#DateCompare(d1, d2,"susi")#", right="0")> 
        <cfset fail("must throw:DateCompare susi")> 
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