<cfif thistag.executionmode EQ "end" or not thistag.hasendtag>
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
	<script src="resources/js/jquery-1.7.2.min.js.cfm"></script>
	<script src="resources/js/jquery.blockUI.js.cfm"></script>
	<script src="resources/js/admin.js.cfm"></script>
	<script>
		$(function(){
			$('#resizewin').click(resizelayout);
		});
		function resizelayout(e)
		{
			$('body').toggleClass('full');
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
			<div id="leftshadow"></div>
			<div id="nav">
				<a href="##" id="resizewin" title="resize window"><span>Resize window</span></a>
				<cfif hasNavigation>
					#attributes.navigation#
				</cfif>
			</div>
			<div id="content">
				<div id="maintitle">
					<span class="box">#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></span>
					<cfif hasNavigation>
						<a class="navsub" style="font-size:9pt;" href="#request.self#?action=logout">Logout</a>
					</cfif>
				</div>
				<div id="innercontent">
					#thistag.generatedContent#
				</div>
			</div>
			<div id="rightshadow"></div>
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