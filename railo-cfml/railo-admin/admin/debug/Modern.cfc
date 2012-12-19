<cfcomponent extends="Debug" output="no">

	<cfscript>
		fields=array(
		group("Execution Time","Execution times for templates, includes, modules, custom tags, and component method calls. Template execution times over this minimum highlight time appear in red.",3)
		,field("Minimal Execution Time","minimal","0",true,{_appendix:"microseconds",_bottom:"Execution times for templates, includes, modules, custom tags, and component method calls. Outputs only templates taking longer than the time (in microseconds) defined above."},"text40")
		,field("Highlight","highlight","250000",true,{_appendix:"microseconds",_bottom:"Highlight templates taking longer than the following (in microseconds) in red."},"text50")
		,group("Custom Debugging Output","Define what is outputted",3)
		,field("Scope Variables","scopes","Enabled",false,"Enable Scope reporting","checkbox","Enabled")
		,field("General Debug Information ","general","Enabled",false,
		"Select this option to show general information about this request. General items are Railo Version, Template, Time Stamp, User Locale, User Agent, User IP, and Host Name. ",
		"checkbox","Enabled")
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
		
		private function isColumnEmpty(query qry,string columnName){
		if(!QueryColumnExists(qry,columnName)) return true;
		return !len(arrayToList(queryColumnData(qry,columnName),""));
		}
	</cfscript>
 
	<cffunction name="isOldIE" output="true">
		<cfif structKeyExists(cgi,'http_user_agent')>
			<cfset var index=findNocase('MSIE',cgi.http_user_agent)>
			<cfif index GT 0>
				<cfset index+=4>
				<cfset var next=find(';',cgi.http_user_agent,index+1)>
				<cfif next GT 0>
					<cfset var sub=trim(mid(cgi.http_user_agent,index,next-index))>
					<cfif isNumeric(sub) and sub LT 8>
						<cfreturn true>
					</cfif>
				</cfif>
			</cfif>
		</cfif>
		<cfreturn false>
	</cffunction>

	<cffunction name="output" returntype="void">
		<cfargument name="custom" type="struct" required="yes" />
		<cfargument name="debugging" required="true" type="struct" />
		<cfargument name="context" type="string" default="web" />
		<cfsilent>
			<cfif !structKeyExists(arguments.custom,'minimal')><cfset arguments.custom.minimal="0"></cfif>
			<cfif !structKeyExists(arguments.custom,'highlight')><cfset arguments.custom.highlight="250000"></cfif>
			<cfif !structKeyExists(arguments.custom,'scopes')><cfset arguments.custom.scopes=false></cfif>
			<cfif !structKeyExists(arguments.custom,'general')><cfset arguments.custom.general="Enabled"></cfif>
			
			<cfset var time=getTickCount() />
			<cfset var _cgi=structKeyExists(arguments.debugging,'cgi')?arguments.debugging.cgi:cgi />
			<cfset var pages=arguments.debugging.pages />
			<cfset var queries=arguments.debugging.queries />
			<cfif not isDefined('arguments.debugging.timers')>
				<cfset arguments.debugging.timers=queryNew('label,time,template') />
			</cfif>
			<cfif not isDefined('arguments.debugging.traces')>
				<cfset arguments.debugging.traces=queryNew('type,category,text,template,line,var,total,trace') />
			</cfif>
			<cfset var timers=arguments.debugging.timers />
			<cfset var traces=arguments.debugging.traces />
			<cfset querySort(pages,"avg","desc") />
			<cfset var implicitAccess=arguments.debugging.implicitAccess />
			<cfset querySort(implicitAccess,"template,line,count","asc,asc,desc") />
			<cfparam name="arguments.custom.unit" default="millisecond">
			<cfparam name="arguments.custom.color" default="black">
			<cfparam name="arguments.custom.bgcolor" default="white">
			<cfparam name="arguments.custom.font" default="Times New Roman">
			<cfparam name="arguments.custom.size" default="medium">
			<cfset var unit={
				millisecond:"ms"
				,microsecond:"µs"
				,nanosecond:"ns"
				} />
			<!--- Plus/minus Image --->
			<cfoutput>
				
				<cfif not isOldIE()>
					<cfset plus='data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw=='>
					<cfset minus='data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7'>
				<cfelse>
					<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/debug_plus.gif.cfm">
					<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/debug_minus.gif.cfm">
				</cfif>
				
				
				<cfsavecontent variable="local.sImgPlus">
					<img src="#plus#">
				</cfsavecontent>
				<cfsavecontent variable="local.sImgMinus">
					<img src="#minus#">
				</cfsavecontent>
			</cfoutput>
		</cfsilent>
		<cfif arguments.context EQ "web">
			</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
		</cfif>
		<cfoutput>
			<style type="text/css">
				.h1 {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 20pt;color:##007bb7;} .h2 {height:6pt;font-size : 12pt;font-weight:normal;color:##007bb7;} .cfdebug {font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;} .cfdebuglge {color:#arguments.custom.color#;background-color:#arguments.custom.bgcolor#;font-family:#arguments.custom.font#; font-size:
				<cfif arguments.custom.size EQ "small">
					small
				<cfelseif arguments.custom.size EQ "medium">
					medium
				<cfelse>
					large
				</cfif>
				;} .template_overage { color: red; background-color: #arguments.custom.bgcolor#; font-family:#arguments.custom.font#; font-weight: bold; font-size:
				<cfif arguments.custom.size EQ "small">
					smaller
				<cfelseif arguments.custom.size EQ "medium">
					small
				<cfelse>
					medium
				</cfif>
				; } .tbl{empty-cells:show;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;} .tblHead{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##f2f2f2;color:##3c3e40} .tblContent {padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##ffffff;} .tblContentRed {padding-left:5px;padding-right:5px;border:1px solid ##cc0000;background-color:##f9e0e0;} .tblContentGreen {padding-left:5px;padding-right:5px;border:1px solid ##009933;background-color:##e0f3e6;} .tblContentYellow {padding-left:5px;padding-right:5px;border:1px solid ##ccad00;background-color:##fff9da;} 
			</style>
			<SCRIPT LANGUAGE="JavaScript"> plus='#plus#'; minus='#minus#'; <!--
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
				} else {
					railoDebugModernSetCookie('railo_debug_modern_'+id,'false');
					data.style.display = 'none';
					//dots.style.display = '';
					img.src=plus;
				}
			}
			-->
			</script>
			
			<table class="tbl">
			<tr>
			<td class="tblContent" style="padding:10px">
			<!--- General --->
			<cfset local.display=structKeyExists(cookie,'railo_debug_modern_info') and cookie.railo_debug_modern_info />
			
		
			<cfif isEnabled(arguments.custom,'general')>
				<span class="h2">Debugging Information</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('info')"><img vspace="4" src="#display?minus:plus#" id="info_img"></a>
						</td>
						<td>
							<table class="tbl" cellpadding="2" cellspacing="0">
								<tr>
									<td class="cfdebug" nowrap>Template</td>
									<td class="cfdebug">#_cgi.SCRIPT_NAME# (#expandPath(_cgi.SCRIPT_NAME)#)</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>User Agent</td>
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
										<cfif StructKeyExists(server.railo,'versionName')>
											(
											<a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a>
											)
										</cfif>
										#ucFirst(server.coldfusion.productlevel)# #uCase(server.railo.state)# #server.railo.version# (CFML Version #server.ColdFusion.ProductVersion#) 
									</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>Time Stamp</td>
									<td class="cfdebug">#LSDateFormat(now())# #LSTimeFormat(now())#</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>Time Zone</td>
									<td class="cfdebug">#getTimeZone()#</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>Locale</td>
									<td class="cfdebug">#ucFirst(GetLocale())#</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>Remote IP</td>
									<td class="cfdebug">#_cgi.remote_addr#</td>
								</tr>
								<tr>
									<td class="cfdebug" nowrap>Host Name</td>
									<td class="cfdebug">#_cgi.server_name#</td>
								</tr>
								<cfif StructKeyExists(server.os,"archModel") and StructKeyExists(server.java,"archModel")>
									<tr>
										<td class="cfdebug" nowrap>Architecture</td>
										<td class="cfdebug">
											<cfif server.os.archModel NEQ server.os.archModel>
												OS #server.os.archModel#bit/JRE #server.java.archModel#bit
											<cfelse>
												#server.os.archModel#bit
											</cfif>
										</td>
									</tr>
								</cfif>
							</table>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			
			
			
		<!--- Execution Time --->
			<cfset display=structKeyExists(cookie,'railo_debug_modern_exe') and cookie.railo_debug_modern_exe />
			<span class="h2"><a name="cfdebug_execution">
					Execution Time
				</a></span>
			<cfset local.loa=0 />
			<cfset local.tot=0 />
			<cfset local.q=0 />
			<cfloop query="pages">
				<cfset tot=tot+pages.total />
				<cfset q=q+pages.query />
				<cfif pages.avg LT arguments.custom.minimal*1000>
					<cfcontinue>
				</cfif>
				<cfset local.bad=pages.avg GTE arguments.custom.highlight*1000 />
				<cfset loa=loa+pages.load />
			</cfloop>
			<table class="tbl" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<a href="javascript:railoDebugModernToggle('exe')"><img vspace="4" src="#display?minus:plus#" id="exe_img" onclick=""></a>
					</td>
					<td>
						<table class="tbl"  cellpadding="2" cellspacing="0">
							<tr>
								<td align="right" nowrap>#formatUnit(arguments.custom.unit, loa)#</td>
								<td width="800">&nbsp;Startup/Compiling</td>
							</tr>
							<tr>
								<td align="right" nowrap>#formatUnit(arguments.custom.unit, tot-q-loa)#</td>
								<td>&nbsp;Application</td>
							</tr>
							<tr>
								<td align="right" nowrap>#formatUnit(arguments.custom.unit, q)#</td>
								<td>&nbsp;Query</td>
							</tr>
							<tr>
								<td align="right" nowrap><b>
										#formatUnit(arguments.custom.unit, tot)#
									</b></td>
								<td>&nbsp;
									<b>
										Total
									</b></td>
							</tr>
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
							<cfset loa=0 />
							<cfset tot=0 />
							<cfset q=0 />
							<cfloop query="pages">
								<cfset tot=tot+pages.total />
								<cfset q=q+pages.query />
								<cfif pages.avg LT arguments.custom.minimal*1000>
									<cfcontinue>
								</cfif>
								<cfset bad=pages.avg GTE arguments.custom.highlight*1000 />
								<cfset loa=loa+pages.load />
								<tr>
									<td align="right" class="tblContent" nowrap>
										<cfif bad>
											<font color="red">
										</cfif>
										#formatUnit(arguments.custom.unit, pages.total-pages.load)#<cfif bad></font></cfif>
									</td>
									<td align="right" class="tblContent" nowrap>
										<cfif bad>
											<font color="red"></cfif>
										#formatUnit(arguments.custom.unit, pages.avg)#<cfif bad></font></cfif>
									</td>
									<td align="center" class="tblContent" nowrap>#pages.count#</td>
									<td align="left" class="tblContent" nowrap>
										<cfif bad>
											<font color="red"></cfif>
										#pages.src#<cfif bad></font></cfif>
									</td>
								</tr>
							</cfloop>
						</table>
						<font color="red">
							red = over #formatUnit(arguments.custom.unit,arguments.custom.highlight*1000)# average execution time
						</font>
						<br>
						<br>
						</div> 
					</td>
				</tr>
			</table>
			<!--- Exceptions --->
			<cfif structKeyExists(arguments.debugging,"exceptions")  and arrayLen(arguments.debugging.exceptions)>
				<cfset display=structKeyExists(cookie,'railo_debug_modern_exp') and cookie.railo_debug_modern_exp />
				<cfset exceptions=debugging.exceptions />
				<span class="h2">Caught Exceptions</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('exp')"><img vspace="4" src="#display?minus:plus#" id="exp_img"></a>
						</td>
						<td>
							<table class="tbl"  cellpadding="2" cellspacing="0">
								<tr>
									<td align="right" nowrap>#arrayLen(debugging.exceptions)#</td>
									<td width="800">
										&nbsp;Exception#arrayLen(debugging.exceptions) GT 1?'s':''# catched
									</td>
								</tr>
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
							</table>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			<!--- Implicit variable Access --->
			<cfif implicitAccess.recordcount>
				<cfset display=structKeyExists(cookie,'railo_debug_modern_acc') and cookie.railo_debug_modern_acc />
				<cfset hasAction=!isColumnEmpty(traces,'action') />
				<cfset hasCategory=!isColumnEmpty(traces,'category') />
				<span class="h2">Implicit variable Access</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('acc')"><img vspace="4" src="#display?minus:plus#" id="acc_img"></a>
						</td>
						<td>
							<table class="tbl"  cellpadding="2" cellspacing="0">
								<tr>
									<td align="right" nowrap>#implicitAccess.recordcount#</td>
									<td width="800">
										&nbsp;implicit variable access#implicitAccess.recordcount GT 1?'es':''#
									</td>
								</tr>
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
								<cfset total=0 />
								<cfloop query="implicitAccess">
									<tr>
										<td align="left" class="tblContent" nowrap>#implicitAccess.scope#</td>
										<td align="left" class="tblContent" nowrap>#implicitAccess.template#</td>
										<td align="left" class="tblContent" nowrap>#implicitAccess.line#</td>
										<td align="left" class="tblContent" nowrap>#implicitAccess.name#</td>
										<td align="left" class="tblContent" nowrap>#implicitAccess.count#</td>
									</tr>
								</cfloop>
							</table>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			<!--- Timers --->
			<cfif timers.recordcount>
				<cfset display=structKeyExists(cookie,'railo_debug_modern_time') and cookie.railo_debug_modern_time />
				<span class="h2">CFTimer Times</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('time')"><img vspace="4" src="#display?minus:plus#" id="time_img"></a>
						</td>
						<td>
							<table class="tbl"  cellpadding="2" cellspacing="0">
								<tr>
									<td align="right" nowrap>#timers.recordcount#</td>
									<td>&nbsp;Timer#timers.recordcount GT 1?'s':''# set</td>
								</tr>
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
										<td align="right" class="tblContent" nowrap>#formatUnit(arguments.custom.unit, timers.time)#</td>
										<td align="right" class="tblContent" nowrap>#timers.template#</td>
									</tr>
								</cfloop>
							</table>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			<!--- Traces --->
			<cfif traces.recordcount>
				<cfset display=structKeyExists(cookie,'railo_debug_modern_trace') and cookie.railo_debug_modern_trace />
				<cfset hasAction=!isColumnEmpty(traces,'action') />
				<cfset hasCategory=!isColumnEmpty(traces,'category') />
				<span class="h2">Trace Points</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('trace')"><img vspace="4" src="#display?minus:plus#" id="trace_img"></a>
						</td>
						<td>
							<table class="tbl"  cellpadding="2" cellspacing="0">
								<tr>
									<td align="right" nowrap>#traces.recordcount#</td>
									<td>&nbsp;Trace#traces.recordcount GT 1?'s':''# set</td>
								</tr>
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
									<cfif hasCategory>
										<td class="tblHead">Category</td>
									</cfif>
									<td class="tblHead">Text</td>
									<td class="tblHead">Template</td>
									<td class="tblHead">Line</td>
									<cfif hasAction>
										<td class="tblHead">Action</td>
									</cfif>
									<td class="tblHead">Var</td>
									<td class="tblHead">Total Time</td>
									<td class="tblHead">Trace Slot Time</td>
								</tr>
								<cfset total=0 />
								<cfloop query="traces">
									<cfset total=total+traces.time />
									<tr>
										<td align="left" class="tblContent" nowrap>#traces.type#</td>
										<cfif hasCategory>
											<td align="left" class="tblContent" nowrap>#traces.category#&nbsp;</td>
										</cfif>
										<td align="let" class="tblContent" nowrap>#traces.text#&nbsp;</td>
										<td align="left" class="tblContent" nowrap>#traces.template#</td>
										<td align="right" class="tblContent" nowrap>#traces.line#</td>
										<cfif hasAction>
											<td align="left" class="tblContent" nowrap>#traces.action#</td>
										</cfif>
										<td align="left" class="tblContent" nowrap>
											<cfif len(traces.varName)>
												#traces.varName#
												<cfif structKeyExists(traces,'varValue')>
													= #traces.varValue#
												</cfif>
											<cfelse>
												&nbsp;
												<br />
											</cfif>
										</td>
										<td align="right" class="tblContent" nowrap>#formatUnit(arguments.custom.unit, total)#</td>
										<td align="right" class="tblContent" nowrap>#formatUnit(arguments.custom.unit, traces.time)#</td>
									</tr>
								</cfloop>
							</table>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			<!--- Queries --->
			<cfif queries.recordcount>
				<cfset local.total=0 />
				<cfset local.records=0 />
				<cfloop query="queries"><cfset total+=queries.time />
					<cfset records+=queries.count /></cfloop>
				<cfset display=structKeyExists(cookie,'railo_debug_modern_qry') and cookie.railo_debug_modern_qry />
				<span class="h2">SQL Queries</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
							<a href="javascript:railoDebugModernToggle('qry')"><img vspace="4" src="#display?minus:plus#" id="qry_img"></a>
						</td>
						<td>
							<table class="tbl"  cellpadding="2" cellspacing="0">
								<tr>
									<td nowrap>#queries.recordcount#</td>
									<td>&nbsp;Quer#timers.recordcount GT 1?'ies':'y'# executed</td>
								</tr>
								<tr>
									<td nowrap>#formatUnit(total, queries.time)#</td>
									<td>&nbsp;Total execution time</td>
								</tr>
								<tr>
									<td nowrap>#records#</td>
									<td>&nbsp;Records</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>
							<div id="qry_body" style="display:#display?"":"none"#;"> 
							<cfloop query="queries">
								<code>
									<b>#queries.name#</b>
									(Datasource=#queries.datasource#, Time=#formatUnit(arguments.custom.unit, queries.time)#, Records=#queries.count#) in #queries.src#
								</code>
								<br/>
								<cfif ListFindNoCase(queries.columnlist,'usage') and IsStruct(queries.usage)>
									<cfset local.usage=queries.usage />
									<cfset local.lstNeverRead="" />
									<cfset local.lstRead = "" />
									<cfloop collection="#usage#" index="local.item" item="local.value">
										<cfif not value>
											<cfset lstNeverRead=ListAppend(lstNeverRead,item,', ') />
										<cfelse>
											<cfset lstRead=ListAppend(lstRead,item,', ') />
										</cfif>
									</cfloop>
									<b>Query usage within the request:</b>
									<br />
									<cfif len(lstRead)>
										<font color="green">
											used columns: <b>#lstRead#</b>
										</font>
										<br />
									</cfif>
									<cfif len(lstNeverRead)>
										<font color="red">
											unused columns: <b>#lstNeverRead#</b>
											<br />
											Usage: <b>#numberFormat(listLen(lstRead)/(listLen(lstRead)+listLen(lstNeverRead))*100, "999.9")# %</b>
										</font>
									</cfif>
								</cfif>
								<br /><b>SQL:</b>
								<pre>#queries.sql#</pre>
							</cfloop>
							<br>
							</div> 
						</td>
					</tr>
				</table>
			</cfif>
			
			
			
			<!--- Scopes --->
			<cfif isEnabled(arguments.custom,"scopes")>
				
				<cfset local.scopes = "application,CGI,cookie,form,request,server,URL">
			
			
				<span class="h2">Scope Information</span>
				<table class="tbl" cellpadding="0" cellspacing="0">
					
							<cfloop list="#local.scopes#" index="local.k">
								<cfset local.v=evaluate(k)>
								<cfset local.display=structKeyExists(cookie,'railo_debug_modern_scope_#k#') and cookie['railo_debug_modern_scope_#k#'] />
								
									<td valign="top" nowrap="true">
										<a href="javascript:railoDebugModernToggle('scope_#k#')"><img vspace="4" src="#display?minus:plus#" id="scope_#k#_img"></a> 
										&nbsp;&nbsp;
									</td>
									<td><b>#UCFirst(k)# Scope</b><br>
									<table class="tbl"  cellpadding="2" cellspacing="0">
										<!---<tr>
											<td align="right">Element Count</td>
											<td>&nbsp;#sizeOf.count#</td>
										</tr>--->
										<cfset local.sc=StructCount(v)>
										<tr>
											<td align="right">Estimate Size</td>
											<td>&nbsp;<cftry>#byteFormat(sc==0?0:sizeOf(v))#<cfcatch>not available</cfcatch></cftry></td>
										</tr>
									</table></td>
								</tr>
								<tr>
									<td></td>
									<td><div id="scope_#k#_body" style="display:#display?"":"none"#;">
										<cfif local.display>
										<cftry><cfdump var="#v#" keys="1000" label="#sc GT 1000?"First 1000 Records":""#"><cfcatch>not available</cfcatch></cftry>
										<cfelse>
											the Scope will be displayed with the next request
										</cfif>
									</div></td>
								</tr>
							</cfloop>
							
				</table>
			</cfif>
			
			</td>
			</tr>
			</table>
		</cfoutput>

	</cffunction>


	<cffunction name="ReplaceSQLStatements" output="No" returntype="struct">
		<cfargument name="sSql" required="Yes" type="string" />
		<cfset var sSql = Replace(arguments.sSql, Chr(9), " ", "ALL") />
		<cfset var aWords = ['select','from','where','order by','group by','having'] />
		<cfloop from="1" to="3" index="local.i">
			<cfset sSql = Replace(sSql, "  ", " ", "ALL") />
			<cfset sSql = Replace(sSql, Chr(10), "", "ALL") />
			<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL") />
		</cfloop>
		<cfset sSql = Replace(sSql, "#CHR(13)# #CHR(13)#", CHR(13), "ALL") />
		<cfloop collection="#aWords#" item="local.sWord">
			<cfset sSql = ReplaceNoCase(sSQL, aWords[sWord], "#UCase(aWords[sWord])##chr(9)#", "ALL") />
		</cfloop>
		<cfset local.stRet       = {} />
		<cfset stRet.sSql        = Trim(sSql) />
		<cfset stRet.Executeable = True />
		<cfset aWords = ["drop ,delete ,update ,insert ,alter database ,alter table "] />
		<cfloop collection="#aWords#" item="sWord">
			<cfif FindNoCase(aWords[sWord], sSql)>
				<cfset stRet.Executeable = False />
				<cfbreak>
			</cfif>
		</cfloop>
		<cfreturn stRet />
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
		private function isEnabled(custom,key){
			return structKeyExists(arguments.custom,key) and (arguments.custom[arguments.key] EQ "Enabled" or arguments.custom[arguments.key] EQ "true");
		}
		
	</cfscript>
 
	<cffunction name="formatUnit" output="no" returntype="string">
		<cfargument name="unit" type="string" required="yes" />
		<cfargument name="time" type="numeric" required="yes" />
		<cfif arguments.time GTE 100000000>
			<!--- 1000ms --->
			<cfreturn int(arguments.time/1000000)&" ms" />
		<cfelseif arguments.time GTE 10000000>
			<!--- 100ms --->
			<cfreturn (int(arguments.time/100000)/10)&" ms" />
		<cfelseif arguments.time GTE 1000000>
			<!--- 10ms --->
			<cfreturn (int(arguments.time/10000)/100)&" ms" />
		<cfelse>
			<!--- 0ms --->
			<cfreturn (int(arguments.time/1000)/1000)&" ms" />
		</cfif>
		<cfreturn (arguments.time/1000000)&" ms" />
	</cffunction>
	
	<cffunction name="byteFormat" output="no">
        <cfargument name="raw" type="numeric">
        <cfif raw EQ 0><cfreturn "0b"></cfif>
        <cfset var b=raw>
        <cfset var rtn="">
        <cfset var kb=b/1024>
        <cfset var mb=kb/1024>
        <cfset var gb=mb/1024>
        <cfset var tb=gb/1024>
        
        <cfif tb GTE 1><cfreturn numberFormat(tb,'0.0')&"tb"></cfif>
        <cfif gb GTE 1><cfreturn numberFormat(gb,'0.0')&"gb"></cfif>
        <cfif mb GTE 1><cfreturn numberFormat(mb,'0.0')&"mb"></cfif>
        <cfif  b GT 100><cfreturn numberFormat(kb,'0.0')&"kb"></cfif>
		<cfreturn b&"b">
    </cffunction>

</cfcomponent>
