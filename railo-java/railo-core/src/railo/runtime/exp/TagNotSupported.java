package railo.runtime.exp;

public class TagNotSupported extends ApplicationException {

	public TagNotSupported(String tag) {
		super("tag "+tag+" is not supported");
	}

}
