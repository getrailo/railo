package railo.runtime.text.xml.storage;

import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.Time;

/**
 * A Object to store to XML File
 */
public abstract class StorageItem {
    
    /**
     * gets a value from the storage item as String
     * @param key key of the value to get
     * @return matching value
     * @throws StorageException
     */
    public String getString(String key) throws StorageException {
        throw new StorageException("there is no value with the key "+key);
    }
    
    /**
     * gets a value from the storage item as int
     * @param key key of the value to get
     * @return matching value
     * @throws StorageException
     */
    public int getInt(String key) throws StorageException {
        throw new StorageException("there is no value with the key "+key);
    }
    
    /**
     * gets a value from the storage item as Date Object
     * @param key key of the value to get
     * @return matching value
     * @throws StorageException
     */
    public Date getDate(String key) throws StorageException {
        throw new StorageException("there is no value with the key "+key);
    }
    
    /**
     * gets a value from the storage item as Time Object
     * @param key key of the value to get
     * @return matching value
     * @throws StorageException
     */
    public Time getTime(String key) throws StorageException {
        throw new StorageException("there is no value with the key "+key);
    }
    
    /**
     * gets a value from the storage item as Date Object
     * @param key key of the value to get
     * @return matching value
     * @throws StorageException
     */
    public DateTime getDateTime(String key) throws StorageException {
        throw new StorageException("there is no value with the key "+key);
    }
    
    /**
     * sets a value to the storage item as String
     * @param key key of the value to set
     * @param value value to set
     * @throws StorageException
     */
    public void setString(String key,String value) throws StorageException {
        throw new StorageException("key "+key+" is not supported for this item");
    }
    
    /**
     * sets a value to the storage item as int
     * @param key key of the value to set
     * @param value value to set
     * @throws StorageException
     */
    public void setInt(String key,int value) throws StorageException {
        throw new StorageException("key "+key+" is not supported for this item");
    }
    
    /**
     * sets a value to the storage item as Date Object
     * @param key key of the value to set
     * @param value value to set
     * @throws StorageException
     */
    public void setDate(String key,Date value) throws StorageException {
        throw new StorageException("key "+key+" is not supported for this item");
    }
    
    /**
     * sets a value to the storage item as Time Object
     * @param key key of the value to set
     * @param value value to set
     * @throws StorageException
     */
    public void setTime(String key,Time value) throws StorageException {
        throw new StorageException("key "+key+" is not supported for this item");
    }
    
    /**
     * sets a value to the storage item as DateTime Object
     * @param key key of the value to set
     * @param value value to set
     * @throws StorageException
     */
    public void setDateTime(String key,DateTime value) throws StorageException {
        throw new StorageException("key "+key+" is not supported for this item");
    }
}