<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.parent=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(parent,true)>
	</cffunction>
	
	<cffunction name="testDirectoryRename" localMode="modern">

<!--- begin old test code --->
<cflock name="testdirectoryDelete" timeout="1" throwontimeout="no" type="exclusive">
<cfset dir=parent&createUUID()>
<cfset nn="DirectoryRename-"&createUUID()>
<cfset dir2=parent&nn>

<cfset directoryCreate(dir)>
<cfset directoryRename(dir,dir2)>


<cfset valueEquals(left="#directoryExists(dir)#", right="#false#")>
<cfset valueEquals(left="#directoryExists(dir2)#", right="#true#")>

<cfset directorydelete(dir2,true)>





<cfset directoryCreate(dir)>
<cfset directoryRename(dir,nn)>


<cfset valueEquals(left="#directoryExists(dir)#", right="#false#")>
<cfset valueEquals(left="#directoryExists(dir2)#", right="#true#")>

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