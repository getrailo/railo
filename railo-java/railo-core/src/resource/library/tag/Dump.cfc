component {
	variables.NEWLINE="
";

	variables.baseClassesLookup = {tdBase: 'tdBase', tableDump: 'tableDump', baseHeader: 'baseHeader', header: 'header', meta: 'meta', tdClickName: 'tdClickName'};

	variables.TAB = chr(9);
	variables.default={};
	variables.default.browser="modern";
	variables.default.console="text";
	variables.default.debug="modern";
	variables.supportedFormats=["simple","text","modern","classic"];
	variables.bSuppressType = false;
	variables.c = [1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144,524288,1048576,2097152,4194304,8388608,16777216,33554432,67108864,134217728,268435456,536870912,1073741824,2147483648];

	variables.stLookUp = {
		"pcmxmiqlh0t3"  : "Array",
		"1rvcaasi2aqm4" : "Mongo",
		"85gopel6mgsd"  : "Object",
		"e04eba8zticl"  : "SubXML",
		"15jnbij2ut8kx" : "Query",
		"1onzgocz2cmqa" : "SimpleValue",
		"qmv6wur3y70b"  : "Struct",
		"11qd885fmomo3" : "XML",
		"9b4chzb1lb2r"  : "Component",
		"1az70pyvew10n" : "PublicMethods",
		"1qxnu2flpyax6" : "PrivateMethods",
		"1x1pjgrb7nmix" : "Method"
	};
	
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
- browser: the result is written the the browser response stream (default).
- console: the result is written to the console (System.out).
- false: output will not be written, effectively disabling the dump."},
		metainfo:{required:false,type:"boolean",default:true,hint="Includes information about the query in the cfdump results."},
		keys:{required:false,type:"number",default:9999,hint="For a structure, number of keys to display."},
		hide:{required:false,type:"string",default:"all",hint="hide column or keys."},
		format:{required:false,type:"string",default:"",hint="specify the output format of the dump, the following formats are available by default:
- html - the default browser output
- text - this is the default when outputting to console and plain text in the browser
- classic - classic view with html/css/javascript
- simple - no formatting in the output
You can use your custom style by creating a corresponding file in the railo/dump/skins folder. Check the folder for examples.",
},
		abort:{required:false,type:"boolean",default:false,hint="stops further processing of request."},
		contextlevel:{required:false,type:"number",default:2,hidden:true},
		async:{required:false, type="boolean", default=false, hint="if true and output is not to browser, Railo builds the output in a new thread that runs in parallel to the thread that called the dump.  please note that if the calling thread modifies the data before the dump takes place, it is possible that the dump will show the modified data."},
		export:{required:false, type="boolean", default=false, hint="You can export the serialised dump with this function."}
	};


	/* ==================================================================================================
	   INIT invoked after tag is constructed                                                            =
	================================================================================================== */
	void function init(required boolean hasEndTag, component parent) {

		if(server.railo.version LT "4.2")
			throw message="you need at least version [4.2] to execute this tag";
	}

	/* ==================================================================================================
	   onStartTag                                                                                       =
	================================================================================================== */
	boolean function onStartTag(required struct attributes, required struct caller) {
		// inital settings
		var attrib = arguments.attributes;

		// if output is false, do nothing and exit
		if (attrib.output EQ false) return true;

		// format
		attrib['format'] = trim(attrib.format);
		if (attrib.format eq "html") {
			attrib.format = "modern";
		}
		variables.addJS_CSS = !(attrib.format eq "simple" || attrib.format eq "text");

		if (len(attrib.format) EQ 0) {
			if (attrib.output EQ "console") 
				attrib['format'] = variables.default.console;
			else if (attrib.output EQ "browser" OR attrib.output EQ true)
				attrib['format'] = variables.default.browser;
			else if (attrib.output EQ "debug")
				attrib['format'] = variables.default.debug;
			else
				attrib['format'] = variables.default.browser;
		} else if( !arrayFindNoCase( variables.supportedFormats, attrib.format ) ) {
			if (!fileExists('railo/dump/skins/#attrib.format#.cfm')) {
				directory name="local.allowedFormats" action="list" listinfo="name" directory="railo/dump/skins" filter="*.cfm";
				local.sFormats = valueList(allowedFormats.name);
				sFormats = replaceNoCase(sFormats, ".cfm", "", "ALL");
				throw message="format [#attrib.format#] is invalid. Only the following formats are supported: #sFormats#. You can add your own format by adding a skin file into the directory #expandPath('railo/dump/skin')#.";
			}
		}
		if (attrib.output EQ "debug") {
			attrib.expand = false;
		}

		//eval
		if (not structKeyExists(attrib,'var') and structKeyExists(attrib,'eval')) {
			if(not len(attrib.label))
				attrib['label'] = attrib.eval;

			attrib['var'] = evaluate(attrib.eval, arguments.caller);
		}

		// context
		var context = GetCurrentContext();
		var contextLevel = structKeyExists(attrib,'contextLevel') ? attrib.contextLevel : 2;
		contextLevel = min(contextLevel,arrayLen(context));
		if ( contextLevel gt 0 ) {
			context = context[contextLevel].template & ":" &
					context[contextLevel].line;
		} else {
			context = '[unknown file]:[unknown line]';
		}

		if (attrib['export']) {
			try {
				attrib.var = serialize(attrib.var);
			} catch (e) {
				attrib.var = e.message;
			}
		}

		// create dump struct out of the object
		try {
			var meta = dumpStruct(structKeyExists(attrib,'var') ? attrib.var : nullValue(), attrib.top, attrib.show, attrib.hide, attrib.keys, attrib.metaInfo, attrib.showUDFs, attrib.label);
		}
		catch(e) {
			var meta = dumpStruct(structKeyExists(attrib,'var') ? attrib.var : nullValue(), attrib.top, attrib.show, attrib.hide, attrib.keys, attrib.metaInfo, attrib.showUDFs);
		}
		// set global variables
		variables.format = attrib.format;
		variables.expand = attrib.expand;
		variables.topElement = attrib.eval ?: "var";
		variables.colors = getSafeColors(meta.colors);
		variables.dumpID  = createId();
		variables.context =	'title="' & context & '"';
		variables.hasReference = structKeyExists( meta,'hasReference' ) && meta.hasReference;

		if ( attrib.async && ( attrib.output NEQ "browser" ) ) {
			thread name="dump-#createUUID()#" attrib="#attrib#" meta="#meta#" context="#context#" {
				doOutput( attrib, meta);
			}
		} else {
			doOutput( attrib, meta);
		}

		if( attrib.abort )
			abort;

		return true;
	}

	function doOutput( attrib, meta ) {
		variables.aOutput = [];
		variables.level = 0;

		if (variables.addJS_CSS) {
			setCSS(getCSS(attrib.format), meta.colors);
			setJS();
		}

		// sleep( 5000 );	// simulate long process to test async=true

		if (arguments.attrib.output EQ "browser" OR arguments.attrib.output eq true) {
			if (arguments.attrib.format eq "text") {
				echo('<pre>');
				text( arguments.meta, variables.context );
				writeOutput(arrayToList(variables.aOutput, ""));
				echo('</pre>' & variables.NEWLINE);
			} else {
				echo(variables.NEWLINE & '<!-- ==start== dump #now()# format: #arguments.attrib.format# -->' & variables.NEWLINE);
				if (arguments.attrib.format eq "simple") {
					simple( arguments.meta, variables.context );
					echo( arrayToList(variables.aOutput, "") );
				} else {
					echo('<div id="#variables.dumpID#" class="-railo-dump">');
					html( arguments.meta, variables.context );
					echo( arrayToList(variables.aOutput, "") );
					echo('</div>' & variables.NEWLINE);
				}
				echo('<!-- ==stop== dump -->' & variables.NEWLINE);
			}
		} else {
			if (arguments.attrib.format eq "text") {
				text( arguments.meta, variables.context );
			} else if (arguments.attrib.format eq "simple") {
				simple( arguments.meta, variables.context );
			} else {
				html( arguments.meta, variables.context );
			}
			if (arguments.attrib.output EQ "console") {
				systemOutput(arrayToList(aOutput, ""), true);
			} else if (attrib.output EQ "debug") {
				admin action="addDump" dump="#arrayToList(aOutput, "")#";
			} else {
				file action="write" addnewline="yes" file="#arguments.attrib.output#" output="#arrayToList(aOutput, "")#";
			}
		}
	}

	string function html( required struct meta,
								   string title = "") {
		local.columnCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) : 0;
		//local.sType = ucfirst(variables.format) & (ucfirst(arguments.meta.colorID) ?: (isEmpty(arguments.meta.type ?: "") ? "Other" : listLast(arguments.meta.type, ".")));
		local.sType = ucfirst(variables.format) & (variables.stLookUp[arguments.meta.colorID] ?: (isEmpty(arguments.meta.colorID ?: "") ? "Other" : arguments.meta.colorID));
		//Lookup von ColorID
		local.id    = "";
		local.italic = !variables.expand ? 'style="font-style:italic;"' : '';
		local.hidden = !variables.expand ? 'style="display:none;"' : '';
		arrayAppend(variables.aOutput, '<table class="tableDump#variables.format#" #arguments.title#>' & variables.NEWLINE);
		if(structKeyExists(arguments.meta, 'title')){
			id = createUUID();
			local.metaID = variables.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
			local.comment = structKeyExists(arguments.meta,'comment') ? "<br />" & replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all') : '';
			arrayAppend(variables.aOutput, '<tr class="baseHeader#variables.format# td#sType#Header clickHeader" #italic# onClick="dump_toggle(this, false)">' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '<td class="header#variables.format#" colspan="#columnCount#"><span style="white-space: nowrap">#arguments.meta.title#</span>' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '<span class="meta#variables.format#"><span style="white-space: nowrap">#comment#</span></span></td>' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '</tr>' & variables.NEWLINE);
		}
		if(columnCount) {

			local.stClasses = {
				1: '<td class="tdBase#variables.format# td#sType#Name tdClickName#sType#',
				0: '<td class="tdBase#variables.format# td#sType#Value tdClickValue#sType#'
			};

			local.qMeta = arguments.meta.data;
			local.nodeID = len(id) ? ' name="#id#"' : '';
			local.hidden = !variables.expand && len(id) ? ' style="display:none"' : '';
			loop query="qMeta" {
				arrayAppend(variables.aOutput, '<tr class="trTableDump" #hidden#>' & variables.NEWLINE);
				local.col = 1;
				loop from="1" to="#columnCount-1#" index="col" {
					var node = qMeta["data" & col];

					if(qMeta.highlight EQ 1) {
						local.bColor = col EQ 1 ? 1 : 0;
					} else if(qMeta.highlight EQ 0) {
						local.bColor = 0;
					} else if(qMeta.highlight EQ -1) {
						local.bColor = 1;
					} else {
						local.bColor = bitand(qMeta.highlight, variables.c[col] ?: 0) ? 1 : 0;
					}

					if(isStruct(node)) {
						arrayAppend(variables.aOutput, stClasses[bColor] & '">' & variables.NEWLINE);
						html(node);
						arrayAppend(variables.aOutput, '</td>' & variables.NEWLINE);	
					}
					else {	
/*						
// If you want to suppress the type of an element, just uncomment these lines and set the variable in the corresponding skin method below
if (variables.bSuppressType) {
							if (col eq 1) {
								if (sType neq "ClassicSimpleValue" OR !bColor) {
									arrayAppend(variables.aOutput, stClasses[bColor]);
									arrayAppend(variables.aOutput, '" onClick="dump_toggle(this, true)">#HTMLEditFormat(node)#</div>' & variables.NEWLINE);
								}
							} else {
								arrayAppend(variables.aOutput, stClasses[bColor]);
								arrayAppend(variables.aOutput, '">');
								arrayAppend(variables.aOutput, HTMLEditFormat(node));
								arrayAppend(variables.aOutput, '</div>' & variables.NEWLINE);
							}
						} else { */
							if (col eq 1) {
								arrayAppend(variables.aOutput, stClasses[bColor]);
								if (arguments.meta.colorID eq "1onzgocz2cmqa") { // Simple Values are not clickable
									arrayAppend(variables.aOutput, '">#HTMLEditFormat(node)#</td>' & variables.NEWLINE);
								} else {
									if (arguments.meta.colorID eq "15jnbij2ut8kx" && qMeta.currentRow eq 1) { // Reset removed columns
										arrayAppend(variables.aOutput, ' tdQueryReset" title="Restore columns" onClick="dump_resetColumns(this)">&nbsp;</td>' & variables.NEWLINE);
									} else {
										if (bColor and columnCount eq 3) { // ignore for non query elements, that have several columns
											arrayAppend(variables.aOutput, '" onClick="dump_toggle(this, true)">#HTMLEditFormat(node)#</td>' & variables.NEWLINE);
										} else {
											arrayAppend(variables.aOutput, '">#HTMLEditFormat(node)#</td>' & variables.NEWLINE);
										}
									}
								}
							} else {
								arrayAppend(variables.aOutput, stClasses[bColor]);
								if (arguments.meta.colorID eq "15jnbij2ut8kx" && bColor) { // Allow JS remove columns
									arrayAppend(variables.aOutput, '" title="Click to remove column" onClick="dump_hideColumn(this, #col-1#)"');
								}
								arrayAppend(variables.aOutput, '">');
								arrayAppend(variables.aOutput, HTMLEditFormat(node));
								arrayAppend(variables.aOutput, '</td>' & variables.NEWLINE);
							}
/*						} */
					}
				}
				arrayAppend(variables.aOutput, stClasses[bColor] & '" style="display:none">susi</td></tr>');
			}
		}
		arrayAppend(variables.aOutput, '</table>');
	}

	/* ==================================================================================================
		simple                                                                                          =
	================================================================================================== */

	string function simple( required struct meta,
								   string title = "") {
		local.columnCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) : 0;
		local.id    = "";
		local.stColors = variables.colors[arguments.meta.colorID];
		arrayAppend(variables.aOutput, '<table cellpadding="1" cellspacing="0" border="1" #arguments.title#>' & variables.NEWLINE);
		if(structKeyExists(arguments.meta, 'title')){
			id = createUUID();
			local.metaID = variables.hasReference && structKeyExists(arguments.meta,'id') ? ' [#arguments.meta.id#]' : '';
			local.comment = structKeyExists(arguments.meta,'comment') ? "<br />" & replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all') : '';
			arrayAppend(variables.aOutput, '<tr>' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '<td colspan="#columnCount#" bgColor="#stColors.highLightColor#"><span style="white-space: nowrap">#arguments.meta.title#</span>' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '<span><span style="white-space: nowrap">#comment#</span></span></td>' & variables.NEWLINE);
			arrayAppend(variables.aOutput, '</tr>' & variables.NEWLINE);
		}
		if(columnCount) {

			local.qMeta = arguments.meta.data;
			local.nodeID = len(id) ? ' name="#id#"' : '';
			loop query="qMeta" {
				arrayAppend(variables.aOutput, '<tr>' & variables.NEWLINE);
				local.col = 1;
				loop from="1" to="#columnCount-1#" index="col" {
					var node = qMeta["data" & col];

					if(qMeta.highlight EQ 1) {
						local.sColor = col EQ 1 ? stColors.highLightColor : stColors.normalColor;
					} else if(qMeta.highlight EQ 0) {
						local.sColor = stColors.normalColor;
					} else if(qMeta.highlight EQ -1) {
						local.sColor = stColors.highLightColor;
					} else {
						local.sColor = bitand(qMeta.highlight, variables.c[col] ?: 0) ? stColors.highLightColor : stColors.normalColor;
					}

					if(isStruct(node)) {
						arrayAppend(variables.aOutput, '<td bgcolor="#sColor#">' & variables.NEWLINE);
						simple(node);
						arrayAppend(variables.aOutput, '</td>' & variables.NEWLINE);	
					}
					else {	
						arrayAppend(variables.aOutput, '<td bgcolor="#sColor#">#HTMLEditFormat(node)#</td>' & variables.NEWLINE);
					}
				}
				arrayAppend(variables.aOutput, '</tr>');
			}
		}
		arrayAppend(variables.aOutput, '</table>');
	}

	/* ==================================================================================================
	   text                                                                                             =
	================================================================================================== */
	string function text( required struct meta,
						  string title = "") {

		var dataCount = structKeyExists(arguments.meta,'data') ? listLen(arguments.meta.data.columnlist) - 1 : 0;
		var indent = repeatString("    ", variables.level);
		var type = structKeyExists(arguments.meta,'type') ? arguments.meta.type : '';
		variables.level++;
		// title
		if(structKeyExists(arguments.meta, 'title')) {
			arrayAppend(variables.aOutput, trim(arguments.meta.title));
			arrayAppend(variables.aOutput, structKeyExists(arguments.meta,'comment') ? ' [' & trim(arguments.meta.comment) & ']' : '');
			arrayAppend(variables.aOutput, variables.NEWLINE);
		}

		// data
		if(dataCount GT 0) {
			var qRecords = arguments.meta.data;

			loop query="qRecords" {
				var needNewLine = true;

				for(var x=1; x LTE dataCount; x++) {
					var node = qRecords["data" & x];
					if(needNewLine) {
						arrayAppend(variables.aOutput, variables.NEWLINE & indent);
						needNewLine = false;
					}

					if(type EQ "udf") {
						if(needNewLine) {
							arrayAppend(variables.aOutput, len(trim(node)) EQ 0 ? "[blank] " : node & " ");
						} else {
							arrayAppend(variables.aOutput, len(trim(node)) EQ 0 ? "[blank] " : node & " ");
						}
					} else if(isStruct(node)) {
						this.text(node, "");
					} else if(len(trim(node)) GT 0) {
						arrayAppend(variables.aOutput, node & " ");
					}

				}
			}
		}

		variables.level--;
		return;
	}


	string function createId(){
		return  "x" & createUniqueId();
	}

	string function setCSS( required struct stClasses
						  , required struct dumpStructClasses ) {

		var uFormat = ucFirst(variables.format);

		local.sStyle = '<style type="text/css">';
		// Query reset and remove style
		sStyle &= '.tdQueryReset { background: url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==) no-repeat; height:18px; background-position:2px 4px;}' & variables.NEWLINE;

		loop collection = "#arguments.stClasses.stBaseClasses#" index="local.sKey" item="local.sKeyValue"{
			sStyle &= '.#variables.baseClassesLookup[sKey]##variables.format# ' & sKeyValue.style & variables.NEWLINE;
		};
		// default settings
		loop collection = "#arguments.dumpStructClasses#" index="local.sKey" item="local.sKeyValue"{
			local.sStyleKey = variables.stLookUp[sKey] ?: sKey;
			if (structKeyExists(arguments.stClasses.stCustomClasses, sStyleKey)) {
				local.stStyle = arguments.stClasses.stCustomClasses[sStyleKey];
				sStyle &= '.td' & uFormat & sStyleKey & "Header {background-color:##" & stStyle.headerColor & "; border: #(stStyle.border ?: 1)#px solid ##000; color: ##" & (stStyle.textColorHeader ?: "000") & "} ";
				sStyle &= '.td' & uFormat & sStyleKey & "Name {#(stStyle.pointer ?: 1) ? 'cursor:pointer;' : ''# background-color:##" & stStyle.darkColor & " !important; border: #(stStyle.border ?: 1)#px solid ##000; color: ##" & (stStyle.textColor ?: "000") & "} ";
				sStyle &= '.td' & uFormat & sStyleKey & "Value {background-color:##" & stStyle.lightColor & "; border: #(stStyle.border ?: 1)#px solid ##000; color: ##" & (stStyle.textColor ?: "000") & "} ";
				sStyle &= variables.NEWLINE;
			} else {
				local.createdDumpStructStyle = '.td#uFormat##sStyleKey#Header {background-color: #arguments.dumpStructClasses[sKey]['highlightColor']#; border: 1px solid #arguments.dumpStructClasses[sKey].borderColor#; color: #arguments.dumpStructClasses[sKey].fontColor#} '
				&'.td#uFormat##sStyleKey#Name {cursor: pointer;background-color: #arguments.dumpStructClasses[sKey]['highlightColor']#; border: 1px solid #arguments.dumpStructClasses[sKey].borderColor#; color: #arguments.dumpStructClasses[sKey].fontColor#} '
				&'.td#uFormat##sStyleKey#Value {background-color: #arguments.dumpStructClasses[sKey]['normalColor']#; border: 1px solid #arguments.dumpStructClasses[sKey].borderColor#; color: #arguments.dumpStructClasses[sKey].fontColor#} ';
				sStyle &= createdDumpStructStyle & variables.NEWLINE;
			}
		}
		sStyle &= '</style>';
		arrayAppend(variables.aOutput, sStyle);
	}

	string function setJS() {
		arrayAppend(variables.aOutput, '<script type="text/javascript">' & variables.NEWLINE);
		arrayAppend(variables.aOutput, 'var sBase = "' & variables.topElement & '";' & variables.NEWLINE);

/* 		arrayAppend(variables.aOutput, "
//	Uncomment, if you want to work on the real Javascript code

function dump_toggle(oObj, bReplaceInfo){
	var node = oObj;
	node = node.nextElementSibling || node.nextSibling;
	while( node && (node.nodeType === 1) && (node !== oObj)) {
		var oOldNode = node;
		s = oOldNode.style;
		if(s.display=='none') {
			s.display='';
		} else {
			s.display='none';
		}
		node = node.nextElementSibling || node.nextSibling;
	}
	if (oObj.style.fontStyle=='italic') {
		oObj.style.fontStyle='normal';
	} else {
		oObj.style.fontStyle='italic';
	}
	if (bReplaceInfo) {
		var oParentNode = oObj;
		var sText = '';
		var sInnerText = '';
		while (oParentNode) {
			sText = oParentNode.innerHTML;
			if (isNumber(sText)) { 
				sText = '[' + sText + ']'; 
			} 
			if (sInnerText == '') {
				sInnerText = sText;
			} else {
				sInnerText = sText + (sInnerText.substring(0,1) == '[' ? '' : '.') + sInnerText;
			}
			oParentNode = getNextLevelUp(oParentNode);
		}
		oOldNode.innerHTML = sBase + (sInnerText.substring(0,1) == '[' ? '' : '.') + sInnerText;
		selectText(oOldNode);
	}
}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

function getNextLevelUp(oNode) {
	oCurrent = oNode;
	while (oNode) {
		oNode = oNode.parentNode;
		if (oNode && oNode.className && oNode.className.toUpperCase() == 'TRTABLEDUMP') {
			if (!oNode.firstElementChild) {
				var oTmp = oNode.children[0];
			} else {
				var oTmp = oNode.firstElementChild;
			}
			if (oTmp) {
				if (oTmp !== oCurrent && oTmp.className.toUpperCase().indexOf('NAME TDCLICK') != -1) {
					return oTmp;
				}
			}
			oNode = oNode.parentNode;
		}
	}
	return;
}

function selectText(oElement) {
	if (document.body.createTextRange) { // ms
		var range = document.body.createTextRange();
		range.moveToElementText(oElement);
		range.select();
	} else if (window.getSelection) { // moz, opera, webkit
		var selection = window.getSelection();			
		var range = document.createRange();
		range.selectNodeContents(oElement);
		selection.removeAllRanges();
		selection.addRange(range);
	}
}

function dump_hideColumn(oObj, iCol) {
	var oNode = oObj.parentNode;
	while( oNode && (oNode.nodeType === 1)) {
		var oChildren = oNode.children;
		if (oChildren[iCol] && oChildren[iCol].tagName == 'TD') {
			oChildren[iCol].style.display = 'none';
		}
		oNode = oNode.nextElementSibling || oNode.nextSibling;
	}

}

function dump_resetColumns(oObj, iCol) {
	var oNode = oObj.parentNode;
	while( oNode && (oNode.nodeType === 1)) {
		var oChildren = oNode.children;
		for (var i=0;i<oChildren.length-1;i++) {
			if (i == oChildren.length-1) {
				oChildren[i].style.display = 'none';
			} else {
				oChildren[i].style.display = '';
			}
		}
		oNode = oNode.nextElementSibling || oNode.nextSibling;
	}
}
"); */

		// compressed JS Version
		arrayAppend(variables.aOutput, 'function dump_toggle(e,t){var n=e;n=n.nextElementSibling||n.nextSibling;while(n&&n.nodeType===1&&n!==e){var r=n;s=r.style;if(s.display=="none"){s.display=""}else{s.display="none"}n=n.nextElementSibling||n.nextSibling}if(e.style.fontStyle=="italic"){e.style.fontStyle="normal"}else{e.style.fontStyle="italic"}if(t){var i=e;var o="";var u="";while(i){o=i.innerHTML;if(isNumber(o)){o="["+o+"]"}if(u==""){u=o}else{u=o+(u.substring(0,1)=="["?"":".")+u}i=getNextLevelUp(i)}r.innerHTML=sBase+(u.substring(0,1)=="["?"":".")+u;selectText(r)}}function isNumber(e){return!isNaN(parseFloat(e))&&isFinite(e)}function getNextLevelUp(e){oCurrent=e;while(e){e=e.parentNode;if(e&&e.className&&e.className.toUpperCase()=="TRTABLEDUMP"){if(!e.firstElementChild){var t=e.children[0]}else{var t=e.firstElementChild}if(t){if(t!==oCurrent&&t.className.toUpperCase().indexOf("NAME TDCLICK")!=-1){return t}}e=e.parentNode}}return}function selectText(e){if(document.body.createTextRange){var t=document.body.createTextRange();t.moveToElementText(e);t.select()}else if(window.getSelection){var n=window.getSelection();var t=document.createRange();t.selectNodeContents(e);n.removeAllRanges();n.addRange(t)}}function dump_hideColumn(e,t){var n=e.parentNode;while(n&&n.nodeType===1){var r=n.children;if(r[t]&&r[t].tagName=="TD"){r[t].style.display="none"}n=n.nextElementSibling||n.nextSibling}}function dump_resetColumns(e,t){var n=e.parentNode;while(n&&n.nodeType===1){var r=n.children;for(var i=0;i<r.length-1;i++){if(i==r.length-1){r[i].style.display="none"}else{r[i].style.display=""}}n=n.nextElementSibling||n.nextSibling}}');

		arrayAppend(variables.aOutput, '</script>' & variables.NEWLINE);
	}

	private struct function getCSS( string sFormat = "modern" ) {
		
		var stClasses = { 
			stCustomClasses : {
				Array:{headerColor:'9c3', darkColor:'9c3', lightColor:'cf3' },
				Mongo:{headerColor:'393', darkColor:'393', lightColor:'966' },
				Object:{headerColor:'c99', darkColor:'c99', lightColor:'fcc' },
				Query:{headerColor:'c9c', darkColor:'c9c', lightColor:'fcf' },
				SimpleValue:{headerColor:'f60', darkColor:'f60', lightColor:'fc9', pointer:0 },
				Struct:{headerColor:'99f', darkColor:'99f', lightColor:'ccf' },
				SubXML:{headerColor:'996', darkColor:'996', lightColor:'cc9' }, 
				XML:{headerColor:'c99', darkColor:'c99', lightColor:'fff' },
				white: {headerColor:'fff', darkColor:'fff', lightColor:'ccc' },
				Component:{headerColor:'9c9', darkColor:'9c9', lightColor:'cfc'},
				PublicMethods:{headerColor:'fc9', darkColor:'fc9', lightColor:'ffc'},
				PrivateMethods:{headerColor:'fc3', darkColor:'fc3', lightColor:'f96'},
				Method:{headerColor:'c6f', darkColor:'c6f', lightColor:'fcf'}
			},
			stBaseClasses : {
				tdBase: {		style:'{border: 1px solid ##000;padding: 2px;vertical-align: top;}'},
				tableDump: {	style:'{font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;font-size: 11px;background-color: ##eee;color: ##000;border-spacing: 1px;border-collapse:separate;}'},
				baseHeader: {	style:'{border: 1px solid ##000;padding: 2px;text-align: left;vertical-align: top;cursor:pointer;margin: 1px 1px 0px 1px;}'},
				header: {		style:'{font-weight: bold; border:1px solid ##000;}'},
				meta: {			style:'{font-weight: normal}'},
				tdClickName: { 	style:'{empty-cells: show}'}
			}
		};
		
		if (sFormat != "modern" && fileExists("railo/dump/skins/#arguments.sFormat#.cfm")) {			
			include "railo/dump/skins/#arguments.sFormat#.cfm";
		}
		
		return stClasses;
	}

	private struct function getSafeColors(required struct stColors) {
		local.stRet = {};
		loop collection="#arguments.stColors#" index="local.sKey" item="local.stColor" {
			loop collection="#stColor#" index="local.sColorName" item="local.sColor" {
				if (len(sColor) eq 4) {
					sColor = "##" & sColor[2] & sColor[2] & sColor[3] & sColor[3] & sColor[4] & sColor[4];
				}
				stRet[sKey][sColorName] = sColor;
			}
		}
		return stRet;
	}

}