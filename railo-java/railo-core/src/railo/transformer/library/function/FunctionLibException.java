package railo.transformer.library.function;

import java.io.IOException;


/**
 * Execption Klasse, welche durch die verschiedenen Klassen dieses Package geworfen werden kann.
 */
public final class FunctionLibException extends IOException {

	/**
	 * Standart Konstruktor fuer die Klasse FunctionLibException.
	 * @param message Fehlermeldungstext.
	 */
	public FunctionLibException(String message) {
		super(message);
	}
}