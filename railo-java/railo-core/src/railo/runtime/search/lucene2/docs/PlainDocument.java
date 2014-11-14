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
package railo.runtime.search.lucene2.docs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.lucene.document.Document;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

/** A utility for making Lucene Documents from a File. */

public final class PlainDocument {
    
    private static final int SUMMERY_SIZE=20;

    //private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);
  /** Makes a document for a File.
    <p>
    The document has three fields:
    <ul>
    <li><code>path</code>--containing the pathname of the file, as a stored,
    tokenized field;
    <li><code>modified</code>--containing the last modified date of the file as
    a keyword field as encoded by <a
    href="lucene.document.DateField.html">DateField</a>; and
    <li><code>contents</code>--containing the full contents of the file, as a
    Reader field;
 * @param f
 * @return matching document
 * @throws IOException
    */
  public static Document Document(Resource f,String charset)
       throws IOException {
	 
    // make a new, empty document
    Document doc = new Document();
    
    doc.add(FieldUtil.UnIndexed("path", f.getPath()));

    InputStream is = null;
    try {
    	is=IOUtil.toBufferedInputStream(f.getInputStream());
    	String content=IOUtil.toString(is,charset);
    	FieldUtil.setMimeType(doc, "text/plain");
    	FieldUtil.setRaw(doc,content);
    	FieldUtil.setContent(doc, content);
    	//doc.add(FieldUtil.Text("contents", content.toLowerCase()));
    	FieldUtil.setSummary(doc, StringUtil.max(content,SUMMERY_SIZE),false);
    }
    finally {
    	IOUtil.closeEL(is);
    }
    
    //Reader reader = new BufferedReader(new InputStreamReader(is));
   

    // return the document
    return doc;
  }

  private PlainDocument() {}
}
    