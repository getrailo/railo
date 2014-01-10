<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.parent=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(parent,true)>
	</cffunction>
	<cffunction name="testDirectoryDelete" localMode="modern">

<!--- begin old test code --->
<cflock name="testdirectoryDelete" timeout="1" throwontimeout="no" type="exclusive">
<cfset dir=parent&createUUID()>

<cfset directoryCreate(dir)>
<cfset directorydelete(dir)>

<cftry>
	<cfset directorydelete(dir)>
	<cfset fail("must throw:does not exist")>
	<cfcatch></cfcatch>
</cftry>
   

<cfset dir2=dir&"/a/b/c/">
<cfset directoryCreate(dir2)>
<cftry>
	<cfset directorydelete(dir)>
	<cfset fail("must throw:The specified directory ... could not be deleted.")>
	<cfcatch></cfcatch>
</cftry>
<cftry>
	<cfset directorydelete(dir,false)>
	<cfset fail("must throw:The specified directory ... could not be deleted.")>
	<cfcatch></cfcatch>
</cftry>
<cfset directorydelete(dir,true)>

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