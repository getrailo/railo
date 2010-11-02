<cfsilent>
<cfparam name="cookie.RAILO_ADMIN_LANG" default="en">
<cfinclude template="debugging-console-lang.cfm">
<cfif isDefined("form.storeSettings")>
    <cfset server.stDebugOptions = struct("iTime":"#form.filterTime#","bExclude":"#form.filterType#","lstDebugFilters":"#form.templates#")>
	<cflocation addtoken="No" url="#cgi.script_name#">
<cfelseif isDefined("form.resetSettings")>
	<cfset structDelete(server, "stDebugOptions")>
	<cfset form.filterType = 0>
	<cfset form.templates  = "">
	<cflocation addtoken="No" url="#cgi.script_name#">
<cfelse>
	<cfif structKeyExists(server, "stDebugOptions")>
		<cfset form.filterType = server.stDebugOptions.bExclude>
		<cfset form.filterTime = server.stDebugOptions.iTime>
		<cfset form.templates  = server.stDebugOptions.lstDebugFilters>
	</cfif>
</cfif>
<cfparam name="cookie.DISPLAY_OPTIONS" default="3">
<cfset sWeb_ID = getPageContext().getConfig().getId()>
<cfif isDefined("url.action") and url.action eq "reset_debug">
	<cfset structClear(server[sWeb_ID])>
	<cfset structDelete(server, sWeb_ID)>
	<cflocation url="#cgi.script_name#" addtoken="No">
</cfif>
<cfset bFilter4MyIP = (BitAnd(cookie.display_options, 16) eq 16)>
<cfparam name="url.opt" default="0|5">
</cfsilent>
<html>
	<head>
		<title>Railo 3.2 - Debugging console</title>
		<style type="text/css">
			.checkbox {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;}
			.cfdebug {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;}
			.cfdebuglge {color:#6699BB;background-color:white;font-family:"Verdana", Times, serif; font-size:small;}
			.cfdebug_head {color:#224499;background-color:white;font-family:"Verdana", Times, serif; font-size:small;font-weight: bold; }
			a {color:#0099FF;}
			a.cfdebuglink {color:blue; background-color:white;}
			.template {	color: black; font-family: "Verdana", Times, serif; font-weight: normal; }
			.template_overage {	background-color:white; font-family: "Verdana", Times, serif; font-weight: bold; }
			body {background-color:#FFFFFF;text-align:left;color:#666666;font-family:Arial, Helvetica, sans-serif;}
			.tblContent{padding-left:5px;padding-right:5px;border:1px solid #CFD7E2;background-color:#DFE9F6;font-size:12px;}
			.tbl{border:0px solid #CFD7E2;}
			.tblHead{padding-left:5px;padding-right:5px;background-color:#CFD7E2;border:1px solid #CFD7E2;font-weight:bold;font-size:12px;}
			.btn {background-color:#99CCFF;color:#0000CC}
		</style>
	</head><body>
	<cfoutput>
	<table class="tbl" width="100%"><tr>
	<cfif not StructKeyExists(server, sWeb_ID)>
		#sNoDebugInfoAvailable#
	<cfelse>
		<cfset iStart = server[sWeb_ID].debugEntryCount>
		<td valign="top" width="100%">
			<table class="tbl" width="100%">
				<tr>
					<td class="tblHead" width="15%">#sTitleRequestTime#</td>
					<td class="tblHead" width="15%">#sTitleExecTime#</td>
					<td class="tblHead" colspan="2" width="60%">#sTitleCalledURL#</td>
					<td class="tblHead" width="10%">#sTitleOptions#</td>
				</tr>
				<cfset bDrawn = False>
				<cfloop from="1" to="10" index="i">
					<cfif iStart lt 1><cfset iStart = 10></cfif>
					<cfif ArrayIndexExists(server[sWeb_ID].debugEntries, iStart)>
						<cfif structKeyExists(server[sWeb_ID].debugEntries[iStart], "ipAddress")>
							<cfset sIPAddress = server[sWeb_ID].debugEntries[iStart].ipAddress>
						<cfelse>
							<cfset sIPAddress = "">
						</cfif>
					</cfif>
					<cfif (bFilter4MyIP AND (sIPAddress eq cgi.remote_addr)) or not bFilter4MyIP>
						<cfif ArrayIndexExists(server[sWeb_ID].debugEntries, iStart)>
							<cfset stDebug = server[sWeb_ID].debugEntries[iStart]>
							<cfset iQry   = 0>
							<cfset iTotal = 0>
							<cfif not structKeyExists(stDebug, "iTotal")>
								<cfloop query="stDebug.debugInfo.pages">
									<cfset iTotal = iTotal + stDebug.debugInfo.pages.total>
								</cfloop>
							<cfelse>
								<cfset iTotal = stDebug.iTotal>
							</cfif>
							<cfloop query="stDebug.debugInfo.queries">
								<cfset iQry = iQry + stDebug.debugInfo.queries.time>
							</cfloop>
							
							<tr>
								<td class="tblContent" valign="top">
								<span title="#sTimeStampRecorded#" alt="#sTimeStampRecorded#">#DateFormat(stDebug.recorded, "dd.mm.yyyy")#&nbsp;
								#TimeFormat(stDebug.recorded, "HH.mm.ss")#</span>
								<cfif sIPAddress neq "">
									<span title="#sCallersIPAddress#" alt="#sCallersIPAddress#">#sIPAddress#</span>
								</cfif>
								</td>
								<td class="tblContent" align="right" valign="top"><span title="#sTotalRequestTime#" alt="#sTotalRequestTime#">#Numberformat(iTotal, "9,990")#ms</span>&nbsp;&nbsp;&nbsp;
									<span title="Total template execution time" alt="Total template execution time"><b>T:</b> #iTotal-iQry#ms</span>&nbsp;&nbsp;
									<span title="Total query execution time" alt="Total query execution time"><b>Q:</b> #iQry#ms</span>
								</td>
								<td class="tblContent" valign="top">
									<span title="#sRequestedURL#" alt="#sRequestedURL#">
									<a href="debugging-console-output.cfm?requestID=#iStart#" target="dsp_debug">
									<cfif len(stDebug.calledURL) gt 80>
										#Left(stDebug.calledURL, 80)#...
									<cfelse>
										#stDebug.calledURL#
									</cfif>
									</a>
									</span>
									
								</td>
								<td class="tblContent" valign="top">
									<span title="#sStore2File#" alt="#sStore2File#">
									<a href="debugging-console-output.cfm?requestID=#iStart#&_debug_action=store" target="dsp_debug">store2file</a>
									</span>&nbsp;<a href="#stDebug.calledURL#" target="_blank">#sReplay#</a>
								</td>
								<cfif not bDrawn>
									<cfset bDrawn = True>
									<form action="#cgi.script_name#" method="post" name="options">
									<td rowspan="10" class="tblContent" valign="top">
									#sReload# <input type="Checkbox" id="reloadAutomatically"<cfif ListFirst(url.opt, "|") eq 1> checked</cfif>><br>
									#sEvery#: <input type="Text" name="interval" value="#ListLast(url.opt, '|')#" size="1" id="interval"> #sSeconds#<br><br>
									</td>
									</form>
								</cfif>
							</tr>
						</cfif>
					</cfif>
					<cfset iStart = iStart - 1>
				</cfloop>
			</table>
			<a href="#cgi.script_name#?action=reset_debug">Reset debugging struct</a>
		</td>
	</cfif>
	<td valign="top" align="right">
		<table cellpadding="0" cellspacing="0" height="312"><tr>
			<td class="tblHead" style="border-right: 1px solid ##ffffff;cursor:pointer;" id="DEBUGOPTIONS_TD" onclick="javascript:toggle('DEBUGOPTIONS')">></td>
			<td>
				<table class="tbl" bgcolor="white" align="right" border="1" cellpadding="3" cellspacing="0" id="DEBUGOPTIONS" style="border-collapse:collapse"><!---
					---><tr><!---
					---><td class="tblHead" colspan="2"><!---
						---><b>Display Options:</b><!---
					---></td></tr><!---
					---><tr><!---
					---><td class="tblContent"><!---
						---><input type="Checkbox" id="outputexecution" value="1" onclick="enableOption()" class="checkbox" checked><span title="#sTitleExecTimes#" alt="#sTitleExecTimes#">#sExecTimes#</span><br><!---
						---><input type="Checkbox" id="outputastree" value="8" onclick="enableOption()" class="checkbox" checked><span title="#sTitleDispAsTree#" alt="#sTitleDispAsTree#">#sDisplayAsTree#</span><br><!---
					---></td><td class="tblContent"><!---
						---><input type="Checkbox" id="outputsql" value="2" onclick="enableOption()" class="checkbox" checked><span title="#sTitleShowSQL#" alt="#sTitleShowSQL#">#sShowSQLStatements#</span><br><!---
						---><input type="Checkbox" id="hidestatements" value="4" onclick="enableOption()" class="checkbox" checked><span title="#sTitleHideSQL#" alt="#sTitleHideSQL#">#sHideSQLStatements#</span><br><!---
						---><input type="Checkbox" id="plainoutput" value="32" onclick="enableOption()" class="checkbox" checked><span title="#sTitleHideSQL#" alt="#sTitleHideSQL#">#sPlainSQLStatements#</span><!---
					---></td></tr><!---
					---><tr><td class="tblContent"><!---
						---><span title="#sTitleDisplay#" alt="#sTitleDisplay#">#sDisplay#&nbsp;<select name="outputMaxFiles" id="outputMaxFiles" onchange="enableOption()">
							<option value="-1">all</option>
							<option value="10">10</option>
							<option value="50">50</option>
							<option value="100">100</option>
							<option value="500">500</option>
							<option value="1000">1000</option>
						</select>&nbsp;#sFiles#</span><!---
					---></td><td class="tblContent"><!---
						---><span title="#sTitleDisplay#" alt="#sTitleDisplay#">#sDisplay#&nbsp;<select name="outputMaxQueries" id="outputMaxQueries" onchange="enableOption()">
							<option value="-1">all</option>
							<option value="10">10</option>
							<option value="50">50</option>
							<option value="100">100</option>
							<option value="500">500</option>
							<option value="1000">1000</option>
						</select>&nbsp;#sQueries#</span><!---
					---></td></tr><!---
					---><tr><!---
						---><td class="tblContent">#sSortOrder#:<br><select id="filenamesortorder" onchange="enableOption()" class="cfdebug"><!---
							---><option value="1">ExecutionTime (desc)</option><!---
							---><option value="2">FileName (asc)</option><!---
							---><option value="3">Count (desc)</option><!---
							---><option value="4">Average (desc)</option><!---
						---></select></td><!---
						---><td class="tblContent">#sSortorder#:<br><select id="querysortorder" onchange="enableOption()" class="cfdebug"><!---
							---><option value="1">ExecutionTime (desc)</option><!---
							---><option value="2">QueryName (asc)</option><!---
							---><option value="3">Records (desc)</option><!---
							---><option value="4">File (asc)</option><!---
							---><option value="5">Chronological</option><!---
						---></select></td><!---
					---></tr><tr><td class="tblContent" colspan="2"><!---
						---><input type="Checkbox" id="filter4myip" value="16" onclick="enableOption()" class="checkbox" checked><span title="#sTitleOwnAddress#" alt="#sTitleOwnAddress#">#sFilterOwnAddress#</span>&nbsp;<!---
					---></td></tr><tr><form action="#cgi.script_name#" method="post"><td class="tblContent" colspan="2"><!---
						---><cfparam name="form.templates" default=""><!--- 
						---><cfparam name="form.filterType" default="0"><!--- 
						---><cfparam name="form.filterTime" default="0"><!--- 
						---><span title="#sTitleFilterTemplates#" alt="#sTitleFilterTemplates#">#sFilterTemplates#</span>:<br><!---
						---><input type="Radio" name="filterType" value="0" class="checkbox" <cfif trim(form.filterType) eq 0>checked</cfif>><span title="#sTitleIncludeOnly#" alt="#sTitleIncludeOnly#">#sIncludeOnlyTemplates#</span>&nbsp;<!---
						---><input type="Radio" name="filterType" value="1" class="checkbox" <cfif trim(form.filterType) neq 0>checked</cfif>><span title="#sTitleExcludeOnly#" alt="#sTitleExcludeOnly#">#sExcludeOnlyTemplates#</span><br><!---
						---><textarea name="templates" style="width:380px;height:50px">#form.templates#</textarea><br><!---
						--->#sExecutionTimeLimit#<input type="Text" name="filterTime" class="cfdebug" value="#form.filterTime#">ms<br><!---
						---><input type="Submit" value="#sSave#" name="storeSettings" class="btn">&nbsp;<!---
						---><input type="Submit" value="#sResetSettings#" name="resetSettings" class="btn"><!---
					---></td></form></tr></table></td></tr><!---
				---></table>
			</td>
		</tr></table>
	</td></tr></table>
	Debugging output:
	<script language="JavaScript">
		function reloadMe() {
			if (document.getElementById("reloadAutomatically").checked) {
				window.location.href = "#cgi.script_Name#?opt=1|" + document.getElementById("interval").value;
			}
			window.setTimeout("reloadMe()", #listLast(url.opt, "|")*1000#)
		}
		window.setTimeout("reloadMe()", #listLast(url.opt, "|")*1000#)
		
		<!--
		
		/*
		   name - name of the cookie
		   value - value of the cookie
		   [expires] - expiration date of the cookie
		     (defaults to end of current session)
		   [path] - path for which the cookie is valid
		     (defaults to path of calling document)
		   [domain] - domain for which the cookie is valid
		     (defaults to domain of calling document)
		   [secure] - Boolean value indicating if the cookie transmission requires
		     a secure transmission
		   * an argument defaults when it is assigned null as a placeholder
		   * a null placeholder is not required for trailing omitted arguments
		*/
		
		function setCookie(name, value, expires, path, domain, secure) {
		  var curCookie = name + "=" + escape(value) +
		      ((expires) ? "; expires=" + expires.toGMTString() : "") +
/*		      ((path) ? "; path=/" + path : "") + */
		      ("; path=/") +
		      ((domain) ? "; domain=" + domain : "") +
		      ((secure) ? "; secure" : "");
		  document.cookie = curCookie;
		}
		
		/*
		  name - name of the desired cookie
		  return string containing value of specified cookie or null
		  if cookie does not exist
		*/
		
		function getCookie(name) {
		  var dc = document.cookie;
		  var prefix = name + "=";
		  var begin = dc.indexOf("; " + prefix);
		  if (begin == -1) {
		    begin = dc.indexOf(prefix);
		    if (begin != 0) return null;
		  } else
		    begin += 2;
		  var end = document.cookie.indexOf(";", begin);
		  if (end == -1)
		    end = dc.length;
		  return unescape(dc.substring(begin + prefix.length, end));
		}
		
		
		/*
		   name - name of the cookie
		   [path] - path of the cookie (must be same as path used to create cookie)
		   [domain] - domain of the cookie (must be same as domain used to
		     create cookie)
		   path and domain default if assigned null or omitted if no explicit
		     argument proceeds
		*/
		
		function deleteCookie(name, path, domain) {
		  if (getCookie(name)) {
		    document.cookie = name + "=" +
/* 		    ((path) ? "; path=" + path : "") + */
		    ("; path=/") +
		    ((domain) ? "; domain=" + domain : "") +
		    "; expires=Thu, 01-Jan-70 00:00:01 GMT";
		  }
		}
		
		// date - any instance of the Date object
		// * hand all instances of the Date object to this function for "repairs"
		
		function fixDate(date) {
		  var base = new Date(0);
		  var skew = base.getTime();
		  if (skew > 0)
		    date.setTime(date.getTime() - skew);
		}

		function enableOption() {
			var aOptions = new Array('outputexecution','outputsql','hidestatements','outputastree','filter4myip','plainoutput');
			var iOptions = 0;
			for (var i = 0; i < aOptions.length; ++i) {
				if (document.getElementById(aOptions[i]).checked) {
					iOptions += parseInt(document.getElementById(aOptions[i]).value);
				}
			}
			setCookie('DISPLAY_OPTIONS', iOptions);
			setCookie('FILE_SORTORDER', document.getElementById('filenamesortorder').value);
			setCookie('QUERY_SORTORDER', document.getElementById('querysortorder').value);
			setCookie('OUTPUTMAXQUERIES', document.getElementById('outputMaxQueries').value);
			setCookie('OUTPUTMAXFILES', document.getElementById('outputMaxFiles').value);
		}
	
		document.getElementById('outputexecution').checked = (getCookie('DISPLAY_OPTIONS') & 1) == 1;
		document.getElementById('outputsql').checked       = (getCookie('DISPLAY_OPTIONS') & 2) == 2;
		document.getElementById('hidestatements').checked  = (getCookie('DISPLAY_OPTIONS') & 4) == 4;
		document.getElementById('outputastree').checked    = (getCookie('DISPLAY_OPTIONS') & 8) == 8;
		document.getElementById('filter4myip').checked     = (getCookie('DISPLAY_OPTIONS') & 16) == 16;
		document.getElementById('filenamesortorder').value = getCookie('FILE_SORTORDER');
		document.getElementById('querysortorder').value    = getCookie('QUERY_SORTORDER');
		document.getElementById('outputMaxQueries').value  = getCookie('OUTPUTMAXQUERIES');
		document.getElementById('outputMaxFiles').value    = getCookie('OUTPUTMAXFILES');
	</script>
	<iframe style="width:100%;height:1200;border:0px;padding:0px;overflow:auto" name="dsp_debug">
	</iframe>
	<cfflush>

<script language="JavaScript">
	function toggle(sID) {
		var oObj = document.getElementById(sID);
		var oObjTD = document.getElementById(sID + "_TD");
		if (oObj.style.display == "none") {
			oObj.style.display = "";
			oObjTD.innerHTML = '&gt;';
		} else {
			oObj.style.display = "none";
			oObjTD.innerHTML = '&lt;';
		}
	
	}
</script>

	</cfoutput>
	</body>
</html>
