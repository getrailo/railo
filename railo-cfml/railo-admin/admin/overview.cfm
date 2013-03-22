<!--- 
Defaults --->
<cfset error.message="">
<cfset error.detail="">
<cfparam name="form.mainAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
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
   			<h3>#pool[usage.type]#</h3>
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
	
	<cfif request.adminType EQ "server">
		<cfset names=StructKeyArray(info.servlets)>
		<cfif !ArrayContainsNoCase(names,"Rest")>
			<div class="warning nofocus">
				The REST Servlet is not configured in your enviroment.
				Follow these <a href="https://github.com/getrailo/railo/wiki/Configuration:web.xml##wiki-REST" target="_blank">instructions</a> to enable REST.
			</div>
		</cfif>	
	</cfif>
	
	<table>
		<tr>
			<td valign="top" width="65%">
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
				<script type="text/javascript">
					function updateBindError()
					{
						//console.log(arguments);
						$('##updateinfo').after('<div class=""error"">Update info could not be retrieved</div>');
					}
				</script>
				<h2 id="updateinfo">Update Info</h2>
				<!--- Update Infoupdate.cfm?#session.urltoken#&adminType=#request.admintype# --->
				<cfdiv onBindError="updateBindError"
				bind="url:update.cfm?adminType=#request.admintype#" bindonload="true" id="updateInfo"/>

				<!--- Memory Usage --->
				<cftry>
					<cfsavecontent variable="memoryInfo">
						<h2>Memory Usage</h2>
						
						#printMemory(getmemoryUsage("heap"))#

						#printMemory(getmemoryUsage("non_heap"))#
					</cfsavecontent>
					#memoryInfo#
					<cfcatch></cfcatch>
				</cftry>
	
				<!--- Support --->
				<h2>#stText.Overview.Support#</h2>
				<div class="txt">
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
					<div class="comment">#stText.Overview.bookDesc#</div>
					
					<!--- Blog --->
					<h3>
						<a href="http://blog.getrailo.com/" target="_blank">#stText.Overview.blog#</a>
					</h3>
					<div class="comment">#stText.Overview.bookDesc#</div>
					
					
					
					<!--- Twitter --->
					<h3>
						<a href="https://twitter.com/##!/railo" target="_blank">#stText.Overview.twitter#</a>
					</h3>
					<div class="comment">#stText.Overview.twitterDesc#</div>
				</div>
			</td>
		</tr>
	</table>
	
	<cfif request.admintype EQ "server">
		<cfadmin 
			action="getContexts"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			returnVariable="rst">
	
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
					<cfloop query="rst">
						<tr>
							<td>
								<input type="hidden" name="hash_#rst.currentrow#" value="#rst.hash#"/>
								<input type="text" style="width:99%" name="label_#rst.currentrow#" value="#rst.label#"/>
							</td>
							<td><cfif len(rst.url)><a target="_blank" href="#rst.url#/railo-context/admin/web.cfm">#rst.url#</a></cfif></td>
							<td><input type="text" class="xlarge" name="path_#rst.currentrow#" value="#rst.path#" readonly="readonly"/></td>
							<td><input type="text" class="xlarge" style="width:99%" name="cf_#rst.currentrow#" value="#rst.config_file#" readonly="readonly"/></td>
						</tr>
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
