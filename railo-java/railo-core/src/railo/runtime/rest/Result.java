package railo.runtime.rest;


import java.util.List;

import railo.commons.lang.mimetype.MimeType;
import railo.runtime.type.Struct;

public class Result {

	private final Source source;
	private final String[] path;
	private final Struct variables;
	private final int format;
	private final Struct matrix;
	private Struct rsp;
	private final List<MimeType> accept;
	private final MimeType contentType;
	private final boolean hasFormatExtension;

	public Result(Source source, Struct variables, String[] path,  Struct matrix,int format,boolean hasFormatExtension,List<MimeType> accept,MimeType contentType) {
		this.source=source;
		this.variables=variables;
		this.path=path;
		this.format=format;
		this.matrix=matrix;
		this.hasFormatExtension=hasFormatExtension;
		this.accept=accept;
		this.contentType=contentType;
	}

	/**
	 * @return the hasFormatExtension
	 */
	public boolean hasFormatExtension() {
		return hasFormatExtension;
	}

	/**
	 * @return the accept
	 */
	public MimeType[] getAccept() {
		return accept.toArray(new MimeType[accept.size()]);
	}

	/**
	 * @return the accept
	 */
	public MimeType getContentType() {
		return contentType==null?MimeType.ALL:contentType;
	}

	/**
	 * @return the variables
	 */
	public Struct getVariables() {
		return variables;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the path
	 */
	public String[] getPath() {
		return path;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @return the matrix
	 */
	public Struct getMatrix() {
		return matrix;
	}

	public void setCustomResponse(Struct rsp) {
		this.rsp=rsp;
	}
	public Struct getCustomResponse() {
		return rsp;
	}

}
