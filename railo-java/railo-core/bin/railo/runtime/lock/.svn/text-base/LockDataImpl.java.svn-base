package railo.runtime.lock;

/**
 * all data of a lock
 */
public final class LockDataImpl implements LockData {

    private int type;
    private String name;
    private int id;

    /**
     * constructor of the class
     * @param type type of the token (TYPE_READONLY,TYPE_EXCLUSIVE)
     * @param name name of the token
     * @param id id of the token
     */
    protected LockDataImpl(int type, String name, int id) {
        this.type=type;
        this.name=name.toLowerCase();
        this.id=id;
    }

    /**
     * @see railo.runtime.lock.LockData#isReadOnly()
     */
    public boolean isReadOnly() {
        return type==LockManager.TYPE_READONLY;
    }

    /**
     * @see railo.runtime.lock.LockData#getId()
     */
    public int getId() {
        return id;
    }

    /**
     * @see railo.runtime.lock.LockData#getName()
     */
    public String getName() {
        return name;
    }
}
    