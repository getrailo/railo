<cfset bAsTree=(bitAnd(cookie.display_options, 8) eq 8)>
<cfoutput>
<table border="0" cellpadding="2" cellspacing="0" class="cfdebug" style="border-collapse:collapse">
	<tr>
		<td class="cfdebug" align="center"><b>Total Time</b></td>
		<td class="cfdebug" align="center"><b>Load Time</b></td>
		<td class="cfdebug" align="center"><b>Execute</b></td>
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
		<cfloop query="debugging.history">
			<cfset stPage = stPages[debugging.history.id]>
			<tr>
				<td align="right" class="cfdebug" nowrap><span class="template_overage">#stPage.total#</span> ms</td>
				<td align="right" class="cfdebug" nowrap><span class="template_overage">#stPage.load#</span> ms</td>
				<td align="right" class="cfdebug" nowrap><span class="template_overage">#stPage.execute#</span> ms</td>
				<td align="right" class="cfdebug" nowrap><span class="template_overage">#stPage.avg#</span> ms</td>
				<td align="right" class="cfdebug" nowrap>#stPage.prct#</td>
				<td align="center" class="cfdebug" nowrap>#stPage.count#</td>
				<td align="left" class="cfdebug" nowrap><span class="template_overage">#Repeatstring("-&nbsp;", debugging.history.level)##stPage.src#</span></td>
			</tr>
			<cfset stPage.total = "">
			<cfset stPage.prct  = "">
			<cfset stPage.load  = "">
			<cfset stPage.avg   = "">
			<cfset stPage.count = "">
		</cfloop>
	<cfelse>
		<cfloop query="pages">
			<cfset iPct = pages.total/tot>
			<cfset iExPct = Min(1, pages.total / 100)>
			<cfset sColor = RGBtoHex(255 * iExPct, 160 * (1 - iExPct), 0)>
			<cfif pages.currentRow lte sMax>
				<tr>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.total#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.load#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.total-pages.load#</span></font> ms</td>
					<td align="right" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.avg#</span></font> ms</td>
					<cfset sPct = NumberFormat(Int(iPct * 10000) / 100, "0.0") & "%">
					<td align="right" class="cfdebug" nowrap>#sPct#</td>
					<td align="center" class="cfdebug" nowrap>#pages.count#</td>
					<td align="left" class="cfdebug" nowrap><font color="#sColor#"><span class="template_overage">#pages.src#</span></font></td>
				</tr>
			<cfelse>
				<cfset bSuppressed = true>
			</cfif>
		</cfloop>
	</cfif>
	<cfif bSuppressed>
		<tr>
			<td align="left" class="cfdebug" nowrap colspan="7">
				<cfoutput>Further <b>#pages.recordCount - sMax#</b> files have been suppressed.</cfoutput>
			</td>
		</tr>
	</cfif>
	<tr>
		<cfset loaPct = NumberFormat(Int(loa/tot * 10000) / 100, "0.0") & "%">
		<td align="right" class="cfdebug" nowrap><i>#loa# ms</i></td>
		<td colspan="3">&nbsp;</td>
		<td align="right" class="cfdebug">#loaPct#</td>
		<td>&nbsp;</td>
		<td align="left" class="cfdebug"><i>STARTUP, PARSING, COMPILING, LOADING, &amp; SHUTDOWN</i></td>
	</tr>
	<tr>
		<cfset appPct = NumberFormat(Int(app/tot * 10000) / 100, "0.0") & "%">
		<td align="right" class="cfdebug" nowrap><i>#app# ms</i></td>
		<td colspan="3">&nbsp;</td>
		<td align="right" class="cfdebug">#appPct#</td>
		<td>&nbsp;</td>
		<td align="left" class="cfdebug"><i>APPLICATION EXECUTION TIME</i></td>
	</tr>
	<tr>
		<cfset qPct = NumberFormat(Int(q/tot * 10000) / 100, "0.0") & "%">
		<td align="right" class="cfdebug" nowrap><i>#q# ms</i></td>
		<td colspan="3">&nbsp;</td>
		<td align="right" class="cfdebug">#qPct#</td>
		<td>&nbsp;</td>
		<td align="left" class="cfdebug"><i>QUERY EXECUTION TIME</i></td>
	</tr>
	<tr>
		<td align="right" class="cfdebug" nowrap><i><b>#app+q+loa# ms</i></b></td><td colspan="5">&nbsp;</td>
		<td align="left" class="cfdebug"><i><b>TOTAL EXECUTION TIME</b></i></td>
	</tr>
</table>
</cfoutput>