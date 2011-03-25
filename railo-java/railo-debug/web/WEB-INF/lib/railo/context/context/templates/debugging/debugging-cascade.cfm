<cfparam name="cookie.DISPLAY_OPTIONS" default="3">
<cfparam name="cookie.FILE_SORTORDER" default="1">
<cfparam name="cookie.QUERY_SORTORDER" default="1">
<cfparam name="cookie.outputMaxQueries" default="100">
<cfparam name="cookie.outputMaxFiles" default="100">
<cfparam name="request.bDebugQueryOutput" default="False">
<cfif request.bDebugQueryOutput><cfabort></cfif>
<cfparam name="url._debug_action" default="display_debug">


<!--- Plus/minus Image --->
<cfif structKeyExists(cgi,'http_user_agent') and findNocase('MSIE',cgi.http_user_agent)>
	<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/debug_plus.gif.cfm">
	<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/debug_minus.gif.cfm">
<cfelse>
    <cfsavecontent variable="plus"><cfinclude template="../../admin/resources/img/debug_plus.gif.cfm"></cfsavecontent>
    <cfsavecontent variable="minus"><cfinclude template="../../admin/resources/img/debug_minus.gif.cfm"></cfsavecontent>
</cfif>

<cfoutput>
<cfsavecontent variable="sImgPlus"><img src="#plus#" style="margin:2px 2px 0px 0px;"></cfsavecontent>
<cfsavecontent variable="sImgMinus"><img src="#minus#" style="margin:2px 2px 0px 0px;"></cfsavecontent>
</cfoutput>
<cfif url._debug_action eq "display_debug">
	<cfset time=getTickCount()>
	<cfadmin 
		action="getDebugData"
		returnVariable="debugging">
	<cfset pages=debugging.pages>
	<cfset queries=debugging.queries>
	<cfif not isDefined('debugging.timers')>
		<cfset debugging.timers=queryNew('label,time,template')>
	</cfif>
	<cfset timers=debugging.timers>
	<cfif not isDefined('debugging.traces')>
		<cfset debugging.traces=queryNew('type,category,text,template,line,var,total,trace')>
	</cfif>
	<cfset traces=debugging.traces>
	<cfset querySort(pages,"total","desc")>
	<cfif cookie.file_sortorder eq "2">
		<cfset querySort(pages, "src", "asc")>
	<cfelseif cookie.file_sortorder eq "3">
		<cfset querySort(pages, "count", "desc")>
	<cfelseif cookie.file_sortorder eq "4">
		<cfset querySort(pages, "avg", "desc")>
	<cfelse>
		<cfset querySort(pages, "total", "desc")>
	</cfif>
	<cfif cookie.query_sortorder eq "1">
		<cfset querySort(queries, "time", "desc")>
	<cfelseif cookie.query_sortorder eq "2">
		<cfset querySort(queries, "name", "asc")>
	<cfelseif cookie.query_sortorder eq "3">
		<cfset querySort(queries, "count", "desc")>
	<cfelseif cookie.query_sortorder eq "4">
		<cfset querySort(queries, "src", "asc")>
	</cfif>
	</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
	<style type="text/css">
		.checkbox {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;}
		.cfdebug {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;clear:both;}
		.cfdebuglge {color:#6699BB;background-color:white;font-family:"Verdana", Times, serif; font-size:small;}
		.cfdebug_head {color:#224499;background-color:white;font-family:"Verdana", Times, serif; font-size:small;font-weight: bold; float:left; margin-right: 10px;}
		a.cfdebuglink {color:blue; background-color:white;}
		.template {	color: black; font-family: "Verdana", Times, serif; font-weight: normal; }
		.template_overage {	background-color:white; font-family: "Verdana", Times, serif; font-weight: bold; }
		.rdebug_switch {	text-align:center;
							float:left;
							width:11px;
							height:11px;
							font-family:Arial;
							font-size:8px;
							color:black;
							background-color:white;
							cursor:pointer;}
	</style>
	
	<cfoutput>
	<!--- prepare Output --->
	<cfset loa=0>
	<cfset tot=0>
	<cfset q=0>
	<cfloop query="pages">
		<cfset loa=loa+pages.load>
		<cfset tot=tot+pages.total>
	</cfloop>
	<cfloop query="queries">
		<cfset q = q + queries.time>
	</cfloop>
	<cfset app=(tot-q)-loa><cfif app LT 0><cfset app=0></cfif>
	<cfset tot = Max(tot, 0.001)>
	<cfif BitAnd(cookie.display_options, 1) eq 1>
		<cfsaveContent variable="sExecution">
			<cfinclude template="../display/debugging-console-output-pages.cfm">
		</cfsavecontent>
	<cfelse>
		<cfset sExecution = "Execution times are hidden in Display options">
	</cfif>
	<cfsavecontent variable="sQueryExecution">
		<cfif BitAnd(cookie.display_options, 2) eq 2>
			<cfset bHideStatements = (BitAnd(cookie.display_options, 4) eq 4)>
			<cfinclude template="../display/debugging-console-output-queries.cfm">
		<cfelse>
			Queries are hidden in Display options
		</cfif>
	</cfsavecontent>	
	<cfsavecontent variable="sTimerOutput">
		<!--- Timers --->
		<cfif timers.recordcount>
			<p class="cfdebug"><hr/><b class="cfdebuglge">CFTimer Times</b></p>
			<table border="1" cellpadding="2" cellspacing="0" class="cfdebug">
			<tr>
				<td class="cfdebug" align="center"><b>Label</b></td>
				<td class="cfdebug"><b>Time</b></td>
				<td class="cfdebug"><b>Template</b></td>
			</tr>
			<cfloop query="timers">
				<tr>
					<td align="right" class="cfdebug" nowrap>#timers.label#</td>
					<td align="right" class="cfdebug" nowrap>#timers.time# ms</td>
					<td align="right" class="cfdebug" nowrap>#timers.template#</td>
				</tr>
			</cfloop>                
		 </table>
		</cfif>
	</cfsavecontent>
	<cfsavecontent variable="sTraceOutput">
		<!--- Traces --->
		<cfif traces.recordcount>
			<p class="cfdebug"><hr/><b class="cfdebuglge">Trace Points</b></p>
				<table border="1" cellpadding="2" cellspacing="0" class="cfdebug" style="border-collapse:collapse">
				<tr>
					<td class="cfdebug"><b>Type</b></td>
					<td class="cfdebug"><b>Category</b></td>
					<td class="cfdebug"><b>Text</b></td>
					<td class="cfdebug"><b>Template</b></td>
					<td class="cfdebug"><b>Line</b></td>
					<td class="cfdebug"><b>Var</b></td>
					<td class="cfdebug"><b>Total Time</b></td>
					<td class="cfdebug"><b>Trace Slot Time</b></td>
				</tr>
		<cfset total=0>
		<cfloop query="traces">
		<cfset total=total+traces.time>
				<tr>
					<td align="left" class="cfdebug" nowrap valign="top">#traces.type#</td>
					<td align="left" class="cfdebug" nowrap valign="top">#traces.category#&nbsp;</td>
					<td align="let" class="cfdebug" nowrap valign="top">#traces.text#&nbsp;</td>
					<td align="left" class="cfdebug" nowrap valign="top">#traces.template#</td>
					<td align="right" class="cfdebug" nowrap valign="top">#traces.line#</td>
					<td align="left" class="cfdebug" nowrap valign="top"><cfif len(traces.varName)>#traces.varName# = #traces.varValue#<cftry><cfdump var="#evaluate(traces.varValue)#" label="#traces.varName#"><cfcatch></cfcatch></cftry><cfelse>&nbsp;<br />
					</cfif></td>
					<td align="right" class="cfdebug" nowrap valign="top">#total# ms</td>
					<td align="right" class="cfdebug" nowrap valign="top">#traces.time# ms</td>
				</tr>
		</cfloop>                
		 </table>
		</cfif> 
	</cfsavecontent>
	
	<table class="cfdebug" bgcolor="white" align="left" border="0">
	<tr>
		<td width="10" valign="top"><div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('DEBUGINFO'))" id="debugInfoImage">#sImgPlus#</div>&nbsp;</td>
		<td width="100%"><div class="cfdebug_head"><a name="debug-start">Debugging:</a></div><div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('DEBUGOPTIONS'))" id="debugOptionImage">#sImgPlus#</div>&nbsp;<b>Display Options:</b><!--- 
			---><table class="cfdebug" bgcolor="white" align="left" border="0" id="DEBUGOPTIONS" style="display:none"><!---
				---><tr><td><b>Output:</b></td></tr><!---
				---><tr><td><!---
					---><input type="Checkbox" id="outputexecution" value="1" onclick="enableOption()" class="checkbox" checked>Execution times&nbsp;&nbsp;<!---
				---></td><td colspan="3"><!---
					---><input type="Checkbox" id="outputsql" value="2" onclick="enableOption()" class="checkbox" checked>SQL<!---
					---><input type="Checkbox" id="hidestatements" value="4" onclick="enableOption()" class="checkbox" checked>Hide Statements<!---
				---></td></tr><!---
				---><tr><td><!---
					--->Display&nbsp;<select name="outputMaxFiles" id="outputMaxFiles" onchange="enableOption()">
						<option value="-1">all</option>
						<option value="10">10</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="500">500</option>
						<option value="1000">1000</option>
					</select>&nbsp;files<!---
				---></td><td colspan="3"><!---
					--->Display&nbsp;<select name="outputMaxQueries" id="outputMaxQueries" onchange="enableOption()">
						<option value="-1">all</option>
						<option value="10">10</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="500">500</option>
						<option value="1000">1000</option>
					</select>&nbsp;queries<!---
				---></td></tr><!---
				---><tr><!---
					---><td>Sortorder:<br><select id="filenamesortorder" onchange="enableOption()" class="cfdebug"><!---
						---><option value="1">ExecutionTime (desc)</option><!---
						---><option value="2">FileName (asc)</option><!---
						---><option value="3">Count (desc)</option><!---
						---><option value="4">Average (desc)</option><!---
					---></select></td><!---
					---><td>Sortorder:<br><select id="querysortorder" onchange="enableOption()" class="cfdebug"><!---
						---><option value="1">ExecutionTime (desc)</option><!---
						---><option value="2">QueryName (asc)</option><!---
						---><option value="3">Records (desc)</option><!---
						---><option value="4">File (asc)</option><!---
						---><option value="5">Chronological</option><!---
					---></select></td><!---
				---></tr><!---
				---><tr><td colspan="4"><b>Scopes:</b></td></tr><!---
				---><tr><td><!---
					---><input type="Checkbox" id="outputvariablesapplication" value="8" onclick="enableOption()" class="checkbox">Application&nbsp;<!---
				---></td><td><!---
					---><input type="Checkbox" id="outputvariablessession" value="16" onclick="enableOption()" class="checkbox">Session&nbsp;<!---
				---></td><td colspan="2"><!---
					---><input type="Checkbox" id="outputvariablesrequest" value="32" onclick="enableOption()" class="checkbox">Request<!---
				---></td></tr><!---
				---><tr><td><!---
					---><input type="Checkbox" id="outputvariablescookie" value="64" onclick="enableOption()" class="checkbox">Cookie&nbsp;&nbsp;<!---
				---></td><td><!---
					---><input type="Checkbox" id="outputvariablesurl" value="128" onclick="enableOption()" class="checkbox">Url&nbsp;&nbsp;<!---
				---></td><td><!---
					---><input type="Checkbox" id="outputvariablesform" value="256" onclick="enableOption()" class="checkbox">Form&nbsp;&nbsp;<!---
				---></td><td><!---
					---><input type="Checkbox" id="outputvariablescgi" value="512" onclick="enableOption()" class="checkbox">CGI&nbsp;&nbsp;<br><!---
				---></td></tr><!---
			---></table><!---
		---></td>
	</tr>
	<tr><td colspan="2">
		<table class="cfdebug" bgcolor="white" align="left" border="0" id="DEBUGINFO" style="display:none">
			<tr><td>
				<table cellpadding="0" cellspacing="0" border="0">
					<tr><td align="left" style="width:20px">
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('DEBUGTIMES'))" id="debugTimesImage">#sImgPlus#</div>
					</td><td>
						<span class="cfdebuglge" title="&Oslash; <cftry>#NumberFormat((app+q+loa)/pages.recordCount, '990.0')#<cfcatch>0</cfcatch></cftry> ms/template&nbsp;<cftry>#NumberFormat(pages.recordCount/(app+q+loa)*1000, '990.0')#<cfcatch>enough</cfcatch></cftry> templates/s"><b>Execution Time</b>&nbsp;</span>
						<span class="cfdebug">
							Total: <b>#pages.recordCount#</b> 
							Total time: <b>#app+q+loa#</b> ms 
						</span>
					</td></tr>
					<tr id="DEBUGTIMES" style="display:none"><td>&nbsp;</td><td>
						#sExecution#
					</td></tr>
				</table>
			</td></tr><tr><td>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr><td align="left" style="width:20px">
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('QUERYDEBUGTIMES'))" id="queryDebugTimesImage">#sImgPlus#</div>
					</td><td>
						<span class="cfdebuglge" title="&Oslash; <cftry>#NumberFormat((q)/queries.recordCount, '990.0')#<cfcatch>0</cfcatch></cftry> ms/query&nbsp;<cftry>#NumberFormat(queries.recordCount/(q)*1000, '990.0')#<cfcatch>enough</cfcatch></cftry> queries/s"><b>SQL Queries</b></span>
						&nbsp;<span class="cfdebug">Total: <b>#queries.recordcount#</b> Total time: <b>#q#</b> ms</span>
					</td></tr>
				<tr id="QUERYDEBUGTIMES" style="display:none"><td>&nbsp;</td><td>
					#sQueryExecution#
				</td></tr>
				</table>
			</td></tr><tr><td>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr><td align="left" style="width:20px">
						<cfif timers.recordcount neq 0><div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('TIMERTIMES'))" id="timerTimesImage">#sImgPlus#</div></cfif>
					</td><td>
						<span class="cfdebuglge"><b>Timers</b></span>&nbsp;<span class="cfdebug">Total: <b>#timers.recordcount#</b></span>
					</td></tr>
				<tr id="TIMERTIMES" style="display:none"><td></td><td>
					#sTimerOutput#
				</td>
				</tr>
				</table>
			</td></tr><tr><td>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr><td align="left" style="width:20px">
						<cfif traces.recordcount neq 0><div class="rdebug_switch"onclick="toggleObject(this,document.getElementById('TRACES'))" id="tracesImage">#sImgPlus#</div></cfif>
					</td><td>
						<span class="cfdebuglge"><b>Traces</b></span>&nbsp;<span class="cfdebug">Total: <b>#traces.recordcount#</b></span>
					</td></tr>
				<tr id="TRACES" style="display:none"><td></td><td>
					#sTraceOutput#
				</td>
				</tr>
				</table>
			</td></tr><tr><td>
				<table cellspacing="0" cellpadding="0" border="0">
				<tr><td>
					<table cellpadding="0" cellspacing="0" border="0">
					<tr><td align="left" style="width:20px">
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('RAILOINFO'))" id="railoInfoImage">#sImgPlus#</div>
					</td><td>
						<span class="cfdebuglge"><b>Railo server information</b></span>
					</td></tr>
					<tr><td>&nbsp;</td><td>
						<table class="cfdebug" id="RAILOINFO" cellpadding="0" cellspacing="2" border="0" style="display:none">
						<tr>
							<td class="cfdebug" colspan="3" nowrap><b>
							#server.coldfusion.productname#
							#uCaseFirst(server.coldfusion.productlevel)# 
							#uCase(server.railo.state)#
							#server.railo.version#
							(CFML Version #server.ColdFusion.ProductVersion#)</b>
							</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Template </b></td><td>&nbsp;</td><td class="cfdebug">#cgi.SCRIPT_NAME# (#getBaseTemplatePath()#)</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Time Stamp </b></td><td>&nbsp;</td><td class="cfdebug">#LSDateFormat(now())# #LSTimeFormat(now())#</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Time Zone </b></td><td>&nbsp;</td><td class="cfdebug"><cftry>#GetPageContext().getConfig().getTimeZone().getDisplayName()#<cfcatch></cfcatch></cftry></td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Locale </b></td><td>&nbsp;</td><td class="cfdebug">#uCaseFirst(GetLocale())#</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> User Agent </b></td><td>&nbsp;</td><td class="cfdebug">#cgi.http_user_agent#</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Remote IP </b></td><td>&nbsp;</td><td class="cfdebug">#cgi.remote_addr#</td>
						</tr><tr>
							<td class="cfdebug" nowrap><b> Host Name </b></td><td>&nbsp;</td><td class="cfdebug">#cgi.server_name#</td>
						</tr>
						</table>
					</td></tr>
					</table>
				</td></tr>
				</table>
			</td></tr>
			<tr><td><a href="##debug-start"><span class="cfdebuglge">Goto debug-start</span></a></td></tr>
		</table>
	</td></tr>
	<cfset aScopes = array("Application", "Session", "Request", "Cookie", "Url", "Form", "CGI")>
	<cfsaveContent variable="sOut"><!---
		---><cfloop collection="#aScopes#" item="i"><!---
			---><cfif BitAnd(cookie.display_options, 2^(i+2)) eq 2^(i+2)><!---
				---><br><b>#aScopes[i]#:</b><!---
				---><cftry><!---
					---><cfdump var="#evaluate(aScopes[i])#"><!---
				---><cfcatch type="Any"><!---
					--->The #aScopes[i]# scope is not available<!---
				---></cfcatch><!---
				---></cftry><!---
			---></cfif><!---
		---></cfloop>
	</cfsavecontent>
	<cfset sOut = Trim(sOut)>
	<cfif sOut neq "">
		<tr><td colspan="3" align="left">#sOut#</td></tr>
	</cfif>
	
	</table>
	
	
	<SCRIPT LANGUAGE="JavaScript">
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
		
		function setCookie(name, value, domain, expires, path, secure) {
		  var curCookie = name + "=" + escape(value) +
		      ((expires) ? "; expires=" + expires.toGMTString() : "") +
/*		      ((path) ? "; path=/" + path : "") + */
		      ("; path=/");
/*		      ((domain) ? "; domain=" + domain : "") +
		      ((secure) ? "; secure" : "");*/
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
		
		function deleteCookie(name, domain, path) {
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
		
		function toggleObject(oThis, oObj, iDisplay) {
			if (oObj.style.display == 'none' || iDisplay == 1) {
				oObj.style.display = '';
				oThis.innerHTML='#sImgMinus#';
				if (oObj.id.substr(0,3) != 'sql') {
					setCookie(oObj.id, 1)
				}
			} else {
				oObj.style.display = 'none';
				oThis.innerHTML='#sImgPlus#';
				if (oObj.id.substr(0,3) != 'sql') {
					deleteCookie(oObj.id)
				}
			}
		}
		
		function enableOption() {
			var aOptions = new Array('outputexecution','outputsql','hidestatements','outputvariablesapplication','outputvariablessession','outputvariablesrequest','outputvariablescookie','outputvariablesurl','outputvariablesform','outputvariablescgi');
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
	
		// Start of Page
		if (getCookie('DEBUGINFO') == '1') { toggleObject(document.getElementById('debugInfoImage'),document.getElementById('DEBUGINFO'), 0); }
		if (getCookie('RAILOINFO') == '1') { toggleObject(document.getElementById('railoInfoImage'),document.getElementById('RAILOINFO'), 0); }
		if (getCookie('DEBUGTIMES') == '1') { toggleObject(document.getElementById('debugTimesImage'),document.getElementById('DEBUGTIMES'), 0); }
		if (getCookie('DEBUGOPTIONS') == '1') { toggleObject(document.getElementById('debugOptionImage'),document.getElementById('DEBUGOPTIONS'), 0); }
		if (getCookie('QUERYDEBUGTIMES') == '1') { toggleObject(document.getElementById('queryDebugTimesImage'),document.getElementById('QUERYDEBUGTIMES'), 0); }
		if (getCookie('TIMERTIMES') == '1') { toggleObject(document.getElementById('timerTimesImage'),document.getElementById('TIMERTIMES'), 1); }
		
		document.getElementById('outputexecution').checked            = (getCookie('DISPLAY_OPTIONS') & 1) == 1;
		document.getElementById('outputsql').checked                  = (getCookie('DISPLAY_OPTIONS') & 2) == 2;
		document.getElementById('hidestatements').checked             = (getCookie('DISPLAY_OPTIONS') & 4) == 4;
		document.getElementById('outputvariablesapplication').checked = (getCookie('DISPLAY_OPTIONS') & 8) == 8;
		document.getElementById('outputvariablessession').checked     = (getCookie('DISPLAY_OPTIONS') & 16) == 16;
		document.getElementById('outputvariablesrequest').checked     = (getCookie('DISPLAY_OPTIONS') & 32) == 32;
		document.getElementById('outputvariablescookie').checked      = (getCookie('DISPLAY_OPTIONS') & 64) == 64;
		document.getElementById('outputvariablesurl').checked         = (getCookie('DISPLAY_OPTIONS') & 128) == 128;
		document.getElementById('outputvariablesform').checked        = (getCookie('DISPLAY_OPTIONS') & 256) == 256;
		document.getElementById('outputvariablescgi').checked         = (getCookie('DISPLAY_OPTIONS') & 512) == 512;
		document.getElementById('filenamesortorder').value            = getCookie('FILE_SORTORDER');
		document.getElementById('querysortorder').value               = getCookie('QUERY_SORTORDER');
		document.getElementById('outputMaxQueries').value             = getCookie('OUTPUTMAXQUERIES');
		document.getElementById('outputMaxFiles').value               = getCookie('OUTPUTMAXFILES');
/*		for (var i = 1; i <= #queries.recordCount#; i++) {
			if(document.getElementById('sql' + i)) {
				toggleObject(document.getElementById('sqlImage' + i),document.getElementById('sql' + i));
			}
		}*/
	// -->
	
	
	
	</script>
	</cfoutput>
<cfelse>
	<cfset request.bDebugQueryOutput = True>
	<style>
		table {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
		.tdhead {background-color:#66BBFF;font-weight:bold;}
		.tddetail {background-color:#00CCFF;}
		body {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
	</style>
	<cfoutput>
	<script language="JavaScript">
		function toggleObject(oThis, oObj) {
			if (oObj.style.display == 'none') {
				oObj.style.display = '';
				oThis.innerHTML = '#sImgMinus#';
				}
			} else {
				oObj.style.display = 'none';
				oThis.innerHTML = '#sImgPlus#';
				}
			}
		}
	</script>
	
	<h3>Query Information:</h3>
	<table cellpadding="2" cellspacing="2" border="1" style="border-collapse:collapse">
	<tr>
		<td class="tdhead">Name:</td>
		<td class="tddetail">#form.queryName#</td>
	</tr>
	<tr>
		<td class="tdhead">Datasource:</td>
		<td class="tddetail">#form.datasource#</td>
	</tr>
	<tr>
		<td class="tdhead">Source File:</td>
		<td class="tddetail">#form.src#</td>
	</tr>
	<tr>
		<td class="tdhead">Records:</td>
		<td class="tddetail">#form.records#</td>
	</tr>
	<tr>
		<td class="tdhead">Execution time:</td>
		<td class="tddetail">#form.executionTime#ms</td>
	</tr>
	<tr>
		<td class="tdhead">SQL statement:</td>
		<td class="tddetail">#form.sql#</td>
	</tr>
	
	<cfset sError = "">
	<cfset iTimer = GetTickCount()>
	<cftry>
		<cfquery name="qry" datasource="#form.datasource#" psq="no">
			#form.sql#
		</cfquery>
		<cfset iTimer = GetTickCount() - iTimer>
		<cfcatch type="Database">
			<cfsavecontent variable="sError">
				An error occured while executing the query. Click the detail-link to see the occured error
				<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('errorInfo'))" id="railoErrorInfoImage">#sImgPlus#</div>
				<div id="errorInfo" style="display:none">
					<cfdump var="#cfcatch#" label="occured error">
				</div>
			</cfsavecontent>
		</cfcatch>
	</cftry>
	
	<cfif sError neq "">
		</table>
		#sError#
	<cfelse>
		<tr>
			<td class="tdhead">Serialized Result:</td>
			<td class="tddetail">#serialize(qry)#</td>
		</tr>
		</table>
		<br><br>
		Records of executed query:<br>
		<cfdump var="#qry#" label="#form.queryName#">
		Execution time: #iTimer#ms
	</cfif>
	</cfoutput>
</cfif>
<cfscript>
function RGBtoHex(r,g,b){
	Var hexColor="";
	Var hexPart = '';
	Var i=0;
	   
	/* Loop through the Arguments array, containing the RGB triplets */
	for (i=1; i lte 3; i=i+1){
		/* Derive hex color part */
		hexPart = formatBaseN(Arguments[i],16);
		/* Pad with "0" if needed */
		if (len(hexPart) eq 1){ hexPart = '0' & hexPart; } 
		      
		/* Add hex color part to hexadecimal color string */
		hexColor = hexColor & hexPart;
	}
	return '##' & hexColor;
}

/**
 * do first Letter Upper case
 * @param str String to operate
 * @return uppercase string
 */
function uCaseFirst(String str) {
	var size=len(str);
	if(     size EQ 0)return str;
	else if(size EQ 1) return uCase(str);
	else {
		return uCase(mid(str,1,1))&mid(str,2,size);
	}
}
</cfscript>
<cffunction name="ReplaceSQLStatements" output="No" returntype="struct">
	<cfargument name="sSql" required="Yes" type="string">
	<cfset var sSql = Replace(arguments.sSql, Chr(9), " ", "ALL")>
	<cfset var aWords = ['select','from','where','order by','group by','having']>
	<cfloop from="1" to="3" index="local.i">
		<cfset sSql = Replace(sSql, "  ", " ", "ALL")>
		<cfset sSql = Replace(sSql, Chr(10), "", "ALL")>
		<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL")>
	</cfloop>
	<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL")>
	<cfloop collection="#aWords#" item="local.sWord">
		<cfset sSql = ReplaceNoCase(sSQL, aWords[sWord], "#UCase(aWords[sWord])##chr(9)#", "ALL")>
	</cfloop>
	<cfset local.stRet       = {}>
	<cfset stRet.sSql        = Trim(sSql)>
	<cfset stRet.Executeable = True>
	<cfset aWords = ["drop ,delete ,update ,insert ,alter database ,alter table "]>
	<cfloop collection="#aWords#" item="sWord">
		<cfif FindNoCase(aWords[sWord], sSql)>
			<cfset stRet.Executeable = False>
			<cfbreak>
		</cfif>
	</cfloop>
	<cfreturn stRet>
</cffunction>