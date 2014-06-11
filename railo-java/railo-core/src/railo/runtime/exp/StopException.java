package railo.runtime.exp;

public class StopException extends Exception implements Stop {
	public StopException(){
		super("Thread forced to stop!");
	}
}
