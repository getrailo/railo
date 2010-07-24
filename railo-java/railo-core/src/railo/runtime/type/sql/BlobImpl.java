package railo.runtime.type.sql;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;

  
 
 /**
 * Implementation of the Interface java.sql.Blob
 */
public final class BlobImpl implements java.sql.Blob, Serializable {
     byte[] binaryData = null;
 
     /**
      * constructor of the class
     * @param data
     */
    private BlobImpl(byte[] data)   {
         binaryData = data;
     }
 
     /**
     * @see java.sql.Blob#length()
     */
    public long length() throws SQLException {
         return binaryData.length;
     }
 
     /**
     * @see java.sql.Blob#getBytes(long, int)
     */
    public byte[] getBytes(long pos, int length) throws SQLException   {
         byte[] newData = new byte[length]; 
         System.arraycopy(binaryData, (int) (pos - 1), newData, 0, length);
         return newData;
     }
 
     /**
     * @see java.sql.Blob#getBinaryStream()
     */
    public java.io.InputStream getBinaryStream() throws SQLException	{
        return new ByteArrayInputStream(binaryData);
    }
    
    /**
     * @see java.sql.Blob#getBinaryStream(long, long)
     */
    public java.io.InputStream getBinaryStream(long pos, long length)	{
    	// TODO impl this
    	return new ByteArrayInputStream(binaryData);
    }

     /**
     * @see java.sql.Blob#position(byte[], long)
     */
    public long position(byte pattern[], long start) throws SQLException	{
         return (new String(binaryData)).indexOf(new String(pattern), (int) start);
     }
 
     /**
     * @see java.sql.Blob#position(java.sql.Blob, long)
     */
    public long position(java.sql.Blob pattern, long start) throws SQLException	{
         return position(pattern.getBytes(0, (int) pattern.length()), start);
     }
 
     /**
     * @see java.sql.Blob#setBytes(long, byte[])
     */
    public int setBytes(long pos, byte[] bytes) throws SQLException	{
         // TODO impl.
         throw new SQLException("JDBC 3.0 Method setBytes not implemented");
     }

     /**
     * @see java.sql.Blob#setBytes(long, byte[], int, int)
     */
    public int setBytes(long pos, byte[] bytes, int offset, int len)	throws SQLException	{
         // TODO impl.
         throw new SQLException("JDBC 3.0 Method setBytes not implemented");
     }
 
     /**
     * @see java.sql.Blob#setBinaryStream(long)
     */
    public java.io.OutputStream setBinaryStream(long pos) throws SQLException	{
         // TODO impl.
         throw new SQLException("JDBC 3.0 Method setBinaryStream not implemented");
     }
 
     /**
     * @see java.sql.Blob#truncate(long)
     */
    public void truncate(long len) throws SQLException	{
         // TODO impl.
         throw new SQLException("JDBC 3.0 Method truncate not implemented");
     }

    public static Blob toBlob(Object value) throws ExpressionException {
        if(value instanceof Blob) return (Blob)value;
        return new BlobImpl(Caster.toBinary(value));
    }
    
    /*public static Blob toBlob(byte[] value) {
    	
    	Class blobClass = ClassUtil.loadClass("oracle.sql.BLOB",null);
    	if(blobClass!=null){
	    	try {
	    		//BLOB blob = BLOB.getEmptyBLOB();
				Method getEmptyBLOB = blobClass.getMethod("getEmptyBLOB",new Class[]{});
	    		Object blob = getEmptyBLOB.invoke(null, ArrayUtil.OBJECT_EMPTY);
	
	    		//blob.setBytes(value);
	    		Method setBytes = blobClass.getMethod("setBytes", new Class[]{byte[].class});
	    		setBytes.invoke(blob, new Object[]{value});
	    		
	    		return (Blob) blob;
			} 
	    	catch (Exception e) {}
    	}
    	return new BlobImpl(value);
    }*/
    

	public void free() {
		binaryData=new byte[0];
	}
}