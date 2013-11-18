<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.dir=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(dir,true)>
	</cffunction>
	
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDirectoryCreate" localMode="modern">

<!--- begin old test code --->
<cflock name="testdirectoryCreate" timeout="1" throwontimeout="no" type="exclusive">
<cfset _dir=dir&createUUID()>

<cfset valueEquals(left="#DirectoryExists(_dir)#", right="#false#")>
<cfset directoryCreate(_dir)>
<cfset valueEquals(left="#DirectoryExists(_dir)#", right="#true#")>

<cftry>
	<cfset directoryCreate(_dir)>
	<cfset fail("must throw:The specified directory ... could not be created.")>
	<cfcatch></cfcatch>
</cftry>
<cfset directorydelete(_dir)>

   

<cfset dir2=_dir&"/a/b/c/">
<cfset valueEquals(left="#DirectoryExists(dir2)#", right="#false#")>
<cfset directoryCreate(dir2)>
<cfset valueEquals(left="#DirectoryExists(dir2)#", right="#true#")>
<cfset directorydelete(_dir,true)>

</cflock>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>