package railo.commons.cli;

import java.io.IOException;

public class CommandException extends IOException {

	public CommandException(String message) {
		super(message!=null?message:"");
	}
}
