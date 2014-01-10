component {
	public TestObject function init(
		required numeric levels = 0
	) {
		variables.a = (arguments.levels > 0)?new TestObject( levels = arguments.levels - 1 ):{};
		return this;
	}
}