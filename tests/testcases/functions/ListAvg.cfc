component extends="org.railo.cfml.test.RailoTestCase" {
	function testListAvg(){
		var arr="1,2,3";
		assertEquals(2,ListAvg("1,2,3"));
		assertEquals(3.5,ListAvg("1,2,3,4,5,6"));
		assertEquals(3.5,ListAvg("1,2,3,4,5,6",',;.'));
		assertEquals(3.5,ListAvg("1,2,3,4,5,6",',;.'));
	}
}