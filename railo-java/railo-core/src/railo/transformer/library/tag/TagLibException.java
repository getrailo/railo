package railo.transformer.library.tag;

import java.io.IOException;





/**
 * Execption Klasse, welche durch die verschiedenen Klassen dieses Package geworfen werden kann.
 */
public final class TagLibException extends IOException {

    /* *
     * Standart Konstruktor fuer die Klasse TagLibException.
     * @param message Fehlermeldungstext.
     * /
    public TagLibException(String message) {
        super(message);
    }*/
    
    /**
     * Standart Konstruktor fuer die Klasse TagLibException.
     * @param t Throwable
     */
    public TagLibException(Throwable t) {
        initCause(t);
    }

	public TagLibException(String message) {
		super(message);
	}
    
}