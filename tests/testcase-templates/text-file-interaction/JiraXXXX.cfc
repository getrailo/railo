<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public function beforeTests(){
		// variables.filePath=createFile("susi.txt","Susi Sorglos");
	}
	public function afterTests(){
		// deleteFile("susi.txt");
	}

	public function setUp(){
	}

	/*public void function test(){
		file  action="read" file="#variables.filePath#" variable="local.content";
		assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}
	}*/
	
	/**
	* creates a file in the ram resource and returnthe absoulte path to this file
	* @filename name of the file, for example "test.txt"
	* @content string content for the file
	*/
	private string function createFile(required string filename, required string content) {
		local.path="ram:///"&filename;
		file action="write" file="#path#" output="#content#";
		return path;
	}
	
	/**
	* creates a file in the ram resource and returnthe absoulte path to this file
	* @filename name of the file, for example "test.txt"
	* @content string content for the file
	*/
	private void function deleteFile(required string filename) {
		local.path="ram:///"&filename;
		file action="delete" file="#path#";
	}
	
	
	
} 
</cfscript>