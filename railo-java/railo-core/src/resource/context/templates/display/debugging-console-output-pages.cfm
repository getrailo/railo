<cfset bAsTree=(bitAnd(cookie.display_options, 8) eq 8)>
<cfoutput>
<cfif bAsTree>
	<cfset stPages = StructNew()>
	<cfloop query="pages">
		<cfset stPages[pages.id].id      = pages.currentRow>
		<cfset stPages[pages.id].total   = pages.total>
		<cfset stPages[pages.id].prct    = NumberFormat(Int(pages.total/tot * 10000) / 100, "0.0") & "%">
		<cfset stPages[pages.id].load    = pages.load>
		<cfset stPages[pages.id].execute = pages.total - pages.load>
		<cfset stPages[pages.id].avg     = pages.avg>
		<cfset stPages[pages.id].Count   = pages.count>
		<cfset stPages[pages.id].src     = pages.src>
	</cfloop>
	<cfset sOldLevel = 0>
	<cfset qry = duplicate(debugging.history)>
	<cfset aLevel    = ArrayNew(1)>
	<cfset aParents  = ArrayNew(1)>
	<cfset aChildren = ArrayNew(1)>
	<cfset ArraySet(aLevel, 1, qry.recordCount, 0)>
	<cfset ArraySet(aParents, 1, qry.recordCount, 0)>
	<cfset ArraySet(aChildren, 1, qry.recordCount, 0)>
	<cfloop query="qry">
		<cfif sOldLevel lt qry.level and qry.currentRow gt 1>
			<cfset aParents[qry.currentRow - 1] = 1>
		</cfif>
		<cfif sOldLevel gt qry.level>
			<cfset iOldPos = aLevel[qry.level]>
			<cfset aChildren[iOldPos] = qry.currentRow - iOldPos - 1>
		</cfif>
		<cfset aLevel[qry.level] = qry.currentRow>
		<cfset sOldLevel = qry.level>
	</cfloop>
	<cfset QueryAddColumn(qry, "hasParent", aParents)>
	<cfset QueryAddColumn(qry, "nChildren", aChildren)>
	<cfset sOldLevel = 0>
	<cfloop query="qry">
		<cfset iRec = qry.currentRow>
		<cfset stPage = stPages[qry.id]>
		<cfif sOldLevel gte qry.level>
			<cfloop from="#sOldLevel#" to="#qry.level#" step="-1" index="i">
			</div></div>
			</cfloop>
		</cfif>
		<div class="cfdebug">
			<cfif qry.hasParent>
				<cfif iRec eq 1>
					<div class="rdebug_switch" style="padding-left: #(qry.level - 1)*10#px" onclick="toggleObject(this,document.getElementById('tree#iRec#'))">#sImgPlus#</div>
				<cfelse>
					<div class="rdebug_switch" style="padding-left: #(qry.level - 1)*10#px" onclick="toggleObject(this,document.getElementById('tree#iRec#'))">#sImgMinus#</div>
				</cfif>
			<cfelse>
				<div class="rdebug_switch" style="padding-left: #(qry.level - 1)*10#px"></div>
			</cfif>
			 #stPage.src#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(total: #stPage.total# ms - ##child templates: #qry.nChildren#)
			<div id="tree#iRec#" <cfif iRec eq 1>style="display:none"</cfif>>
		<cfset stPage.total = "">
		<cfset stPage.prct  = "">
		<cfset stPage.load  = "">
		<cfset stPage.avg   = "">
		<cfset stPage.count = "">
		<cfset sOldLevel = qry.level>
	</cfloop>
	</div></div>
<cfelse>
	<table border="0" cellpadding="2" cellspacing="0" class="cfdebug" style="border-collapse:collapse">
		<tr>
			<td class="cfdebug" align="center"><b>Total Time</b></td>
			<td class="cfdebug" align="center"><b>Load Time</b></td>
			<td class="cfdebug" align="center"><b>Execute</b></td>
			<td class="cfdebug" align="center"><b>Query</b></td>
			<td class="cfdebug" align="center"><b>Avg Time</b></td>
			<td class="cfdebug" align="center"><b>Pct.</b></td>
			<td class="cfdebug" align="center"><b>Count</b></td>
			<td class="cfdebug"><b>Template</b></td>
		</tr>
		<cfif cookie.outputMaxFiles eq -1>
			<cfset sMax = pages.recordCount>
		<cfelse>
			<cfset sMax = cookie.outputMaxFiles>
		</cfif>
		<cfset bSuppressed = false>
		<cfloop query="pages">
			<cfset iPct = pages.total/tot>
			<cfset iExPct = Min(1, pages.total / 100)>
			<cfset sColor = RGBtoHex(255 * iExPct, 160 * (1 - iExPct), 0)>
			<cfif pages.currentRow lte sMax>
				<tr>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.total#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.load#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.total-pages.load#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.query#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.avg#</span></font> ms</td>
					<cfset sPct = NumberFormat(Int(iPct * 10000) / 100, "0.0") & "%">
					<td align="right" class="cfdebug" nowrap>#sPct#</td>
					<td align="right" class="cfdebug" nowrap>#pages.count#&nbsp;&nbsp;</td>
					<td align="left" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.src#</span></font></td>
				</tr>
			<cfelse>
				<cfset bSuppressed = true>
			</cfif>
		</cfloop>
		<cfif bSuppressed>
			<tr>
				<td align="left" class="cfdebug" nowrap colspan="8">
					<cfoutput>Further <b>#pages.recordCount - sMax#</b> files have been suppressed.</cfoutput>
				</td>
			</tr>
		</cfif>
	
		<tr>
			<cfset loaPct = NumberFormat(Int(loa/tot * 10000) / 100, "0.0") & "%">
			<td align="right" class="cfdebug" nowrap><i>#loa# ms</i></td>
			<td colspan="4">&nbsp;</td>
			<td align="right" class="cfdebug">#loaPct#</td>
			<td align="right" class="cfdebug" nowrap>#debugging.history.recordCount#&nbsp;&nbsp;</td>
			<td align="left" class="cfdebug"><i>STARTUP, PARSING, COMPILING, LOADING, &amp; SHUTDOWN</i></td>
		</tr>
		<tr>
			<cfset appPct = NumberFormat(Int(app/tot * 10000) / 100, "0.0") & "%">
			<td align="right" class="cfdebug" nowrap><i>#app# ms</i></td>
			<td colspan="4">&nbsp;</td>
			<td align="right" class="cfdebug">#appPct#</td>
			<td>&nbsp;</td>
			<td align="left" class="cfdebug"><i>APPLICATION EXECUTION TIME</i></td>
		</tr>
		<tr>
			<cfset qPct = NumberFormat(Int(q/tot * 10000) / 100, "0.0") & "%">
			<td align="right" class="cfdebug" nowrap><i>#q# ms</i></td>
			<td colspan="4">&nbsp;</td>
			<td align="right" class="cfdebug">#qPct#</td>
			<td>&nbsp;</td>
			<td align="left" class="cfdebug"><i>QUERY EXECUTION TIME</i></td>
		</tr>
		<tr>
			<td align="right" class="cfdebug" nowrap><i><b>#app+q+loa# ms</i></b></td><td colspan="6">&nbsp;</td>
			<td align="left" class="cfdebug"><i><b>TOTAL EXECUTION TIME</b></i></td>
		</tr>
	</table>
</cfif>
</cfoutput>