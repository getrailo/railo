<cfif thistag.executionmode EQ "end" or not thistag.hasendtag>
	<cfset variables.stText = application.stText[session.railo_admin_lang] />
	<cfparam name="attributes.navigation" default="">
	<cfparam name="attributes.title" default="">
	<cfparam name="attributes.content" default="">
	<cfparam name="attributes.right" default="">
	<cfparam name="attributes.width" default="780">

	<cfscript>
		ad=request.adminType;
		hasNavigation=len(attributes.navigation) GT 0;
		other=iif(request.adminType EQ "web","'server'","'web'");
		otherURL=other&".cfm"
		if(structKeyExists(url,'action'))otherURL&="?action="&url.action;
	</cfscript>
	<cfset request.mode="full">
	
<cfcontent reset="yes" /><!DOCTYPE HTML>
<cfoutput>
<html>
<head>
	<title>Railo #ucFirst(request.adminType)# Administrator</title>
</cfoutput>
	<link rel="stylesheet" href="resources/css/style40.css.cfm" type="text/css" />
	<script src="resources/js/jquery-1.7.2.min.js.cfm" type="text/javascript"></script>
	<script src="resources/js/jquery.blockUI.js.cfm" type="text/javascript"></script>
	<script src="resources/js/admin.js.cfm" type="text/javascript"></script>
</head>
<cfoutput>
<body id="body" class="#request.adminType#<cfif application.adminfunctions.getdata('fullscreen') eq 1> full</cfif>" <cfif structKeyExists(attributes,"onload")>onload="#attributes.onload#"</cfif>>
	<div id="layout">
		<table id="layouttbl">
			<tbody>
				<tr>
					<td colspan="2" id="logotd" class="lotd">
						<div id="logo">
							<a href="#request.self#"><h2>Railo</h2></a>
						</div>
					</td>
					<td id="tabstd" class="lotd">
						<a <cfif ad EQ "web">href="#otherURL#"<cfelse>href="server.cfm"</cfif>><img src="resources/img/left-tab-#ad#.png.cfm" alt="Server Administrator tab" title="Go to the Server Administrator" /></a><!---
						---><a <cfif ad EQ "server">href="#otherURL#"<cfelse>href="web.cfm"</cfif>><img src="resources/img/right-tab-#ad#.png.cfm" alt="Web Administrator tab" title="Go to the Web Administrator" /></a>
					</td>
					<td class="lotd"></td>
				</tr>
				<tr>
					<td id="leftshadow" rowspan="2" class="lotd"></td>
					<td id="navtd" class="lotd">
						<div id="nav">
							<a href="##" id="resizewin" title="resize window"><span>Resize window</span></a>
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
					<td id="rightshadow" rowspan="2" class="lotd"></td>
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
<!---	<cfif false and StructKeyExists(application,'notTranslated')>
		<cfset keys=structKeyArray(application.notTranslated)>
		<cfset ArraySort(keys,'textnocase')>
	<!-- The following text is not translated the the current language
<cfloop array="#keys#" index="key"><data key="#key#">#trim(application.notTranslated[key])#</data>
</cfloop>
	-->
	</cfif>
--->
</body>
</html>
</cfoutput>
	<cfset thistag.generatedcontent="">
</cfif>
<cfparam name="url.showdebugoutput" default="no">
<cfsetting showdebugoutput="#url.showdebugoutput#">