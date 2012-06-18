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
	<link rel="stylesheet" href="resources/css/style.css.cfm" type="text/css" />
	<script src="resources/js/jquery-1.7.2.min.js.cfm" type="text/javascript"></script>
	<script src="resources/js/jquery.blockUI.js.cfm" type="text/javascript"></script>
	<script src="resources/js/admin.js.cfm" type="text/javascript"></script>
	<script type="text/javascript">
<!---		function resizemid()
		{
			return;
			var mh = $('#mainholder');
			mh.css('height', $(document).height() - parseInt(mh.css('padding-top'), 10) - $('#copyright').height() - 15);
		}
		$(function(){
			$('#resizewin').click(resizelayout);
			resizemid();
		});
--->
		$(function(){
			$('#resizewin').click(resizelayout);
		});
		function resizelayout(e)
		{
			$('body').toggleClass('full');
			<!---setTimeout(resizemid, 100);--->
			e.preventDefault();
			return false;
		};
	</script>
</head>
<cfoutput>
<body id="body" class="#request.adminType#" <cfif structKeyExists(attributes,"onload")>onload="#attributes.onload#"</cfif>>
	<div id="layout">
		<div id="logo">
			<a href="#request.self#"><h2>Railo</h2></a>
		</div>
		<div id="admintypetabs">
			<a <cfif ad EQ "web">href="#otherURL#"<cfelse>href="server.cfm"</cfif>><img src="resources/img/left-tab-#ad#.png.cfm" alt="Server Administrator tab" title="Go to the Server Administrator" /></a><!---
			---><a <cfif ad EQ "server">href="#otherURL#"<cfelse>href="web.cfm"</cfif>><img src="resources/img/right-tab-#ad#.png.cfm" alt="Web Administrator tab" title="Go to the Web Administrator" /></a>
		</div>
		
		<div id="mainholder">
			<div id="toprow"></div>
			<div id="mainrow">
				<div id="leftshadow"></div>
				<div id="nav">
					<a href="##" id="resizewin" title="resize window"><span>Resize window</span></a>
					<cfif hasNavigation>
						<form method="get" action="#cgi.SCRIPT_NAME#">
							<input type="hidden" name="action" value="admin.search" />
							<input type="text" name="q" size="15" id="navsearch" />
							<input type="submit" class="button submit" value="Search" style="padding-left:0; padding-right:0;" />
						</form>
						#attributes.navigation#
					</cfif>
				</div>
				<div id="content">
					<div id="maintitle">
						<span class="box">#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></span>
						<cfif hasNavigation>
							<a class="navsub" style="font-size:9pt;" href="#request.self#?action=logout">#variables.stText.help.logout#</a>
							<!--- make favorite --->
							<cfparam name="url.action" default="" />
							<cfif url.action neq "">
								<cfif application.adminfunctions.isFavorite(url.action)>
									<a href="#request.self#?action=internal.savedata&action2=removefavorite&favorite=#url.action#" class="favorite tooltipMe" title="Remove this page from your favorites"><span>remove favorite</span></a>
								<cfelse>
									<a href="#request.self#?action=internal.savedata&action2=addfavorite&favorite=#url.action#" class="favorite tooltipMe favorite_inactive" title="Add this page to your favorites"><span>add favorite</span></a>
								</cfif>
							</cfif>
						</cfif>
					</div>
					<div id="innercontent">
						#thistag.generatedContent#
					</div>
				</div>
				<div id="rightshadow"></div>
			</div>
			<div id="bottomrow"></div>
		</div>
		<div id="copyright" class="copy">
			&copy; #year(Now())#
			<a href="http://www.getrailo.com" target="_blank">Railo Technologies GmbH Switzerland</a>.
			All Rights Reserved. |
			Designed by <a href="http://www.blueriver.com/from/railo/" target="_blank">Blue River Interactive Group, Inc.</a>
			<br><br>
		</div>
	</div>

	<cfif false and StructKeyExists(application,'notTranslated')>
		<cfset keys=structKeyArray(application.notTranslated)>
		<cfset ArraySort(keys,'textnocase')>
	<!--
The following text is not translated the the current language
<cfloop  array="#keys#" index="key"><data key="#key#">#trim(application.notTranslated[key])#</data>
</cfloop>
	-->
	</cfif>
</body>
</html>
</cfoutput>
	<cfset thistag.generatedcontent="">
</cfif>
<cfparam name="url.showdebugoutput" default="no">
<cfsetting showdebugoutput="#url.showdebugoutput#">