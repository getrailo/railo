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

--->body {
	min-width:600px;
	background:#f7f7f7 url(../img/web-back.png.cfm) repeat-x top;
	margin:0;
	padding:0;
}
body.server {
	background-image:url(../img/server-back.png.cfm);
}
body, td, th {
	font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;
	font-size : 12px;
	color:#3c3e40;
}
table {
	border-collapse:collapse;
}
h1, h2, h3, h4, h5 {
	font-weight:normal;
	font-size : 18px;
	color:#007bb7;
	margin:0;
	padding:0 0 4px 0;
}
h1 {padding-bottom:10px}
h2 {font-size:16px;}
h3 {font-size:14px;}
h4 {font-size:12px;}
h5 {font-size:10px;}
* + h1, * + h2 {
	padding-top: 20px;
}
table + h3, div + h3 {
	padding-top: 10px;
}
a {
	color:#007bb7;
	text-decoration:underline
}
img, a img { border:0; }
form, div { margin:0; padding:0; }

.clear { clear:both }
.right { text-align:right; }
.left { text-align:left; }
.center { text-align:center }







/* site main layout */
#layout {
	width: 1030px;
	margin:0px auto;
	position:relative;
}
body.full #layout {
	width:100%;
}

#logo {
	position:absolute;
	top:34px;
	left:0px;
	padding:5px 0px;
}
body.full #logo {
	top: 0px;
	left:5px;
}
#logo a {
	display:block;
	background-image:url(../img/web-railo.png.cfm);
	width: 102px;
	height:69px;
}
body.full #logo a {
	background-image:url(../img/web-railo-small.png.cfm);
	width:50px;
	height:34px;
}
#logo h2 {
	display:none;
}
body.server #logo a {
	background-image:url(../img/server-railo.png.cfm);
}
body.server.full #logo a {
	background-image:url(../img/server-railo-small.png.cfm);
}

#admintypetabs {
	position:absolute;
	top:76px;
	right:11px;
}
body.full #admintypetabs {
	top:7px;
}
#mainholder {
	width:100%;
	display:table;
	padding:113px 0 0 0;
}
body.full #mainholder {
	padding-top:44px;
}
#mainholder > div {
	display:table-cell;
	vertical-align:top;
}

#leftshadow, #rightshadow {
	width:11px;
	background:transparent url(../img/shadow-left.gif.cfm) no-repeat 0px 77px;
}
div#rightshadow {
	background-image:url(../img/shadow-right.gif.cfm);
}
#nav {
	width:160px;
	padding:10px 10px 50px 10px;
	background-color:#e6e6e6;
	border-top-left-radius: 10px;
	border-right:1px solid #d2d2d2;
}

#resizewin {
	display:block;
	width:22px;
	height:22px;
	overflow:hidden;
	background-image:url(../img/maxl.png.cfm);
}
#resizewin span {
	visibility:hidden;
}
body.full #resizewin {
	background-image:url(../img/minl.png.cfm);
}

#content {
	padding:30px 20px 10px 20px;
	background-color:#fff;
}
#innercontent {
	margin:30px 18px 30px 10px;
}

#copyright {
	padding: 5px 0px 30px 221px;
	text-align:left;
	font-size : 8pt;
	color:#666;
}
#copyright a {
	color:#666;
}


/* page title */	
#maintitle {
	height:29px;
	border:1px solid #cdcdcd;
	border-radius:5px;
	background:#f0f0f0 url(../img/box-bg.png.cfm) repeat-x 0px -1px;
	overflow:hidden;
	padding-left:10px;
}
#maintitle span.box {
	line-height:30px;
}
#maintitle a.navsub {
	float:right;
	width:75px;
	height:29px;
	border-left:1px solid #cdcdcd;
	line-height:27px;
	text-align:center
}
/* text under title */
div.pageintro {
	margin: 0 0 20px 0;
}

/* intro text for a section (i.e. under an h2; above a table) */	
.itemintro {
	font-style:italic;
	margin: -3px 0 10px 0;
}


/* tables */
table {empty-cells:show;}
td, th {
	padding:3px;
	vertical-align:top;
}
.tbl td, .tbl th {
	border:1px solid #ddd;
}
th {/* like .tblHead */
	background-color:#f2f2f2;
	color:#3c3e40;
	font-weight:normal;
	text-align:left;
}
table.nospacing {
	border-collapse:collapse;
}
/*.tblHead{padding-left:5px;padding-right:5px;border:1px solid #e0e0e0;background-color:#f2f2f2;color:#3c3e40}
.tblContent			{padding-left:5px;padding-right:5px;border:1px solid #e0e0e0;}
*/
.tblContentRed		{padding-left:5px;padding-right:5px;border:1px solid #cc0000;background-color:#f9e0e0;}
.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid #009933;background-color:#e0f3e6;}
.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid #ccad00;background-color:#fff9da;}
/* tables */
.maintbl {
	width:100%;
}
.autowidth {
	width: auto;
}
.maintbl td, .maintbl th {
	padding: 3px 5px;
	text-align:left;
	font-weight:normal;
	empty-cells:show;
}
.maintbl td, .maintbl th {/* like .tblContent */
	border:1px solid #e0e0e0;
}
.maintbl > tbody > tr > th {/* like .tblHead */
	width: 30%;
}
.maintbl tfoot td {
	border:none;
}
td.fieldPadded {
	padding-top:10px;
	padding-bottom:10px;
}


/* display boxes etc. */
.commentError{font-size : 10px;color:#cc0000;text-decoration:none;}
.comment{
	font-size:11px;
	color:#787a7d;
	text-decoration:none;
	padding:2px 0 5px 0;
}
.commentHead{font-size : 11px;color:#DFE9F6;}
div.comment + * {
	margin-top: 10px;
}
.checkbox + .comment, .radio + .comment {
	display:inline;
	padding-left:10px;
}
h3 + .comment {
	padding-top:0px;
}

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



/* unorganized */
.box {
	font-weight:normal;
	font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;
	font-size : 14pt;
	color:#007bb7;
}
div.hr{border-color:red;border-style:solid;border-color:#e0e0e0;border-width:0px 0px 1px 0px;margin:0px 16px 4px 0px;}

td.inactivTab{border-style:solid;border-color:#e0e0e0;padding: 0px 5px 0px 5px;background-color:white;}
a.inactivTab{color:#3c3e40;text-decoration:none;}

td.activTab{border-style:solid;border-color:#e0e0e0;border-width:1px 1px 0px 1px ;padding: 2px 10px 2px 10px;background-color:#e0e0e0;}
a.activTab{font-weight:bold;color:#3c3e40;text-decoration:none;}

td.tab {border-color:#e0e0e0;border-width:1px;border-style:solid;border-top:0px;padding:10px;background-color:white;}
td.tabtop {border-style:solid;border-color:#e0e0e0;border-width:0px 0px 1px 0px ;padding: 0px 1px 0px 0px;}


.CheckOk{font-weight:bold;color:#009933;font-size : 12px;}
.CheckError{font-weight:bold;color:#cc0000;font-size : 12px;}



/* forms */
input {
	background: url('../img/input-shadow.png.cfm') repeat-x 0 0;
	background-color:white;
	padding:3px 2px 3px 3px;
	margin:3px 1px 3px 1px;
	color:#3c3e40;
	border:1px solid;
	border-color: #aaa #ddd #ddd #aaa;
}
select {font-size : 11px;color:#3c3e40;margin:3px 0px 3px 0px;}
.button,.submit,.reset {
	background: url('../img/input-button.png.cfm') repeat-x 0 0;
	background-color:#f2f2f2;
	color:#3c3e40;
	font-weight:bold;
	padding:3px 10px;
	margin:0px;
	border-color: #777;
	border-radius:5px;
}
label:hover {
	background-color:#f6f6f6;
	cursor:pointer;
}
.checkbox, .radio {border:0px;}
.radiolist {
	list-style:none;
	padding:0;
	margin:0;
}
.radiolist .comment {
	padding-left:20px;
}
.radiolist label + table {
	margin-left:20px;
}
.InputError{
	background:#fae2e2 url('../img/input-shadow-error.png.cfm') repeat-x 0 0;
}
.xlarge {width:99%}
.large  {width:60%}
.medium {width:40%}
.small  {width:20%}
.xsmall {width:10%}
.number { width:40px; text-align:right }

/* menu */
#menu, #menu ul {
	list-style-type:none;
	margin: 0;
	padding: 0;
}
#menu {
	margin:10px 0px 0px 10px;
}
#menu a {
  display: block;
  text-decoration: none;	
}

#menu li {margin-top: 1px;}

#menu li a {margin-top:8px;margin-bottom:3px;color:#333;font-weight:bold;font-size : 9pt;}
#menu li a:hover {color:#000;}

#menu li ul li a {
margin-top:0px;margin-bottom:0px;font-weight:normal;
 text-decoration:none;color:#007bb7;font-size : 8pt;
  padding-left: 10px;
  background-image:url('../img/arrow.gif.cfm');background-repeat:no-repeat;
}

#menu li ul li a:hover, #menu li ul li a.menu_active {
margin-top:0px;margin-bottom:0px;font-weight:normal;
 text-decoration:none;color:#007bb7;font-size : 8pt;
  padding-left: 10px;
  background-image:url('../img/arrow-active.gif.cfm');background-repeat:no-repeat;
}
#menu li ul li a.menu_active {
	font-weight:bold;
}




/* server admin */
body.server {background-image:url('../img/server-back.png.cfm')}
body.server .box, body.server h1, body.server h2, body.server h3, body.server h4, body.server a, body.server #menu li ul li a
, body.server #menu li ul li a:hover, body.server #menu li ul li a.menu_active, body.server .extensionthumb a:hover {color:#9c0000}

/* percentage bars: <div class="percentagebar"><div style="width:60%"></div></div> */
div.percentagebar {
	height:13px;
	font-size: 10px;
	border:1px solid #999;
	background-color: #d6eed4;
}
div.percentagebar div {
	height:100%;
	overflow:hidden;
	font-size: 10px;
	background-color:#eee2d4;
	border-right:1px solid #999;
	padding-left:2px;
}






.optionslist {border:0; border-collapse:collapse; width:auto;}
.optionslist td, .optionslist th { padding:3px; vertical-align:top;}
.contentlayout { border-collapse:collapse; width:100%; }
.contentlayout td, .contentlayout th { border:0; }


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





/* module Extensions > Applications */
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
/* install extension*/
textarea.licensetext {
	height:200px;
	width:100%;
	font-family:"Courier New",Courier,monospace;
	font-size : 8pt;
	color:##595F73;
	border: 1px solid #666;
}

/* page Overview / home */
div.classpaths {
	font-family:"Courier New",Courier,monospace;
	font-size: 10px;
	overflow:auto;
	max-height:100px;
	border:1px solid #333;
}
div.classpaths div {
	padding:1px 5px;
}
div.classpaths div:nth-child(odd) {
	background-color:#d2e0ee;
}
