<cfscript>

// SETUP THE ROOTS OF THE BROWSER RIGHT HERE
currentDirectory= getDirectoryFromPath(getCurrentTemplatePath());
rootPath= currentDirectory&"testcases/";
rootMapping 	= contractPath(rootPath);

/**
* converts a path to a package name
*/
function toPackage(string path) {
	return ListCompact(replace(replace(mid(path,1,len(path)-4),'/','.','all'),'\','.','all'),'.');
}

/**
* checks if given cfc name is a valid testcase component
*/
function isTestCase(string cfc, boolean defaultValue) localmode="true" {
	try{
		meta=getComponentMetaData(cfc);
		while(true) {
			if(meta.fullname=='testbox.system.testing.compat.framework.TestCase') return true;
			if(!structKeyExists(meta,'extends')) return false;
			meta=meta.extends;
		}
	}
	catch(e){}
	return defaultValue
}

// Logo	
logo="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";


// Decodes & Path Defaults
if(isNull(url.path))url.path="/";
url.path = urlDecode( url.path );
if(!len( url.path )) url.path = "/";

// Prepare TestBox 
testbox = new testbox.system.testing.TestBox();


</cfscript>





<!--- Run Tests Action?--->
<cfif structKeyExists( url, "action")>
	<cfif directoryExists( expandPath( rootMapping & url.path ) )>
		
		<cfoutput>
#rootMapping & url.path#
#testbox.init( directory=rootMapping & url.path ).run(directory:{mapping:rootMapping & url.path,recurse:false})#</cfoutput>
<!--- reporter:"testbox.system.testing.reports.RailoReporter" --->
	<cfelse>
		<cfoutput><h1>Invalid incoming directory: #rootMapping & url.path#</h1></cfoutput>
	</cfif>
	<cfabort>
	
</cfif>

<!--- Get list of files --->
<cfdirectory action="list" directory="#rootPath & url.path#" name="qResults" sort="asc" >


<!--- Get the execute path --->
<cfset executePath = rootMapping & ( url.path eq "/" ? "/" : url.path & "/" )>
<!--- Get the Back Path --->
<cfif url.path neq "/">
	<cfset backPath = replacenocase( url.path, listLast( url.path, "/" ), "" )>
	<cfset backPath = reReplace( backpath, "/$", "" )>
</cfif>

<!--- Do HTML --->
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="generator" content="TestBox v#testbox.getVersion()#">
	<title>TestBox Global Runner</title>
	<script><cfinclude template="/testbox/system/testing/reports/assets/js/jquery.js"></script>
	<script>
	$(document).ready(function() {
		
	});
	function runTests(){
		$("#btn-run").html( 'Running...' ).css( "opacity", "0.5" );
		$("#tb-results").load( "index.cfm", $("#runnerForm").serialize(), function( data ){
			$("#btn-run").html( 'Run' ).css( "opacity", "1" );
		} );
	}
	function clearResults(){
		$("#tb-results").html( '' );
		$("#target").html( '' );
		$("#labels").html( '' );
	}
	</script>
	<style>
	body{
		font-family:  Monaco, "Lucida Console", monospace;
		font-size: 10.5px;
		line-height: 20px;
	}
	h1,h2,h3,h4{ margin-top: 3px;}
	h1{ font-size: 14px;}
	h2{ font-size: 13px;}
	h3{ font-size: 12px;}
	h4{ font-size: 11px; font-style: italic;}
	ul{ margin-left: -10px;}
	li{ margin-left: -10px; list-style: none;}
	a{ text-decoration: none;}
	a:hover{ text-decoration: underline;}
	/** utility **/
	.centered { text-align: center !important; }
	.inline{ display: inline !important; }
	.margin10{ margin: 10px; }
	.padding10{ padding: 10px; }
	.margin0{ margin: 0px; }
	.padding0{ padding: 0px; }
	.box{ border:1px solid gray; margin: 10px 0px; padding: 10px; background-color: #f5f5f5}
	.pull-right{ float: right;}
	.pull-left{ float: left;}
	#tb-runner{ min-height: 155px}
	#tb-runner #tb-left{ width: 17%; margin-right: 10px; margin-top: 0px; height: 135px; float:left;}
	#tb-runner #tb-right{ width: 80%; }
	#tb-runner fieldset{ padding: 10px; margin: 10px 0px; border: 1px dotted gray;}
	#tb-runner input{ padding: 5px; margin: 2px 0px;}
	#tb-runner .btn-red {
		background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #f24537), color-stop(1, #c62d1f) );
		background:-moz-linear-gradient( center top, #f24537 5%, #c62d1f 100% );
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#f24537', endColorstr='#c62d1f');
		background-color:#f24537;
		-webkit-border-top-left-radius:5px;
		-moz-border-radius-topleft:5px;
		border-top-left-radius:5px;
		-webkit-border-top-right-radius:5px;
		-moz-border-radius-topright:5px;
		border-top-right-radius:5px;
		-webkit-border-bottom-right-radius:5px;
		-moz-border-radius-bottomright:5px;
		border-bottom-right-radius:5px;
		-webkit-border-bottom-left-radius:5px;
		-moz-border-radius-bottomleft:5px;
		border-bottom-left-radius:5px;
		text-indent:1.31px;
		border:1px solid #d02718;
		display:inline-block;
		color:#ffffff;
		font-weight:bold;
		font-style:normal;
		height:25px;
		width:71px;
		text-decoration:none;
		text-align:center;
		cursor: pointer;
	}
	#tb-runner .btn-red:hover {
		background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #c62d1f), color-stop(1, #f24537) );
		background:-moz-linear-gradient( center top, #c62d1f 5%, #f24537 100% );
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#c62d1f', endColorstr='#f24537');
		background-color:#c62d1f;
	}
	#tb-runner .btn-red:active {
		position:relative;
		top:1px;
	}
	#tb-results{ padding: 10px;}
	code{ padding: 2px 4px; color: #d14; white-space: nowrap; background-color: #f7f7f9; border: 1px solid #e1e1e8;}
	</style>
</head>
<cfoutput>
<body>

<!--- Title --->
<div id="tb-runner" class="box">
<form name="runnerForm" id="runnerForm">
<input type="hidden" name="opt_run" id="opt_run" value="true">
	<div id="tb-left" class="centered">
		<img src="data:image/png;base64,#logo#" alt="TestBox" id="tb-logo"/><br>v#testbox.getVersion()#<br>

		<a href="index.cfm?action=runTestBox&path=#URLEncodedFormat( url.path )#" target="_blank"><button class="btn-red" type="button">Run All</button></a>
	</div>

	<div id="tb-right">
		<h1>TestBox Test Browser: </h1>
		<p>
			Below is a listing of the files and folders starting from your root <code>#rootPath#</code>.  You can click on individual tests in order to execute them
			or click on the <strong>Run All</strong> button on your left and it will execute a directory runner from the visible folder.
		</p>
		
		<fieldset><legend>Contents: #executePath#</legend>
		<cfif url.path neq "/">
			<a href="index.cfm?path=#URLEncodedFormat( backPath )#"><button type="button" class="btn-red">&lt;&lt; Back</button></a><br><hr>
		</cfif>
		<cfloop query="qResults">
			<cfif refind( "^\.", qResults.name )>
				<cfcontinue>
			</cfif>

			<cfset dirPath = URLEncodedFormat( ( url.path neq '/' ? '#url.path#/' : '/' ) & qResults.name )>
			<cfif qResults.type eq "Dir">
				<cfif !fileExists(qResults.directory&"/"&qResults.name&".cfc")>
					+<a href="index.cfm?path=#dirPath#">#qResults.name#</a><br/>
				</cfif>
			<!--- <cfelseif listLast( qresults.name, ".") eq "cfm">
				<a href="#executePath & qResults.name#" target="_blank">#qResults.name#</a><br/> --->
			<cfelseif listLast( qresults.name, ".") eq "cfc" && isTestcase(toPackage(executePath & qResults.name),false)>
				<a class="test" href="#executePath & qResults.name#?method=runRemote" target="_blank"><button type="button">#qResults.name#</button></a><br/>
			<!--- <cfelse>
				#qResults.name#<br/> --->
			</cfif>
				
		</cfloop>
		</fieldset>

	</div>

</form>
</div>

<!--- Results --->
<div id="tb-results"></div>

</body>
</html>
</cfoutput>