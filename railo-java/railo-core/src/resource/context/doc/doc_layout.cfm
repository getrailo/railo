<cfif thistag.executionmode == "start">

	<cfexit method="exittemplate">
</cfif>

<cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfinclude template="/railo-context/admin/resources/text.cfm">

<cfcontent reset="#true#">

<cfset railoVersion = listFirst( server.railo.version, '.' ) & '.' & listGetAt( server.railo.version, 2, '.' )>

<cfparam name="Attributes.title" default="Railo Doc Refernce #railoVersion#">
<cfparam name="Attributes.description" default="Railo Tag, Function, and Member Methods Refernce for Railo #railoVersion#">


<!DOCTYPE HTML>
<html>
	<cfoutput>

	<head>
		<title>#Attributes.title#</title>
		<meta name="description" value="#Attributes.description#">

		<link rel="stylesheet" href="../res/css/normalize2.min.css.cfm">
		<link rel="stylesheet" href="../res/css/doc.css.cfm">

		<cfif Request.keyExists( "htmlHead" )>
			#Request.htmlHead#
		</cfif>
	</head>
	<body id="body-#listFirst( listLast( CGI.SCRIPT_NAME, '/' ), '.' )#">
		<div id="wrapper">
			<header>
				<img id="logo" src="../res/img/logo.png.cfm">

				<nav>
					<a href="tags.cfm" class="tags">Tags</a>
					&middot;
					<a href="functions.cfm" class="functions">Functions</a>
					&middot;
					<a href="objects.cfm" class="objects">Objects</a>
				</nav>
				<div id="header-title">Railo #railoVersion# Reference</div>
			</header>
			<div id="content">
				#thistag.generatedcontent#
			</div>
			<footer>
				<br><br><br>
				<nav>
					<div class="centered">
						<a href="tags.cfm">Tags</a>
						&middot;
						<a href="functions.cfm">Functions</a>
						&middot;
						<a href="objects.cfm">Objects</a>
					</div>
				</nav>
				<br>
				<div class="x-small" style="text-align: center;">
					Railo Doc reference version #server.railo.version#
					<br>
					&copy; #year( now() )# <a href="http://www.getrailo.com/">Railo Technologies GmbH Switzerland</a>. All Rights Reserved.
				</div>
			</footer>
		</div>	<!--- #wrapper !--->

	</cfoutput>

		<script src="../res/js/jquery-1.9.min.js.cfm"></script>

		<script type="text/javascript">

			<cfoutput>

				var baseUrl = "#CGI.SCRIPT_NAME#";
			</cfoutput>

			$( function() { 

				$( "#form-item-selector input[type=submit]" ).hide();

				$( "#select-item" ).change( function() { 

					var item = $( this ).val();

					if ( item )
						window.location = baseUrl + "?item=" + item;
				} );
			} );
		</script>

		<cfif Request.keyExists( "htmlBody" )>
			<cfoutput>#Request.htmlBody#</cfoutput>
		</cfif>
	</body>
</html>


<cfset thistag.generatedcontent="">

<cfparam name="showdebugoutput" default="#false#">
<cfsetting showdebugoutput="#showdebugoutput#">
