<cfsetting requesttimeout="360">
<cfprocessingdirective suppresswhitespace="No">
<cfset sWeb_ID = getPageContext().getConfig().getId()>
<cfparam name="cookie.DISPLAY_OPTIONS" default="3">
<cfparam name="cookie.FILE_SORTORDER" default="1">
<cfparam name="cookie.QUERY_SORTORDER" default="1">
<cfparam name="cookie.outputMaxQueries" default="100">
<cfparam name="cookie.outputMaxFiles" default="100">
<cfparam name="url.requestID" default="0">
<cfif url.requestID eq 0>
	requestID has not been passed...
	<cfabort>
</cfif>


<!--- Plus/minus Image --->
<cfif structKeyExists(cgi,'http_user_agent') and findNocase('MSIE',cgi.http_user_agent)>
	<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/debug_plus.gif.cfm">
	<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/debug_minus.gif.cfm">
<cfelse>
    <cfsavecontent variable="plus"><cfinclude template="../../admin/resources/img/debug_plus.gif.cfm"></cfsavecontent>
    <cfsavecontent variable="minus"><cfinclude template="../../admin/resources/img/debug_minus.gif.cfm"></cfsavecontent>
</cfif>

<cfparam name="url._debug_action" default="display_debug">
<cfoutput><cfsavecontent variable="sImgPlus"><img src="#plus#" style="margin:2px 2px 0px 0px;"></cfsavecontent>
<cfsavecontent variable="sImgMinus"><img src="#minus#" style="margin:2px 2px 0px 0px;"></cfsavecontent></cfoutput>
<cfif url._debug_action eq "display_debug">
	<cfset callDebugOutput(sImgMinus, sImgPlus)>
<cfelseif url._debug_action eq "query">
	<cfset request.bDebugQueryOutput = True>
	<style>
		table {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
		.tdhead {background-color:#66BBFF;font-weight:bold;}
		.tddetail {background-color:#00CCFF;}
		body {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
	</style>
	<cfoutput>
	<script language="JavaScript">
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
		<cfif FindListNoCase("INSERT ,DROP , DELETE ,ALTER ,UPDATE ,CREATE ,DROP ,REVOKE ,GRANT ", form.sql)>
			<cfthrow message="Query is not executable">
		<cfelse>
			<cfquery name="qry" datasource="#form.datasource#" psq="no" result="result">
				#form.sql#
			</cfquery>
		</cfif>
		<cfset iTimer = GetTickCount() - iTimer>
		<cfcatch type="Database">
			<cfsavecontent variable="sError">
				An error occured while executing the query. Click the detail-link to see the occured error
				<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('errorInfo'))" id="railoErrorInfoImage">&raquo;</div>
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
		<cftry>
			<tr>
				<td class="tdhead">Serialized Result:</td>
				<td class="tddetail">#serialize(qry)#</td>
			</tr>
			<cfcatch></cfcatch>
		</cftry>
		</table>
		<br><br>
		<cftry>
			<cfsavecontent variable="susi">
				Records of executed query:<br>
				<cfdump var="#qry#" label="#form.queryName#">
			</cfsavecontent>
			<cfoutput>#susi#</cfoutput>
			<cfcatch>
				No records in the resultset, update or insert statement...<br>
				<cfdump var="#result#" label="#form.queryName#">
			</cfcatch>
		</cftry>
		Execution time: #iTimer#ms
	</cfif>
	</cfoutput>
<cfelseif url._debug_action eq "store">
	<cfif not DirectoryExists("#cgi.context_path#/railo-context/stored_debug")>
		<cfdirectory action="CREATE" directory="#cgi.context_path#/railo-context/stored_debug">
	</cfif>
	<cfsavecontent variable="sOut">
		<cfset callDebugOutput("-", "+")>
	</cfsavecontent>
	<cfset sFileName = "DebugOutput" & DateFormat(now(), "YYYY-MM-DD") & "_" & TimeFormat(now(), "HH-MM-SS") & ".html">
	<cffile action="WRITE" file="#cgi.context_path#/railo-context/stored_debug/#sFileName#" output="#sOut#">
	<style>
		table {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:12px;}
		.tdhead {background-color:#66BBFF;font-weight:bold;}
		.tddetail {background-color:#AADDFF;}
		body {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:12px;}
	</style>
	<cfoutput>
		<table cellpadding="5" cellspacing="1">
			<tr><td class="tdHead" colspan="2">File stored</td></tr>
			<tr><td class="tddetail"><b>relative</b></td><td class="tddetail" title="This file is not reachable, since it is placed in a mapping only available to CFM files">#cgi.context_path#/railo-context/stored_debug/#sFileName#</td></tr>
			<tr><td class="tddetail"><b>absolute</b></td><td class="tddetail">#expandPath(cgi.context_path & "/railo-context/stored_debug/" & sFileName)#</td></tr>
		</table>
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
</cfprocessingdirective>

<cffunction name="ReplaceSQLStatements" output="No" returntype="struct">
	<cfargument name="sSql" required="Yes" type="string">
	<cfset sSql = Replace(arguments.sSql, Chr(9), " ", "ALL")>
	<cfloop from="1" to="3" index="i">
		<cfset sSql = Replace(sSql, "  ", " ", "ALL")>
		<cfset sSql = Replace(sSql, Chr(10), "", "ALL")>
		<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL")>
	</cfloop>
	<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL")>
	<cfset aWords = array('select','from','where','order by','group by','having','inner join','outer join','left join','right join')>
	<cfloop collection="#aWords#" item="sWord">
		<cfset sSql = ReplaceNoCase(sSQL, aWords[sWord], "#UCase(aWords[sWord])##chr(9)#", "ALL")>
	</cfloop>
	<cfset stRet = StructNew()>
	<cfset stRet.sSql        = Trim(sSql)>
	<cfset stRet.Executeable = True>
	<cfset aWords = array("drop ,delete ,update ,insert ,alter database ,alter table ")>
	<cfloop collection="#aWords#" item="sWord">
		<cfif FindNoCase(aWords[sWord], sSql)>
			<cfset stRet.Executeable = False>
			<cfbreak>
		</cfif>
	</cfloop>
	<cfreturn stRet>
</cffunction>

<cffunction name="convert2Query" output="No" returntype="query">
	<cfargument name="stObjects" type="struct" required="Yes">
	<cfargument name="sSortOrder" type="string" required="No">
	<cfset var bCreated = false>
	<cfset var iRC = 0>
	<cfloop collection="#stObjects#" item="sKey">
		<cfif not bCreated>
			<cfset qry = QueryNew(ArrayToList(StructKeyArray(stObjects[sKey])))>
			<cfset bCreated = true>
		</cfif>
		<cfset queryAddRow(qry, 1)>
		<cfset iRC = iRC + 1>
		<cfloop collection="#stObjects[sKey]#" item="sCol">
			<cfset querySetCell(qry, sCol, stObjects[sKey][sCol], iRC)>
		</cfloop>
		<cfif iRC gt 1000><cfbreak></cfif>
	</cfloop>
	<cfif isDefined("arguments.sSortOrder")>
		<cfquery name="qry" dbtype="query">
			Select * From qry Order by #arguments.sSortOrder#
		</cfquery>
	</cfif>
	<cfreturn qry>
</cffunction>

<cffunction name="callDebugOutput" output="Yes">
	<cfargument name="sImgMinus" required="Yes" type="string">
	<cfargument name="sImgPlus" required="Yes" type="string">
	<cfsilent>
		<cfset time=getTickCount()>
		<cfset debugging = server[sWeb_ID].debugEntries[url.requestID].debugInfo>
		<cfset sUrl      = server[sWeb_ID].debugEntries[url.requestID].calledURL>
		<cfset pages=debugging.pages>
		<cfset queries=debugging.queries>
		<cfset history=debugging.history>
		<cfset querySort(pages,"total","desc")>
		<cfif not isDefined('debugging.timers')>
			<cfset debugging.timers=queryNew('label,time,template')>
		</cfif>
		<cfset timers=debugging.timers>
		<cfif not isDefined('debugging.traces')>
			<cfset debugging.traces=queryNew('type,category,text,template,line,var,total,trace')>
		</cfif>
		<cfset traces=debugging.traces>
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
	</cfsilent>
	</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
	<style type="text/css">
		.checkbox {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;}
		.cfdebug {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;clear:both;}
		.cfdebuglge {color:##6699BB;background-color:white;font-family:"Verdana", Times, serif; font-size:small;}
		.cfdebug_head {color:##224499;background-color:white;font-family:"Verdana", Times, serif; font-size:small;font-weight: bold; float:left; margin-right: 10px;}
		a.cfdebuglink {color:blue; background-color:white;}
		.template {	color: black; font-family: "Verdana", Times, serif; font-weight: normal; }
		.template_overage {	background-color:white; font-family: "Verdana", Times, serif; font-weight: bold; }
		.rdebug_switch {	margin-right:5px;
							opacity:0.8;
							text-align:center;
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
			<cfinclude template="debugging-console-output-pages.cfm">
		</cfsavecontent>
	<cfelse>
		<cfset sExecution = "Execution times are hidden in Display options">
	</cfif>
	<cfif BitAnd(cookie.display_options, 2) eq 2>
		<cfsavecontent variable="sQueryExecution">
			<cfset bHideStatements = (BitAnd(cookie.display_options, 4) eq 4)>
			<cfinclude template="debugging-console-output-queries.cfm">
		</cfsavecontent>	
	<cfelse>
		<cfset sQueryExecution = "Queries are hidden in Display options">
	</cfif>
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
		<td width="10" valign="top"><div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('DEBUGINFO'))" id="debugInfoImage">#arguments.sImgPlus#</div>&nbsp;</td>
		<td width="100%"><div class="cfdebug_head"><a name="debug-start">Debugging:</a><br>#sUrl#</div>
	</tr>
	<tr><td colspan="2">
		<table class="cfdebug" bgcolor="white" align="left" border="0" id="DEBUGINFO" style="display:none">
			<tr><td>
				<table cellpadding="0" cellspacing="0" border="0">
					<tr><td align="left" style="width:20px">
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('DEBUGTIMES'))" id="debugTimesImage">#arguments.sImgPlus#</div>
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
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('QUERYDEBUGTIMES'))" id="queryDebugTimesImage">#arguments.sImgPlus#</div>
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
						<cfif timers.recordcount neq 0><div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('TIMERTIMES'))" id="timerTimesImage">#arguments.sImgPlus#</div></cfif>
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
						<cfif traces.recordcount neq 0><div class="rdebug_switch"onclick="toggleObject(this,document.getElementById('TRACES'))" id="tracesImage">#arguments.sImgPlus#</div></cfif>
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
						<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('RAILOINFO'))" id="railoInfoImage">#arguments.sImgPlus#</div>
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
		      ("; path=/");
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
				oThis.innerHTML='#arguments.sImgMinus#';
				if (oObj.id.substr(0,3) != 'sql') {
					setCookie(oObj.id, 1)
				}
			} else {
				oObj.style.display = 'none';
				oThis.innerHTML='#arguments.sImgPlus#';
				if (oObj.id.substr(0,3) != 'sql') {
					deleteCookie(oObj.id)
				}
			}
		}
		
		// Start of Page
		if (getCookie('DEBUGINFO') == '1') { toggleObject(document.getElementById('debugInfoImage'),document.getElementById('DEBUGINFO'), 0); }
		if (getCookie('RAILOINFO') == '1') { toggleObject(document.getElementById('railoInfoImage'),document.getElementById('RAILOINFO'), 0); }
		if (getCookie('DEBUGTIMES') == '1') { toggleObject(document.getElementById('debugTimesImage'),document.getElementById('DEBUGTIMES'), 0); }
		if (getCookie('DEBUGOPTIONS') == '1') { toggleObject(document.getElementById('debugOptionImage'),document.getElementById('DEBUGOPTIONS'), 0); }
		if (getCookie('QUERYDEBUGTIMES') == '1') { toggleObject(document.getElementById('queryDebugTimesImage'),document.getElementById('QUERYDEBUGTIMES'), 0); }
		if (getCookie('TIMERTIMES') == '1') { toggleObject(document.getElementById('timerTimesImage'),document.getElementById('TIMERTIMES'), 1); }
	</script>
	</cfoutput>
</cffunction>

<cffunction name="FindListNoCase" returntype="numeric" output="No">
	<cfargument name="lstElements" required="Yes" type="string">
	<cfargument name="sString" required="Yes" type="string">
	<cfset var iFound = 0>
	<cfloop list="#arguments.lstElements#" index="local.lst">
		<cfset iFound = FindNoCase(lst, arguments.sString)>
		<cfif iFound neq 0>
			<cfbreak>
		</cfif>
	</cfloop>
	<cfreturn iFound>
</cffunction>