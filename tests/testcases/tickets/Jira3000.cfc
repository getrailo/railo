<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	private void function test(){
		savecontent variable="local.c" {
			iterations = 0;
			[1,2,3,4].each(function(v,i){
				iterations++;
				writeOutput("#iterations#-");
			});
		}
		assertEquals("1-2-3-4-",c);
	}

	public void function testWithStrictScopeCacading(){
		try{
			var defaultScopeCascading=getApplicationSettings().scopeCascading;
			application action="update" scopeCascading="strict";
			test();
		}
		finally {
			application action="update" scopeCascading="#defaultScopeCascading#";
		}	
	}


	public void function testWithSmallScopeCacading(){
		try{
			var defaultScopeCascading=getApplicationSettings().scopeCascading;
			application action="update" scopeCascading="small";
			test();
		}
		finally {
			application action="update" scopeCascading="#defaultScopeCascading#";
		}	
	}


	public void function testWithStandardScopeCacading(){
		try{
			var defaultScopeCascading=getApplicationSettings().scopeCascading;
			application action="update" scopeCascading="standard";
			test();
		}
		finally {
			application action="update" scopeCascading="#defaultScopeCascading#";
		}	
	}
} 
</cfscript>