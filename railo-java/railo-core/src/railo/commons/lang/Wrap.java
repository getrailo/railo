package railo.commons.lang;

/**
 * Wras a Object
 */
public final class Wrap {

    private Object value;

    /**
     * @param value
     */
    public Wrap(Object value) {
        this.value=value;
    }
    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }
}