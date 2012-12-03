package railo.runtime.type.scope;


/**
 * interface for scope server
 */
public interface Server extends Scope {

    /**
     * @param sn
     */
    public abstract void reload();

}