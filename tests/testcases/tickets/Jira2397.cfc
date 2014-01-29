component extends="org.railo.cfml.test.RailoTestCase" {


	/**
	* @mxunit:expectedException expression
	*/
	public function testNoDefault() {

		var str = { A: 1, B: 2, C: 3 };

		echo( str.find( "X" ) );
	}


	public function testDefault() {

		var str = { A: 1, B: 2, C: 3 };

		var x = str.find( "X", 24 );

		assertEquals( x, 24 );
	}
}