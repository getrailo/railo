<cfcomponent extends="Debug" output="no">

	<cfscript>
		fields=array(
			  group("Execution Time","Execution times for templates, includes, modules, custom tags, and component method calls. Template execution times over this minimum highlight time appear in red.",3)
			, field("Minimal Execution Time","minimal","0",true,{_appendix:"microseconds",_bottom:"Execution times for templates, includes, modules, custom tags, and component method calls. Outputs only templates taking longer than the time (in microseconds) defined above."},"text40")
			, field("Highlight","highlight","250000",true,{_appendix:"microseconds",_bottom:"Highlight templates taking longer than the following (in microseconds) in red."},"text50")
			, group("Custom Debugging Output","Define what is outputted",3)
			, field("Scope Variables","scopes","Enabled",false,"Enable Scope reporting","checkbox","Enabled")
			, field("General Debug Information ","general","Enabled",false,
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

		private function isSectionOpen( string name ) {

			if ( name == "ALL" && !structKeyExists( Cookie, variables.cookieName ) )
				return true;

			var cookieValue = structKeyExists( Cookie, variables.cookieName ) ? Cookie[ variables.cookieName ] : 0;

			return cookieValue && ( bitAnd( cookieValue, variables.allSections[ name ] ) );
		}


		private function isEnabled( custom, key ) {
		
			return structKeyExists( arguments.custom, key ) && ( arguments.custom[ arguments.key ] == "Enabled" || arguments.custom[ arguments.key ] == "true" );
		}


		variables.cookieName = "railo_debug_modern";

		variables.scopeNames = [ "Application", "CGI", "Client", "Cookie", "Form", "Request", "Server", "Session", "URL" ];

		variables.allSections = buildSectionStruct();

		private function buildSectionStruct() {

			var otherSections = [ "ALL", "ImpAccess", "ExecTime", "ExecOrder", "Exceptions", "Info", "Query", "Timer", "Trace" ];
			var i = 0;

			var result = {};

			for ( var k in otherSections )
				result[ k ] = 2 ^ i++;

			for ( var k in variables.scopeNames )
				result[ k ] = 2 ^ i++;

			return result;
		}
	</cfscript>
 
	<cffunction name="output" returntype="void">
		<cfargument name="custom" type="struct" required="yes" />
		<cfargument name="debugging" required="true" type="struct" />
		<cfargument name="context" type="string" default="web" />

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

		<cfset var isExecOrder = isSectionOpen( "ExecOrder" )>

		<cfif isExecOrder>
			
			<cfset querySort(pages,"id","asc") />
		<cfelse>

			<cfset querySort(pages,"avg","desc") />
		</cfif>
		
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
		
		<cfif arguments.context EQ "web">
			</td></td></td></th></th></th></tr></tr></tr></table></table></table></a></abbrev></acronym></address></applet></au></b></banner></big></blink></blockquote></bq></caption></center></cite></code></comment></del></dfn></dir></div></div></dl></em></fig></fn></font></form></frame></frameset></h1></h2></h3></h4></h5></h6></head></i></ins></kbd></listing></map></marquee></menu></multicol></nobr></noframes></noscript></note></ol></p></param></person></plaintext></pre></q></s></samp></script></select></small></strike></strong></sub></sup></table></td></textarea></th></title></tr></tt></u></ul></var></wbr></xmp>
		</cfif>
		

			<style type="text/css">
		
				#-railo-debug 			{ margin: 2.5em 1em 0 1em; padding: 1em; border: 1px solid #CCC; border-radius: 5px; background-color: #FFF; color: #222; }
				#-railo-debug legend 	{ padding: 0 1em; background-color: #FFF; color: #222; }

				#-railo-debug, #-railo-debug td	{ font-family: Helvetica, Arial, sans-serif; font-size: 9pt; line-height: 1.35; }
				#-railo-debug.large, #-railo-debug.large td	{ font-size: 10pt; }
				#-railo-debug.small, #-railo-debug.small td	{ font-size: 8.5pt; }

				#-railo-debug table		{ empty-cells: show; }				
				#-railo-debug table.details	{ margin-top: 0.5em; border: 1px solid #999; margin-left: 9pt; }
				
				#-railo-debug table.details th { border:1px solid #e0e0e0; font-size: 0.9em; font-weight: normal; background-color: #f2f2f2; color: #3c3e40; }
				#-railo-debug table.details td, #-railo-debug table.details th { padding: 2px 5px; }
				#-railo-debug table.details td	{ border-bottom: 1px solid #e0e0e0; }
				#-railo-debug table.details tr:last-child td { border-bottom: 0; }
				
				#-railo-debug .section-title	{ margin-top: 1.25em; font-size: 1.25em; font-weight: normal; color:#007bb7; }
				#-railo-debug .section-title:first-child	{ margin-top: auto; }
				#-railo-debug .label		{ white-space: nowrap; vertical-align: top; text-align: right; padding-right: 1em; }

				#-railo-debug .collapsed	{ display: none; }

				#-railo-debug .bold 		{ font-weight: bold; }
				#-railo-debug .txt-c 	{ text-align: center; }
				#-railo-debug .txt-r 	{ text-align: right; }
				#-railo-debug .num-lsv 	{ font-weight: normal; }
				#-railo-debug tr.nowrap td { white-space: nowrap; }
				#-railo-debug tr.red td, #-railo-debug .red 	{ background-color: #FDD; }

				#-railo-debug .underline { text-decoration: underline; }
				#-railo-debug .underline.selected, .underline:hover { background-color: #222; color: #FFF; }

				.-railo-icon-plus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==)
			    					no-repeat left center; padding: 4px 0 4px 16px; }

				.-railo-icon-minus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7)
									no-repeat left center; padding: 4px 0 4px 16px; }

				.pad 	{ padding-left: 16px; }
			</style>

			<script>

				var __RAILO = __RAILO || {};

				__RAILO.util = 	{

					getCookie: 			function( name, def ) {

						var cookies = document.cookie.split( '; ' );
						var len = cookies.length;
						var parts;

						for ( var i=0; i<len; i++ ) {

							parts = cookies[ i ].split( '=' );

							if ( parts[ 0 ] == name )
								return unescape( parts[ 1 ] );
						}

						return def;
					}

					, getCookieNames:	function() {

						var result = [];

						var cookies = document.cookie.split( '; ' );
						var len = cookies.length;
						var parts;

						for ( var i=0; i<len; i++ ) {

							parts = cookies[ i ].split( '=' );

							result.push( parts[ 0 ] );
						}

						return result;
					}

					, setCookie: 		function( name, value, expires ) {

						document.cookie = name + "=" + escape( value ) + ( (expires) ? "; expires=" + expires.toGMTString() : "" );
					}

					, removeCookie: 	function( name ) {

						__RAILO.util.setCookie( name, "", new Date( 0 ) );
					}


					/** returns the element if it is an object, or finds the object by id */
					, getDomObject: 	function( obj ) {

						if ( typeof obj == 'string' || obj instanceof String )
							return document.getElementById( obj );

						return obj;
					}

					, hasCssClass: 		function( obj, cls ) {

						obj = __RAILO.util.getDomObject( obj );

						return ( obj.className.indexOf( cls ) > -1 );
					}

					, addCssClass: 		function( obj, cls ) {

						if ( __RAILO.util.hasCssClass( obj, cls ) )
							return;

						obj = __RAILO.util.getDomObject( obj );

						obj.className += " " + cls;
					}

					, removeCssClass: 	function( obj, cls ) {

						obj = __RAILO.util.getDomObject( obj );

						obj.className = obj.className.replace( cls, "" );
					}

					, toggleCssClass: 	function( obj, cls ) {	// returns true if class was added

						obj = __RAILO.util.getDomObject( obj );

						if ( __RAILO.util.hasCssClass( obj, cls ) )
							__RAILO.util.removeCssClass( obj, cls );
						else
							__RAILO.util.addCssClass( obj, cls );

						return ( __RAILO.util.hasCssClass( obj, cls ) );
					}
				};


				__RAILO.debug = {

					<cfoutput>
					  cookieName: 	"#variables.cookieName#"
					, bitmaskAll: 	Math.pow( 2, 31 ) - 1
					, allSections: 	#serializeJSON( variables.allSections )#
					</cfoutput>

					, setFlag: 		function( name ) {

						var value = __RAILO.util.getCookie( __RAILO.debug.cookieName, 0 ) | __RAILO.debug.allSections[ name ];

						__RAILO.util.setCookie( __RAILO.debug.cookieName, value );

						return value;
					}

					, clearFlag: 	function( name ) {

						var value = __RAILO.util.getCookie( __RAILO.debug.cookieName, 0 ) & ( __RAILO.debug.bitmaskAll - __RAILO.debug.allSections[ name ] );

						__RAILO.util.setCookie( __RAILO.debug.cookieName, value );

						return value;
					}

					, toggleSection: 	function( name ) {

						var btn = __RAILO.util.getDomObject( "-railo-debug-btn-" + name );
						var obj = __RAILO.util.getDomObject( "-railo-debug-" + name )

						var isOpen = ( __RAILO.util.getCookie( __RAILO.debug.cookieName, 0 ) & __RAILO.debug.allSections[ name ] ) > 0;

						if ( isOpen ) {

							__RAILO.util.removeCssClass( btn, '-railo-icon-minus' );
							__RAILO.util.addCssClass( btn, '-railo-icon-plus' );

							__RAILO.util.addCssClass( obj, 'collapsed' );

							__RAILO.debug.clearFlag( name );
						} else {

							__RAILO.util.removeCssClass( btn, '-railo-icon-plus' );
							__RAILO.util.addCssClass( btn, '-railo-icon-minus' );

							__RAILO.util.removeCssClass( obj, 'collapsed' );

							__RAILO.debug.setFlag( name );
						}
					}

					, selectText: 	function( id ) {

				        if ( document.selection ) {

				            var range = document.body.createTextRange();
				            range.moveToElementText( document.getElementById( id ) );
				            range.select();
				        } else if ( window.getSelection ) {

				            var range = document.createRange();
				            range.selectNode( document.getElementById( id ) );
				            window.getSelection().addRange( range );
				        }
				    }
				};
			</script>

		<cfoutput>

			<!-- Railo Debug Output !-->
			<fieldset id="-railo-debug" class="#arguments.custom.size#">

				<cfset sectionId = "ALL">
				<cfset isOpen = isSectionOpen( sectionId )>
				<legend><a id="-railo-debug-btn-#sectionId#" class="-railo-icon-#isOpen ? 'minus' : 'plus'#" style="cursor: pointer;" onclick="__RAILO.debug.toggleSection( '#sectionId#' );">
				 Railo Debug Output</a></legend>


				<div id="-railo-debug-ALL" class="#isOpen ? '' : 'collapsed'#">


					<!--- General --->
					<cfif isEnabled( arguments.custom, 'general' )>

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
								<td colspan="2" id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table style="margin-left: 14px;">
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
									</table>
								</td>
							</tr>
						</table>
					</cfif>
					
					
					<!--- Execution Time --->
					<cfset sectionId = "ExecTime">
					<cfset isOpen = isSectionOpen( sectionId )>
					
					<div class="section-title">Execution Time</div>
					<cfset local.loa=0>
					<cfset local.tot=0>
					<cfset local.q=0>
					
					<cfloop query="pages">
						<cfset tot=tot+pages.total>
						<cfset q=q+pages.query>
						<cfif pages.avg LT arguments.custom.minimal*1000>
							<cfcontinue>
						</cfif>
						<cfset local.bad=pages.avg GTE arguments.custom.highlight*1000>
						<cfset loa=loa+pages.load />
					</cfloop>

					<table>

						<cfset renderSectionHeadTR( sectionId
							, "#unitFormat( arguments.custom.unit, tot-q-loa, true )# 
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Application" )>

						<tr><td><table>
							<tr>
								<td class="pad txt-r">#unitFormat( arguments.custom.unit, loa )#</td>
								<td class="pad">Startup/Compilation</td>
							</tr>
							<tr>
								<td class="pad txt-r">#unitFormat( arguments.custom.unit, q )#</td>
								<td class="pad">Query</td>
							</tr>
							<tr>
								<td class="pad txt-r bold">#unitFormat( arguments.custom.unit, tot, true )#</td>
								<td class="pad bold">Total</td>
							</tr>
						</table></td></tr>

						<tr>
							<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
								<table class="details">

									<tr>
										<th>Total Time</th>
										<th><cfif isExecOrder><a onclick="__RAILO.debug.clearFlag( 'ExecOrder' ); __RAILO.util.addCssClass( this, 'selected' );" class="underline" style="cursor: pointer;" title="Order by Avg Time (starting with the next request)">Avg Time</a><cfelse>Avg Time</cfif></th>
										<th>Count</th>
										<th>Template</th>
										<th><cfif isExecOrder>ID<cfelse><a onclick="__RAILO.debug.setFlag( 'ExecOrder' ); __RAILO.util.addCssClass( this, 'selected' );" class="underline" style="cursor: pointer;" title="Order by ID (starting with the next request)">ID</a></cfif></th>
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
										<tr class="nowrap #bad ? 'red' : ''#">
											<td class="txt-r" title="#pages.total - pages.load#">#unitFormat(arguments.custom.unit, pages.total-pages.load)#</td>
											<td class="txt-r" title="#pages.avg#">#unitFormat(arguments.custom.unit, pages.avg)#</td>
											<td class="txt-c">#pages.count#</td>
											<td id="-railo-debug-pages-#pages.currentRow#" oncontextmenu="__RAILO.debug.selectText( this.id );">#pages.src#</td>
											<td class="txt-r" style="color: ##999;" title="#pages.id#">#pages.id % 10000#</td>
										</tr>
									</cfloop>
									<tr class="red"><td colspan="3">red = over #unitFormat( arguments.custom.unit, arguments.custom.highlight * 1000 )# average execution time</td></tr>

								</table>
							</td>	<!--- id="-railo-debug-#sectionId#" !--->
						</tr>
					</table>


					<!--- Exceptions --->
					<cfif structKeyExists( arguments.debugging,"exceptions" ) && arrayLen( arguments.debugging.exceptions )>

						<cfset sectionId = "Exceptions">
						<cfset isOpen = isSectionOpen( sectionId )>

						<div class="section-title">Caught Exceptions</div>
						<table>

							<cfset renderSectionHeadTR( sectionId, "#arrayLen(arguments.debugging.exceptions)# Exception#arrayLen( arguments.debugging.exceptions ) GT 1 ? 's' : ''# Caught" )>

							<tr>
								<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table class="details">

										<tr>
											<th>Type</th>
											<th>Message</th>
											<th>Detail</th>
											<th>Template</th>
											<th>Line</th>
										</tr>
										<cfloop array="#arguments.debugging.exceptions#" index="local.exp">
											<tr class="nowrap">
												<td>#exp.type#</td>
												<td>#exp.message#</td>
												<td>#exp.detail#</td>
												<td>#exp.TagContext[1].template#</td>
												<td class="txt-r">#exp.TagContext[1].line#</td>
											</tr>
										</cfloop>
										
									</table>
								</td>	<!--- id="-railo-debug-#sectionId#" !--->
							</tr>
						</table>
					</cfif>


					<!--- Implicit variable Access --->
					<cfif implicitAccess.recordcount>

						<cfset sectionId = "ImpAccess">
						<cfset isOpen = isSectionOpen( sectionId )>

						<div class="section-title">Implicit Variable Access</div>

						<table>

							<cfset renderSectionHeadTR( sectionId, "#implicitAccess.recordcount# Implicit Variable Access#( implicitAccess.recordcount GT 1 ) ? 'es' : ''#" )>

							<tr>
								<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table class="details">

										<tr>
											<th>Scope</th>
											<th>Template</th>
											<th>Line</th>
											<th>Var</th>
											<th>Count</th>
										</tr>
										<cfset total=0 />
										<cfloop query="implicitAccess">
											<tr class="nowrap">
												<td>#implicitAccess.scope#</td>
												<td>#implicitAccess.template#</td>
												<td class="txt-r">#implicitAccess.line#</td>
												<td>#implicitAccess.name#</td>
												<td class="txt-r">#implicitAccess.count#</td>
											</tr>
										</cfloop>
									
									</table>
								</td>	<!--- id="-railo-debug-#sectionId#" !--->
							</tr>
						</table>
					</cfif>


					<!--- Timers --->
					<cfif timers.recordcount>

						<cfset sectionId = "Timer">
						<cfset isOpen = isSectionOpen( sectionId )>

						<div class="section-title">CFTimer Times</div>

						<table>

							<cfset renderSectionHeadTR( sectionId, "#timers.recordcount# Timer#( timers.recordcount GT 1 ) ? 's' : ''# Set" )>

							<tr>
								<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table class="details">

										<tr>
											<th align="center">Label</th>
											<th>Time</th>
											<th>Template</th>
										</tr>
										<cfloop query="timers">
											<tr class="nowrap">
												<td class="txt-r">#timers.label#</td>
												<td class="txt-r">#unitFormat( arguments.custom.unit, timers.time * 1000000 )#</td>
												<td class="txt-r">#timers.template#</td>
											</tr>
										</cfloop>
										
									</table>
								</td>	<!--- id="-railo-debug-#sectionId#" !--->
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

							<cfset renderSectionHeadTR( sectionId, "#traces.recordcount# Trace Point#( traces.recordcount GT 1 ) ? 's' : ''#" )>

							<tr>
								<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table class="details">

										<tr>
											<th>Type</th>
											<cfif hasCategory>
												<th>Category</th>
											</cfif>
											<th>Text</th>
											<th>Template</th>
											<th>Line</th>
											<cfif hasAction>
												<th>Action</th>
											</cfif>
											<th>Var</th>
											<th>Total Time</th>
											<th>Trace Slot Time</th>
										</tr>
										<cfset total=0 />
										<cfloop query="traces">
											<cfset total=total+traces.time />
											<tr class="nowrap">
												<td>#traces.type#</td>
												<cfif hasCategory>
													<td>#traces.category#&nbsp;</td>
												</cfif>
												<td>#traces.text#&nbsp;</td>
												<td>#traces.template#</td>
												<td class="txt-r">#traces.line#</td>
												<cfif hasAction>
													<td>#traces.action#</td>
												</cfif>
												<td>
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
												<td class="txt-r">#unitFormat(arguments.custom.unit, total)#</td>
												<td class="txt-r">#unitFormat(arguments.custom.unit, traces.time)#</td>
											</tr>
										</cfloop>
									
									</table>
								</td>	<!--- id="-railo-debug-#sectionId#" !--->
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
							
							<cfset renderSectionHeadTR( sectionId, "#queries.recordcount# Quer#queries.recordcount GT 1 ? 'ies' : 'y'# Executed (Total Records: #records#; Total Time: #unitFormat(total, queries.time)#)" )>

							<tr>
								<td id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table><tr><td>

										<cfloop query="queries">

											<table class="details">
												<tr>
													<th></th>
													<th>Name</th>
													<th>Records</th>
													<th>Time</th>
													<th>Datasource</th>
													<th>Source</th>
												</tr>
												<tr>
													<th></th>
													<td class="bold">#queries.name#</td>
													<td class="txt-r">#queries.count#</td>
													<td class="txt-r">#unitFormat(arguments.custom.unit, queries.time)#</td>
													<td>#queries.datasource#</td>
													<td>#queries.src#</td>
												</tr>
												<tr>
													<th class="label">SQL Code:</th>
													<td id="-railo-debug-query-sql-#queries.currentRow#" colspan="5" oncontextmenu="__RAILO.debug.selectText( this.id );"><pre>#trim( queries.sql )#</pre></td>
												</tr>

												<cfif listFindNoCase(queries.columnlist, 'usage') && isStruct(queries.usage)>

													<cfset local.usage=queries.usage>
													<cfset local.usageNotRead = []>
													<cfset local.usageRead  = []>

													<cfloop collection="#usage#" index="local.item" item="local.value">
														<cfif !value>
															<cfset arrayAppend( usageNotRead, item )>
														<cfelse>
															<cfset arrayAppend( usageRead, item )>
														</cfif>
													</cfloop>

													<tr>
														<th></th>
														<th colspan="5" style="text-align: left;">Query usage within the request:</th>
													</tr>

													<cfset local.arr = usageRead>
													<cfset local.arrLenU = arrayLen( arr )>
													<cfif arrLenU>
														<tr>
															<th class="label">Used:</th>
															<td>
																<cfloop from="1" to="#arrLenU#" index="local.ii">
																	#arr[ ii ]# <cfif ii LT arrLenU>, </cfif>
																</cfloop>
															</td>
														</tr>
													</cfif>
													<cfset local.arr = usageNotRead>
													<cfset local.arrLenN = arrayLen( arr )>
													<cfif arrLenN>
														<tr class="red">
															<th class="label">Unused:</th>
															<td>
																<cfloop from="1" to="#arrLenN#" index="local.ii">
																	#arr[ ii ]# <cfif ii LT arrLenN>, </cfif>
																</cfloop>
															</td>
														</tr>
														<tr class="red">
															<th></th>
															<td><b>#arrLenU ? numberFormat( arrLenU / ( arrLenU + arrLenN ) * 100, "999.9" ) : 100# %</b></td>
														</tr>
													</cfif>
												</cfif>

											</table>

										</cfloop>
									
									</tr></td></table>
								</td>	<!--- id="-railo-debug-#sectionId#" !--->
							</tr>
						</table>

					</cfif>
					
					
					<!--- Scopes --->
					<cfif isEnabled( arguments.custom, "scopes" )>

						<cfset local.scopes = variables.scopeNames>

						<cfset local.appSettings = getApplicationSettings()>
						<cfset local.isScopeEnabled = true>
					
						<div class="section-title">Scope Information</div>
						<table cellpadding="0" cellspacing="0">
							
							<cfloop array="#local.scopes#" index="local.k">

								<tr><td style="font-size: 4px;">&nbsp;</td></tr>
								
								<cfset sectionId = k>
								
								<cfswitch expression="#k#">
									
									<cfcase value="Client">
										
										<cfset isScopeEnabled = local.appSettings.clientManagement>
									</cfcase>
									<cfcase value="Session">
										
										<cfset isScopeEnabled = local.appSettings.sessionManagement>
									</cfcase>
									<cfdefaultcase>
										
										<cfset isScopeEnabled = true>
									</cfdefaultcase>
								</cfswitch>

								<cfif isScopeEnabled>
									
									<cfset isOpen = isSectionOpen( sectionId )>
									<cfset local.v = evaluate( k )>
									<cfset local.sc = structCount( v )>

									<cftry>

										<cfset local.estSize = byteFormat( sc == 0 ? 0 : sizeOf( v ) )>

										<cfcatch>

											<cfset local.estSize = "not available">
										</cfcatch>
									</cftry>
									
									<cfset renderSectionHeadTR( sectionId, "<b>#k# Scope</b> #sc ? '(~#estSize#)' : '(Empty)' #" )>

									<tr><td colspan="3">

										<table id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#" style="margin-left: 14px;"><tr><td>

											<cfif isOpen>
												<cftry><cfdump var="#v#" keys="1000" label="#sc GT 1000?"First 1000 Records":""#"><cfcatch>not available</cfcatch></cftry>
											<cfelse>
												the Scope will be displayed with the next request
											</cfif>

										</td></tr></table>	<!--- id="-railo-debug-#sectionId#" !--->
									</td></tr>
								<cfelse>

									<tr>
										<td style="padding-left: 16px; color: ##667;"><b>#k# Scope</b> (Not Enabled for this Application)</td>
									</tr>
								</cfif>							
							</cfloop>
									
						</table>
					</cfif>
				
				</div>	<!--- div#-railo-debug-ALL !--->

			</fieldset>	<!--- id="-railo-err" !--->
		</cfoutput>

	</cffunction>


	<cffunction name="renderSectionHeadTR" output="#true#">
		
		<cfargument name="sectionId">
		<cfargument name="label1">
		<cfargument name="label2" default="">

		<tr>
			<td><a id="-railo-debug-btn-#sectionId#" class="-railo-icon-#isOpen ? 'minus' : 'plus'#" style="cursor: pointer;" onclick="__RAILO.debug.toggleSection( '#sectionId#' );">
				#label1#</a></td>
			<td class="pad"><a style="cursor: pointer;" onclick="__RAILO.debug.toggleSection( '#sectionId#' );">#label2#</a></td>
		</tr>
	</cffunction>

	<cfscript>

		function unitFormat( string unit, numeric time, boolean prettify=false ) {

			var result = numberFormat( time / 1000000, "0.00" );

			if ( prettify )
				result = listFirst( result, '.' ) & "<span class='num-lsv'>." & listGetAt( result, 2, '.' ) & "</span>";

			return result & " ms";

			/*/ not sure what this confusing old impl was supposed to do; arguments.unit was ignored anyway!
			if ( arguments.time >= 100000000 )
				return int( arguments.time / 1000000 ) & " ms";

			if ( arguments.time >=  10000000 )
				return ( int( arguments.time / 100000 ) / 10 ) & " ms";

			if ( arguments.time >=   1000000 )
				return ( int( arguments.time / 10000 ) / 100 ) & " ms";

			return ( int( arguments.time / 1000 ) / 1000 ) & " ms";
			//*/
		}


		function byteFormat( numeric size ) {

			var values = [ [ 1099511627776, 'TB' ], [ 1073741824, 'GB' ], [ 1048576, 'MB' ], [ 1024, 'KB' ] ];

			for ( var i in values ) {

				if ( arguments.size >= i[ 1 ] )
					return numberFormat( arguments.size / i[ 1 ], '9.99' ) & i[ 2 ];
			}

			return arguments.size & 'B';
		}
	</cfscript>


</cfcomponent>