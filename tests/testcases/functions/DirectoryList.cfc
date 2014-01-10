<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.parent=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(parent,true)>
	</cffunction>
	<cffunction name="testDirectoryList" localMode="modern">

<!--- begin old test code --->
<cflock name="testdirectoryList" timeout="1" throwontimeout="no" type="exclusive">
<cfset path=parent&createUUID()>
<cfset path2=path&"/a">
<cfset directoryCreate(path2)>
<cffile action="write" addnewline="yes" file="#path#/b.txt" output="aaa" fixnewline="no">
<cffile action="write" addnewline="yes" file="#path2#/c.txt" output="aaa" fixnewline="no">


<cfset dir=directoryList(path)>
<cfset valueEquals(left="#arrayLen(dir)#", right="#2#")>
<cfset valueEquals(left="#listSort(arrayToList(dir),'textnocase')#", right="#path#/a,#path#/b.txt")>


<cfset dir=directoryList(path,true)>
<cfset valueEquals(left="#arrayLen(dir)#", right="#3#")>
<cfset valueEquals(left="#listSort(arrayToList(dir),'textnocase')#", right="#path#/a,#path#/a/c.txt,#path#/b.txt")>


<cfset dir=directoryList(path,true,"name")>
<cfset valueEquals(left="#arrayLen(dir)#", right="#3#")>
<cfset valueEquals(left="#listSort(arrayToList(dir),'textnocase')#", right="a,b.txt,c.txt")>

<cfset dir=directoryList(path,true,"path")>
<cfset valueEquals(left="#arrayLen(dir)#", right="#3#")>
<cfset valueEquals(left="#listSort(arrayToList(dir),'textnocase')#", right="#path#/a,#path#/a/c.txt,#path#/b.txt")>

<cfset dir=directoryList(path,true,"query")>




<cfset directoryDelete(path,true)>


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