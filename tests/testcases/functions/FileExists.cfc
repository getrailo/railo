component extends="org.railo.cfml.test.RailoTestCase" {
	
	public function setUp() localmode="true"{}

	function testFileExists(){
		assertEquals(true,"#FileExists(GetCurrentTemplatePath())#");
		assertEquals(false,"#FileExists(GetDirectoryFromPath(GetCurrentTemplatePath()))#");
		assertEquals(true,"#directoryExists(GetDirectoryFromPath(GetCurrentTemplatePath()))#");
		assertEquals(true,"#FileExists(ucase(GetCurrentTemplatePath()))#");

		path=structNew();
		path.abs=GetCurrentTemplatePath();
		path.real=ListLast(path.abs,"/\");


		assertEquals(true,"#fileExists(path.abs)#");
		assertEquals(true,"#fileExists(path.real)#");
		assertEquals(false,"#evaluate('fileExists(path.real,false)')#");
		assertEquals(true,"#evaluate('fileExists(path.real,true)')#");
		
		//assertEquals(2,find("PNG",CharsetEncode(binary,"utf-8")));
	}
}