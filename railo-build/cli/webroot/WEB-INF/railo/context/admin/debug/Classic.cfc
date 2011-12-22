<cfcomponent extends="Debug" output="no"><cfscript>

    fields=array(
		
		group("Execution Time","Execution times for templates, includes, modules, custom tags, and component method calls. Template execution times over this minimum highlight time appear in red.",3)
		//,field("Extensions","extension","cfm,cfc,cfml",false,"Output the templates with the following extensions","checkbox","cfm,cfc,cfml")
		
		
		,field("Minimal","minimal","0",true,
				"output only templates taking longer than the following (ms).","text40")
		
		,field("Highlight","highlight","250",true,
				"Highlight templates taking longer than the following (ms) in red.","text40")
		
		
		,group("Custom Debugging Output","Define what is outputted",3)

		


		,field("General Debug Information ","general",true,false,
				"Select this option to show general information about this request. General items are Railo Version, Template, Time Stamp, User Locale, User Agent, User IP, and Host Name. ","checkbox")
		
		,field("Scope Variables","scopes","Application,CGI,Client,Cookie,Form,Request,Server,Session,URL",true,"Enable Scope reporting","checkbox","Application,CGI,Client,Cookie,Form,Request,Server,Session,URL")
		
		,field("Database Activity","database",true,false,"Select this option to show the database activity for the SQL Query events and Stored Procedure events in the debugging output.","checkbox")
		
		,field("Exceptions","exception",true,false,"Select this option to output all exceptions raised for the request. ","checkbox")
		
		,field("Tracing","tracing",true,false,"Select this option to show trace event information. Tracing lets a developer track program flow and efficiency through the use of the CFTRACE tag.","checkbox")
		
		,field("Timer","timer",true,false,"Select this option to show timer event information. Timers let a developer track the execution time of the code between the start and end tags of the CFTIMER tag. ","checkbox")
		
		
		,group("Output Format","Define details to the fomrat of the debug output",3)
		,field("Background Color","bgcolor","white",true,"Color in the back, ","text80")
		,field("Font Color","color","black",true,"Color used for the Font, ","text80")
		,field("Font Family","font","Times New Roman, Times, serif",true,"What kind of Font is used, ","text200")
		,field("Font Size","size","medium",true,"What kind of Font is usedThe size of the font in Pixel, ","select","small,medium,large")
	);
    
    



string function getLabel(){
	return "Classic";
}
string function getDescription(){
	return "The old style debug template";
}
string function getid(){
	return "railo-classic";
}


void function onBeforeUpdate(struct custom){
	throwWhenEmpty(custom,"color");
	throwWhenEmpty(custom,"bgcolor");
}



private void function throwWhenEmpty(struct custom, string name){
	if(!structKeyExists(custom,name) or len(trim(custom[name])) EQ 0)
		throw "value for ["&name&"] is not defined";
}

private function isColumnEmpty(string columnName){
	if(!isDefined(columnName)) return true;
	return !len(replace(valueList(""&columnName),',','','all'));
}


</cfscript>   
    
    
    <cffunction name="output" returntype="void">
    	<cfargument name="custom" type="struct" required="yes">
		<cfargument name="debugging" required="true" type="struct"><cfsilent>
<cfset time=getTickCount()>

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

<cfparam name="custom.color" default="black">
<cfparam name="custom.bgcolor" default="white">
<cfparam name="custom.font" default="Times New Roman">
<cfparam name="custom.size" default="medium">


</cfsilent></td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
<style type="text/css">
<cfoutput>
.cfdebug {color:#custom.color#;background-color:#custom.bgcolor#;font-family:"#custom.font#";
	font-size:<cfif custom.size EQ "small">smaller<cfelseif custom.size EQ "medium">small<cfelse>medium</cfif>;}
.cfdebuglge {color:#custom.color#;background-color:#custom.bgcolor#;font-family:#custom.font#;
	font-size:<cfif custom.size EQ "small">small<cfelseif custom.size EQ "medium">medium<cfelse>large</cfif>;}

.template_overage {	color: red; background-color: #custom.bgcolor#; font-family:#custom.font#; font-weight: bold;
	font-size:<cfif custom.size EQ "small">smaller<cfelseif custom.size EQ "medium">small<cfelse>medium</cfif>; }
</style>
<table class="cfdebug" bgcolor="#custom.bgcolor#" style="border-color:#custom.color#">
<tr>
	<td>
 <!--- General --->
    <cfif structKeyExists(custom,"general") and custom.general>
		<p class="cfdebug"><hr/>
		<b class="cfdebuglge"><a name="cfdebug_top">Debugging Information</a></b>
		<table class="cfdebug">
		<tr>
			<td class="cfdebug" colspan="2" nowrap>
			#server.coldfusion.productname#
			<cfif StructKeyExists(server.railo,'versionName')>(<a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a>)</cfif>
			#ucFirst(server.coldfusion.productlevel)# 
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
			<td class="cfdebug">#getTimeZone()#</td>
		</tr>
		<tr>
			<td class="cfdebug" nowrap> Locale </td>
			<td class="cfdebug">#ucFirst(GetLocale())#</td>
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
		<cfif StructKeyExists(server.os,"archModel") and StructKeyExists(server.java,"archModel")><tr>
			<td class="cfdebug" nowrap> Architecture</td>
			<td class="cfdebug"><cfif server.os.archModel NEQ server.os.archModel>OS #server.os.archModel#bit/JRE #server.java.archModel#bit<cfelse>#server.os.archModel#bit</cfif></td>
		</tr></cfif>
		</table>
		</p>
	</cfif>
<!--- Execution Time --->
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
		<cfif pages.avg LT custom.minimal><cfcontinue></cfif>
		<cfset bad=pages.avg GTE custom.highlight><cfset loa=loa+pages.load><cfset tot=tot+pages.total><cfset q=q+pages.query>
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
<font color="red"><span class="template_overage">red = over #custom.highlight# ms average execution time</span></font>
</a>



<!--- Exceptions --->
<cfif structKeyExists(custom,"exception") and custom.exception and structKeyExists(debugging,"exceptions")  and arrayLen(debugging.exceptions)>
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
<cfif structKeyExists(custom,"timer") and custom.timer and  timers.recordcount>
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
<cfif structKeyExists(custom,"tracing") and custom.tracing and traces.recordcount>
	<cfset hasAction=!isColumnEmpty('traces.action')>
	<cfset hasCategory=!isColumnEmpty('traces.category')>
	<p class="cfdebug"><hr/><b class="cfdebuglge">Trace Points</b></p>
		<table border="1" cellpadding="2" cellspacing="0" class="cfdebug">
		<tr>
			<td class="cfdebug"><b>Type</b></td>
			<cfif hasCategory><td class="cfdebug"><b>Category</b></td></cfif>
			<td class="cfdebug"><b>Text</b></td>
			<td class="cfdebug"><b>Template</b></td>
			<td class="cfdebug"><b>Line</b></td>
			<cfif hasAction><td class="cfdebug"><b>Action</b></td></cfif>
			<td class="cfdebug"><b>Var</b></td>
			<td class="cfdebug"><b>Total Time</b></td>
			<td class="cfdebug"><b>Trace Slot Time</b></td>
		</tr>
<cfset total=0>
<cfloop query="traces">
<cfset total=total+traces.time>
		<tr>
			<td align="left" class="cfdebug" nowrap>#traces.type#</td>
			<cfif hasCategory><td align="left" class="cfdebug" nowrap>#traces.category#&nbsp;</td></cfif>
			<td align="let" class="cfdebug" nowrap>#traces.text#&nbsp;</td>
			<td align="left" class="cfdebug" nowrap>#traces.template#</td>
			<td align="right" class="cfdebug" nowrap>#traces.line#</td>
			<cfif hasAction><td align="left" class="cfdebug" nowrap>#traces.action#</td></cfif>
			<td align="left" class="cfdebug" nowrap><cfif len(traces.varName)>#traces.varName#<cfif structKeyExists(traces,'varValue')> = #traces.varValue#</cfif><cfelse>&nbsp;<br />
			</cfif></td>
			<td align="right" class="cfdebug" nowrap>#total# ms</td>
			<td align="right" class="cfdebug" nowrap>#traces.time# ms</td>
		</tr>
</cfloop>                
 </table>
</cfif> 


<!--- Queries --->
<cfif structKeyExists(custom,"database") and custom.database and queries.recordcount>
<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_sql">SQL Queries</a></b></p>
<cfloop query="queries">	
<code><b>#queries.name#</b> (Datasource=#queries.datasource#, Time=#queries.time#ms, Records=#queries.count#) in #queries.src#</code><br />
<pre>#queries.sql#</pre></cfloop>
</cfif>


<!--- Scopes --->
<cfset scopes="Application,CGI,Client,Cookie,Form,Request,Server,Session,URL">
<cfif not structKeyExists(custom,"scopes")><cfset custom.scopes=""></cfif>
<cfif len(custom.scopes)>
<p class="cfdebug"><hr/><b class="cfdebuglge"><a name="cfdebug_scopevars">Scope Variables</a></b></p>
<cfloop list="#scopes#" index="name"><cfif not ListFindNoCase(custom.scopes,name)><cfcontinue></cfif>
<cfset doPrint=true>
<cftry>
	<cfset scp=evaluate(name)>
    <cfcatch><cfset doPrint=false></cfcatch>
</cftry>

<cfif doPrint and structCount(scp)>
<pre class="cfdebug"><b>#name# Variables:</b><cftry><cfloop index="key" list="#ListSort(StructKeyList(scp),"textnocase")#">
#(key)#=<cftry><cfif IsSimpleValue(scp[key])>#scp[key]#<!--- 
---><cfelseif isArray(scp[key])>Array (#arrayLen(scp[key])#)<!--- 
---><cfelseif isValid('component',scp[key])>Component (#GetMetaData(scp[key]).name#)<!--- 
---><cfelseif isStruct(scp[key])>Struct (#StructCount(scp[key])#)<!--- 
---><cfelseif IsQuery(scp[key])>Query (#scp[key].recordcount#)<!--- 
---><cfelse>Complex type</cfif><cfcatch></cfcatch></cftry></cfloop><cfcatch>error (#cfcatch.message#) occurred while displaying Scope #name#</cfcatch></cftry>
</pre>
</cfif>
</cfloop>
</cfif>
<font size="-1" class="cfdebug"><i>Debug Rendering Time: #getTickCount()-time# ms</i></font><br />
	</td>
</tr>
</table>
</cfoutput>
    
    </cffunction>
    
</cfcomponent>

