<cfcomponent><cfscript>

this.name="railo_context";
this.clientmanagement="no";
this.clientstorage="file"; 
this.scriptprotect="none";
this.sessionmanagement="yes";
this.sessiontimeout="#createTimeSpan(0,0,30,0)#";
this.setclientcookies="yes";
this.setdomaincookies="no"; 
this.applicationtimeout="#createTimeSpan(1,0,0,0)#";


</cfscript></cfcomponent>