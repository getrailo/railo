package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public final class ContentFactory extends S3Factory {

	private boolean insideContents=false;
	private boolean insideOwners=false;

	private Content content; 
	private List contents=new ArrayList();
	private final S3 s3;
	private String bucketName;
	private boolean isTruncated; 


	/**
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param file File Objekt auf die TLD.
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public ContentFactory(InputStream in,S3 s3) throws IOException, SAXException {
		super();
		this.s3=s3;
		init(in);
	}

	public void doStartElement(String uri, String name, String qName, Attributes atts) {
		if(qName.equals("Contents")) startContents();
		if(qName.equals("Owner")) insideOwners=true;
		
	}
    
	public void doEndElement(String uri, String name, String qName) throws SAXException {
		if(qName.equals("Contents")) endContents();
		if(qName.equals("Owner")) insideOwners=false;
		
	}
	
	
	protected void setContent(String value) throws SAXException 	{
		if(insideContents)	{
			if(insideOwners){
				if(inside.equals("ID")) 					content.setOwnerIdKey(value);
				else if(inside.equals("DisplayName")) 		content.setOwnerDisplayName(value);
				
			}
			else {
				try {
					if(inside.equals("Key")) 				{
						content.setKey(value);
					}
					else if(inside.equals("LastModified"))	content.setLastModified(
							S3.toDate(value,s3.getTimeZone()).getTime());
					else if(inside.equals("ETag"))			content.setETag(value); // MUST HTML Encoder
					else if(inside.equals("Size")) 			content.setSize(Caster.toLongValue(value,0L));
					else if(inside.equals("StorageClass")) 	content.setStorageClass(value);
				} 
				catch (PageException e) {
					throw new SAXException(e.getMessage());
				}
			}
			
    	}
		else {
			if(inside.equals("Name")) 				bucketName=value;
			else if(inside.equals("IsTruncated")) 		isTruncated=Caster.toBooleanValue(value,false);
			
		}
		
    }	
	/*


	<ListBucketResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
		<Contents>
			<Owner>
				<ID>5f8fc1a3e7f09e6e1bf5f7d7d0dfadfd0efc2e0afbf7789836f3ab67b2dddfdf</ID>
				<DisplayName>michael.streit</DisplayName>
			</Owner>
			<StorageClass>STANDARD</StorageClass>
			</Contents>
			<Contents>
				<Key>susi2</Key>
				<LastModified>2008-03-05T14:10:43.000Z</LastModified>
				<ETag>&quot;f84bcc91f8fd1321a251311c7e9fbfce&quot;</ETag><Size>156169</Size><Owner><ID>5f8fc1a3e7f09e6e1bf5f7d7d0dfadfd0efc2e0afbf7789836f3ab67b2dddfdf</ID><DisplayName>michael.streit</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>

	 */
	
	
	
	/**
	 * Wird jedesmal wenn das Tag attribute beginnt aufgerufen, um intern in einen anderen Zustand zu gelangen.
	 */
	private void startContents()	{
    	insideContents=true;
    	content=new Content(s3); 
    }
	
	
	/**
	 * Wird jedesmal wenn das Tag tag endet aufgerufen, um intern in einen anderen Zustand zu gelangen.
	 */
	private void endContents()	{
		content.setBucketName(bucketName);
		content.setTruncated(isTruncated);
		contents.add(content);
    	insideContents=false;
    }

	public Content[] getContents() {
		return (Content[]) contents.toArray(new Content[contents.size()]);
	}
/*


<ListBucketResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
	<Name>halloWelt</Name>
	<Prefix></Prefix>
	<Marker></Marker>
	<MaxKeys>1000</MaxKeys>
	<IsTruncated>false</IsTruncated>
	<Contents>
		<Key>susi</Key>
		<LastModified>2008-03-05T14:10:41.000Z</LastModified>
		<ETag>&quot;f84bcc91f8fd1321a251311c7e9fbfce&quot;</ETag>
		<Size>156169</Size>
		<Owner>
			<ID>5f8fc1a3e7f09e6e1bf5f7d7d0dfadfd0efc2e0afbf7789836f3ab67b2dddfdf</ID>
			<DisplayName>michael.streit</DisplayName>
		</Owner>
		<StorageClass>STANDARD</StorageClass>
		</Contents>
		<Contents>
			<Key>susi2</Key>
			<LastModified>2008-03-05T14:10:43.000Z</LastModified>
			<ETag>&quot;f84bcc91f8fd1321a251311c7e9fbfce&quot;</ETag><Size>156169</Size><Owner><ID>5f8fc1a3e7f09e6e1bf5f7d7d0dfadfd0efc2e0afbf7789836f3ab67b2dddfdf</ID><DisplayName>michael.streit</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>

 */

	/**
	 * @return the isTruncated
	 */
	public boolean isTruncated() {
		return isTruncated;
	}
}