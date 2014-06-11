<cfsetting showdebugoutput="false">
<cfscript>
	com1 = new Component1();
	if(url.type=="closure")com1.testClosure();
	if(url.type=="udf")com1.testUDF();
	abort;
</cfscript>