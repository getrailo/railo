<!--- 
Defaults --->
<cfset error.message="">
<cfset error.detail="">
<cfparam name="form.mainAction" default="none">

<!--- load asynchron all extension providers  --->
<cfparam name="application[request.admintype].preloadedExtensionProviders" default="false" type="boolean">
<cfif !application[request.admintype].preloadedExtensionProviders>
	<cfinclude template="extension.functions.cfm">
	<cfset loadAllProvidersData(0,false)>
	<cfset application[request.admintype].preloadedExtensionProviders=true>
</cfif>

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE Label --->
		<cfcase value="#stText.Buttons.Update#">
			<cfset data.label=toArrayFromForm("label")>
			<cfset data.hash=toArrayFromForm("hash")>
            
			<cfloop index="idx" from="1" to="#arrayLen(data.label)#">
				<cfif len(trim(data.label[idx]))>
                	<cfadmin 
                    action="updateLabel"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    label="#data.label[idx]#"
                    hash="#data.hash[idx]#">
                 </cfif>
            </cfloop>
		</cfcase>
	<!--- UPDATE API Key --->
		<cfcase value="#stText.Buttons.OK#">
			<cfadmin 
                    action="updateApiKey"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    key="#trim(form.apiKey)#">
		</cfcase>
	</cfswitch>
	<cfcatch>
	
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction NEQ "none">
	<cflocation url="#request.self#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>



<cfset pool['HEAP']="Heap">
<cfset pool['NON_HEAP']="Non-Heap">

<cfset pool['HEAP_desc']="The JVM (Java Virtual Machine) has a heap that is the runtime data area from which memory for all objects are allocated.">
<cfset pool['NON_HEAP_desc']="Also, the JVM has memory other than the heap, referred to as non-heap memory. It stores all cfc/cfm templates, java classes, interned Strings and meta-data.">

<cfset pool["Par Eden Space"]="The pool from which memory is initially allocated for most objects.">
<cfset pool["Par Survivor Space"]="The pool containing objects that have survived the garbage collection of the Eden space.">
<cfset pool["CMS Old Gen"]="The pool containing objects that have existed for some time in the survivor space.">
<cfset pool["CMS Perm Gen"]="The pool containing all the reflective data of the virtual machine itself, such as class and method objects.">
<cfset pool["Code Cache"]="The HotSpot Java VM also includes a code cache, containing memory that is used for compilation and storage of native code.">


<cfset pool["Eden Space"]=pool["Par Eden Space"]>
<cfset pool["PS Eden Space"]=pool["Par Eden Space"]>

<cfset pool["Survivor Space"]=pool["Par Survivor Space"]>
<cfset pool["PS Survivor Space"]=pool["Par Survivor Space"]>

<cfset pool["Perm Gen"]=pool["CMS Perm Gen"]>

<cfset pool["Tenured Gen"]=pool["CMS Old Gen"]>
<cfset pool["PS Old Gen"]=pool["CMS Old Gen"]>


<cffunction name="printMemory" returntype="string">
	<cfargument name="usage" type="query" required="yes">
    <cfset var height=12>
    <cfset var width=100>
    	<cfset var used=evaluate(ValueList(arguments.usage.used,'+'))>
    	<cfset var max=evaluate(ValueList(arguments.usage.max,'+'))>
    	<cfset var init=evaluate(ValueList(arguments.usage.init,'+'))>
        
		<cfset var qry=QueryNew(arguments.usage.columnlist)>
		<cfset QueryAddRow(qry)>
        <cfset QuerySetCell(qry,"type",arguments.usage.type)>
        <cfset QuerySetCell(qry,"name",variables.pool[arguments.usage.type])>
        <cfset QuerySetCell(qry,"init",init,qry.recordcount)>
        <cfset QuerySetCell(qry,"max",max,qry.recordcount)>
        <cfset QuerySetCell(qry,"used",used,qry.recordcount)>
        
        <cfset arguments.usage=qry>
		<cfset var ret = "" />
		<cfsavecontent variable="ret"><cfoutput>
   			<b>#pool[usage.type]#</b>
			<cfloop query="usage">
       			<cfset local._used=int(width/arguments.usage.max*arguments.usage.used)>
        		<cfset local._free=width-_used> 
				<cfset local.pused=int(100/arguments.usage.max*arguments.usage.used)>
       			<cfset local.pfree=100-pused>
        		<div class="percentagebar tooltipMe" title="#pfree#% available (#round((usage.max-usage.used)/1024/1024)#mb), #pused#% in use (#round(usage.used/1024/1024)#mb)"><!---
					---><div style="width:#pused#%"><span>#pused#%</span></div><!---
				---></div>
    		</cfloop>
        	<cfif StructKeyExists(pool,usage.type& "_desc")>
				<div class="comment">#pool[usage.type& "_desc"]#</div>
			</cfif>
		</cfoutput></cfsavecontent>
		<cfreturn ret />
	</cffunction>

<cfset total=query(
	name:["Total"],
	type:[""],
	used:[server.java.totalMemory-server.java.freeMemory],
	max:[server.java.totalMemory],
	init:[0]
)>
<cfoutput>
	<div class="pageintro">
		#stText.Overview.introdesc[request.adminType]#
	</div>

	<cfadmin 
		action="getInfo"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnVariable="info">
		
	<cfadmin 
		action="getAPIKey"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnVariable="apiKey">
		
<cfadmin 
	action="getCompilerSettings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="compiler">
	

<cfadmin 
	action="getScope"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="scope">
	
<cfadmin 
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	action="getPerformanceSettings"
	returnVariable="performance">

<cfadmin 
	action="getContexts"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="contexts">
	
	<cfif request.adminType EQ "server">
		<cfset names=StructKeyArray(info.servlets)>
		<cfif !ArrayContainsNoCase(names,"Rest")>
			<div class="warning nofocus">
				The REST Servlet is not configured in your enviroment.
				Follow these <a href="https://github.com/getrailo/railo/wiki/Configuration:web.xml##wiki-REST" target="_blank">instructions</a> to enable REST.
			</div>
		</cfif>	
	</cfif>


	<cfif !info.javaAgentSupported>
		<div class="warning nofocus">
			There is no Java Agent defined in this enviroment. 
			The Java Agent is needed to improve memory (PermGen Space) consumption for templates.
			To enable the Java Agent follow this instructions:
			<ul>
				<li>Add the "-javaagent" JVM argument and set it to point to the railo-inst.jar in your lib directory
				<br>
				<cfif !isNull(info.javaAgentPath) && len( info.javaAgentPath )>
					in this environment that would be: <em>-javaagent:#replace( info.javaAgentPath, server.java.executionPath, "" )#</em>
				<cfelse>

					if the railo-inst.jar is not present in your lib folder you can download it from <a href="http://www.getrailo.org/download" target="new">here</a>.					
				</cfif>
                </li>
			</ul>			
		</div>
	</cfif>
	
	<table>
		<tr>
			<td valign="top" width="65%">
				
				<h2>#stText.overview.langPerf#</h2>
				<table class="maintbl">
					<tbody>
							<tr>
								<th scope="row">#stText.setting.inspectTemplate#</th>
								<td <cfif performance.inspectTemplate EQ "always">style="color:##cc0000"</cfif>>
									<cfif performance.inspectTemplate EQ "never">
										#stText.setting.inspectTemplateNever#
									<cfelseif performance.inspectTemplate EQ "once">
										#stText.setting.inspectTemplateOnce#
									<cfelseif performance.inspectTemplate EQ "always">
										#stText.setting.inspectTemplateAlways#
									</cfif>
								</td>
							</tr>
							<tr>
								<th scope="row">#stText.compiler.nullSupport#</th>
								<td <cfif !compiler.nullSupport>style="color:##cc0000"</cfif>>
									<cfif compiler.nullSupport>
										#stText.compiler.nullSupportFull#
									<cfelse>
										#stText.compiler.nullSupportPartial#
									</cfif>
							</td>
							</tr>
							<tr>
								<th scope="row">#stText.setting.dotNotation#</th>
								<td <cfif compiler.DotNotationUpperCase>style="color:##cc0000"</cfif>>
									<cfif compiler.DotNotationUpperCase>#stText.setting.dotNotationUpperCase#<cfelse>#stText.setting.dotNotationOriginalCase#</cfif>
								</td>
							</tr>
							<!---<tr>
								<th scope="row">#stText.setting.suppressWSBeforeArg#</th>
								<td <cfif !compiler.suppressWSBeforeArg>style="color:##cc0000"</cfif>>#yesNoFormat(compiler.suppressWSBeforeArg)#</td>
							</tr> --->
							
							<tr>
								<th scope="row">#stText.Scopes.LocalMode#</th>
								<td <cfif scope.localMode EQ "classic">style="color:##cc0000"</cfif>>
									<cfif scope.localMode EQ "modern">#stText.Scopes.LocalModeModern#<cfelse>#stText.Scopes.LocalModeClassic#</cfif>
								</td>
							</tr>
							
					</tbody>
				</table>
				<cfset stText.io.title="Railo IO">
				<cfset stText.io.desc="Railo.io is your one stop shop to all that is Railo. From managing your Extension Store licenses, to monitoring your servers and keeping all your settings in sync and everything in between.">
				<cfset stText.io.id="Railo.ID">
				<cfset stText.io.idDesc="To interact with RailoIO, you need a Railo.ID, you can get this ID from <a target=""top"" href=""http://beta.railo.io/index.cfm/account"">here</a>">
				<h2>#stText.io.title#</h2>
				#stText.io.desc#
				
				<table class="maintbl">
					<tbody>
						<!--- has api key --->
						<cfif !isNull(apiKey) && len(apiKey)>
							<tr>
								<cfform onerror="customError" action="#request.self#" method="post">
								<th scope="row">#stText.io.id#</th>
								<td><input type="text" style="width:250px" name="apiKey" value="#apiKey#"/><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.ok#"><br>
								<span class="comment">#stText.io.idDesc#</span></td>
								</cfform>
							</tr>
						</cfif>
						
					</tbody>
				</table>
				
				<h2>#stText.Overview.Info#</h2>
				<table class="maintbl">
					<tbody>
						<cfif request.adminType EQ "web">
							<tr>
								<th scope="row">#stText.Overview.label#</th>
								<td>#info.label#</td>
							</tr>
							<tr>
								<th scope="row">#stText.Overview.hash#</th>
								<td>#info.hash#</td>
							</tr>
						</cfif>
						<tr>
							<th scope="row">#stText.Overview.Version#</th>
							<td>Railo #server.railo.version# #server.railo.state#</td>
						</tr>
						<cfif StructKeyExists(server.railo,'versionName')>
							<tr>
								<th scope="row">#stText.Overview.VersionName#</th>
								<td><a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a></td>
							</tr>
						</cfif>
						<tr>
							<th scope="row">#stText.Overview.ReleaseDate#</th>
							<td>#lsDateFormat(server.railo['release-date'])#</td>
						</tr>
						<tr>
							<th scope="row">#stText.Overview.CFCompatibility#</th>
							<td>#replace(server.ColdFusion.ProductVersion,',','.','all')#</td>
						</tr>
					</tbody>
				</table>
				<br />
				<table class="maintbl">
					<tbody>
						<tr>
							<th scope="row">#stText.Overview.config#</th>
							<td>#info.config#</td>
						</tr>
						<cfif request.adminType EQ "web">
							<tr>
								<th scope="row">#stText.Overview.webroot#</th>
								<td>#info.root#</td>
							</tr>
		
							<cfadmin 
								action="getTLDs"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								returnVariable="tlds">
							<cfadmin 
								action="getFLDs"
								type="#request.adminType#"
								password="#session["password"&request.adminType]#"
								returnVariable="flds">
		
							<cfif isQuery(tlds)>
								<cfset tlds=listToArray(valueList(tlds.displayname))>
							</cfif>
							<cfif isQuery(flds)>
								<cfset flds=listToArray(valueList(flds.displayname))>
							</cfif>
						</cfif>
		
						<tr>
							<th scope="row">#stText.Overview.OS#</th>
							<td>#server.OS.Name# (#server.OS.Version#)<cfif structKeyExists(server.os,"archModel")> #server.os.archModel#bit</cfif></td>
						</tr>
						<tr>
							<th scope="row">#stText.Overview.remote_addr#</th>
							<td>#cgi.remote_addr#</td>
						</tr>
						<tr>
							<th scope="row">#stText.Overview.server_name#</th>
							<td>#cgi.server_name#</td>
						</tr>
						<tr>
							<th scope="row">#stText.overview.servletContainer#</th>
							<td>#server.servlet.name#</td>
						</tr>
						<tr>
							<th scope="row">#stText.overview.railoID#</th>
							<td>#getRailoId().server.id#</td>
						</tr>
						<!---
						<tr>
							<th scope="row">#stText.overview.railoID#</th>
							<td>#getRailoId().server.ioid#</td>
						</tr>
						--->
						<cfif request.adminType EQ "web">
							<tr>
								<th scope="row">#stText.Overview.InstalledTLs#</th>
								<td>
									<cfloop index="idx" from="1" to="#arrayLen(tlds)#">
										- #tlds[idx]# <!--- ( #iif(tlds[idx].type EQ "cfml",de('railo'),de('jsp'))# ) ---><br>
									</cfloop>
								</td>
							</tr>
							<tr>
								<th scope="row">#stText.Overview.InstalledFLs#</th>
								<td>
									<cfloop index="idx" from="1" to="#arrayLen(flds)#">
										- #flds[idx]#<br>
									</cfloop>
								</td>
							</tr>
							
							<tr>
								<th scope="row">#stText.Overview.DateTime#</th>
								<td>
									#lsdateFormat(now())#
									#lstimeFormat(now())#
								</td> 
							</tr>
							<tr>
								<th scope="row">#stText.Overview.ServerTime#</th>
								<td>
									
									#lsdateFormat(date:now(),timezone:"jvm")#
									#lstimeFormat(time:now(),timezone:"jvm")#
								</td> 
							</tr>
						</cfif>
						<tr>
							<th scope="row">Java</th>
							<td>
								<!--- <cfset serverNow=createObject('java','java.util.Date')> --->
								#server.java.version# (#server.java.vendor#)<cfif structKeyExists(server.java,"archModel")> #server.java.archModel#bit</cfif>
							</td> 
						</tr>
						<cfif StructKeyExists(server.os,"archModel") and StructKeyExists(server.java,"archModel")>
							<tr>
								<th scope="row">Architecture</th>
								<td>
									<cfif server.os.archModel NEQ server.os.archModel>OS #server.os.archModel#bit/JRE #server.java.archModel#bit<cfelse>#server.os.archModel#bit</cfif>
								</td> 
							</tr>
						</cfif>
						<tr>
							<th scope="row">Classpath</th>
							<td>
								<div class="classpaths longwords">
									<cfset arr=getClasspath()>
									<cfloop from="1" to="#arrayLen(arr)#" index="line">
										<div<cfif line mod 2> class="odd"</cfif>>#arr[line]#</div>
									</cfloop>
								</div>
							</td> 
						</tr>
					</tbody>
				</table>
			</td>
			<td width="2%"></td>
			<td valign="top" width="33%">
				<br><br>
				<div id="updateInfoDesc"><div style="text-align: center;"><img src="../res/img/spinner16.gif.cfm"></div></div>

				<cfsavecontent variable="Request.htmlBody" append="true">
					<script type="text/javascript">

						$( function() {

							$('##updateInfoDesc').load('update.cfm?#session.urltoken#&adminType=#request.admintype#');
						} );
					</script>
				</cfsavecontent>

					<!--- Memory Usage --->
					<cftry>
						<cfsavecontent variable="memoryInfo">
							<h3>Memory Usage</h3>
							#printMemory(getmemoryUsage("heap"))#
							#printMemory(getmemoryUsage("non_heap"))#
						</cfsavecontent>
						#memoryInfo#
						<cfcatch></cfcatch>
					</cftry>
	
					<!--- Professional --->
					<h3>
						<a href="http://www.getrailo.com/index.cfm/services/support/" target="_blank">#stText.Overview.Professional#</a>
					</h3>
					<div class="comment">#stText.Overview.ProfessionalDesc#</div>
					
					<!--- Mailing list --->
					<h3>
						<a href="http://groups.google.com/group/railo" target="_blank">#stText.Overview.Mailinglist#</a>
					</h3>
					<div class="comment">#stText.Overview.MailinglistDesc#</div>
					
					<!--- Book --->
					<h3>
						<a href="http://www.packtpub.com/railo-3-beginners-guide-to-develop-deploy-complex-applications-online/book" target="_blank">#stText.Overview.book#</a>
					</h3>
					<div class="comment">#stText.Overview.bookDesc#</div>
					
					
					<!--- <a href="http://www.linkedin.com/e/gis/71368/0CF7D323BBC1" target="_blank">Linked in</a>--->
					
					<!--- Jira --->
					<h3>
						<a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank">#stText.Overview.issueTracker#</a>
					</h3>
					<div class="comment">#stText.Overview.issuetrackerDesc#</div>
					
					<!--- Blog --->
					<h3>
						<a href="http://blog.getrailo.com/" target="_blank">#stText.Overview.blog#</a>
					</h3>
					<div class="comment">#stText.Overview.blogDesc#</div>
					
					
					
					<!--- Twitter --->
					<h3>
						<a href="https://twitter.com/##!/railo" target="_blank">#stText.Overview.twitter#</a>
					</h3>
					<div class="comment">#stText.Overview.twitterDesc#</div>
				
			</td>
		</tr>
	</table>
	
	<cfif request.admintype EQ "server">
		
		<h2>#stText.Overview.contexts.title#</h2>
		<div class="itemintro">
			You can label your web contexts here, so they are more clearly distinguishable for use with extensions etc.
		</div>
		<cfform onerror="customError" action="#request.self#" method="post">
			<table class="maintbl">
				<thead>
					<tr>
						<th width="15%">#stText.Overview.contexts.label#</th>
						<th width="25%">#stText.Overview.contexts.url#</th>
						<th width="30%">#stText.Overview.contexts.webroot#</th>
						<th width="30%">#stText.Overview.contexts.config_file#</th>
					</tr>
				</thead>
				<tbody>
					<cfset rst=contexts>
					<cfloop query="contexts">
						<tr>
							<td>
								<input type="hidden" name="hash_#rst.currentrow#" value="#rst.hash#"/>
								<input type="text" style="width:99%" name="label_#rst.currentrow#" value="#rst.label#"/>
							</td>
							<td><cfif len(rst.url)><a target="_blank" href="#rst.url#/railo-context/admin/web.cfm">#rst.url#</a></cfif></td>
							<td><input type="text" class="xlarge" name="path_#rst.currentrow#" value="#rst.path#" readonly="readonly"/></td>
							<td><input type="text" class="xlarge" style="width:99%" name="cf_#rst.currentrow#" value="#rst.config_file#" readonly="readonly"/></td>
						</tr>

						<cfset filesThreshold = 100000>
						<cfif ( contexts.clientElements GT filesThreshold ) || ( contexts.sessionElements GT filesThreshold )>

							<tr>
								<td colspan="4" style="background-color:##FCC;" align="center">
									Warning:
									<cfif ( contexts.clientElements GT filesThreshold )>
										<b>#numberFormat( contexts.clientElements, "," )#</b> Client files
									</cfif>
									<cfif ( contexts.sessionElements GT filesThreshold )>
										<b>#numberFormat( contexts.sessionElements, "," )#</b> Session files
									</cfif>
								</td>
							</tr>
						</cfif>
					</cfloop>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">
							<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input class="button reset" type="reset" name="cancel" value="#stText.Buttons.Cancel#">
						</td>
					</tr>
				</tfoot>
			</table>
		</cfform>
	</cfif>
</cfoutput>
