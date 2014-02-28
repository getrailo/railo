component extends="org.railo.cfml.test.RailoTestCase"	{
	

	public function testEmptyListFirst() {

		var list = "";

		assertEquals("", listFirst(list, ",;"));
	}

	public function testEmptyListRest() {

		var list = "";

		assertEquals("", listRest(list, ",;"));
	}
	

	public function testOriginal1() {

		var list = "A,B,C,D,E,F,G";

		assert(listFirst(list, ",;") == "A");
	}

	public function testOriginal2() {

		var list = "A,B,C,D,E,F,G";

		assert(listRest(list, ",;") == "B,C,D,E,F,G");
	}

	public function testOriginal3() {

		var list = ",,A,B,,C,D,E,F,G,";

		assert(listFirst(list, ",;", false) == "A");
	}

	public function testOriginal4() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("B,,C,D,E,F,G,", listRest(list, ",;"));
	}
	
	public function testOriginal5() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("", listFirst(list, ",;", true));
	}

	public function testOriginal6() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals(",A,B,,C,D,E,F,G,", listRest(list, ",;", true));
	}

	public function testOriginal7() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("A", listFirst(list, ",;", false));
	}

	public function testOriginal8() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("B,,C,D,E,F,G,", listRest(list, ",;", false));
	}
	
	public function testCount1() {

		var list = "A,B,C,D,E,F,G";

		assertEquals("A,B,C", listFirst(list, ",;", false, 3));
	}

	public function testCount2() {

		var list = "A,B,C,D,E,F,G";

		assertEquals("A,B,C", listFirst(list, ",;", true, 3));
	}

	public function testCount3() {

		var list = "A,B,C,D,E,F,G";

		assertEquals("D,E,F,G", listRest(list, ",;", false, 3));
	}

	public function testCount4() {

		var list = "A,B,C,D,E,F,G";

		assertEquals("D,E,F,G", listRest(list, ",;", true, 3));
	}

	public function testCount5() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("D,E,F,G,", listRest(list, ",;", false, 3));
	}

	public function testCount6() {

		var list = ",,A,B,,C,D,E,F,G,";

		assertEquals("B,,C,D,E,F,G,", listRest(list, ",;", true, 3));
	}

	public function testCount7() {

		var list = "http://localhost:8888/railo-tests/index.cfm";

		assertEquals("http://localhost:8888", listFirst(list, "/", true, 3));
	}

	public function testCount8() {

		var list = "http://localhost:8888/railo-tests/index.cfm";

		assertEquals("railo-tests/index.cfm", listRest(list, "/", true, 3));
	}


}