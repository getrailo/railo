<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDateTimeFormat" localMode="modern">

<!--- begin old test code --->
<cfset d=CreateDateTime(2000,1,2,3,4,5)>

<cfset valueEquals(left="#DateTimeFormat(d, "yyyy.MM.dd G 'at' HH:nn:ss z")#", right="2000.01.02 AD at 03:04:05 CET")>


<cfset valueEquals(left="#DateTimeFormat(d)#", right="02-Jan-2000 03:04:05")>
<cfset valueEquals(left="#DateTimeFormat(d, "yyyy.MM.dd G 'at' HH:nn:ss z")#", right="2000.01.02 AD at 03:04:05 CET")>
<cfset valueEquals(left="#DateTimeFormat(d, "EEE, MMM d, ''yy")#", right="Sun, Jan 2, '00")>
<cfset valueEquals(left="#DateTimeFormat(d, "h:nn a")#", right="3:04 AM")>
<cfset valueEquals(left="#DateTimeFormat(d, "hh 'o''clock' a, zzzz")#", right="03 o'clock AM, Central European Time")>
<cfset valueEquals(left="#DateTimeFormat(d, "K:nn a, z")#", right="3:04 AM, CET")>
<cfset valueEquals(left="#DateTimeFormat(d, "yyyyy.MMMMM.dd GGG hh:nn aaa")#", right="02000.January.02 AD 03:04 AM")>
<cfset valueEquals(left="#DateTimeFormat(d, "EEE, d MMM yyyy HH:nn:ss Z")#", right="Sun, 2 Jan 2000 03:04:05 +0100")>
<cfset valueEquals(left="#DateTimeFormat(d, "yyMMddHHnnssZ", "GMT")#", right="000102020405+0000")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>