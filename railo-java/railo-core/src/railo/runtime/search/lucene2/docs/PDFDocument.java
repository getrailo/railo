package railo.runtime.search.lucene2.docs;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * This class is used to create a document for the lucene search engine.
 * This should easily plug into the IndexHTML or IndexFiles that comes with
 * the lucene project.  This class will populate the following fields.
 * <table>
 *      <tr>
 *          <td>Lucene Field Name</td>
 *          <td>Description</td>
 *      </tr>
 *      <tr>
 *          <td>path</td>
 *          <td>File system path if loaded from a file</td>
 *      </tr>
 *      <tr>
 *          <td>url</td>
 *          <td>URL to PDF document</td>
 *      </tr>
 *      <tr>
 *          <td>contents</td>
 *          <td>Entire contents of PDF document, indexed but not stored</td>
 *      </tr>
 *      <tr>
 *          <td>summary</td>
 *          <td>First 500 characters of content</td>
 *      </tr>
 *      <tr>
 *          <td>modified</td>
 *          <td>The modified date/time according to the url or path</td>
 *      </tr>
 *      <tr>
 *          <td>uid</td>
 *          <td>A unique identifier for the Lucene document.</td>
 *      </tr>
 *      <tr>
 *          <td>CreationDate</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Creator</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Keywords</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>ModificationDate</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Producer</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Subject</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Trapped</td>
 *          <td>From PDF meta-data if available</td>
 *      </tr>
 * </table>
 *
 */
public final class PDFDocument
{
    private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);
    

    /**
     * private constructor because there are only static methods.
     */
    private PDFDocument()
    {
        //utility class should not be instantiated
    }
    
    /**
     * This will get a lucene document from a PDF file.
     * @param is The stream to read the PDF from.
     * @return The lucene document.
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument(StringBuffer content, InputStream is ) {
        Document document = new Document();
        addContent(content, document, is);
        return document;
    }

    /**
     * This will get a lucene document from a PDF file.
     * @param res The file to get the document for.
     * @return The lucene document.
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( Resource res ) {
        Document document = new Document();
        document.add(FieldUtil.UnIndexed("mime-type", "application/pdf"));
        document.add( FieldUtil.UnIndexed("path", res.getPath() ) );
        
        String uid = res.getPath().replace(FILE_SEPARATOR, '\u0000') + "\u0000" +
               DateField.timeToString(res.lastModified() );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        document.add(FieldUtil.Text("uid", uid, false));
        //document.add(new Field("uid", uid, Field.Store.NO,Field.Index.UN_TOKENIZED));
        //document.add(new Field("uid", uid, false, true, false));

        InputStream is = null;
        try
        {
            is = IOUtil.toBufferedInputStream(res.getInputStream());
            addContent(null, document, is);
        }
        catch(IOException ioe) {
            
        }
        finally {
            IOUtil.closeEL(is);
        }


        // return the document

        return document;
    }

    /**
     * This will add the contents to the lucene document.
     * @param content 
     *
     * @param document The document to add the contents to.
     * @param is The stream to get the contents from.
     * @param documentLocation The location of the document, used just for debug messages.
     *
     * @throws IOException If there is an error parsing the document.
     */
    private static void addContent( StringBuffer content, Document document, InputStream is) {
        
        PDDocument pdfDocument=null;
        try {
            pdfDocument = PDDocument.load( is );

            if( pdfDocument.isEncrypted() )
            {
                //Just try using the default password and move on
                pdfDocument.decrypt( "" );
            }
            
            //create a writer where to append the text content.
            StringWriter writer = new StringWriter();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText( pdfDocument, writer );

            // Note: the buffer to string operation is costless;
            // the char array value of the writer buffer and the content string
            // is shared as long as the buffer content is not modified, which will
            // not occur here.
            String contents = writer.getBuffer().toString();
            if(content!=null)content.append(contents);
            //StringReader reader = new StringReader( contents );

            // Add the tag-stripped contents as a Reader-valued Text field so it will
            // get tokenized and indexed.
            //document.add( Field.Text( "contents", reader.toString().toLowerCase() ) );

            document.add( FieldUtil.UnIndexed("raw", contents ) );
            document.add( FieldUtil.Text( "contents", contents.toLowerCase() ) );
            
            PDDocumentInformation info = pdfDocument.getDocumentInformation();
            if( info.getAuthor() != null )
            {
                document.add(FieldUtil.Text( "Author", info.getAuthor() ) );
                
            }
            if( info.getCreationDate() != null )
            {
                Date date = info.getCreationDate().getTime();
                //for some reason lucene cannot handle dates before the epoch
                //and throws a nasty RuntimeException, so we will check and
                //verify that this does not happen
                if( date.getTime() >= 0 )
                {
                    document.add(FieldUtil.Text("CreationDate", DateField.dateToString( date ) ) );
                }
            }
            if( info.getCreator() != null )
            {
                document.add( FieldUtil.Text( "Creator", info.getCreator() ) );
            }
            if( info.getKeywords() != null )
            {
                document.add( FieldUtil.Text( "Keywords", info.getKeywords() ) );
            }
            if( info.getModificationDate() != null )
            {
                Date date = info.getModificationDate().getTime();
                //for some reason lucene cannot handle dates before the epoch
                //and throws a nasty RuntimeException, so we will check and
                //verify that this does not happen
                if( date.getTime() >= 0 )
                {
                    document.add(FieldUtil.Text("ModificationDate", DateField.dateToString( date ) ) );
                }
            }
            if( info.getProducer() != null )
            {
                document.add( FieldUtil.Text( "Producer", info.getProducer() ) );
            }
            if( info.getSubject() != null )
            {
                document.add( FieldUtil.Text( "Subject", info.getSubject() ) );
            }
            if( info.getTitle() != null )
            {
                document.add( FieldUtil.Text( "Title", info.getTitle() ) );
            }
            if( info.getTrapped() != null )
            {
                document.add( FieldUtil.Text( "Trapped", info.getTrapped() ) );
            }

            int summarySize = Math.min( contents.length(), 500 );
            String summary = contents.substring( 0, summarySize );
            // Add the summary as an UnIndexed field, so that it is stored and returned
            // with hit documents for display.
            document.add( FieldUtil.UnIndexed( "summary", summary ) );
        }
        catch(Throwable t) {}
        finally {
            if( pdfDocument != null ) {
                try {
                    pdfDocument.close();
                } 
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
}