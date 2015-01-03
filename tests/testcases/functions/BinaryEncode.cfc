component extends="org.railo.cfml.test.RailoTestCase" {

	public function setUp() {
		variables.emptyBinary = CharsetDecode("", "utf-8");
		variables.binaryHello = CharsetDecode("Hello", "utf-8");
	}

	function testHex() {
		$assert.isEqualWithCase("", BinaryEncode(variables.emptyBinary, "hex"));
		$assert.isEqualWithCase("48656C6C6F", BinaryEncode(variables.binaryHello, "hex"));
	}

}
