<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfset variables.name=ListFirst(ListLast(getCurrentTemplatePath(),"\/"),".")>
		<cfset variables.dir=getDirectoryFromPath(getCurrentTemplatePath())&name&"/">
		
	</cffunction>
	<cffunction name="afterTests">
		<cfset directorydelete(dir,true)>
	</cffunction>
	
	<cffunction name="testdirectoryCopy" localMode="modern">

<!--- begin old test code --->


<!--- inital create --->
<cfdirectory directory="#dir#inc" action="create" mode="777">
<cffile action="write" file="#dir#/inc/test1.txt">hello1</cffile>
<cffile action="write" file="#dir#/inc/abra.txt">hello1</cffile>
<cfdirectory directory="#dir#inc/sub" action="create" mode="777">
<cffile action="write" file="#dir#inc/sub/test3.txt">hello2</cffile>


<!--- copy --->	
<cfscript>
directoryCopy("#dir#inc","#dir#inc2");
directoryCopy("#dir#inc","#dir#inc3",true);
directoryCopy("#dir#inc","#dir#inc4",false,"test*");
directoryCopy("#dir#inc","#dir#inc5",true,"test*"); 
</cfscript>

<!--- test --->
<cfdirectory directory="#dir#inc2" action="list" name="qry" recurse="yes">
<cfset valueEquals(left="#listSort(valueList(qry.name),'textnocase')#", right="abra.txt,test1.txt")>

<cfdirectory directory="#dir#inc3" action="list" name="qry" recurse="yes">
<cfset valueEquals(left="#listSort(valueList(qry.name),'textnocase')#", right="abra.txt,sub,test1.txt,test3.txt")>

<cfdirectory directory="#dir#inc4" action="list" name="qry" recurse="yes">
<cfset valueEquals(left="#listSort(valueList(qry.name),'textnocase')#", right="test1.txt")>

<cfdirectory directory="#dir#inc5" action="list" name="qry" recurse="yes">
<cfset valueEquals(left="#listSort(valueList(qry.name),'textnocase')#", right="sub,test1.txt,test3.txt")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>