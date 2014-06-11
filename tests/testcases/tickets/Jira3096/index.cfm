<cfsetting showdebugoutput="no">
<cfscript>
	// first check if the jar is availble in general
	echo(createObject("java","a.Test").init().hello("Susi"));
	
echo("-");

	// now we do a proxy for the interface
	proxy=createDynamicProxy(new Test(),"a.ITest");
	echo(proxy.hello("Urs"));
</cfscript>