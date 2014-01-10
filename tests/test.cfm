<cfparam name="url.print_js_resources" type="boolean" default="true" />
<cfparam name="url.toggledebug" type="boolean" default="true" />
<cfsetting requesttimeout="3600"><!-- 1 hour -->

<cfscript>
	
	testSuite = new org.railo.cfml.test.RailoTestSuite();
	testSuite.addPackage('tickets');
	//testSuite.setMaxThreads(10);
	
	results = testSuite.run();
    echo(results.getResultsOutput('html'));
    qry=results.getResultsOutput('query');
	

/*
TODO

overwrite TestResult.getHTMLResults();
testSuite.maxParallelThread(); // default 1
testSuite.addTestCase('...',1000); // timeout setting for the testcase, it should be possible to define this also on suite level 
*/
templ=getPageContext().getConfig().getErrorTemplate(500);
dump(templ);
</cfscript>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
    <title>Expand table rows with jQuery - jExpand plugin - JankoAtWarpSpeed demos</title>
    <style type="text/css">
        body { font-family:Arial, Helvetica, Sans-Serif; font-size:0.8em;}
        #report { border-collapse:collapse;}
        #report h4 { margin:0px; padding:0px;}
        
		/* header */ 
		#report th { background:#7CB8E2; color:#fff; padding:7px 15px; text-align:left;}
        /* hidden row*/
		#report td { background:#eee none repeat-x scroll center left; color:#000; padding:7px 15px; }
        
		/* row */
		#report tr.odd td { background:#ccc; cursor:pointer; }
        
		/* arrow*/
		#report div.arrow { background:transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAgCAYAAAAbifjMAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAABh0RVh0U29mdHdhcmUAUGFpbnQuTkVUIHYzLjEwcrIlkgAAAGBJREFUSEtjYBgFhELgPyEF+ORBmmGYZHOQNZNsCDbNRBuCTzNBQ4jRjNMQUjQTdAnJIT6qYTQEqBYCVEnKpBiC0+XEGELQ2xQVKDDTKSrSsBlC0Nm4FFBUrJNt6xDTCADiA2GfrRoZNQAAAABJRU5ErkJggg==) no-repeat scroll 0px -16px; width:16px; height:16px; display:block;}
        #report div.up { background-position:0px 0px;}
    </style>
	<style>
	#-railo-err			{ font-family: Verdana, Geneva, Arial, Helvetica, sans-serif; font-size: 11px; background-color:red; border: 1px solid black; }
	#-railo-err td 		{ border: 1px solid #350606; color: #222; background-color: #FFCC00; line-height: 1.35; }
	#-railo-err td.label	{ background-color: #FFB200; font-weight: bold; white-space: nowrap; vertical-align: top; }

	#-railo-err .collapsed	{ display: none; }
	#-railo-err .expanded 	{ display: block; }

	.-railo-icon-plus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==)
    					no-repeat left center; padding: 4px 0 4px 16px; }

	.-railo-icon-minus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7)
						no-repeat left center; padding: 4px 0 4px 16px; }
</style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript">  
        $(document).ready(function(){
            $("#report tr:odd").addClass("odd");
            $("#report tr:not(.odd)").hide();
            $("#report tr:first-child").show();
            
            $("#report tr.odd").click(function(){
                $(this).next("tr").toggle();
                $(this).find(".arrow").toggleClass("up");
            });
            //$("#report").jExpand();
        });
    </script>
	
	<script>

	var __RAILO = {

		oc: 	function ( btn ) {

			var id = btn.id.split( '$' )[ 1 ];

			var curBtnClass = btn.attributes[ 'class' ];	// bracket-notation required for IE<9
			var cur = curBtnClass.value;

			var curCstClass = document.getElementById( '__cst$' + id ).attributes[ 'class' ];

			if ( cur == '-railo-icon-plus' ) {

				curBtnClass.value = '-railo-icon-minus';
				curCstClass.value = 'expanded';
			} else {

				curBtnClass.value = '-railo-icon-plus';
				curCstClass.value = 'collapsed';
			}
		}
	}
</script>        
</head>
<body>
<table id="report">
    <tr>
        <th>Test</th>
        <th>Result</th>
        <th>Speed</th>
        <th></th>
    </tr>
<cfoutput query="#qry#">
    <tr>
        <td>#qry.component#.#qry.testname#</td>
        <td>#qry.testStatus#</td>
        <td>#qry.time#</td>
        <td><div class="arrow"></div></td>
    </tr>
    <tr>
        <td colspan="4">
			<cfif qry.testStatus neq "passed">
			    <cfset catch=qry.error>
				
				<!--- TODO do better impl for this --->
				<cfset entryIndex=1>
				<cfloop array="#catch.TagContext#" index="i" item="sct">
					<cfif not findNoCase("mxunit/framework",sct.template) and not findNoCase("org/railo/cfml/test",sct.template)> 
						<cfset entryIndex=i>
						<cfbreak>
					</cfif>
				</cfloop>
	
				<cfif entryIndex GT 1>
					<cfset catch.TagContext=arraySlice(catch.TagContext,entryIndex)>
				</cfif>
				<cfinclude template="error.cfm">
			</cfif>
        </td>
    </tr>
</cfoutput>
</table>
  
</body>
</html>


<!--->
<table>
<cfoutput query="#qry#">

    <pre><b style="color:#qry.testStatus EQ 'passed'?'green':'red'#">#qry.component#.#qry.testname# (#qry.testStatus#)</b></pre>
    <cfif qry.testStatus neq "passed">
    <cfset catch=qry.error>
	
	<!--- TODO do better impl for this --->
	<cfset entryIndex=1>
	<cfloop array="#catch.TagContext#" index="i" item="sct">
		<cfif not findNoCase("mxunit/framework",sct.template) and not findNoCase("org/railo/cfml/test",sct.template)> 
			<cfset entryIndex=i>
			<cfbreak>
		</cfif>
	</cfloop>
	
	<cfif entryIndex GT 1>
		<cfset catch.TagContext=arraySlice(catch.TagContext,entryIndex)>
	</cfif>
	
	
	
<pre>
    <cftry>#catch.Message#<cfcatch></cfcatch></cftry>
    <cftry>#qry.dateTime#<cfcatch></cfcatch></cftry>
    <cftry>#catch.tagContext[1].template#:#catch.tagContext[1].line#<cfcatch></cfcatch></cftry>
</pre>
    </cfif>
    </cfoutput>
</table>
--->
