<cfcomponent extends="Debug" output="no">

	<cfscript>
		fields=array(
		group("Execution Time","Execution times for templates, includes, modules, custom tags, and component method calls. Template execution times over this minimum highlight time appear in red.",3)
		,field("Minimal Execution Time","minimal","0",true,{_appendix:"microseconds",_bottom:"Execution times for templates, includes, modules, custom tags, and component method calls. Outputs only templates taking longer than the time (in microseconds) defined above."},"text40")
		,field("Highlight","highlight","250000",true,{_appendix:"microseconds",_bottom:"Highlight templates taking longer than the following (in microseconds) in red."},"text50")
		,group("Custom Debugging Output","Define what is outputted",3)
		,field("Database Activity","database","Enabled",false,
			"Select this option to show the database activity for the SQL Query events and Stored Procedure events in the debugging output."
			,"checkbox","Enabled")
		,field("Exceptions","exception","Enabled",false,
			"Select this option to output all exceptions raised for the request. "
			,"checkbox","Enabled")
		,field("Tracing","tracing","Enabled",false,
			"Select this option to show trace event information. Tracing lets a developer track program flow and efficiency through the use of the CFTRACE tag."
			,"checkbox","Enabled")
		,field("Timer","timer","Enabled",false,
			"Select this option to show timer event information. Timers let a developer track the execution time of the code between the start and end tags of the CFTIMER tag. "
			,"checkbox","Enabled")
		,field("Implicit variable Access","implicitAccess","Enabled",false,
			"Select this option to show all accesses to scopes, queries and threads that happens implicit (cascaded). "
			,"checkbox","Enabled")
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

		variables.cookieName = "railo_debug_modern";

		variables.scopeNames = [ "Application", "CGI", "Cookie", "Form", "Request", "Server", "Session", "URL" ];
		variables.otherNames = [ "ImpAccess", "ExecTime", "Exceptions", "Info", "Query", "Timer", "Trace" ];

		variables.allSections = buildSectionStruct();

		function buildSectionStruct() {

			var i = 0;

			var result = {};

			for ( var k in variables.scopeNames ) 
				result[ k ] = 2 ^ i++;

			for ( var k in variables.otherNames ) 
				result[ k ] = 2 ^ i++;

			return result;
		}

		private function isSectionOpen( string name ) {

			var cookieValue = structKeyExists( Cookie, variables.cookieName ) ? Cookie[ variables.cookieName ] : 0;

			return ( bitAnd( cookieValue, variables.allSections[ name ] ) );
		}


		private function isEnabled( custom, key ) {
		
			return structKeyExists( arguments.custom, key ) && ( arguments.custom[ arguments.key ] == "Enabled" || arguments.custom[ arguments.key ] == "true");
		}
		
	</cfscript>
 
	<cffunction name="output" returntype="void">
		<cfargument name="custom" type="struct" required="yes" />
		<cfargument name="debugging" required="true" type="struct" />
		<cfargument name="context" type="string" default="web" />

		<cfsilent>
			<cfif !structKeyExists(arguments.custom,'minimal')><cfset arguments.custom.minimal="0"></cfif>
			<cfif !structKeyExists(arguments.custom,'highlight')><cfset arguments.custom.highlight="250000"></cfif>
			<cfif !structKeyExists(arguments.custom,'scopes')><cfset arguments.custom.scopes=false></cfif>
			<cfif !structKeyExists(arguments.custom,'tracing')><cfset arguments.custom.tracing="Enabled"></cfif>
			<cfif !structKeyExists(arguments.custom,'timer')><cfset arguments.custom.timer="Enabled"></cfif>
			<cfif !structKeyExists(arguments.custom,'implicitAccess')><cfset arguments.custom.implicitAccess="Enabled"></cfif>
			<cfif !structKeyExists(arguments.custom,'exception')><cfset arguments.custom.exception="Enabled"></cfif>
			<cfif !structKeyExists(arguments.custom,'database')><cfset arguments.custom.database="Enabled"></cfif>
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
		</cfsilent>
		<cfif arguments.context EQ "web">
			</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
		</cfif>
		<cfoutput>
			<style type="text/css">
				
		
				.tblHead{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##f2f2f2;color:##3c3e40} 
				.tblContent {padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##ffffff;} 
				

				##-railo-err 			{ border: 1px dashed ##CCC; padding: 0.5em; }

				##-railo-err, ##-railo-err td	{ font-family: 'Helvetica Neue', Arial, Helvetica, sans-serif; font-size: 9pt; }
				##-railo-err table		{ empty-cells: show; }
				
				##-railo-err .section-title	{ margin-top: 1em; font-size: 12pt; font-weight: normal; color:##007bb7; }
				##-railo-err .section-title:first-child	{ margin-top: auto; }
				##-railo-err td.label	{ white-space: nowrap; vertical-align: top; }

				##-railo-err .collapsed	{ display: none; }
				##-railo-err .expanded 	{ display: block; }

				##-railo-err .bold 		{ font-weight: bold; }
				##-railo-err .right 	{ text-align: right; }
				##-railo-err tr.red td, ##-railo-err .red 	{ color: red; }

				.-railo-icon-plus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==)
			    					no-repeat left center; padding: 4px 0 4px 16px; }

				.-railo-icon-minus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7)
									no-repeat left center; padding: 4px 0 4px 16px; }

				.pad 	{ padding-left: 16px; }
			</style>

			<script>


				var __RAILO = __RAILO || {};

				__RAILO.cookies = {


					get: 	function( name, defaultValue ) {

						var cookies = document.cookie.split( '; ' );
						var len = cookies.length;

						for ( var i=0; i<len; i++ ) {

							var parts = cookies[ i ].split( '=' );

							if ( parts[ 0 ] == name )
								return unescape( parts[ 1 ] );
						}

						return defaultValue;
					}


					, set: 	function( name, value, expires ) {

						document.cookie = name + "=" + escape( value ) + ( (expires) ? "; expires=" + expires.toGMTString() : "" );
					}

					, remove: 	function( name ) {

						__RAILO.cookies.set( name, "", new Date( 0 ) );
					}
				};



				__RAILO.debugModern = {

					cookieName: 	"#variables.cookieName#"

					, bitmaskAll: 	Math.pow( 2, 31 ) - 1

					, allSections: 	#serializeJSON( variables.allSections )#

					, openSection: 	function( name ) {

						var value = __RAILO.cookies.get( __RAILO.debugModern.cookieName, 0 ) | __RAILO.debugModern.allSections[ name ];

						__RAILO.cookies.set( __RAILO.debugModern.cookieName, value );

						return value;
					}

					, closeSection: 	function( name ) {

						var value = __RAILO.cookies.get( __RAILO.debugModern.cookieName, 0 ) & ( __RAILO.debugModern.bitmaskAll - __RAILO.debugModern.allSections[ name ] );

						if ( value > 0 )
							__RAILO.cookies.set( __RAILO.debugModern.cookieName, value );
						else
							__RAILO.cookies.remove( __RAILO.debugModern.cookieName );

						return value;
					}

					, toggleSection: function( name ) {

						var btnClass = document.getElementById( "-railo-debug-btn-" + name ).attributes[ 'class' ];	// bracket-notation required for IE<9

						var objClass = document.getElementById( "-railo-debug-" + name ).attributes[ 'class' ];

						var isOpen = ( __RAILO.cookies.get( __RAILO.debugModern.cookieName, 0 ) & __RAILO.debugModern.allSections[ name ] ) > 0;

						if ( isOpen ) {

							btnClass.value = '-railo-icon-plus';
							objClass.value = 'collapsed';

							__RAILO.debugModern.closeSection( name );
						} else {

							btnClass.value = '-railo-icon-minus';
							objClass.value = 'expanded';

							__RAILO.debugModern.openSection( name );
						}
					}
				};
			</script>

			

			<div id="-railo-err">

			
				<!--- General --->
				<cfif isEnabled(arguments.custom,'general')>

					<div class="section-title">Debugging Information</div>

					<cfset sectionId = "Info">
					<cfset isOpen = isSectionOpen( sectionId )>
					<table>

						<cfset renderSectionHeadTR( sectionId, "Template:", "#_cgi.SCRIPT_NAME# (#expandPath(_cgi.SCRIPT_NAME)#)" )>

						<tr>
							<td class="pad label">User Agent:</td>
							<td class="pad">#_cgi.http_user_agent#</td>
						</tr>
						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

								<tr>
									<td class="label" colspan="2">
										#server.coldfusion.productname# 
										<cfif StructKeyExists(server.railo,'versionName')>(<a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a>)
										</cfif>
										#ucFirst(server.coldfusion.productlevel)# #uCase(server.railo.state)# #server.railo.version# (CFML Version #server.ColdFusion.ProductVersion#) 
									</td>
								</tr>
								<tr>
									<td class="label">Time Stamp</td>
									<td class="cfdebug">#LSDateFormat(now())# #LSTimeFormat(now())#</td>
								</tr>
								<tr>
									<td class="label">Time Zone</td>
									<td class="cfdebug">#getTimeZone()#</td>
								</tr>
								<tr>
									<td class="label">Locale</td>
									<td class="cfdebug">#ucFirst(GetLocale())#</td>
								</tr>
								<tr>
									<td class="label">Remote IP</td>
									<td class="cfdebug">#_cgi.remote_addr#</td>
								</tr>
								<tr>
									<td class="label">Host Name</td>
									<td class="cfdebug">#_cgi.server_name#</td>
								</tr>
								<cfif StructKeyExists(server.os,"archModel") and StructKeyExists(server.java,"archModel")>
									<tr>
										<td class="label">Architecture</td>
										<td class="cfdebug">
											<cfif server.os.archModel NEQ server.os.archModel>
												OS #server.os.archModel#bit/JRE #server.java.archModel#bit
											<cfelse>
												#server.os.archModel#bit
											</cfif>
										</td>
									</tr>
								</cfif>
								
							</td></tr></table>
						</tr>
					</table>
				</cfif>
				
				
				
			<!--- Execution Time --->
				<cfset sectionId = "ExecTime">
				<cfset isOpen = isSectionOpen( sectionId )>

				<div class="section-title"><a name="cfdebug_execution">Execution Time</a></div>
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

				<table>

					<cfset renderSectionHeadTR( sectionId, formatUnit( arguments.custom.unit, loa ), "Startup/Compiling" )>

					<tr>
						<td class="pad right">#formatUnit( arguments.custom.unit, tot-q-loa )#</td>
						<td class="pad">Application</td>
					</tr>
					<tr>
						<td class="pad right">#formatUnit( arguments.custom.unit, q )#</td>
						<td class="pad">Query</td>
					</tr>
					<tr>
						<td class="pad right bold">#formatUnit( arguments.custom.unit, tot )#</td>
						<td class="pad bold">Total</td>
					</tr>
					<tr>
						<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

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
								<cfset tot=tot+pages.total>
								<cfset q=q+pages.query>
								<cfif pages.avg LT arguments.custom.minimal * 1000>
									<cfcontinue>
								</cfif>
								<cfset bad=pages.avg GTE arguments.custom.highlight * 1000>
								<cfset loa=loa+pages.load>
								<tr #bad ? 'class="red"' : ''#>
									<td align="right" class="tblContent" nowrap>#formatUnit(arguments.custom.unit, pages.total-pages.load)#</td>
									<td align="right" class="tblContent" nowrap>#formatUnit(arguments.custom.unit, pages.avg)#</td>
									<td align="center" class="tblContent" nowrap>#pages.count#</td>
									<td align="left" class="tblContent" nowrap>#pages.src# <!-- [#pages.id#] !--></td>
								</tr>
							</cfloop>
							<tr><td colspan="2" class="red">red = over #formatUnit( arguments.custom.unit, arguments.custom.highlight * 1000 )# average execution time</td></tr>

						</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
					</tr>
				</table>



				<!--- Exceptions --->
				<cfif structKeyExists( arguments.debugging,"exceptions" ) && arrayLen( arguments.debugging.exceptions )>

					<cfset sectionId = "Exceptions">
					<cfset isOpen = isSectionOpen( sectionId )>

					<div class="section-title">Caught Exceptions</div>
					<table>

						<cfset renderSectionHeadTR( sectionId, arrayLen(arguments.debugging.exceptions), "Exception#arrayLen( arguments.debugging.exceptions ) GT 1 ? 's' : ''# Caught" )>

						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

								<tr>
									<td class="tblHead">Type</td>
									<td class="tblHead">Message</td>
									<td class="tblHead">Detail</td>
									<td class="tblHead">Template</td>
								</tr>
								<cfloop array="#arguments.debugging.exceptions#" index="local.exp">
									<tr>
										<td class="tblContent" nowrap>#exp.type#</td>
										<td class="tblContent" nowrap>#exp.message#</td>
										<td class="tblContent" nowrap>#exp.detail#</td>
										<td class="tblContent" nowrap>#exp.TagContext[1].template#:#exp.TagContext[1].line#</td>
									</tr>
								</cfloop>
								
							</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>
				</cfif>



				<!--- Implicit variable Access --->
				<cfif implicitAccess.recordcount>

					<cfset sectionId = "ImpAccess">
					<cfset isOpen = isSectionOpen( sectionId )>

					<div class="section-title">Implicit Variable Access</div>

					<table>

						<cfset renderSectionHeadTR( sectionId, implicitAccess.recordcount, "Implicit Variable Access#( implicitAccess.recordcount GT 1 ) ? 'es' : ''#" )>

						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

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
										<td align="right" class="tblContent" nowrap>#implicitAccess.line#</td>
										<td align="left" class="tblContent" nowrap>#implicitAccess.name#</td>
										<td align="right" class="tblContent" nowrap>#implicitAccess.count#</td>
									</tr>
								</cfloop>
								
							</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>
				</cfif>



				<!--- Timers --->
				<cfif timers.recordcount>

					<cfset sectionId = "Timer">
					<cfset isOpen = isSectionOpen( sectionId )>

					<div class="section-title">CFTimer Times</div>

					<table>

						<cfset renderSectionHeadTR( sectionId, timers.recordcount, "Timer#( timers.recordcount GT 1 ) ? 's' : ''# Set" )>

						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

								<tr>
									<td class="tblHead" align="center">Label</td>
									<td class="tblHead">Time</td>
									<td class="tblHead">Template</td>
								</tr>
								<cfloop query="timers">
									<tr>
										<td align="right" class="tblContent" nowrap>#timers.label#</td>
										<td align="right" class="tblContent" nowrap>#formatUnit( arguments.custom.unit, timers.time * 1000000 )#</td>
										<td align="right" class="tblContent" nowrap>#timers.template#</td>
									</tr>
								</cfloop>
								
							</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>
				</cfif>



				<!--- Traces --->
				<cfif traces.recordcount>

					<cfset sectionId = "Trace">
					<cfset isOpen = isSectionOpen( sectionId )>

					<div class="section-title">Trace Points</div>
					
					<cfset hasAction=!isColumnEmpty(traces,'action') />
					<cfset hasCategory=!isColumnEmpty(traces,'category') />
					
					<table>

						<cfset renderSectionHeadTR( sectionId, traces.recordcount, "Trace Point#( traces.recordcount GT 1 ) ? 's' : ''#" )>

						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

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
								
							</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>

				</cfif>



				<!--- Queries --->
				<cfif queries.recordcount>

					<cfset sectionId = "Query">
					<cfset isOpen = isSectionOpen( sectionId )>

					<cfset local.total=0>
					<cfset local.records=0>

					<cfloop query="queries">
						<cfset total+=queries.time>
						<cfset records+=queries.count>
					</cfloop>
					

					<div class="section-title">SQL Queries</div>
					<table>
						
						<cfset renderSectionHeadTR( sectionId, queries.recordcount, "Quer#queries.recordcount GT 1 ? 'ies' : 'y'# Executed" )>

						<tr>
							<td nowrap class="pad right">#formatUnit(total, queries.time)#</td>
							<td class="pad">Total Execution Time</td>
						</tr>
						<tr>
							<td nowrap class="pad right">#records#</td>
							<td class="pad">Total Records</td>
						</tr>
						<tr>
							<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

								<cfloop query="queries">

									<div style="margin-top: 4px; padding: 0.5em; border: 1px dashed ##CCC;">

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
													<cfset lstNeverRead=ListAppend(lstNeverRead,item, ',') />
												<cfelse>
													<cfset lstRead=ListAppend(lstRead,item, ',') />
												</cfif>
											</cfloop>
											<b>Query usage within the request:</b>
											<br>
											<cfif len(lstRead)>
												<font color="green">
													Used Columns: <b>#replace( lstRead, ',', ', ', 'all' )#</b>
												</font>
												<br>
											</cfif>
											<cfif len(lstNeverRead)>
												<font color="red">
													Unused Columns: <b>#replace( lstNeverRead, ',', ', ', 'all' )#</b>
													<br>
													Usage: <b>#numberFormat(listLen(lstRead)/(listLen(lstRead)+listLen(lstNeverRead))*100, "999.9")# %</b>
												</font>
											</cfif>
										</cfif>
										<br><b>SQL:</b>
										<pre>#rtrim( queries.sql )#</pre>

									</div>
								</cfloop>
								
							</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>

				</cfif>

				
				
				<!--- Scopes --->
				<cfif isEnabled(arguments.custom,"scopes")>					

					<cfset local.scopes = variables.scopeNames>			
				
					<div class="section-title">Scope Information</div>
					<table class="tbl" cellpadding="0" cellspacing="0">
						
						<cfloop array="#local.scopes#" index="local.k">
							
							<cfset sectionId = k>
							<cfset isOpen = isSectionOpen( sectionId )>

							<cfset local.v = evaluate( k )>
							<cfset local.sc = structCount(v)>

							<cftry>

								<cfset local.estSize = byteFormat( sc==0 ? 0 : sizeOf( v ) )>

								<cfcatch><cfset local.estSize = "not available"></cfcatch>
							</cftry>						

							<tr><td style="height:0.5em;"></td></tr>
							<cfset renderSectionHeadTR( sectionId, "<b>#k# Scope</b> #sc ? '(Estimated Size: #estSize#)' : '(Empty)' #" )>

							<tr><td colspan="3">

								<table id="-railo-debug-#sectionId#" class="#isOpen ? 'expanded' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

									<cfif isOpen>
										<cftry><cfdump var="#v#" keys="1000" label="#sc GT 1000?"First 1000 Records":""#"><cfcatch>not available</cfcatch></cftry>
									<cfelse>
										the Scope will be displayed with the next request
									</cfif>

								</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
							
							</td></tr>
						</cfloop>
								
					</table>
				</cfif>
			

			</div>	<!--- id="-railo-err" !--->
		</cfoutput>

	</cffunction>


	<cffunction name="renderSectionHeadTR" output="#true#">
		
		<cfargument name="sectionId">
		<cfargument name="label1">
		<cfargument name="label2" default="">

		<tr>
			<td><a id="-railo-debug-btn-#sectionId#" class="-railo-icon-#isOpen ? 'minus' : 'plus'#" style="cursor: pointer;" onclick="__RAILO.debugModern.toggleSection( '#sectionId#' );">
				#label1#</a></td>
			<td class="pad"><a style="cursor: pointer;" onclick="__RAILO.debugModern.toggleSection( '#sectionId#' );">#label2#</a></td>
		</tr>
	</cffunction>

 
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
