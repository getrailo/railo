<cfset time=getTickCount()>
<cfscript>
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
<cfadmin 
	action="getDebugData"
	returnVariable="debugging">
<cfset pages=debugging.pages>
<cfset queries=debugging.queries>
<cfif not isDefined('debugging.timers')>
	<cfset debugging.timers=queryNew('label,time,template')>
</cfif>
<cfif not isDefined('debugging.traces')>
	<cfset debugging.traces=queryNew('type,category,text,template,line,var,total,trace')>
</cfif>
<cfset timers=debugging.timers>
<cfset traces=debugging.traces>
<cfset querySort(pages,"avg","desc")>


</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
<style type="text/css">

.cfdebug {color:black;background-color:white;font-family:"Times New Roman", Times, serif;font-size:small;}
.cfdebuglge {color:black;background-color:white;font-family:"Times New Roman", Times, serif; font-size:medium;}
a.cfdebuglink {color:blue; background-color:white }

.template {	color: black; font-family: "Times New Roman", Times, serif; font-weight: normal; }
.template_overage {	color: red; background-color: white; font-family: "Times New Roman", Times, serif; font-weight: bold; }
</style>
<cfoutput>
<table class="cfdebug" bgcolor="white">
<tr>
	<td>
		<p class="cfdebug"><hr/>
		<b class="cfdebuglge"><a name="cfdebug_top">Debugging Information</a></b>
		<table class="cfdebug">
		<tr>
			<td class="cfdebug" colspan="2" nowrap>
			#server.coldfusion.productname#
			<cfif StructKeyExists(server.railo,'versionName')>(<a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a>)</cfif>
			#uCaseFirst(server.coldfusion.productlevel)# 
			#uCase(server.railo.state)#
			#server.railo.version#
			(CFML Version #server.ColdFusion.ProductVersion#)
			</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Template </td>
			<td class="cfdebug">#cgi.SCRIPT_NAME# (#getBaseTemplatePath()#)</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Time Stamp </td>
			<td class="cfdebug">#LSDateFormat(now())# #LSTimeFormat(now())#</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Time Zone </td>
			<td class="cfdebug"><cftry>#GetPageContext().getConfig().getTimeZone().getDisplayName()#<cfcatch></cfcatch></cftry></td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Locale </td>
			<td class="cfdebug">#uCaseFirst(GetLocale())#</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> User Agent </td>
			<td class="cfdebug">#cgi.http_user_agent#</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Remote IP </td>
			<td class="cfdebug">#cgi.remote_addr#</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Host Name </td>
			<td class="cfdebug">#cgi.server_name#</td>
		</tr>
		</table>
		</p>
	

	<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_execution">Execution Time</a></b></p>
	<a name="cfdebug_templates">
		<table border="1" cellpadding="2" cellspacing="0" class="cfdebug">
		<tr>
			<td class="cfdebug" align="center"><b>Total Time</b></td>
			<td class="cfdebug" align="center"><b>Avg Time</b></td>
			<td class="cfdebug" align="center"><b>Count</b></td>
			<td class="cfdebug"><b>Template</b></td>
		</tr>
<cfset loa=0>
<cfset tot=0>
<cfset q=0>
<cfloop query="pages">
		<cfset bad=pages.avg GTE 250><cfset loa=loa+pages.load><cfset tot=tot+pages.total><cfset q=q+pages.query>
		<tr>
			<td align="right" class="cfdebug" nowrap><cfif bad><font color="red"><span class="template_overage"></cfif>#pages.total-pages.load#<cfif bad></span></font></cfif> ms</td>
			<td align="right" class="cfdebug" nowrap><cfif bad><font color="red"><span class="template_overage"></cfif>#pages.avg#<cfif bad></span></font></cfif> ms</td>
			<td align="center" class="cfdebug" nowrap>#pages.count#</td>
			<td align="left" class="cfdebug" nowrap><cfif bad><font color="red"><span class="template_overage"></cfif>#pages.src#<cfif bad></span></font></cfif></td>
		</tr>
</cfloop>                
            
<tr>
	<td align="right" class="cfdebug" nowrap><i>#loa# ms</i></td><td colspan=2>&nbsp;</td>
	<td align="left" class="cfdebug"><i>STARTUP, PARSING, COMPILING, LOADING, &amp; SHUTDOWN</i></td>
</tr>
<tr>
	<td align="right" class="cfdebug" nowrap><i>#(tot-q-loa)# ms</i></td><td colspan=2>&nbsp;</td>
	<td align="left" class="cfdebug"><i>APPLICATION EXECUTION TIME</i></td>
</tr>
<tr>
	<td align="right" class="cfdebug" nowrap><i>#q# ms</i></td><td colspan=2>&nbsp;</td>
	<td align="left" class="cfdebug"><i>QUERY EXECUTION TIME</i></td>
</tr>
<tr>
	<td align="right" class="cfdebug" nowrap><i><b>#tot# ms</i></b></td><td colspan=2>&nbsp;</td>
	<td align="left" class="cfdebug"><i><b>TOTAL EXECUTION TIME</b></i></td>
</tr>
</table>
<font color="red"><span class="template_overage">red = over 250 ms average execution time</span></font>
</a>



<!--- Exceptions --->
<cfif structKeyExists(debugging,"exceptions")  and arrayLen(debugging.exceptions)>
	<cfset exceptions=debugging.exceptions>
    
	<p class="cfdebug"><hr/><b class="cfdebuglge">Caught Exceptions</b></p>
		<table border="1" cellpadding="2" cellspacing="0" class="cfdebug">
		<tr>
			<td class="cfdebug"><b>Type</b></td>
			<td class="cfdebug"><b>Message</b></td>
			<td class="cfdebug"><b>Detail</b></td>
			<td class="cfdebug"><b>Template</b></td>
		</tr>
<cfloop array="#exceptions#" index="exp">
		<tr>
			<td class="cfdebug" nowrap>#exp.type#</td>
			<td class="cfdebug" nowrap>#exp.message#</td>
			<td class="cfdebug" nowrap>#exp.detail#</td>
			<td class="cfdebug" nowrap>#exp.TagContext[1].template#:#exp.TagContext[1].line#</td>
		</tr>
</cfloop>                
 </table>
</cfif>


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
<!--- Traces --->
<cfif traces.recordcount>
	<p class="cfdebug"><hr/><b class="cfdebuglge">Trace Points</b></p>
		<table border="1" cellpadding="2" cellspacing="0" class="cfdebug">
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
			<td align="left" class="cfdebug" nowrap>#traces.type#</td>
			<td align="left" class="cfdebug" nowrap>#traces.category#&nbsp;</td>
			<td align="let" class="cfdebug" nowrap>#traces.text#&nbsp;</td>
			<td align="left" class="cfdebug" nowrap>#traces.template#</td>
			<td align="right" class="cfdebug" nowrap>#traces.line#</td>
			<td align="left" class="cfdebug" nowrap><cfif len(traces.varName)>#traces.varName# = #traces.varValue#<cftry><cfdump var="#evaluate(traces.varValue)#" label="#traces.varName#"><cfcatch></cfcatch></cftry><cfelse>&nbsp;<br />
			</cfif></td>
			<td align="right" class="cfdebug" nowrap>#total# ms</td>
			<td align="right" class="cfdebug" nowrap>#traces.time# ms</td>
		</tr>
</cfloop>                
 </table>
</cfif> 
<cfif queries.recordcount>
<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_sql">SQL Queries</a></b></p>
<cfloop query="queries">	
<code><b>#queries.name#</b> (Datasource=#queries.datasource#, Time=#queries.time#ms, Records=#queries.count#) in #queries.src#</code><br />
<pre>#queries.sql#</pre></cfloop>
</cfif>


<!--- 
<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_exceptions">Exceptions</a></b></p>

	<div class="cfdebug">22:27:42.042 - Database Exception - in D:\projects\jmuffin\webroot\cfmx\jm\test\tags\query.cfm : line 23</div>
	
	<pre>
	Error Executing Database Query.
	</pre>
 --->
<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_scopevars">Scope Variables</a></b></p>

<cfloop list="Application,CGI,Client,Cookie,Form,Request,Server,Session,URL" index="name">
<cfset doPrint=true>
<cftry>
	<cfset scp=evaluate(name)>
    <cfcatch><cfset doPrint=false></cfcatch>
</cftry>

<cfif doPrint and structCount(scp)>
<pre><b>#name# Variables:</b><cftry><cfloop index="key" list="#ListSort(StructKeyList(scp),"textnocase")#">
#(key)#=<cftry><cfif IsSimpleValue(scp[key])>#scp[key]#<!--- 
---><cfelseif isArray(scp[key])>Array (#arrayLen(scp[key])#)<!--- 
---><cfelseif isValid('component',scp[key])>Component (#GetMetaData(scp[key]).name#)<!--- 
---><cfelseif isStruct(scp[key])>Struct (#StructCount(scp[key])#)<!--- 
---><cfelseif IsQuery(scp[key])>Query (#scp[key].recordcount#)<!--- 
---><cfelse>Complex type</cfif><cfcatch></cfcatch></cftry></cfloop><cfcatch>error (#cfcatch.message#) occurred while displaying Scope #name#</cfcatch></cftry>
</pre>
</cfif>
</cfloop>

<font size="-1" class="cfdebug"><i>Debug Rendering Time: #getTickCount()-time# ms</i></font><br />
	</td>
</tr>
</table>
</cfoutput>