<cfparam name="url.print_js_resources" type="boolean" default="true" />
<cfparam name="url.toggledebug" type="boolean" default="true" />
<cfparam name="url.action" type="string" default="none" />
<cfset currentDirectory= getDirectoryFromPath(getCurrentTemplatePath())>
<cfsetting requesttimeout="3600"><!-- 1 hour -->

<cfscript>
string function toPackage(string directory){
	var currentDirectory= getDirectoryFromPath(getCurrentTemplatePath());
	return replace(replace(replace(arguments.directory,currentDirectory,''),'\','.','all'),'/','.','all');
}
string function toName(string name){
	return mid(arguments.name,1,len(arguments.name)-4);
}


</cfscript>

<cfdirectory action="list" recurse="true"  sort="dateLastModified desc"  directory="./testcases" name="dir" filter="*.cfc">

<cfoutput>
<h1>Jira Tickets</h1>
<ul>
	<li><a href="index.cfm?action=all">Run all tickets</a></li>
	<li><a href="index.cfm?action=latest">Run latest 10 tickets</a></li>
	<ul>
		
	<cfloop query="#dir#" endrow="10">
		<cfset package=toPackage(dir.directory)>
		<cfset n=toName(dir.name)>
		<!---<cfif left(package,len("testcase-templates")) == "testcase-templates">
			<cfcontinue>
		</cfif>--->
		
		<li>
			<a href="index.cfm?action=single&testcase=#package#.#n#">Run #package#.#n#</a>
		</li>
	</cfloop>
	</ul>
	<li>
		<form action="index.cfm">Run
		<input type="hidden" name="action" value="single">
		<input type="text" name="ticket" value="<cfif isNull(url.ticket)>tickets.Jira<cfelse>#url.ticket#</cfif>">
		<input type="submit" name="submit" value="Go">
		</form>
	</li>
</ul>
</cfoutput>
<cfif url.action EQ "list">
	

</cfif>


<cfscript>
if(url.action != "none") {
	
	
	testSuite = new org.railo.cfml.test.RailoTestSuite();
	//testSuite.maxTheads(20);
	
	if(url.action == "single") {
		cfcName=url.testcase;
		cfc=createObject('component',cfcName);
		testSuite.addAll(cfcName,cfc);
	}
	else if(url.action == "latest" || url.action == "all") {
		loop query="#dir#" endrow="#url.action=="latest"?10:1000000#" {
			package=toPackage(dir.directory);
			if(listLen(package,'.')>2) continue;
			n=toName(dir.name);
			
			cfcName=package&'.'&n;
			cfc=createObject('component',cfcName);
			testSuite.addAll(cfcName,cfc);
		}
	}
	//testSuite.setMaxThreads(10);
	
	results = testSuite.run();
    echo(results.getResultsOutput('html'));
    qry=results.getResultsOutput('query');
	
}
/*
TODO

overwrite TestResult.getHTMLResults();
testSuite.maxParallelThread(); // default 1
testSuite.addTestCase('...',1000); // timeout setting for the testcase, it should be possible to define this also on suite level 
*/

</cfscript>

