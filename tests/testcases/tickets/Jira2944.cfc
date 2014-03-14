<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testStruct() localmode="modern" {
		src = {"test & test":'test & test'};
		
		wddx action="cfml2wddx" input="#src#" output="wddx" validate='yes';
		wddx action="wddx2cfml" input="#wddx#" output="trg";
		
		assertEquals(serialize(src),serialize(trg));
	}
	public void function testQuery() localmode="modern" {
		src = query("a&1":[1,2,3]);

		wddx action="cfml2wddx" input="#src#" output="wddx" validate='yes';
		wddx action="wddx2cfml" input="#wddx#" output="trg";
		
		assertEquals(serialize(src),serialize(trg));
	}

	public void function testComponent() localmode="modern" {
		src = new "railo-context.Component"();
		src["a&1"]=1;
		wddx action="cfml2wddx" input="#src#" output="wddx" validate='yes';
		
		wddx action="wddx2cfml" input="#wddx#" output="trg";
		assertEquals(serialize(src),serialize(trg));
	}


	private void function test() localmode="modern" {
		tmp = {"test & test":'test & test'};
		
		
		wddx action="cfml2wddx" input="#tmp#" output="wddxInvoicedata" validate='yes';

		dump(var="#wddxInvoicedata#");
		dump("#tmp#");
abort;
		wddx action="wddx2cfml" input="#wddxInvoicedata#" output="sameInvoicedata";
		dump("#sameInvoicedata#");
		

		assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}
	}
} 
</cfscript>