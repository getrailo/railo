<cfscript>
component {

	ColorCaster=createObject('java','railo.commons.color.ColorCaster');
	NEWLINE="
";
	TAB = chr(9);
	default={};
	default.browser="html";
	default.console="text";
	supportedFormats=["simple","text","html","classic"];

	// Meta data
	this.metadata.hint="Outputs the elements, variables and values of most kinds of CFML objects. Useful for debugging. You can display the contents of simple and complex variables, objects, components, user-defined functions, and other elements.";
	this.metadata.attributetype="fixed";
	this.metadata.attributes={
		var:{required:false,type:"any",hint="Variable to display. Enclose a variable name in pound signs."},
		eval:{required:false,type:"any",hint="name of the variable to display, also used as label, when no label defined."},
		expand:{required:false,type:"boolean",default:true,hint="expands views"},
		label:{required:false,type:"string",default:"",hint="header for the dump output."},
		top:{required:false,type:"number",default:9999,hint="The number of rows to display."},
		showUDFs:{required:false,type:"boolean",default:true,hint="show UDFs in cfdump output."},
		show:{required:false,type:"string",default:"all",hint="show column or keys."},
		output:{required:false,type:"string",default:"browser",hint="Where to send the results:
- console: the result is written to the console (System.out).
- browser (default): the result is written the the browser response stream."},
		metainfo:{required:false,type:"boolean",default:true,hint="Includes information about the query in the cfdump results."},
		keys:{required:false,type:"number",default:9999,hint="For a structure, number of keys to display."},
		hide:{required:false,type:"string",default:"all",hint="hide column or keys."},
		format:{required:false,type:"string",default:"",hint="specify the output format of the dump, the following formats are supported:
- simple: - a simple html output (no javascript or css)
- text (default when output equal console): plain text output (no html)
- html (default when output  equal ""browser""): regular output with html/css/javascript
- classic: classic view with html/css/javascript"},
		abort:{required:false,type:"boolean",default:false,hint="stops further processing of request."}
	};


	/* ==================================================================================================
	   INIT invoked after tag is constructed                                                            =
	================================================================================================== */
	void function init(required boolean hasEndTag, component parent) {

		if(server.railo.version LT "3.1.1.011")
			throw message="you need at least version [3.1.1.011] to execute this tag";
	}

	/* ==================================================================================================
	   onStartTag                                                                                       =
	================================================================================================== */
	boolean function onStartTag(required struct attributes, required struct caller) {
		// inital settings
		var dumpID = "dump-" & hash(CreateUUID());
		var attrib = arguments.attributes;

		//eval
		if(not structKeyExists(attrib,'var') and structKeyExists(attrib,'eval')) {
			if(not len(attrib.label))
				attrib['label'] = attrib.eval;

			attrib['var'] = evaluate(attrib.eval, arguments.caller);
		}

		// context
		var context = GetCurrentContext();
		var contextLevel = structKeyExists(attrib,'contextLevel') ? attrib.contextLevel : 2;
		context = context[contextLevel].template & ":" & context[contextLevel].line;

		// format
		attrib['format'] = trim(attrib.format);

		if(len(attrib.format) EQ 0) {
			if(attrib.output EQ "console")      attrib['format'] = default.console;
			else if(attrib.output EQ "browser") attrib['format'] = default.browser;
			else                                attrib['format'] = default.console;
		}
		else if(not arrayFind(supportedFormats, attrib.format)){
			throw message="format [#attrib.format#] is not supported, supported formats are [#arrayToList(supportedFormats)#]";
		}

		// create dump struct out of the object
		try {
			var meta = dumpStruct(structKeyExists(attrib,'var') ? attrib.var : nullValue(), attrib.top, attrib.show, attrib.hide, attrib.keys, attrib.metaInfo, attrib.showUDFs, attrib.label);
		}
		catch(e) {
			var meta = dumpStruct(structKeyExists(attrib,'var') ? attrib.var : nullValue(), attrib.top, attrib.show, attrib.hide, attrib.keys, attrib.metaInfo, attrib.showUDFs);
		}

		// create output
		var hasReference = structKeyExists(meta,'hasReference') && meta.hasReference;
		var result = this[attrib.format](meta, context, attrib.expand, attrib.output, hasReference, 0, dumpID);

		// output
		if(attrib.output EQ "browser") {
			echo(NEWLINE & '<!-- ==start== dump #now()# format: #attrib.format# -->' & NEWLINE);
			echo('<div id="#dumpID#">#result#</div>' & NEWLINE);
			echo('<!--  ==stop== dump -->' & NEWLINE);
		}
		else if(attrib.output EQ "console") {
			systemOutput(result);
		}
		else {
			file action="write" addnewline="yes" file="#attrib.output#" output="#result#";
		}

		// abort
		if(attrib.abort) abort;

		return true;
	}

	/* ==================================================================================================
	   html                                                                                             =
	================================================================================================== */
	string function html( required struct meta,
						  string context = "",
						  string expand = "",
						  string output = "",
						  string hasReference = false,
						  string level = 0,
						  string dumpID = "" ) {

		var id = "_dump" & hash(CreateUUID());
		var rtn = "";
		var columnCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) : 0;
		var title = !arguments.level ? 'title="#arguments.context#" ' : '';
		var width = structKeyExists(arguments.meta,'width') ? ' width="' & arguments.meta.width & '"' : '';
		var height = structKeyExists(arguments.meta,'height') ? ' height="' & arguments.meta.height & '"' : '';
		var indent = repeatString(TAB, arguments.level);

		savecontent variable="rtn" {
			if(arguments.level EQ 0){
				// javascript
				echo("<script>" & NEWLINE);
				echo("function dumpOC(name){" & NEWLINE);
				echo(TAB & "var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);" & NEWLINE);
				echo(TAB & "var s=null;" & NEWLINE);
				echo(TAB & "name='_'+name;" & NEWLINE);
				echo(TAB & "for(var i=0;i<tds.length;i++) {" & NEWLINE);
				echo(TAB & TAB & "if(document.all && tds[i].name!=name)continue;" & NEWLINE);
				echo(TAB & TAB & "s=tds[i].style;" & NEWLINE);
				echo(TAB & TAB & "if(s.display=='none') s.display='';" & NEWLINE);
				echo(TAB & TAB & "else s.display='none';" & NEWLINE);
				echo(TAB & "}" & NEWLINE);
				echo("}" & NEWLINE);
				echo("</script>" & NEWLINE);

				// styles
				echo('<style type="text/css">' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# table {font-family:Verdana, Geneva, Arial, Helvetica, sans-serif; font-size:11px; empty-cells:show; color:#arguments.meta.fontColor#;}' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# td {border:1px solid #arguments.meta.borderColor#; vertical-align:top; padding:2px; empty-cells:show;}' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# td span {font-weight:bold;}' & NEWLINE);
				echo('</style>' & NEWLINE);
			}

			echo(indent & '<table cellspacing="1"#width##height#>' & NEWLINE);

			// title
			if(structKeyExists(arguments.meta, 'title')){
				var metaID = arguments.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
				var comment = structKeyExists(arguments.meta,'comment') ? "<br />" & replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all') : '';

				echo(indent & '<tr>' & NEWLINE);
				echo(indent & TAB & '<td title="#arguments.context#" onclick="dumpOC(''#id#'');" colspan="#columnCount#" style="background:#arguments.meta.highLightColor#;">');
				echo('<span>#arguments.meta.title##metaID#</span>');
				echo(comment & '</td>' & NEWLINE);
				echo(indent & '</tr>' & NEWLINE);
			}
			else {
				id = "";
			}

			// data
			if(columnCount) {
				loop query="arguments.meta.data" {
					var c = 1;
					var nodeID = len(id) ? ' name="_#id#"' : '';
					var hidden = !arguments.expand && len(id) ? ' style="display:none"' : '';

					echo(indent & '<tr#nodeID##hidden#>' & NEWLINE);

					for(var col=1; col LTE columnCount-1; col++) {
						var node = arguments.meta.data["data" & col];

						if(isStruct(node)) {
							var value = this.html(node, "", arguments.expand, arguments.output, arguments.hasReference, arguments.level+1);

							echo(indent & TAB & '<td #title#style="background:#bgColor(arguments.meta,c)#;">' & NEWLINE);
							echo(value);
							echo(indent & TAB & '</td>' & NEWLINE);
						}
						else {
							echo(indent & TAB & '<td #title#style="background:#bgColor(arguments.meta,c)#;">' & HTMLEditFormat(node) & '</td>' & NEWLINE);
						}
						c *= 2;
					}
					echo(indent & '</tr>' & NEWLINE);
				}
			}
			echo(indent & '</table>' & NEWLINE);
		}
		return rtn;
	}

	/* ==================================================================================================
	   classic                                                                                          =
	================================================================================================== */
	string function classic( required struct meta,
							 string context = "",
							 string expand = "",
							 string output = "",
							 string hasReference = false,
							 string level = 0,
							 string dumpID = "" ) {

		var id = "_dump" & hash(CreateUUID());
		var rtn = "";
		var columnCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) : 0;
		var title = !arguments.level ? 'title="#arguments.context#" ' : '';
		var width = structKeyExists(arguments.meta,'width') ? ' width="' & arguments.meta.width & '"' : '';
		var height = structKeyExists(arguments.meta,'height') ? ' height="' & arguments.meta.height & '"' : '';
		var indent = repeatString(TAB, arguments.level);

		// define colors
		var h1Color = arguments.meta.highLightColor;
		var h2Color = arguments.meta.normalColor;
		var borderColor = arguments.meta.highLightColor;

		arguments.meta.normalColor = "white";

		try {
			borderColor = ColorCaster.toHexString(ColorCaster.toColor(h1Color).darker().darker());
		}
		catch(e) {}

		savecontent variable="rtn" {
			if(arguments.level EQ 0){
				// javascript
				echo("<script>" & NEWLINE);
				echo("function dumpOC(name){" & NEWLINE);
				echo(TAB & "var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);" & NEWLINE);
				echo(TAB & "var s=null;" & NEWLINE);
				echo(TAB & "name='_'+name;" & NEWLINE);
				echo(TAB & "for(var i=0;i<tds.length;i++) {" & NEWLINE);
				echo(TAB & TAB & "if(document.all && tds[i].name!=name)continue;" & NEWLINE);
				echo(TAB & TAB & "s=tds[i].style;" & NEWLINE);
				echo(TAB & TAB & "if(s.display=='none') s.display='';" & NEWLINE);
				echo(TAB & TAB & "else s.display='none';" & NEWLINE);
				echo(TAB & "}" & NEWLINE);
				echo("}" & NEWLINE);
				echo("</script>" & NEWLINE);

				// styles
				echo('<style type="text/css">' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# table {font-family:Verdana, Geneva, Arial, Helvetica, sans-serif; font-size:11px; empty-cells:show; color:#arguments.meta.fontColor#; border: 1px solid black; border-collapse:collapse;}' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# td {border:1px solid #arguments.meta.borderColor#; vertical-align:top; padding:2px; empty-cells:show;}' & NEWLINE);
				echo(TAB & 'div###arguments.dumpID# td span {font-weight:bold;}' & NEWLINE);
				echo('</style>' & NEWLINE);
			}

			echo(indent & '<table cellspacing="0"#width##height# style="color:#arguments.meta.fontColor#; border-color:#borderColor#;">' & NEWLINE);

			// title
			if(structKeyExists(arguments.meta, 'title')){
				var metaID = arguments.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
				var comment = structKeyExists(arguments.meta,'comment') ? "<br />" & replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all') : '';

				echo(indent & '<tr>' & NEWLINE);
				echo(indent & TAB & '<td title="#arguments.context#" onclick="dumpOC(''#id#'');" colspan="#columnCount#" style="background:#h1Color#; border-color:#borderColor#; color:white;">');
				echo('<span>#arguments.meta.title##metaID#</span>');
				echo(comment & '</td>' & NEWLINE);
				echo(indent & '</tr>' & NEWLINE);
			}
			else {
				id = "";
			}

			// data
			if(columnCount) {
				loop query="arguments.meta.data" {
					var c = 1;
					var nodeID = len(id) ? ' name="_#id#"' : '';
					var hidden = !arguments.expand && len(id) ? ' style="display:none"' : '';

					echo(indent & '<tr#nodeID##hidden#>' & NEWLINE);

					for(var col=1; col LTE columnCount-1; col++) {
						var node = arguments.meta.data["data" & col];

						if(isStruct(node)) {
							var value = this.classic(node, "", arguments.expand, arguments.output, arguments.hasReference, arguments.level+1);

							echo(indent & TAB & '<td #title#style="background:#bgColor(arguments.meta,c,h2Color)#; border-color:#borderColor#;">' & NEWLINE);
							echo(value);
							echo(indent & TAB & '</td>' & NEWLINE);
						}
						else {
							echo(indent & TAB & '<td #title#style="background:#bgColor(arguments.meta,c,h2Color)#; border-color:#borderColor#;">' & HTMLEditFormat(node) & '</td>' & NEWLINE);
						}
						c *= 2;
					}
					echo(indent & '</tr>' & NEWLINE);
				}
			}
			echo(indent & '</table>' & NEWLINE);
		}
		return rtn;
	}

	/* ==================================================================================================
	   simple                                                                                           =
	================================================================================================== */
	string function simple( required struct meta,
							string context = "",
							string expand = "",
							string output = "",
							string hasReference = false,
							string level = 0 ) {

		var rtn = "";
		var col = 0;
		var columnCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) : 0;
		var width = structKeyExists(arguments.meta,'width') ? ' width="' & arguments.meta.width & '"' : '';
		var height = structKeyExists(arguments.meta,'height') ? ' height="' & arguments.meta.height & '"' : '';
		var indent = repeatString(TAB, arguments.level);

		savecontent variable="rtn" {
			echo(indent & '<table cellpadding="1" cellspacing="0" border="1"#width##height#>' & NEWLINE);

			// title
			if(structKeyExists(arguments.meta, 'title')){
				var metaID = arguments.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
				var comment = structKeyExists(arguments.meta,'comment') ? "<br />" & replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all') : '';

				echo(indent & '<tr>' & NEWLINE);
				echo(indent & TAB & '<td title="#arguments.context#" colspan="#columnCount#" bgcolor="#arguments.meta.highLightColor#">');
				echo('<b>#arguments.meta.title##metaID#</b>');
				echo(comment & '</td>' & NEWLINE);
				echo(indent & '</tr>' & NEWLINE);
			}

			// data
			var c = 1;
			if(columnCount) {
				loop query="arguments.meta.data" {
					c = 1;

					echo(indent & '<tr>' & NEWLINE);

					for(col=1; col LTE columnCount-1; col++) {
						var node = arguments.meta.data["data" & col];

						if(isStruct(node)) {
							var value = this.simple(node, "", arguments.expand, arguments.output, arguments.hasReference, arguments.level+1);

							echo(indent & TAB & '<td title="#arguments.context#" bgcolor="#bgColor(arguments.meta,c)#">' & NEWLINE);
							echo(value);
							echo(indent & TAB & '</td>' & NEWLINE);
						}
						else {
							echo(indent & TAB & '<td title="#arguments.context#" bgcolor="#bgColor(arguments.meta,c)#">' & HTMLEditFormat(node) & '</td>' & NEWLINE);
						}
						c *= 2;
					}
					echo(indent & '</tr>' & NEWLINE);
				}
			}
			echo(indent & '</table>' & NEWLINE);
		}
		return rtn;
	}

	/* ==================================================================================================
	   text                                                                                             =
	================================================================================================== */
	string function text( required struct meta,
						  string context = "",
						  string expand = "",
						  string output = "",
						  string hasReference = false,
						  string level = 0,
						  string parentIndent = "" ) {

		var rtn = "";
		var dataCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) - 1 : 0;
		var indent = repeatString("    ", arguments.level);
		var type = structKeyExists(arguments.meta,'type') ? arguments.meta.type : '';

		// title
		if(structKeyExists(arguments.meta, 'title')) {
			rtn = trim(arguments.meta.title);
			rtn &= arguments.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
			rtn &= structKeyExists(arguments.meta,'comment') ? ' [' & trim(arguments.meta.comment) & ']' : '';
			rtn &= NEWLINE;
		}

		// data
		if(dataCount GT 0) {
			var qRecords = arguments.meta.data;

			loop query="qRecords" {
				var needNewLine = true;

				for(var x=1; x LTE dataCount; x++) {
					var node = qRecords["data" & x];

					if(type EQ "udf") {
						if(needNewLine) {
							rtn &= NEWLINE & arguments.parentIndent;
							rtn &= len(trim(node)) EQ 0 ? "[blank] " : node & " ";
							needNewLine = false;
						}
						else {
							rtn &= len(trim(node)) EQ 0 ? "[blank] " : node & " ";
						}
					}
					else if(isStruct(node)) {
						rtn &= this.text(node, "", arguments.expand, arguments.output, arguments.hasReference, arguments.level+1, indent) & NEWLINE;
					}
					else if(len(trim(node)) GT 0) {
						var test = asc(right(rtn, 1));

						if( test EQ 10 || test EQ 13) {
							rtn &= arguments.parentIndent & node & " ";
						}
						else {
							rtn &= node & " ";
						}

					}

				}
			}
		}
		if(arguments.output NEQ "console" && arguments.level EQ 0) {
			return "<pre>" & rtn & "</pre>";
		}

		return rTrim(rtn);
	}

	/* ==================================================================================================
	   bgColor                                                                                          =
	================================================================================================== */
	string function bgColor( required struct meta,
							 required numeric c,
							 string highLightColor = "#arguments.meta.highLightColor#" ) {

		if(arguments.meta.data.highlight EQ -1) {
			return highLightColor;
		}
		else if(arguments.meta.data.highlight EQ 0) {
			return arguments.meta.normalColor;
		}
		else {
			return bitand(arguments.meta.data.highlight, c) ? highLightColor : arguments.meta.normalColor;
		}
	}
}
</cfscript>