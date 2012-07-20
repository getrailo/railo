<cfsetting showdebugoutput="no">
<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- create a string to be used as an Etag - in the response header --->
	<cfset filepath = getCurrentTemplatePath() />
	<cfset lastModified = application.oHTTPCaching.getFileDateLastModified(filepath) />
	<cfset etag = lastModified & '-' & hash(filepath) />
	<cfset mimetype = "text/css" />
	
	<!--- check if the content was cached on the browser, and set the ETag header.
	No expires header is set, because this file might get updated after a Railo update. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype)>
		<cfexit method="exittemplate" />
	</cfif>
	
	<!--- file was not cached; send the data --->
	<cfcontent reset="yes" type="#mimetype#" />
	
	<!--- PK: this style tag is here, so my editor color-codes the content underneath. (it won't get outputted) --->
	<style type="text/css">
	
</cfsilent><!---

--->body {background-image:url('../img/web-back.png.cfm');background-repeat:repeat-x;background-color:#f7f7f7;margin-top:0px;margin-left:0px;}
body, th, td, div {font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:#3c3e40;}
.box {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 14pt;color:#007bb7;}
h1 {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 20pt;color:#007bb7;}
h2 {font-size:12pt;font-weight:normal;color:#007bb7; margin:0; padding: 0 0 10px 0;}
a{color:#007bb7; text-decoration:underline}
img, a img { border:0; }
form, div { margin:0; padding:0; }

.comment{font-size : 10px;color:#787a7d;text-decoration:none;}
.commentHead{font-size : 10px;color:#DFE9F6;}
.copy { font-size : 8pt;color:#666666;}

div.hr{border-color:red;border-style:solid;border-color:#e0e0e0;border-width:0px 0px 1px 0px;margin:0px 16px 4px 0px;}
.tbl{empty-cells:show;}
.tblHead{padding-left:5px;padding-right:5px;border:1px solid #e0e0e0;background-color:#f2f2f2;color:#3c3e40}
.tblContent			{padding-left:5px;padding-right:5px;border:1px solid #e0e0e0;}
.tblContentRed		{padding-left:5px;padding-right:5px;border:1px solid #cc0000;background-color:#f9e0e0;}
.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid #009933;background-color:#e0f3e6;}
.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid #ccad00;background-color:#fff9da;}

td.inactivTab{border-style:solid;border-color:#e0e0e0;padding: 0px 5px 0px 5px;background-color:white;}
a.inactivTab{color:#3c3e40;text-decoration:none;}

td.activTab{border-style:solid;border-color:#e0e0e0;border-width:1px 1px 0px 1px ;padding: 2px 10px 2px 10px;background-color:#e0e0e0;}
a.activTab{font-weight:bold;color:#3c3e40;text-decoration:none;}

td.tab {border-color:#e0e0e0;border-width:1px;border-style:solid;border-top:0px;padding:10px;background-color:white;}
td.tabtop {border-style:solid;border-color:#e0e0e0;border-width:0px 0px 1px 0px ;padding: 0px 1px 0px 0px;}


.CheckOk{font-weight:bold;color:#009933;font-size : 12px;}
.CheckError{font-weight:bold;color:#cc0000;font-size : 12px;}

input {
	background: url('../img/input-shadow.png.cfm') repeat-x 0 0;
	background-color:white;
	padding:3px 2px 3px 3px;
	margin:3px 1px 3px 1px;
	color:#3c3e40;
	border:1px solid #e0e0e0;
}
.button,.submit,.reset {
	background: url('../img/input-button.png.cfm') repeat-x 0 0;
	background-color:#f2f2f2;
	color:#3c3e40;
	font-weight:bold;
	padding:3px 10px;
	margin:0px;
}
select {font-size : 11px;color:#3c3e40;margin:3px 0px 3px 0px;}
.checkbox,.radio {border:0px;}

/* menu */
ul#menu, ul#menu ul {
  list-style-type:none;
  margin: 0;
  padding: 0;
}

ul#menu a {
  display: block;
  text-decoration: none;	
}

ul#menu li {margin-top: 1px;}

ul#menu li a {margin-top:8px;margin-bottom:3px;color:#333;font-weight:bold;font-size : 9pt;}
ul#menu li a:hover {color:#000;}

ul#menu li ul li a {
margin-top:0px;margin-bottom:0px;font-weight:normal;
 text-decoration:none;color:#007bb7;font-size : 8pt;
  padding-left: 10px;
  background-image:url('../img/arrow.gif.cfm');background-repeat:no-repeat;
}

ul#menu li ul li a:hover, ul#menu li ul li a.menu_active {
margin-top:0px;margin-bottom:0px;font-weight:normal;
 text-decoration:none;color:#007bb7;font-size : 8pt;
  padding-left: 10px;
  background-image:url('../img/arrow-active.gif.cfm');background-repeat:no-repeat;
}
ul#menu li ul li a.menu_active {
	font-weight:bold;
}

.commentError{font-size : 10px;color:#cc0000;text-decoration:none;}
.InputError{background: url('../img/input-shadow-error.png.cfm') repeat-x 0 0;background-color:#fae2e2;}
		
/*
.darker{background-color:#e0e0e0;}
.brigther{background-color:##bgBrightColor#;}
*/

/* server admin */
body.server {background-image:url('../img/server-back.png.cfm')}
body.server .box, body.server h1, body.server h2, body.server a, body.server ul#menu li ul li a
, body.server ul#menu li ul li a:hover, body.server ul#menu li ul li a.menu_active, body.server .extensionthumb a:hover {color:#9c0000}

/* new layout; Paul K */
.clear { clear:both }
div.error, div.warning, div.message {
	border:2px solid red;
	padding:5px;
	margin:10px 0px;
	font-weight:bold;
	color:red;
}
div.warning {
	border-color: #FC6;
	color:#000;
}
div.message {
	border-color: #0C0;
	color:#000;
}
.right { text-align:right; }
.left { text-align:left; }
.center { text-align:center }

/* tables */
.maintbl {
	width:100%;
	border-collapse:separate;
}
.maintbl td, .maintbl th {
	padding: 2px 5px;
	text-align:left;
	font-weight:normal;
}
.maintbl tbody td, .maintbl tbody th {/* like .tblContent */
	border:1px solid #e0e0e0;
}
.maintbl tbody th {/* like .tblHead */
	background-color:#f2f2f2;
	color:#3c3e40;
	width: 30%;
}
.maintbl tfoot td {
	border:none;
}

.optionslist {border:0; border-collapse:collapse; width:auto;}
.optionslist td, .optionslist th { padding:3px; vertical-align:top;}
.contentlayout { border-collapse:collapse; width:100%; }
.contentlayout td, .contentlayout th { border:0; }

/* page header with H2 and other content */
.modheader {
	margin-top: 20px;
}

/* filter form */
.filterform {
	padding:5px;
	margin:10px 0px;
	border:1px solid #e0e0e0;
	background-color:#f2f2f2;
	color:#3c3e40
}
.filterform ul {
	list-style:none;
	margin:0;
	padding:0;
}
.filterform li {
	width: auto;
	float:left;
	padding-right: 10px;
}
.filterform label {
	width: 200px;
	height:18px;
	display:block;
}
.filterform input.txt, .filterform select {
	width: 200px;
}
.filterform input.submit {
	margin-top: 20px;
}

/* extensions overview */
.extensionlist {
	margin-bottom: 20px;
}
.extensionthumb {
	width:140px;
	height:100px;
	overflow: hidden;
	margin:5px 5px 0px 0px;
	float:left;
	text-align:center;
}
.extensionthumb a {
	display:block;
	padding:2px;
	height: 94px;
	text-decoration:none !important;
	border: 1px solid #E0E0E0;
}
.extensionthumb a:hover {
	background-color:#f8f8f8;
	border-color: #007bb7;
}
.extimg {
	height:50px;
}
textarea.licensetext {
	height:200px;
	width:100%;
	font-family:Courier New;
	font-size : 8pt;
	color:##595F73;
	border: 1px solid #666;
}