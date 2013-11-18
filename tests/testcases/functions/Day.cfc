<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDay" localMode="modern">

<!--- begin old test code --->
<cfset d1=CreateDateTime(2001, 12, 1, 4, 10, 1)> 
<cfset valueEquals(left="#day(d1)#", right="1")>

<cfset valueEquals(left="31" ,right="#Day(1)#")>




<cfset x=struct()>
<cfset x.sDate = "11/01/2009" />
<cfset l=GetLocale()>
<cfset SetLocale("portuguese (brazilian)")>
<cfset valueEquals(left="{ts '2009-01-11 00:00:00'}x", right="#LsParseDateTime(x.sDate)#x")>
<cfset valueEquals(left="{ts '2009-11-01 00:00:00'}x", right="#ParseDateTime(x.sDate)#x")>



<cfset x.sNewDate = LsDateFormat(LsParseDateTime(x.sDate), 'dd/mm/yyyy') />
<cfset valueEquals(left="11/01/2009x", right="#x.sNewDate#x")>



<cfset valueEquals(left="11", right="#Day(LSParseDateTime(x.sNewDate))#")>
<cfset valueEquals(left="1", right="#Day(ParseDateTime(x.sNewDate))#")>
<cfset valueEquals(left="1", right="#Day(x.sNewDate)#")>
<cfset valueEquals(left="11", right="#Month(x.sNewDate)#")>
<cfset valueEquals(left="11", right="#Month(ParseDateTime(x.sNewDate))#")>
<cfset valueEquals(left="1", right="#Month(LsParseDateTime(x.sNewDate))#")>


<cfset SetLocale(l)>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>