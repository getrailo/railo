component extends="org.railo.cfml.test.RailoTestCase" {


	/**
	* @mxunit:expectedException expression
	*/
	public void function testExpressionException() {

		var x = structKeyExists( nullValue() , 'x' );
	}

}