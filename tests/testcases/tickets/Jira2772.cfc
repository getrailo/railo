<cfcomponent extends="org.railo.cfml.test.RailoTestCase">

	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.dir=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		<cfif directoryExists(dir)>
			<cfset afterTests()>
		</cfif>
		<cfdirectory directory="#dir#" action="create" mode="777">
	</cffunction>
	
	<cffunction name="afterTests">
		<cfset directorydelete(dir,true)>
	</cffunction>
	
	<cffunction name="testTagFile" localMode="modern">
		<cfset local.file=dir&"1.txt">
		
		<cffile action="write" file="#file#" output="Hello World" mode="644" addnewline="No">
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("Hello World",content)>
		
		<cffile action="write" file="#file#" output="Hello" mode="644" addnewline="No">
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("Hello",content)>
		
		<cffile action="write" file="#file#" output="" mode="644" addnewline="No">
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("",content)>
	</cffunction>
	
	
	<cffunction name="testFunctionFileWrite" localMode="modern">
		<cfset local.file=dir&"1.txt">
		<cfset fileWrite(file,"Hello World")>
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("Hello World",content)>
		
		<cfset fileWrite(file,"Hello")>
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("Hello",content)>
		
		<cfset fileWrite(file,"")>
		<cffile action="read" file="#file#" variable="local.content" mode="644">
		<cfset assertEquals("",content)>
	</cffunction>
	
</cfcomponent>