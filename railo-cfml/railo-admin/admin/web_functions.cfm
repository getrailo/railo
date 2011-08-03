<cfscript>
/**
* cast a String to a File Object
* @param strFile string to cast
* @return File Object
*/
function printError(error,boolean longversion=false) {
	if(IsSimpleValue(error))error=struct(message:error);
	if(not StructKeyExists(error,'detail'))error.detail="";
	else if(error.message EQ error.detail)error.detail="";
	// if(!IsSimpleValue(error) && error.getClass().getName() EQ "railo.runtime.exp.CatchBlock")createObject("java","railo.print").e(error.getPageException())
	if(StructKeyExists(arguments.error,'message') and arguments.error.message NEQ "") {
		writeOutput('<span class="CheckError">');
		writeOutput(br(arguments.error.message));
		writeOutput('<br>');
		writeOutput(br(arguments.error.detail));
		
		if(longversion) {
			if(StructKeyExists(error,"TagContext")){
				if(arrayLen(error.TagContext)){
					writeOutput('<br>');
					writeOutput('<b>error occured in '&listLast(error.TagContext[1].template,'/\')&':'&error.TagContext[1].line&'</b>');
					writeOutput('<br>');
					writeOutput(error.TagContext[1].codePrintHTML);
					//dump(error.TagContext[1]);
				}
			}
		}
		//ErrorCode,addional,TagContext,StackTrace,type,Detail,Message,ExtendedInfo
		writeOutput('</span><br><br>');
	}
}

function noAccess(string text){
	writeOutput('<div class="CheckError">#text#</div></cfoutput><br /><br />');
}

function br(str) {
	str=replace(str,'
','<br>','all');

	return str;
}


/**
* cast a String to a File Object
* @param strFile string to cast
* @return File Object
*/
function toInt(number obj) {
	return obj;
}

function two(number) {
	if(number LT 10) return "0"&number;
	return number;
}

/**
* read all elements from form scope with pattern <arguments.fieldname>_<int>
* @param fieldName wished field name to extract
* @return Array with all values
*/
function toArrayFromForm(fieldName) {
	var len=len(fieldName)+1;
	var rtn=array();
	for(var key in form) {
		if(findNoCase(fieldName&"_",key) EQ 1) {
			var index=right(key,len(key)-len);
			if(isNumeric(index))rtn[index]=form[key];
		}
	}
	return rtn;
}

/**
* returns a specified line from a query as struct
*/
function queryRow2Struct(query,row) {
	var sct=struct();
	var columns=listToArray(query.columnlist);
	for(var el in columns) {
		sct[el]=query[el][row];
	}
	return sct;
}

function nullIfNoDate(fieldName) {
	var d=trim(form[fieldName&"_day"]);
	var m=trim(form[fieldName&"_month"]);
	var y=trim(form[fieldName&"_year"]);
	if(isNumeric(d) and isNumeric(m) and isNumeric(y)) {
		return CreateDate(y,m,d);
	}
}

function nullIfNoTime(fieldName) {
	var h=trim(form[fieldName&"_hour"]);
	var m=trim(form[fieldName&"_minute"]);
	var s=0;
	if(structKeyExists(form,fieldName&"_second") and isNumeric(trim(form[fieldName&"_second"]))) s=trim(form[fieldName&"_second"]);
	if(isNumeric(h) and isNumeric(m)) {
		return CreateTime(h,m,s);
	}
}

function toStructInterval(raw) {
	var interval.raw=raw;
	interval.second=raw;
	interval.minute=0;
	interval.hour=0;
		
	if(interval.second GTE 60*60) {
		interval.hour=int(interval.second/(60*60));
		_hour=interval.hour*60*60;
		interval.second=interval.second-_hour;
	}
		
	if(interval.second GTE 60) {
		interval.minute=int(interval.second/60);
		_minute=interval.minute*60;
		interval.second=interval.second-_minute;
	}
	return interval;
}

function cut(_str,max) {
	if(not isDefined('_str') or len(_str) EQ 0) return "&nbsp;";
	if(len(_str) GT max) return left(_str,max)&"...";
	return _str;
}

function getForm(formKey, default) {
	if(not structKeyExists(form,formKey)) return default;
	return form[formKey];
}

function go(action,action2='',others=struct()) {
	
	var qsArr=listToArray(cgi.query_string,'&');
	var rtn=request.self&"?action="&action;
	if(len(action2)) rtn=rtn&"&action2="&action2;
	var item="";
	var oKeys=structKeyArray(others);
	
	for(var i=1; i LTE arrayLen(qsArr); i=i+1) {
		item=listToArray(qsArr[i],'=');
		if(not structKeyExists(others,item[1]) and item[1] NEQ "action" and item[1] NEQ "action2") {
			rtn=rtn&'&'&item[1]&"="&item[2];
		}
	}
	
	for(i=1; i LTE arrayLen(oKeys); i=i+1) {
		rtn=rtn&'&'&oKeys[i]&"="&others[oKeys[i]];
	}
	
	
	return rtn;
}

/*
// Config
config.web=getPageContext().getConfig();
config.server=config.web.configServer;
config=config[request.adminType];

// SecurityManager
securityManager=config.securityManager;
smClass=createObject('java','railo.runtime.security.SecurityManager');
ACCESS.YES= smClass.VALUE_YES;
ACCESS.NO= smClass.VALUE_NO;
ACCESS.LOCAL= smClass.VALUE_LOCAL;
ACCESS.NONE= smClass.VALUE_NONE;
ACCESS.ALL= smClass.VALUE_ALL;

ACCESS.CFX_USAGE=securityManager.getAccess(smClass.TYPE_CFX_USAGE);
*/
</cfscript>