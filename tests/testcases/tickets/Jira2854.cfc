<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	/**
	@comment_annotation 1
	*/
	private function annotationtest1() inline_annotation=1 {}

	/**
	@comment_annotation 1
	*/
	private function annotationtest2() inline_annotation=1 {}
	
	

	public void function testAnnotations(){
		var udfs=getMetaData(this).functions;
		loop array="#udfs#" item="local.udf" {
			if(!find('annotationtest',udf.name)) continue;
			assertEquals(true,isDefined('udf.inline_annotation'));
			assertEquals(true,isDefined('udf.comment_annotation'));
		}
	}
} 
</cfscript>