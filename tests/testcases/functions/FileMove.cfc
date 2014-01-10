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
	
	<cffunction name="testFileMove" localMode="modern">

<!--- begin old test code --->
<cfset srcDir=dir&"src/">
<cfset trgDir=dir&"trg/">
<cfdirectory directory="#srcDir#" action="create" mode="777">
<cfdirectory directory="#trgDir#" action="create" mode="777">
<cfscript>
// define paths
src=srcDir&"test.txt";
dest1=trgDir&"testx.txt";
dest3=trgDir&'test.txt';

valueEquals(FileExists(dest1),false);
valueEquals(FileExists(dest3),false);


// copy with destination file
if(!FileExists(src))fileWrite(src,"ABC");
fileMove(src,dest1);

// copy with destination dir
if(!FileExists(src))fileWrite(src,"ABC");
fileMove(src,trgDir);



valueEquals(FileExists(dest1),true);
valueEquals(FileExists(dest3),true);
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