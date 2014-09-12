component  {
	variables.validNames={
		'web.cfm':''
		,'server.cfm':''
		,'jquery-1.7.2.min.js.cfm':''
		,'jquery.blockUI.js.cfm':''
		,'admin.js.cfm':''
		,'thumbnail.cfm':''
		,'admin.cfm':''
		,'graph.cfm':''
	};

	this.name="webadmin";
	this.clientmanagement="no";
	this.clientstorage="file"; 
	this.scriptprotect="all";
	this.sessionmanagement="yes";
	this.sessiontimeout="#createTimeSpan(0,0,30,0)#";
	this.setclientcookies="yes";
	this.setdomaincookies="no"; 
	this.applicationtimeout="#createTimeSpan(1,0,0,0)#";
	this.localmode="update";
	this.web.charset="utf-8";


	function onRequest(string template){
		setting showdebugoutput=false;
		var name=listLast(arguments.template,'\/');

		if(find('..',arguments.template) || !structKeyExists(variables.validNames,name)) {
			header statuscode="404" statustext="#arguments.template# Not Found";
			content type="text/html";
			echo('<html>');
			echo('<title>Error 404 #arguments.template# Not Found</title>');
			echo('<head></head>');
			echo('<body>');
			echo('<h2>HTTP ERROR: 404 #arguments.template# Not Found</h2>');
			echo('<p>RequestURI=#arguments.template#</p>');
		}
		else 
			include arguments.template;

	}
}