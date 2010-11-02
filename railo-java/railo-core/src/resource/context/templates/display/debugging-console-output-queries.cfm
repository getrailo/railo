
<cfsavecontent variable="minus"><cfinclude template="../../admin/resources/img/debug_minus.gif.cfm"></cfsavecontent>


<cfset request._debugQueryInfo = ArrayNew(1)>
<cfset bPlainSQL = (BitAnd(cookie.display_options, 32) eq 32)>
<cfoutput>
<cfsavecontent variable="sTempQueries">
	<table cellpadding="0" cellspacing="0" border="0" width="100%" id="queryDebugTimesTable">
	<cfset iDebug = 0>
	<cfif cookie.outputMaxQueries eq -1>
		<cfset sMax = queries.recordCount>
	<cfelse>
		<cfset sMax = cookie.outputMaxQueries>
	</cfif>
	<cfset bSuppressed = false>
	<cfif cookie.query_sortorder eq 2>
		<cfset sOldValue = queries.name>
	<cfelseif cookie.query_sortorder eq 4>
		<cfset sOldValue = queries.src>
	</cfif>
	<cfset iSumTime = 0>
	<cfset iSumRecs = 0>
	<cfset iCount   = 0>
	<cfset bChanged = False>
	<cfset sOutputValue = "">

	<cfloop query="queries">	
		<cfset iDebug = iDebug + 1>
		<cfif iDebug lte sMAx>
			<cfif not bHideStatements>
				<cfset stDisplaySQL = ReplaceSQLStatements(queries.sql)>
				<cfif bPlainSQL>
					#queries.sql#<br>
				<cfelse>
					<cfif stDisplaySQL.Executeable>
						<cfif structKeyExists(url, "requestID")>
							<cfset sAction = cgi.script_name & "?_debug_action=query&requestID=" & url.requestID> 
						<cfelse>
							<cfset sAction = cgi.script_name & "?_debug_action=query"> 
						</cfif>
						<form name="sqlForm#iDebug#" action="#sAction#" method="post" target="_blank">
						<input type="Hidden" name="datasource" value="#queries.datasource#">
						<input type="Hidden" name="queryName" value="#queries.name#">
						<input type="Hidden" name="executionTime" value="#queries.time#">
						<input type="Hidden" name="Records" value="#queries.count#">
						<input type="Hidden" name="src" value="#queries.src#">
					</cfif>
					<tr>
						<td width="20">
							<div class="rdebug_switch" onclick="toggleObject(this,document.getElementById('sql#iDebug#'))" id="sqlImage#iDebug#" title="#Left(stDisplaySQL.sSql, 50)#...">#sImgPlus#</div>
						</td><td width="20">
							<cfif stDisplaySQL.Executeable>
								<input type="Image" width="9" height="9" border="0" src="#cgi.context_path#/railo-context/admin/resources/img/debug_execute.gif.cfm" alt="Execute Query">
							<cfelse>
								<img src="#minus#" width="9" height="9" border="0" alt="Execution not possible, DML-Query">
							</cfif>
						</td>
						<td align="left" class="cfdebug">
							<b>#queries.name#</b> (Datasource=<cftry>#queries.datasource#<cfcatch></cfcatch></cftry>, Time=#queries.time#ms, Records=#queries.count#) in #queries.src#
						</td>
					</tr><tr id="sql#iDebug#" style="display:none">
						<td colspan="3">
							<textarea name="sql" style="width:100%;height:100px;background-color:##DDDDDD;">#stDisplaySQL.sSql#</textarea>
						</td>
					</tr>
					<cfif stDisplaySQL.Executeable>
						</form>
					</cfif>
				</cfif>
			<cfelse>
				<cfset bChanged = true>
				<cfif cookie.query_sortorder eq 2>
					<cfset bChanged = sOldValue neq queries.name>
					<cfset sOutputValue = sOldValue>
					<cfset sOldValue = queries.name>
				<cfelseif cookie.query_sortorder eq 4>
					<cfset bChanged = sOldValue neq queries.src>
					<cfset sOutputValue = sOldValue>
					<cfset sOldValue = queries.src>
				</cfif>
				<cfif bChanged>
					<cfset stDebugInfo = StructNew()>
					<cfset stDebugInfo.src   = sOutputValue>
					<cfset stDebugInfo.time  = iSumTime>
					<cfset stDebugInfo.count = iCount>
					<cfset stDebugInfo.recs  = iSumRecs>
					<cfset ArrayAppend(request._debugQueryInfo, stDebugInfo)>
					<tr>
						<td align="left" class="cfdebug">
						<a name="#sOutputValue#">Totals:</a>
						</td><td align="left" class="cfdebug">&nbsp;&nbsp;
						<b>#iCount#</b>
						</td><td align="right" class="cfdebug">
						<b>#iSumTime#ms</b>
						</td><td align="right" class="cfdebug">
						<b>#iSumRecs#</b>
						</td><td align="left" class="cfdebug">
						</td>
					</tr>
					<cfset iSumTime = 0>
					<cfset iSumRecs = 0>
					<cfset iCount   = 0>
				</cfif>
				<cfset iSumTime = iSumTime + queries.time>
				<cfset iSumRecs = iSumRecs + queries.count>
				<cfset iCount   = iCount   + 1>
				<cfset sCol = "##003366">
				<tr>
					<td align="left" class="cfdebug">
					<cfif cookie.query_sortorder eq 2>
						<font color="#sCol#"><b>#queries.name#</b></font>&nbsp;&nbsp;
					<cfelse>
						<b>#queries.name#</b>&nbsp;&nbsp;
					</cfif>
					</td><td align="left" class="cfdebug">
					<cfif not isDefined("queries.datasource")><cfset sDs = "none"><cfelse><cfset sDs = queries.datasource></cfif>
					(Datasource=#sDs#,&nbsp;&nbsp;
					</td><td align="left" class="cfdebug">
					<cfif cookie.query_sortorder eq 1>
						<font color="#sCol#">Time=#queries.time#ms,&nbsp;&nbsp;</font>
					<cfelse>
						Time=#queries.time#ms,&nbsp;&nbsp;
					</cfif>
					</td><td align="left" class="cfdebug">
					<cfif cookie.query_sortorder eq 3>
						<font color="#sCol#">Records=#queries.count#)</font>&nbsp;&nbsp;
					<cfelse>
						Records=#queries.count#)&nbsp;&nbsp;
					</cfif>
					</td><td align="left" class="cfdebug">
					in&nbsp;
					<cfif cookie.query_sortorder eq 4>
						<font color="#sCol#">#queries.src#</font>&nbsp;&nbsp;
					<cfelse>
						#queries.src#&nbsp;&nbsp;
					</cfif>
					</td>
				</tr>
			</cfif>
		<cfelse>
			<cfset bSuppressed = true>
		</cfif>
	</cfloop>
	<cfif bSuppressed>
		<tr><td colspan="3">
			<cfoutput>Further <b>#queries.recordCount - sMax#</b> queries have been suppressed.</cfoutput>
		</td></tr>			
	</cfif>
	<cfif bHideStatements>
		<cfset stDebugInfo = StructNew()>
		<cfset stDebugInfo.src   = sOutputValue>
		<cfset stDebugInfo.time  = iSumTime>
		<cfset stDebugInfo.count = iCount>
		<cfset stDebugInfo.recs  = iSumRecs>
		<cfset ArrayAppend(request._debugQueryInfo, stDebugInfo)>
		<tr>
			<td align="left" class="cfdebug">
			Totals:
			</td><td align="left" class="cfdebug">&nbsp;&nbsp;
			<b>#iCount#</b>
			</td><td align="right" class="cfdebug">
			<b>#iSumTime#ms</b>
			</td><td align="right" class="cfdebug">
			<b>#iSumRecs#</b>
			</td><td align="left" class="cfdebug">
			</td>
		</tr>
	</cfif>
	</table>
</cfsavecontent>
<cfif bHideStatements>
	<table cellpadding="2" cellspacing="0" border="1" bordercolor="##AAAAAA" width="100%" id="queryDebugTimesTable" style="border-collapse:collapse">
		<tr>
			<td class="cfdebug" align="right"><b>Time</b></td>
			<td class="cfdebug" align="right"><b>Count</b></td>
			<td class="cfdebug" align="right"><b>## of Records</b></td>
			<td class="cfdebug"><b>Source</b></td>
		</tr>
		<cfloop from="1" to="#arrayLen(request._debugQueryInfo)#" index="i">
			<tr>
				<td class="cfdebug" align="right">#request._debugQueryInfo[i].time#ms</td>
				<td class="cfdebug" align="right">#request._debugQueryInfo[i].count#</td>
				<td class="cfdebug" align="right">#request._debugQueryInfo[i].recs#</td>
				<td class="cfdebug"><a href="###request._debugQueryInfo[i].src#" style="color:##003366;text-decoration: none;"><b>#request._debugQueryInfo[i].src#</b></a></td>
			</tr>
		</cfloop>
	</table><br><b>Details:</b><br>
</cfif>
#sTempQueries#
</cfoutput>
