<cfparam name="url.print_js_resources" type="boolean" default="true" />
<cfparam name="url.toggledebug" type="boolean" default="true" />
<cfparam name="url.action" type="string" default="none" />
<cfset currentDirectory= getDirectoryFromPath(getCurrentTemplatePath())>
<cfsetting requesttimeout="3600"><!-- 1 hour -->

<cfscript>
string function toPackage(string directory){
	var currentDirectory= getDirectoryFromPath(getCurrentTemplatePath());
	return replace(replace(replace(arguments.directory,currentDirectory,''),'\','.','all'),'/','.','all');
}
string function toName(string name){
	return mid(arguments.name,1,len(arguments.name)-4);
}
function extFilter(path) {
	var name=listLast(path,'\/');
	var ext=listLast(name,'.');
	if(ext==name)ext="";
	return ext=="cfc" && name!="Application.cfc";
}

</cfscript>

<cfdirectory action="list" recurse="true"  sort="dateLastModified desc"  directory="./testcases" name="dir" filter="#extFilter#">
<cfoutput>
<h1>Jira Tickets</h1>
<ul>
	<li><a href="index.cfm?action=all">Run all tickets</a></li>
	<li><a href="index.cfm?action=latest">Run latest 10 tickets</a></li>
	<ul>
	<cfset count=0>
	<cfloop query="#dir#">
		<cftry>
			
			<cfset package=toPackage(dir.directory)>
			<cfset n=toName(dir.name)>
			<cfset meta=GetComponentMetadata("#package#.#n#")>
			<!---<cfif left(package,len("testcase-templates")) == "testcase-templates">
				<cfcontinue>
			</cfif>--->
			<cfif isDefined('meta.extends.fullname') and (meta.extends.fullname EQ "org.railo.cfml.test.RailoTestCase" or meta.extends.fullname EQ "mxunit.framework.TestCase")>
				<cfif ++count GT 10><cfbreak></cfif>
				<li>
					<a href="index.cfm?action=single&testcase=#package#.#n#">Run #package#.#n#</a>					
				</li>
			</cfif>
		<cfcatch>
			<cfdump var="#cfcatch#" label="#cfcatch.message#" expand="false">
		</cfcatch>
		</cftry>		
	</cfloop>
	</ul>
	<li>
		<form action="index.cfm">Run
		<input type="hidden" name="action" value="single">
		<input type="text" name="ticket" value="<cfif isNull(url.ticket)>tickets.Jira<cfelse>#url.ticket#</cfif>">
		<input type="submit" name="submit" value="Go">
		</form>
	</li>
</ul>
</cfoutput>
<cfif url.action EQ "list">
	

</cfif>


<cfscript>
if(url.action != "none") {
	
	
	testSuite = new org.railo.cfml.test.RailoTestSuite();
	//testSuite.maxTheads(20);
	
	if(url.action == "single") {
		cfcName=url.testcase;
		cfc=createObject('component',cfcName);
		testSuite.addAll(cfcName,cfc);
	}
	else if(url.action == "latest" || url.action == "all") {
		
		loop query="#dir#" endrow="#url.action=="latest"?10:1000000#" {
			package=toPackage(dir.directory);
			if(listLen(package,'.')>2) continue;
			n=toName(dir.name);
			
			cfcName=package&'.'&n;
			cfc=createObject('component',cfcName);
			meta=GetComponentMetadata(cfc);
			isTC=isDefined('meta.extends.fullname') and (meta.extends.fullname EQ "org.railo.cfml.test.RailoTestCase" or meta.extends.fullname EQ "mxunit.framework.TestCase");
			if(isTC)testSuite.addAll(cfcName,cfc);
		}
	}
	//testSuite.setMaxThreads(10);
	
	results = testSuite.run();
    // echo(results.getResultsOutput('html'));
    qry=results.getResultsOutput('query');
	
}
/*
TODO

overwrite TestResult.getHTMLResults();
testSuite.maxParallelThread(); // default 1
testSuite.addTestCase('...',1000); // timeout setting for the testcase, it should be possible to define this also on suite level 
*/

</cfscript>

<cfif !isNull(qry)>
<style type="text/css">
			#-railo-debug 			{ margin: 2.5em 1em 0 1em; padding: 1em; background-color: #FFF; color: #222; border: 1px solid #CCC; border-radius: 5px; text-shadow: none; }
			#-railo-debug.collapsed	{ padding: 0; border-width: 0; }
			#-railo-debug legend 	{ padding: 0 1em; background-color: #FFF; color: #222; }

			#-railo-debug, #-railo-debug td	{ font-family: Helvetica, Arial, sans-serif; font-size: 9pt; line-height: 1.35; }
			#-railo-debug.large, #-railo-debug.large td	{ font-size: 10pt; }
			#-railo-debug.small, #-railo-debug.small td	{ font-size: 8.5pt; }

			#-railo-debug table		{ empty-cells: show; border-collapse: collapse; border-spacing: 0; }
			#-railo-debug table.details	{ margin-top: 0.5em; border: 1px solid #ddd; margin-left: 9pt; max-width: 100%; }
			#-railo-debug table.details th { font-size: 9pt; font-weight: normal; background-color: #f2f2f2; color: #3c3e40; }
			#-railo-debug table.details td, #-railo-debug table.details th { padding: 2px 4px;  border: 1px solid #ddd; }

			#-railo-debug .section-title	{ margin-top: 1.25em; font-size: 1.25em; font-weight: normal; color:#555; }
			#-railo-debug .section-title:first-child	{ margin-top: auto; }
			#-railo-debug .label		{ white-space: nowrap; vertical-align: top; text-align: right; padding-right: 1em; background-color: inherit; color: inherit; text-shadow: none; }
			#-railo-debug .collapsed	{ display: none; }
			#-railo-debug .bold 		{ font-weight: bold; }
			#-railo-debug .txt-c 	{ text-align: center; }
			#-railo-debug .txt-l 	{ text-align: left; }
			#-railo-debug .txt-r 	{ text-align: right; }
			#-railo-debug .faded 	{ color: #999; }
			#-railo-debug .ml14px 	{ margin-left: 14px; }
			#-railo-debug table.details td.txt-r { padding-right: 1em; }
			#-railo-debug .num-lsv 	{ font-weight: normal; }
			#-railo-debug tr.nowrap td { white-space: nowrap; }
			#-railo-debug tr.red td, #-railo-debug .red 	{ background-color: #FDD; }

			#-railo-debug .sortby.selected, #-railo-debug .sortby:hover { background-color: #25A; color: #FFF; }
			#-railo-debug .pad 	{ padding-left: 16px; }
			#-railo-debug a 	{ cursor: pointer; }
			#-railo-debug td a 	{ color: #25A; }
			#-railo-debug td a:hover	{ color: #58C; text-decoration: underline; }
			#-railo-debug pre 	{ background-color: #EEE; padding: 1em; border: solid 1px #333; border-radius: 1em; white-space: pre-wrap; word-break: break-all; word-wrap: break-word; tab-size: 2; }

			.-railo-icon-plus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==) no-repeat left center; padding: 4px 0 4px 16px; }
			.-railo-icon-minus 	{ background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7)     no-repeat left center; padding: 4px 0 4px 16px; }
		</style>




	<cffunction name="renderSectionHeadTR" output="#true#">

		<cfargument name="sectionId">
		<cfargument name="label">
		<cfargument name="isOpen" default="#dalse#">

		<tr>
			<td colspan="2">
				<a 
					style="color:#isOpen?"##990000":"##006600"#"
					id="-railo-debug-btn-#sectionId#" 
					class="-railo-icon-#isOpen ? 'minus' : 'plus'#" 
					onclick="__RAILO.debug.toggleSection( '#sectionId#' );">
						#label#</a>
			</td>
		</tr>
	</cffunction>


	<cffunction name="getFirstNonMxunitElement" output="false">
		<cfargument name="tagcontext">

		<cfloop array="#arguments.tagcontext#" index="local.i" item="local.e">
			
			<cfif findNoCase( "/mxunit", local.e.template ) != 1>
				
				<cfreturn "#local.e.template#:#local.e.line#">
			</cfif>
		</cfloop>

		<cfreturn "">
	</cffunction>


<cfoutput query="#qry#" group="component">

			<cfset sectionId = "ALL">
			<cfset isOpen = false>
			<cfset total=0>
			<cfoutput>
				<cfif qry.testStatus NEQ "Passed"><cfset isOpen=true></cfif>
				<cfset total+=qry.time>
			</cfoutput>
					
			<!-- Railo Debug Output !-->
			<fieldset id="-railo-debug" class="medium #isOpen ? '' : 'collapsed'#">

				<legend><a id="-railo-debug-btn-#sectionId#" style="color:#isOpen?"##990000":"##006600"#" class="-railo-icon-#isOpen ? 'minus' : 'plus'#" onclick="__RAILO.debug.toggleSection( '#sectionId#' ) ? __RAILO.util.removeClass('-railo-debug', 'collapsed') : __RAILO.util.addClass('-railo-debug', 'collapsed');">
				 #qry.component#</a></legend>

<!---<pre><b style="color:#qry.testStatus EQ 'passed'?'green':'red'#"></b></pre>--->

				<div id="-railo-debug-ALL" class="#isOpen ? '' : 'collapsed'#">
					<!--- General --->
					<cfif true>
					<!---<div class="section-title">Debugging Information</div>--->
					<table>
					<cftry>
						<cfset cfc=createObject("component",qry.component)>
						<cfset meta=getmetadata(cfc)>
						
					<tr>
						<td>Location:</td>
						<td><a href="#meta.remoteAddress#" target="_new">#meta.path#</a></td>
					</tr>
					
						<cfcatch></cfcatch>
					</cftry>
					<tr>
						<td>Total Time:</td>
						<td>#total# ms</td>
					</tr>
					
					</table>
					
					<cfoutput>
					<cfset sectionId = replace(qry.component,'.','_','all')&"_"&qry.testName>
					<cfset isOpen = qry.testStatus NEQ "passed">
					<table>

							<cfset renderSectionHeadTR(sectionId , qry.testName&" (#qry.testStatus#)", isOpen)>
							
							<tr>
								<td colspan="2" id="-railo-debug-#sectionId#" class="#isOpen ? '' : 'collapsed'#">
									<table class="ml14px">
										
										<tr>
											<td class="label">Execution Time:</td>
											<td class="cfdebug">#qry.time# ms</td>
										</tr>
										<cfif qry.testStatus NEQ "passed">
										<tr>
											<td class="label">Message:</td>
											<cfset err=qry.error>
											<td class="cfdebug">#err.message#</td>
										</tr>
										<tr>
											<td class="label">Thrown From:</td>
											<td class="cfdebug">#getFirstNonMxunitElement( err.tagcontext )#</td>
										</tr>
										<tr>
											<td class="label">Stacktrace:</td>
											<td class="cfdebug">
												<table>
												<tr>
				<td class="label">Stacktrace</td>
				<td><!--- copy from error.cfm --->The Error Occurred in<br>
				<cfset len=arrayLen( err.tagcontext )>
					<cfloop index="idx" from="1" to="#len#">
						<cfset tc = err.tagcontext[ idx ]>
						<cfparam name="tc.codeprinthtml" default="">
						<cfif len( tc.codeprinthtml )>

							<cfset isFirst = ( idx == 1 )>

							<a class="-railo-icon-#isFirst ? 'minus' : 'plus'#" id="__btn$#idx#" onclick="__RAILO.oc( this );" style="cursor: pointer;">
								#isFirst ? "<b>#tc.template#: line #tc.line#</b>" : "<b>called from</b> #tc.template#: line #tc.line#"#
							</a>
							<br>

							<blockquote class="#isFirst ? 'expanded' : 'collapsed'#" id="__cst$#idx#">
								#tc.codeprinthtml#<br>
							</blockquote>
						</cfif>
					</cfloop>
				</td>
			</tr>
												</table>
												
												</td>
										</tr>
										<tr>
											<td class="label">Java Stacktrace:</td>
											<cfset err=qry.error>
											<cfscript>
											max=20;
											arr=listToArray(err.stacktrace,chr(10));
											st="";
											loop index="i" from="1" to="#arrayLen(arr)>max?max:arrayLen(arr)#" {
												st&=arr[i]&"<br><span style='margin-right: 1em;'>&nbsp;</span>";
											}
											if(arrayLen(arr)>max)
												st&='...';
											</cfscript>
											
											<td class="cfdebug">#st#</td>
										</tr>
										</cfif>
									</table>
								</td>
							</tr>
						</table>
						</cfoutput>
					</cfif>
				</div>	<!--- #-railo-debug-ALL !--->
			</fieldset>	<!--- #-railo-debug !--->
		</cfoutput>

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

					document.cookie = name + "=" + escape( value ) + ( (expires) ? "; expires=" + expires.toGMTString() : "" ) + "; path=/";
				}

				, removeCookie: 	function( name ) {

					__RAILO.util.setCookie( name, "", new Date( 0 ) );
				}

				, getDomObject: 	function( obj ) {			// returns the element if it is an object, or finds the object by id */

					if ( typeof obj == 'string' || obj instanceof String )
						return document.getElementById( obj );

					return obj;
				}

				, hasClass: 		function( obj, cls ) {

					obj = __RAILO.util.getDomObject( obj );
					return ( obj.className.indexOf( cls ) > -1 );
				}

				, addClass: 		function( obj, cls ) {

					if ( __RAILO.util.hasClass( obj, cls ) )
						return;

					obj = __RAILO.util.getDomObject( obj );
					obj.className += " " + cls;
				}

				, removeClass: 		function( obj, cls ) {

					obj = __RAILO.util.getDomObject( obj );
					obj.className = obj.className.replace( cls, "" );
				}

				, toggleClass: 		function( obj, cls ) {

					obj = __RAILO.util.getDomObject( obj );

					if ( __RAILO.util.hasClass( obj, cls ) )
						__RAILO.util.removeClass( obj, cls );
					else
						__RAILO.util.addClass( obj, cls );

					return ( __RAILO.util.hasClass( obj, cls ) );
				}
			};


			__RAILO.debug = {

				

				, setFlag: 		function( name ) {

					var value = __RAILO.util.getCookie( __RAILO.debug.cookieName, __RAILO.debug.allSections.ALL ) | __RAILO.debug.allSections[ name ];
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
					var obj = __RAILO.util.getDomObject( "-railo-debug-" + name );
					var isOpen = ( __RAILO.util.getCookie( __RAILO.debug.cookieName, 0 ) & __RAILO.debug.allSections[ name ] ) > 0;

					if ( isOpen ) {

						__RAILO.util.removeClass( btn, '-railo-icon-minus' );
						__RAILO.util.addClass( btn, '-railo-icon-plus' );
						__RAILO.util.addClass( obj, 'collapsed' );
						__RAILO.debug.clearFlag( name );
					} else {

						__RAILO.util.removeClass( btn, '-railo-icon-plus' );
						__RAILO.util.addClass( btn, '-railo-icon-minus' );
						__RAILO.util.removeClass( obj, 'collapsed' );
						__RAILO.debug.setFlag( name );
					}

					return !isOpen;					// returns true if section is open after the operation
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
</cfif>
