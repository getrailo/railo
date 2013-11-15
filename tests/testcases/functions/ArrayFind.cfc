<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrayFind(){
		
		valueEquals(ArrayFind(listToArray('abba,bb'),'bb'), "2");
		valueEquals(ArrayFind(listToArray('abba,bb,AABBCC,BB'),'BB'),"4");
		valueEquals(ArrayFind(listToArray('abba,bb,AABBCC'),'ZZ'), "0");
		
		var arr=["hello","world"];
    
    	// UDF
    	var res=ArrayFind(arr,doFind);
    	valueEquals(res,2);
    
    	// Closure
    doFind=function (value){
        return value EQ "world";
    };
    res=ArrayFind(arr,doFind);
    valueEquals(res,2);
		
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
	
	private function doFind(value){
        return value EQ "world";
    }
    
	private function valueEquals(left,right) {
		assertEquals(arguments.right,arguments.left);
	}
} 
</cfscript>