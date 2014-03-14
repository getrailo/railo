<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	

	public void function testSuperByCurrent(){
		savecontent variable="local.c" {
			new JIra2902.Test().testDirect();
		}
		assertEquals("{before:test.cfc}{before:abs.cfc}{absabs.cfc}{after:abs.cfc}{after:test.cfc}",c);
	}

	public void function testSuperByBase(){
		savecontent variable="local.c" {
			new JIra2902.Test().testIndirect();
		}
		assertEquals("{before:test.cfc}{before:abs.cfc}{absabs.cfc}{after:abs.cfc}{after:test.cfc}",c);
	}
} 
</cfscript>