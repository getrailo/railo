component extends="org.railo.cfml.test.RailoTestCase" {


	public function testLegacy() {

		var list1 = "AA,BB,CC,BB,DD,dd,EE";

		assertEquals( listRemoveDuplicates( list1 ), "AA,BB,CC,DD,dd,EE" );
	}


	public function testIgnoreCase() {

		var list1 = "AA,BB,CC,BB,DD,dd,EE";

		assertEquals( listRemoveDuplicates( list1, ",", true ), "AA,BB,CC,DD,EE" );
	}

}