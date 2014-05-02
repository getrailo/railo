component extends="org.railo.cfml.test.RailoTestCase" {
	function testListAvg(){
		var arr="1,2,3";


	assertEquals("V1,V2,V3", ListCompact(",V1,V2,V3,",","));
	assertEquals("V1,V2,V3", ListCompact(list=",V1,V2,V3,",delimiter=","));
	assertEquals("V1,V2,V3", ListCompact(delimiter=",",list=",V1,V2,V3,"));
	assertEquals(ListCompact(",V1,V2,V3,",","), ListCompact(list=",V1,V2,V3,",delimiter=","));

	assertEquals(",V1,V2,V3,", ListCompact(",V1,V2,V3,","}"));
	assertEquals(",V1,V2,V3,", ListCompact(list=",V1,V2,V3,",delimiter="}"));
	assertEquals(",V1,V2,V3,", ListCompact(delimiter="}",list=",V1,V2,V3,"));
	assertEquals(ListCompact(",V1,V2,V3,","}"), ListCompact(list=",V1,V2,V3,",delimiter="}"));

	assertEquals(0, Len(ListCompact("","}")) ) ;
	assertEquals(0, Len(ListCompact(list="",delimiter="}")) ) ;
	assertEquals(0, Len(ListCompact(delimiter="}",list="")) ) ;
	assertEquals(Len(ListCompact("","}")) , Len(ListCompact(list="",delimiter="}")) ) ;

	assertEquals("V1,V2,V3", ListCompact(";V1,V2,V3,",",;"));
	assertEquals("V1,V2,V3", ListCompact(";V1,V2,V3,",",;",false));
	assertEquals(";V1,V2,V3,", ListCompact(";V1,V2,V3,",",;",true));
	assertEquals("V1,V2,V3", ListCompact(",;V1,V2,V3,;",",;",true));
	

	}
}