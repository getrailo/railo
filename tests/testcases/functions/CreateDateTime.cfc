<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCreateDateTime" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#CreateDateTime(2000, 12, 1,12,11,10)#x", right="{ts '2000-12-01 12:11:10'}x")> 

<cfset valueEquals(
	left="{ts '1899-12-31 00:00:00'}x" ,
	right="#CreateODBCDateTime(1)#x")>

<cfset valueEquals(
	left="{ts '1899-12-31 02:24:00'}x" ,
	right="#CreateODBCDateTime(1.1)#x")>


<cfset valueEquals( 
	left="{ts '1899-12-31 02:38:24'}x" ,
	right="#CreateODBCDateTime(1.11)#x")>
<cfset valueEquals(
	left="{ts '1899-12-31 02:39:50'}x" ,
	right="#CreateODBCDateTime(1.111)#x")>
<cfset valueEquals(
	left="{ts '1899-12-31 02:39:59'}x" ,
	right="#CreateODBCDateTime(1.1111)#x")>
<cfset valueEquals( 
	left="{ts '1899-12-31 02:39:59'}x" ,
	right="#CreateODBCDateTime(1.11111)#x")>
    
    
<!--- has not really something to do with createDateTime but with date/calendar objects in general --->
<cfset tz = createObject('java','java.util.TimeZone').getTimeZone("America/Mexico_City")>
<cfset c = createObject('java','java.util.Calendar').getInstance()>
<cfset c.setTimeZone(tz)>
<cfset dt=CreateDateTime(2000,1,1,1,1,1)>
<cfset c.setTime(dt)>

<cfset valueEquals(left="#c#x", right="#dt#x")>
<cfset valueEquals(left="#c#x", right="#dt#x")>
<cfset valueEquals(left="#c#x", right="#dt&""#x")>
<cfset valueEquals(left="#c EQ dt#", right="#true#")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>