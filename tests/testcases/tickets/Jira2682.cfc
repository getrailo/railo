<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testLocalFileSystem(){
		if(find("Mac",server.os.name)) {
			assertEquals("/Users/mic/test.txt",getCanonicalPath("/Users\mic//test.txt"));
		}
		else if(find("Windows",server.os.name)) {
			assertEquals("d:\a\b\test.txt",getCanonicalPath("d:/a\b//test.txt"));
		}
	}
	
	public void function testZipFileSystem(){
		if(find("Mac",server.os.name)) {
			assertEquals("zip:///Users/mic/test.txt!/aa/bb/ccc.txt",getCanonicalPath("zip:///Users\mic/test.txt!/aa/bb\ccc.txt"));
		}
		else if(find("Windows",server.os.name)) {
			assertEquals("zip://d:\mic\test.txt!/aa/bb/ccc.txt",getCanonicalPath("zip://d:/mic/test.txt!/aa/bb\ccc.txt"));
		}
	}
	
	public void function testRamFileSystem(){
		assertEquals("ram:///aa/bb/cc.txt",getCanonicalPath("ram:///aa//bb\cc.txt"));
	}
} 
</cfscript>