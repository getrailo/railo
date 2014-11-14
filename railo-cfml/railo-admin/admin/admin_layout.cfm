<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfif thistag.executionmode EQ "end" or not thistag.hasendtag>

	<cfparam name="session.railo_admin_lang" default="en">
	<cfset variables.stText = application.stText[session.railo_admin_lang] />
	<cfparam name="attributes.navigation" default="">
	<cfparam name="attributes.title" default="">
	<cfparam name="attributes.content" default="">
	<cfparam name="attributes.right" default="">
	<cfparam name="attributes.width" default="780">

	<cfscript>
		ad=request.adminType;
		hasNavigation=len(attributes.navigation) GT 0;
		home=request.adminType&".cfm"
		if(structKeyExists(url,'action'))homeQS="?action="&url.action;
		else homeQS="";
	</cfscript>
	<cfset request.mode="full">
	
<cfcontent reset="yes" /><!DOCTYPE HTML>
<!--[if lt IE 9]> <style> body.full #header #logo.sprite { background-image: url(resources/img/server-railo-small.png.cfm); background-position: 0 0; margin-top: 16px; } </style> <![endif]-->	<!--- remove once IE9 is the min version to be supported !--->
<cfoutput>
<html>
<head>
	<title>Railo #ucFirst(request.adminType)# Administrator</title>

	<link rel="stylesheet" href="../res/css/admin42.css.cfm" type="text/css">

	<script src="resources/js/jquery-1.7.2.min.js.cfm" type="text/javascript"></script>
	<script src="resources/js/jquery.blockUI.js.cfm" type="text/javascript"></script>
	<script src="resources/js/admin.js.cfm" type="text/javascript"></script>
</head>

<cfparam name="attributes.onload" default="">

<body id="body" class="admin-#request.adminType# #request.adminType#<cfif application.adminfunctions.getdata('fullscreen') eq 1> full</cfif>" onload="#attributes.onload#">
	<div id="layout">
		<table id="layouttbl">
			<tbody>

				<tr id="tr-header">	<!--- TODO: not sure where height of 275px is coming from? forcing here 113px/63px !--->
					<td></td>
					<td colspan="2">
						<div id="header">

							<a id="logo" class="sprite" href="#home#"></a>
							<div id="admin-tabs" class="clearfix">
								<a href="server.cfm#homeQS#" class="sprite server"></a>
								<a href="web.cfm#homeQS#" class="sprite web"></a>
							</div>
						</div>	<!--- #header !--->
					</td>
					<td></td>
				</tr>

				<tr>
					<td rowspan="2" class="lotd">
						<div style="height: 77px;"></div>
						<div class="sprite colshadow" style="float: right; background-position: -16px 0px;"></div>
					</td>
					<td id="navtd" class="lotd">
						<div id="nav">
							<a href="##" id="resizewin" class="sprite" title="resize window"></a>
							<cfif hasNavigation>
								<form method="get" action="#cgi.SCRIPT_NAME#">
									<input type="hidden" name="action" value="admin.search" />
									<input type="text" name="q" size="15" id="navsearch" placeholder="#stText.buttons.search#" />
									<button type="submit" class="btn-mini btn-search" title="#stText.buttons.search#"><span>#stText.buttons.search#</span></button>
								</form>
								#attributes.navigation#
							</cfif>
						</div>
					</td>
					<td id="contenttd" class="lotd">
						<div id="content">
							<div id="maintitle">
								<cfif hasNavigation>
									<a class="navsub" style="font-size:9pt;" href="#request.self#?action=logout">#variables.stText.help.logout#</a>

									<!--- Favorites --->
									<cfparam name="url.action" default="" />
									<cfset pageIsFavorite = application.adminfunctions.isFavorite(url.action) />
									<div id="favorites">
										<cfif url.action eq "">
											<a href="##" class="favorite tooltipMe" title="Go to your favorite pages">Favorites</a>
										<cfelseif pageIsFavorite>
											<a href="#request.self#?action=internal.savedata&action2=removefavorite&favorite=#url.action#" class="favorite tooltipMe" title="Remove this page from your favorites">Favorites</a>
										<cfelse>
											<a href="#request.self#?action=internal.savedata&action2=addfavorite&favorite=#url.action#" class="tooltipMe favorite_inactive" title="Add this page to your favorites">Favorites</a>
										</cfif>
										<ul>
											<cfif attributes.favorites neq "">
												#attributes.favorites#
											<cfelse>
												<li class="favtext"><i>No items yet.<br />Go to a page you use often, and click on "Favorites" to add it here.</i></li>
											</cfif>
										</ul>
									</div>
								</cfif>
								<span class="box">#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></span>
							</div>
							<div id="innercontent">
								#thistag.generatedContent#
							</div>
						</div>
					</td>
					<td rowspan="2" class="lotd">
						<div style="height: 77px;"></div>
						<div class="sprite colshadow" style="background-position: 0px 0px;"></div>
					</td>
				</tr>
				<tr>
					<td class="lotd">&nbsp;</td>
					<td class="lotd" id="copyrighttd">
						<div id="copyright" class="copy">
							&copy; #year(Now())#
							<a href="http://www.getrailo.com" target="_blank">Railo Technologies GmbH Switzerland</a>.
							All Rights Reserved. |
							Designed by <a href="http://www.blueriver.com/from/railo/" target="_blank">Blue River Interactive Group, Inc.</a>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>

	<!--- TODO: move to reusable script in /res/js/admin.js !--->
	<script>
		var getDomObject = function( obj ) {	// returns the element if it is an object, or finds the object by id */

			if ( typeof obj == 'string' || obj instanceof String )
				return document.getElementById( obj );

			return obj;
		}

		var selectText = function( obj ) {

	        if ( document.selection ) {

	            var range = document.body.createTextRange();
	            range.moveToElementText( getDomObject( obj ) );
	            range.select();
	        } else if ( window.getSelection ) {

	            var range = document.createRange();
	            range.selectNode( getDomObject( obj ) );
	            window.getSelection().addRange( range );
	        }
	    }


		$( function(){

			$( '.coding-tip-trigger-#request.adminType#' ).click( 
				function(){ 
					var $this = $(this);
					$this.next( '.coding-tip-#request.adminType#' ).slideDown();
					$this.hide();
				}
			);

			$( '.coding-tip-#request.adminType# code' ).click( 
				function(){ 					
					selectText(this);					
				}
			).prop("title", "Click to select the text");
		});
	</script>

	<cfif isDefined( "Request.htmlBody" )>#Request.htmlBody#</cfif>
</body>
</html>
</cfoutput>
	<cfset thistag.generatedcontent="">
</cfif>

<cfparam name="url.showdebugoutput" default="no">
<cfsetting showdebugoutput="#url.showdebugoutput#">