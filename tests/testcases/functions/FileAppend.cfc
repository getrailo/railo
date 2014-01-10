<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.dir=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		<cfdirectory directory="#dir#" action="create" mode="777">
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(dir,true)>
	</cffunction>
	
	
	<cffunction name="testFileAppend" localMode="modern">

<!--- begin old test code --->
<cfscript>
// path
_file=dir&"test.txt";

fileWrite(_file,"ABC");
fileAppend(_file,"DEF","UTF-8");

valueEquals(trim(fileRead(_file)),"ABCDEF");
</cfscript>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>