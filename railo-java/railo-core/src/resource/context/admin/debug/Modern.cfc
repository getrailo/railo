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
		,field("Scope cascading","accessScope",true,false,"Select this option to show all cascaded scope accesses. ","checkbox")
		
		
		,group("Output Format","Define details to the fomrat of the debug output",3)
		,field("Background Color","bgcolor","white",true,"Color in the back, ","text80")
		,field("Font Color","color","black",true,"Color used for the Font, ","text80")
		,field("Font Family","font","Times New Roman, Times, serif",true,"What kind of Font is used, ","text200")
		,field("Font Size","size","medium",true,"What kind of Font is usedThe size of the font in Pixel, ","select","small,medium,large")
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
	throwWhenEmpty(custom,"color");
	throwWhenEmpty(custom,"bgcolor");
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
<cfset accessScope=debugging.accessScope>
<cfset querySort(accessScope,"count","desc")>

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

</cfsilent></td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
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
            <td class="cfdebug">#cgi.SCRIPT_NAME# (#getBaseTemplatePath()#)</td>
        </tr>
        <tr>
            <td class="cfdebug" nowrap> User Agent </td>
            <td class="cfdebug">#cgi.http_user_agent#</td>
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




<!--- Cascaded variable Access --->
<cfif structKeyExists(custom,"accessScope") and custom.accessScope and accessScope.recordcount>
	<cfset display=structKeyExists(cookie,'railo_debug_modern_acc') and cookie.railo_debug_modern_acc>
	<cfset hasAction=!isColumnEmpty('traces.action')>
	<cfset hasCategory=!isColumnEmpty('traces.category')>
	<span class="h2">Cascaded variable access</span>
		
<table class="tbl" cellpadding="0" cellspacing="0">
<tr>
	<td valign="top">
		<a href="javascript:railoDebugModernToggle('acc')"><img vspace="4" src="#display?minus:plus#" id="acc_img"></a>
    </td>
    <td>
        <table class="tbl"  cellpadding="2" cellspacing="0">
            <tr><td align="right" nowrap>#accessScope.recordcount#</td><td width="800">&nbsp;cascaded access#accessScope.recordcount GT 1?'es':''#</td></tr>
        </table>
    </td>
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
    		<cfloop query="accessScope">
            <tr>
                <td align="left" class="tblContent" nowrap>#accessScope.scope#</td>
                <td align="left" class="tblContent" nowrap>#accessScope.template#</td>
                <td align="left" class="tblContent" nowrap>#accessScope.line#</td>
                <td align="left" class="tblContent" nowrap>#accessScope.name#</td>
                <td align="left" class="tblContent" nowrap>#accessScope.count#</td>
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

</cfoutput>














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
		.xcheckbox {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;}
		.xcfdebug {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:10px;clear:both;}
		.xcfdebuglge {color:#6699BB;background-color:white;font-family:"Verdana", Times, serif; font-size:small;}
		.xcfdebug_head {color:#224499;background-color:white;font-family:"Verdana", Times, serif; font-size:small;font-weight: bold; float:left; margin-right: 10px;}
		xa.cfdebuglink {color:blue; background-color:white;}
		.xtemplate {	color: black; font-family: "Verdana", Times, serif; font-weight: normal; }
		.xtemplate_overage {	background-color:white; font-family: "Verdana", Times, serif; font-weight: bold; }
		.xrdebug_switch {	text-align:center;
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
            
            
            
            
            
            
            
            
            
            
            
            
		</cfsavecontent>
	<cfelse>
		<cfset sExecution = "Execution times are hidden in Display options">
	</cfif>
	<cfsavecontent variable="sQueryExecution">
		<cfif BitAnd(cookie.display_options, 2) eq 2>
			<cfset bHideStatements = (BitAnd(cookie.display_options, 4) eq 4)>
			<!---<cfinclude template="../display/debugging-console-output-queries.cfm">--->
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
					<td class="cfdebug"><b>Action</b></td>
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
					<cfif isDefined('traces.action')><td align="left" class="cfdebug" nowrap>#traces.action#</td></cfif>
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
							#ucFirst(server.coldfusion.productlevel)# 
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
							<td class="cfdebug" nowrap><b> Locale </b></td><td>&nbsp;</td><td class="cfdebug">#ucFirst(GetLocale())#</td>
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
		xtable {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
		.tdhead {background-color:#66BBFF;font-weight:bold;}
		.tddetail {background-color:#00CCFF;}
		xbody {color:black;background-color:white;font-family:"Verdana", Times, serif;font-size:x-small;}
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

