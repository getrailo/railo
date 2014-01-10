<cfparam name="url.print_js_resources" type="boolean" default="true" />
<cfparam name="url.toggledebug" type="boolean" default="true" />
<cfsetting requesttimeout="3600"><!-- 1 hour -->

<cfscript>
	
	testSuite = new org.railo.cfml.test.RailoTestSuite();
	testSuite.addPackage('tickets');
	//testSuite.setMaxThreads(10);
	
	results = testSuite.run();
    echo(results.getResultsOutput('html'));
    qry=results.getResultsOutput('query');
	


</cfscript>

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
							<!---
							<tr>
								<td class="pad label">User Agent:</td>
								<td class="pad">#cgi.http_user_agent#</td>
							</tr>
							--->
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
											<td class="label">Stacktrace:</td>
											<td class="cfdebug">...</td>
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



<!--->
<table>
<cfoutput query="#qry#">

    <pre><b style="color:#qry.testStatus EQ 'passed'?'green':'red'#">#qry.component#.#qry.testname# (#qry.testStatus#)</b></pre>
    <cfif qry.testStatus neq "passed">
    <cfset catch=qry.error>
	
	<!--- TODO do better impl for this --->
	<cfset entryIndex=1>
	<cfloop array="#catch.TagContext#" index="i" item="sct">
		<cfif not findNoCase("mxunit/framework",sct.template) and not findNoCase("org/railo/cfml/test",sct.template)> 
			<cfset entryIndex=i>
			<cfbreak>
		</cfif>
	</cfloop>
	
	<cfif entryIndex GT 1>
		<cfset catch.TagContext=arraySlice(catch.TagContext,entryIndex)>
	</cfif>
	
	
	
<pre>
    <cftry>#catch.Message#<cfcatch></cfcatch></cftry>
    <cftry>#qry.dateTime#<cfcatch></cfcatch></cftry>
    <cftry>#catch.tagContext[1].template#:#catch.tagContext[1].line#<cfcatch></cfcatch></cftry>
</pre>
    </cfif>
    </cfoutput>
</table>
--->
<cfdump var="#qry#">