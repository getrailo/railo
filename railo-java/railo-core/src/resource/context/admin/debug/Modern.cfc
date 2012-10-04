<cfcomponent extends="Debug" output="no"><cfscript>

    fields=array(
		
		group("Execution Time","Execution times for templates, includes, modules, custom tags, and component method calls. Template execution times over this minimum highlight time appear in red.",3)
		//,field("Extensions","extension","cfm,cfc,cfml",false,"Output the templates with the following extensions","checkbox","cfm,cfc,cfml")
		
		
		//,field("Unit","unit","millisecond",true,"the unit used to display the execution time.","select","millisecond,microsecond,nanosecond")
		
		,field("Minimal Execution Time","minimal","0",true,
				{_appendix:"microseconds",_bottom:"Execution times for templates, includes, modules, custom tags, and component method calls. Outputs only templates taking longer than the time (in microseconds) defined above."},"text40")
		
		,field("Highlight","highlight","250000",true,
				{_appendix:"microseconds",_bottom:"Highlight templates taking longer than the following (in microseconds) in red."},"text50")
		
		
		
		,group("Custom Debugging Output","Define what is outputted",3)

		


		,field("General Debug Information ","general",true,false,
				"Select this option to show general information about this request. General items are Railo Version, Template, Time Stamp, User Locale, User Agent, User IP, and Host Name. ","checkbox")
		
		,field("Scope Variables","scopes","Application,CGI,Client,Cookie,Form,Request,Server,Session,URL",true,"Enable Scope reporting","checkbox","Application,CGI,Client,Cookie,Form,Request,Server,Session,URL")
		
		,field("Database Activity","database",true,false,"Select this option to show the database activity for the SQL Query events and Stored Procedure events in the debugging output.","checkbox")
		
		,field("Exceptions","exception",true,false,"Select this option to output all exceptions raised for the request. ","checkbox")
		
		,field("Tracing","tracing",true,false,"Select this option to show trace event information. Tracing lets a developer track program flow and efficiency through the use of the CFTRACE tag.","checkbox")
		
		,field("Timer","timer",true,false,"Select this option to show timer event information. Timers let a developer track the execution time of the code between the start and end tags of the CFTIMER tag. ","checkbox")
		,field("Implicit variable Access","implicitAccess",true,false,"Select this option to show all accesses to scopes, queries and threads that happens implicit (cascaded). ","checkbox")
		
		
	);
    
    



string function getLabel(){
	return "Modern";
}
string function getDescription(){
	return "The new style debug template";
}
string function getid(){
	return "railo-modern";
}


void function onBeforeUpdate(struct custom){
	//throwWhenEmpty(custom,"color");
	//throwWhenEmpty(custom,"bgcolor");
	throwWhenNotNumeric(custom,"minimal");
	throwWhenNotNumeric(custom,"highlight");
}



private void function throwWhenEmpty(struct custom, string name){
	if(!structKeyExists(custom,name) or len(trim(custom[name])) EQ 0)
		throw "value for ["&name&"] is not defined";
}
private void function throwWhenNotNumeric(struct custom, string name){
	throwWhenEmpty(custom,name);
	if(!isNumeric(trim(custom[name])))
		throw "value for ["&name&"] must be numeric";
}

private function isColumnEmpty(string columnName){
	if(!isDefined(columnName)) return true;
	return !len(replace(valueList(""&columnName),',','','all'));
}


</cfscript>   
    
    
    <cffunction name="output" returntype="void">
    	<cfargument name="custom" type="struct" required="yes">
		<cfargument name="debugging" required="true" type="struct">
		<cfargument name="context" type="string" default="web"><cfsilent>
<cfset var time=getTickCount()>
<cfset var _cgi=structKeyExists(debugging,'cgi')?debugging.cgi:cgi>

<cfset var pages=debugging.pages>
<cfset var queries=debugging.queries>
<cfif not isDefined('debugging.timers')>
	<cfset debugging.timers=queryNew('label,time,template')>
</cfif>
<cfif not isDefined('debugging.traces')>
	<cfset debugging.traces=queryNew('type,category,text,template,line,var,total,trace')>
</cfif>
<cfset timers=debugging.timers>
<cfset traces=debugging.traces>
<cfset querySort(pages,"avg","desc")>
<cfset implicitAccess=debugging.implicitAccess>
<cfset querySort(implicitAccess,"template,line,count","asc,asc,desc")>

<cfparam name="custom.unit" default="millisecond">
<cfparam name="custom.color" default="black">
<cfparam name="custom.bgcolor" default="white">
<cfparam name="custom.font" default="Times New Roman">
<cfparam name="custom.size" default="medium">

<cfset unit={
millisecond:"ms"
,microsecond:"µs"
,nanosecond:"ns"

}>
<!--- Plus/minus Image --->
<cfoutput>
<cfif structKeyExists(cgi,'http_user_agent') and findNocase('MSIE',cgi.http_user_agent)>
	<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/plus.png.cfm">
	<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/mnus.png.cfm">
<cfelse>
    <cfsavecontent variable="plus"><cfinclude template="../../admin/resources/img/plus.png.cfm"></cfsavecontent>
    <cfsavecontent variable="minus"><cfinclude template="../../admin/resources/img/minus.png.cfm"></cfsavecontent>
</cfif>
<cfsavecontent variable="sImgPlus"><img src="#plus#"></cfsavecontent>
<cfsavecontent variable="sImgMinus"><img src="#minus#"></cfsavecontent>
</cfoutput>

</cfsilent><cfif context EQ "web"></td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp></cfif>
<style type="text/css">
<cfoutput>

.h1 {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 20pt;color:##007bb7;}
.h2 {height:6pt;font-size : 12pt;font-weight:normal;color:##007bb7;}


.cfdebug {font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;}

.cfdebuglge {color:#custom.color#;background-color:#custom.bgcolor#;font-family:#custom.font#;
	font-size:<cfif custom.size EQ "small">small<cfelseif custom.size EQ "medium">medium<cfelse>large</cfif>;}

.template_overage {	color: red; background-color: #custom.bgcolor#; font-family:#custom.font#; font-weight: bold;
	font-size:<cfif custom.size EQ "small">smaller<cfelseif custom.size EQ "medium">small<cfelse>medium</cfif>; }
	
.tbl{empty-cells:show;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;}
.tblHead{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##f2f2f2;color:##3c3e40}
.tblContent	{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##ffffff;}
.tblContentRed	{padding-left:5px;padding-right:5px;border:1px solid ##cc0000;background-color:##f9e0e0;}
.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid ##009933;background-color:##e0f3e6;}
.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid ##ccad00;background-color:##fff9da;}

</style>
<SCRIPT LANGUAGE="JavaScript">
plus='#plus#';
minus='#minus#';
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
		
		function railoDebugModernSetCookie(name, value, domain, expires, path, secure) {
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
		* /
		
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
		}*/

function railoDebugModernToggle(id) {
	var data=document.getElementById(id+'_body');
	//var dots=document.getElementById(id+'_body_close');
	var img=document.getElementById(id+'_img');
	if (data.style.display == 'none') {
		railoDebugModernSetCookie('railo_debug_modern_'+id,'true');
		data.style.display = '';
		//dots.style.display = 'none';
		img.src=minus;
	} 
	else {
		railoDebugModernSetCookie('railo_debug_modern_'+id,'false');
		data.style.display = 'none';
		//dots.style.display = '';
		img.src=plus;
	}
}
</script>

<table class="tbl">
<tr>
	<td class="tblContent" style="padding:10px">
 <!--- General --->
<cfset display=structKeyExists(cookie,'railo_debug_modern_info') and cookie.railo_debug_modern_info>
<cfif structKeyExists(custom,"general") and custom.general>

<span class="h2"><a name="cfdebug_top">Debugging Information</a></span>
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top"><a href="javascript:railoDebugModernToggle('info')"><img vspace="4" src="#display?minus:plus#" id="info_img"></a></td>
    <td>
        <table class="tbl" cellpadding="2" cellspacing="0">
        <tr>
            <td class="cfdebug" nowrap> Template</td>
            <td class="cfdebug">#_cgi.SCRIPT_NAME# (#expandPath(_cgi.SCRIPT_NAME)#)</td>
        </tr>
        <tr>
            <td class="cfdebug" nowrap> User Agent </td>
            <td class="cfdebug">#_cgi.http_user_agent#</td>
        </tr>
        </table>
    </td>
</tr>
<tr>
	<td valign="top">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="info_body" style="display:#display?"":"none"#;">
            <table class="tbl" cellpadding="2" cellspacing="0">
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
                <td class="cfdebug" nowrap> Remote IP </td>
                <td class="cfdebug">#_cgi.remote_addr#</td>
            </tr>
            <tr>
                <td class="cfdebug" nowrap> Host Name </td>
                <td class="cfdebug">#_cgi.server_name#</td>
            </tr>
            <cfif StructKeyExists(server.os,"archModel") and StructKeyExists(server.java,"archModel")><tr>
                <td class="cfdebug" nowrap> Architecture</td>
                <td class="cfdebug"><cfif server.os.archModel NEQ server.os.archModel>OS #server.os.archModel#bit/JRE #server.java.archModel#bit<cfelse>#server.os.archModel#bit</cfif></td>
            </tr></cfif>
            </table><br>
        </div>
    </td>
</tr>
</table>
</cfif>


<!--- Execution Time --->
<cfset display=structKeyExists(cookie,'railo_debug_modern_exe') and cookie.railo_debug_modern_exe>
<span class="h2"><a name="cfdebug_execution">Execution Time</a></span>
    
<cfset loa=0>
<cfset tot=0>
<cfset q=0>
<cfloop query="pages">
<cfset tot=tot+pages.total><cfset q=q+pages.query>
<cfif pages.avg LT custom.minimal*1000><cfcontinue></cfif>
<cfset bad=pages.avg GTE custom.highlight*1000><cfset loa=loa+pages.load>
</cfloop>      


<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('exe')"><img vspace="4" src="#display?minus:plus#" id="exe_img" onclick=""></a>
	</td>
    <td>
	    <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#formatUnit(custom.unit, loa)#</td><td width="800">&nbsp;Startup/Compiling</td></tr>
            <tr><td align="right" nowrap>#formatUnit(custom.unit, tot-q-loa)#</td><td>&nbsp;Application</td></tr>
            <tr><td align="right" nowrap>#formatUnit(custom.unit, q)#</td><td>&nbsp;Query</td></tr>
            <tr><td align="right" nowrap><b>#formatUnit(custom.unit, tot)#</b></td><td>&nbsp;<b>Total</b></td></tr>
        </table>
	</td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
		<div id="exe_body" style="display:#display?"":"none"#;">
            <table class="tbl" cellpadding="2" cellspacing="0">
            <tr>
                <td class="tblHead" align="center">Total Time</td>
                <td class="tblHead" align="center">Avg Time</td>
                <td class="tblHead" align="center">Count</td>
                <td class="tblHead">Template</td>
            </tr>
            <cfset loa=0>
            <cfset tot=0>
            <cfset q=0>
            <cfloop query="pages">
            <cfset tot=tot+pages.total><cfset q=q+pages.query>
            <cfif pages.avg LT custom.minimal*1000><cfcontinue></cfif>
            <cfset bad=pages.avg GTE custom.highlight*1000><cfset loa=loa+pages.load>
            <tr>
                <td align="right" class="tblContent" nowrap><cfif bad><font color="red"></cfif>#formatUnit(custom.unit, pages.total-pages.load)#<cfif bad></font></cfif></td>
                <td align="right" class="tblContent" nowrap><cfif bad><font color="red"></cfif>#formatUnit(custom.unit, pages.avg)#<cfif bad></font></cfif></td>
                <td align="center" class="tblContent" nowrap>#pages.count#</td>
                <td align="left" class="tblContent" nowrap><cfif bad><font color="red"></cfif>#pages.src#<cfif bad></font></cfif></td>
            </tr>
            </cfloop>                
            </table>
        
			<font color="red">red = over #formatUnit(custom.unit,custom.highlight*1000)# average execution time</font><br><br>
    	</div>
	</td>
</tr>
</table>


<!--- Exceptions --->
<cfif structKeyExists(custom,"exception") and custom.exception and structKeyExists(debugging,"exceptions")  and arrayLen(debugging.exceptions)>
<cfset display=structKeyExists(cookie,'railo_debug_modern_exp') and cookie.railo_debug_modern_exp>
	<cfset exceptions=debugging.exceptions>
    
<span class="h2">Caught Exceptions</span>
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('exp')"><img vspace="4" src="#display?minus:plus#" id="exp_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#arrayLen(debugging.exceptions)#</td><td width="800">&nbsp;Exception#arrayLen(debugging.exceptions) GT 1?'s':''# catched</td></tr>
        </table>
    </td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="exp_body" style="display:#display?"":"none"#;">
    	<table class="tbl" cellpadding="2" cellspacing="0">
		<tr>
			<td class="tblHead">Type</td>
			<td class="tblHead">Message</td>
			<td class="tblHead">Detail</td>
			<td class="tblHead">Template</td>
		</tr>
<cfloop array="#exceptions#" index="exp">
		<tr>
			<td class="tblContent" nowrap>#exp.type#</td>
			<td class="tblContent" nowrap>#exp.message#</td>
			<td class="tblContent" nowrap>#exp.detail#</td>
			<td class="tblContent" nowrap>#exp.TagContext[1].template#:#exp.TagContext[1].line#</td>
		</tr>
</cfloop>                
 		</table><br></div>
    </td>
</tr>
</table>
</cfif>




<!--- Implicit variable Access --->
<cfif structKeyExists(custom,"implicitAccess") and custom.implicitAccess and implicitAccess.recordcount>
	<cfset display=structKeyExists(cookie,'railo_debug_modern_acc') and cookie.railo_debug_modern_acc>
	<cfset hasAction=!isColumnEmpty('traces.action')>
	<cfset hasCategory=!isColumnEmpty('traces.category')>
	<span class="h2">Implicit variable Access</span>
		
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('acc')"><img vspace="4" src="#display?minus:plus#" id="acc_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#implicitAccess.recordcount#</td><td width="800">&nbsp;implicit variable access#implicitAccess.recordcount GT 1?'es':''#</td></tr>
        </table>
    </td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="acc_body" style="display:#display?"":"none"#;">
        	<table class="tbl" cellpadding="2" cellspacing="0">
            <tr>
                <td class="tblHead">Scope</td>
                <td class="tblHead">Template</td>
                <td class="tblHead">Line</td>
                <td class="tblHead">Var</td>
                <td class="tblHead">Count</td>
            </tr>
    		<cfset total=0>
    		<cfloop query="implicitAccess">
            <tr>
                <td align="left" class="tblContent" nowrap>#implicitAccess.scope#</td>
                <td align="left" class="tblContent" nowrap>#implicitAccess.template#</td>
                <td align="left" class="tblContent" nowrap>#implicitAccess.line#</td>
                <td align="left" class="tblContent" nowrap>#implicitAccess.name#</td>
                <td align="left" class="tblContent" nowrap>#implicitAccess.count#</td>
            </tr>
    		</cfloop>                
     		</table><br>
        </div>
    </td>
</tr>
</table>
</cfif> 


<!--- Timers --->
<cfif structKeyExists(custom,"timer") and custom.timer and  timers.recordcount>
	<cfset display=structKeyExists(cookie,'railo_debug_modern_time') and cookie.railo_debug_modern_time>
<span class="h2">CFTimer Times</span>    
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('time')"><img vspace="4" src="#display?minus:plus#" id="time_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#timers.recordcount#</td><td>&nbsp;Timer#timers.recordcount GT 1?'s':''# set</td></tr>
        </table>
    </td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="time_body" style="display:#display?"":"none"#;">
        	<table class="tbl" cellpadding="2" cellspacing="0">
            <tr>
                <td class="tblHead" align="center">Label</td>
                <td class="tblHead">Time</td>
                <td class="tblHead">Template</td>
            </tr>
    		<cfloop query="timers">
            <tr>
                <td align="right" class="tblContent" nowrap>#timers.label#</td>
                <td align="right" class="tblContent" nowrap>#formatUnit(custom.unit, timers.time)#</td>
                <td align="right" class="tblContent" nowrap>#timers.template#</td>
            </tr>
    		</cfloop>                
     		</table><br>
        </div>
    	</td>
</tr>
</table>
    
    
		
</cfif>

<!--- Traces --->
<cfif structKeyExists(custom,"tracing") and custom.tracing and traces.recordcount>
	<cfset display=structKeyExists(cookie,'railo_debug_modern_trace') and cookie.railo_debug_modern_trace>
	<cfset hasAction=!isColumnEmpty('traces.action')>
	<cfset hasCategory=!isColumnEmpty('traces.category')>
	<span class="h2">Trace Points</span>
		
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('trace')"><img vspace="4" src="#display?minus:plus#" id="trace_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#traces.recordcount#</td><td>&nbsp;Trace#traces.recordcount GT 1?'s':''# set</td></tr>
        </table>
    </td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="trace_body" style="display:#display?"":"none"#;">
        	<table class="tbl" cellpadding="2" cellspacing="0">
            <tr>
                <td class="tblHead">Type</td>
                <cfif hasCategory><td class="tblHead">Category</td></cfif>
                <td class="tblHead">Text</td>
                <td class="tblHead">Template</td>
                <td class="tblHead">Line</td>
                <cfif hasAction><td class="tblHead">Action</td></cfif>
                <td class="tblHead">Var</td>
                <td class="tblHead">Total Time</td>
                <td class="tblHead">Trace Slot Time</td>
            </tr>
    		<cfset total=0>
    		<cfloop query="traces">
    		<cfset total=total+traces.time>
            <tr>
                <td align="left" class="tblContent" nowrap>#traces.type#</td>
                <cfif hasCategory><td align="left" class="tblContent" nowrap>#traces.category#&nbsp;</td></cfif>
                <td align="let" class="tblContent" nowrap>#traces.text#&nbsp;</td>
                <td align="left" class="tblContent" nowrap>#traces.template#</td>
                <td align="right" class="tblContent" nowrap>#traces.line#</td>
                <cfif hasAction><td align="left" class="tblContent" nowrap>#traces.action#</td></cfif>
                <td align="left" class="tblContent" nowrap><cfif len(traces.varName)>#traces.varName#<cfif structKeyExists(traces,'varValue')> = #traces.varValue#</cfif><cfelse>&nbsp;<br />
                </cfif></td>
                <td align="right" class="tblContent" nowrap>#formatUnit(custom.unit, total)#</td>
                <td align="right" class="tblContent" nowrap>#formatUnit(custom.unit, traces.time)#</td>
            </tr>
    		</cfloop>                
     		</table><br>
        </div>
    	</td>
</tr>
</table>
        
        
</cfif> 


<!--- Queries --->
<cfif structKeyExists(custom,"database") and custom.database and queries.recordcount>
<cfset total=0>
<cfset records=0>
<cfloop query="queries">	
	<cfset total+=queries.time>
	<cfset records+=queries.count>
</cfloop>
            


	<cfset display=structKeyExists(cookie,'railo_debug_modern_qry') and cookie.railo_debug_modern_qry>
<span class="h2">SQL Queries</span>

<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('qry')"><img vspace="4" src="#display?minus:plus#" id="qry_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td nowrap>#queries.recordcount#</td><td>&nbsp;Quer#timers.recordcount GT 1?'ies':'y'# executed</td></tr>
            <tr><td nowrap>#formatUnit(total, queries.time)#</td><td>&nbsp;Total execution time</td></tr>
            <tr><td nowrap>#records#</td><td>&nbsp;Records</td></tr>
        </table>
    </td>
</tr>
<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
    	<div id="qry_body" style="display:#display?"":"none"#;">
        
            <cfloop query="queries">	
            <code><b>#queries.name#</b> (Datasource=#queries.datasource#, Time=#formatUnit(custom.unit, queries.time)#, Records=#queries.count#) in #queries.src#</code><br />
            <cfif ListFindNoCase(queries.columnlist,'usage') and IsStruct(queries.usage)><cfset usage=queries.usage><cfset lstNeverRead="">
            <cfloop collection="#usage#" item="item"><cfif not usage[item]><cfset lstNeverRead=ListAppend(lstNeverRead,item,', ')></cfif></cfloop>
            <cfif len(lstNeverRead)><font color="red">the following colum(s) are never read within the request:#lstNeverRead#</font><br /></cfif>
            </cfif>
            <pre>#queries.sql#</pre></cfloop><br>
        </div>
    </td>
</tr>
</table>

</cfif>
</td>
</tr>
</table>
</cfoutput>
















    
    </cffunction>
    
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
   
<cffunction name="formatUnit" output="no" returntype="string">
	<cfargument name="unit" type="string" required="yes">
	<cfargument name="time" type="numeric" required="yes">
    
    <cfif time GTE 100000000><!--- 1000ms --->
    	<cfreturn int(time/1000000)&" ms">
    <cfelseif time GTE 10000000><!--- 100ms --->
    	<cfreturn (int(time/100000)/10)&" ms">
    <cfelseif time GTE 1000000><!--- 10ms --->
    	<cfreturn (int(time/10000)/100)&" ms">
    <cfelse><!--- 0ms --->
    	<cfreturn (int(time/1000)/1000)&" ms">
    </cfif>
    
    
    <cfreturn (time/1000000)&" ms">
</cffunction>   
<!---<cffunction name="formatUnit2" output="no" returntype="string">
	<cfargument name="unit" type="string" required="yes">
	<cfargument name="time" type="numeric" required="yes">
    <cfif unit EQ "millisecond">
    	<cfreturn int(time/1000000)&" ms">
    <cfelseif unit EQ "microsecond">
    	<cfreturn int(time/1000)&" &micro;s">
    <cfelse>
    	<cfreturn int(time)&" ns">
    </cfif>
</cffunction>--->
</cfcomponent>

