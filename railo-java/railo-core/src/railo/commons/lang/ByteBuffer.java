/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.commons.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * class to handle characters, similar to StringBuffer, but dont copy big blocks of char arrays.
 */
public class ByteBuffer {

	private final static int BLOCK_LENGTH = 1024;
	private byte buffer[];
	private int pos=0;
	private int length=0;
	private final Entity root=new Entity(null);
	private Entity curr=root;
	private final String charset;
	
	/**
	 * default constructor
	 */
	public ByteBuffer(String charset) {
		this(charset,BLOCK_LENGTH);
	}

	/**
	 * constructor with size of the buffer
	 * @param size
	 */
	public ByteBuffer(String charset,int size) {
		buffer = new byte[size];
		this.charset=charset;
	}
	
	public void append(char c) throws IOException {
		append(new String(new char[]{c}));
	}
	
	/**
	* method to appennd a charr array to the buffer
	* @param c char array to append
	 * @throws IOException 
	*/
	public void append(char c[]) throws IOException {
		append(new String(c));
	}
	
	public void append(byte c[]) {
		int maxlength=buffer.length-pos;
		if(c.length<maxlength) {
			System.arraycopy(c, 0, buffer, pos, c.length);
			pos+=c.length;
		}
		else {
			System.arraycopy(c, 0, buffer, pos, maxlength);
			curr.next=new Entity(buffer);
			curr=curr.next;
			length+=buffer.length;
			buffer=new byte[(buffer.length>c.length-maxlength)?buffer.length:c.length-maxlength];
			if(c.length>maxlength) {
				System.arraycopy(c, maxlength, buffer, 0, c.length-maxlength);
				pos=c.length-maxlength;
			}
			else {
				pos=0;
			}
		}
	}
	
	/**
	* method to append a part of a char array
	* @param c char array to get part from
	* @param off start index on the char array
	* @param len length of the sequenz to get from array
	 * @throws IOException 
	*/
	public void append(char c[], int off, int len) throws IOException {
		append(new String(c,off,len));
	}
	
	/**
	* Method to append a string to char buffer
	* @param str String to append
	 * @throws IOException 
	*/
	public void append(String str) throws IOException {
		append(str.getBytes(charset));
	}

	/**
	* method to append a part of a String
	* @param str string to get part from
	* @param off start index on the string
	* @param len length of the sequenz to get from string
	 * @throws IOException 
	*/
	public void append(String str, int off, int len) throws IOException {
		append(str.substring(off, off+len));
	}

	/**
	* method to writeout content of the char buffer in a writer,
	* this is faster than get char array with (toCharArray()) and write this
	in writer.
	* @param writer writer to write inside
	* @throws IOException
	*/
	public void writeOut(OutputStream os) throws IOException {
		Entity e=root;
		while(e.next!=null) {
			e=e.next;
			os.write(e.data);
		}
		os.write(buffer,0,pos);
	}
	
	@Override
	public String toString() {
		try {
			return new String(getBytes(),charset);
		} catch (UnsupportedEncodingException e) {
			return new String(getBytes());
		}
	}

	/**
	* clear the content of the buffer
	*/
	public void clear() {
		if(size()==0)return;
        buffer = new byte[buffer.length];
		root.next=null;
		pos = 0;
		length=0;
		curr=root;
	}

	/**
	 * @return returns the size of the content of the buffer
	 */
	public int size() {
		return length+pos;
	}
	
	
	private class Entity {
		private byte[] data;
		private Entity next;
		
		private Entity(byte[] data) {
			this.data=data;
		}
	}


    public byte[] getBytes() {
    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			writeOut(baos);
		} 
		catch (IOException e) {}
        return baos.toByteArray();
    }

	
	public static void main(String[] args) throws IOException {
		ByteBuffer cb=new ByteBuffer("utf-8",3);
		cb.append("12");
		cb.append("34");
		cb.append("5");
		cb.append("6");
		cb.append("7");
		cb.append("890-");
		cb.append("1234567890-1234567890-");
		System.out.println(cb+"->"+cb.size());
		
		cb=new ByteBuffer("utf-8",3);
		cb.append("12",0,2);
		cb.append("34",0,2);
		cb.append("5",0,1);
		cb.append("xxx6ggg",3,1);
		cb.append("7zzz",0,1);
		cb.append("890-",0,4);
		cb.append("1234567890-1234567890-",0,22);
		System.out.println(cb+"->"+cb.size());
		
		
		cb=new ByteBuffer("utf-8",3);
		cb.append(new char[]{'1','2'});
		cb.append(new char[]{'3','4'});
		cb.append(new char[]{'5'});
		cb.append(new char[]{'6'});
		cb.append(new char[]{'7'});
		cb.append(new char[]{'8','9','0','-'});
		cb.append(new char[]{'1','2','3','4','5','6','7','8','9','0','-','1','2','3','4','5','6','7','8','9','0','-'});
		System.out.println(cb+"->"+cb.size());

		
		
		cb=new ByteBuffer("utf-8",3);
		cb.append(new char[]{'X','1','2','X'},1,2);
		cb.append(new char[]{'3','4'},0,2);
		cb.append(new char[]{'5'},0,1);
		cb.append(new char[]{'6'},0,1);
		cb.append(new char[]{'7'},0,1);
		cb.append(new char[]{'8','9','0','-'},0,4);
		cb.append(new char[]{'1','2','3','4','5','6','7','8','9','0','-','1','2','3','4','5','6','7','8','9','0','-'},0,22);
		System.out.println(cb+"->"+cb.size());
		
		cb=new ByteBuffer("utf-8",3);
		cb.append(new char[]{'�'});
		System.out.println(cb+"->"+cb.size());
		
	}
}