package railo.commons.cli;

public class CommandResult {

	private final String out, err;

	public CommandResult( String out, String err ) {

		this.out = out;
		this.err = err;
	}

	public String getOutput() {

		return out;
	}

	public String getError() {

		return err;
	}
}
