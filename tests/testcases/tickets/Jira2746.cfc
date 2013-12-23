<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testRegular() localmode="modern"{
		meta=getmetaData(localmodeOn);
		assertEquals("modern",meta.localmode);
		meta=getmetaData(localmodeOff);
		assertEquals("classic",meta.localmode);
		meta=getmetaData(localmodeNone);
		assertEquals(true,isNull(meta.localmode));
		
	}
	
	public void function testSerializedAndEvaluatedAgain() localmode="modern"{
		
		meta=getmetaData(objectLoad(objectSave(localmodeOn)));
		assertEquals("modern",meta.localmode);
		meta=getmetaData(objectLoad(objectSave(localmodeOff)));
		assertEquals("classic",meta.localmode);
		
	}
	
	private void function localmodeOn() localmode="modern"{}
	private void function localmodeOff() localmode="classic"{}
	private void function localmodeNone() {}
} 
</cfscript>