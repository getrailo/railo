package railo.runtime.net.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.lang.StringUtils;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;


public class MultiPartResponseUtils {

	public static boolean isMultipart(String mimetype) {
		return !StringUtil.isEmpty(extractBoundary(mimetype,null)) 
			&& StringUtil.startsWithIgnoreCase(mimetype, "multipart/");
	}

	public static Array getParts(byte[] barr,String contentTypeHeader) throws IOException, PageException {
		String boundary = extractBoundary(contentTypeHeader,"");
		ByteArrayInputStream bis = new ByteArrayInputStream(barr);
		MultipartStream stream;
		Array result = new ArrayImpl();
		stream = new MultipartStream(bis,getBytes(boundary,"UTF-8"));// 
		
		boolean hasNextPart = stream.skipPreamble();
		while (hasNextPart) {
			result.append(getPartData(stream));
			hasNextPart = stream.readBoundary();
		}
		return result;
	}

	private static String extractBoundary(String contentTypeHeader, String defaultValue) {
		if (contentTypeHeader == null) return defaultValue;
		String[] headerSections = ListUtil.listToStringArray(contentTypeHeader, ';');
		for (String section: headerSections) {
			String[] subHeaderSections = ListUtil.listToStringArray(section,'=');
			String headerName = subHeaderSections[0].trim();
			if (headerName.toLowerCase().equals("boundary")) {
				return subHeaderSections[1];
			}
		
		}
		return defaultValue;
	}

	private static Struct getPartData(MultipartStream stream) throws IOException, PageException {
		Struct headers = extractHeaders(stream.readHeaders());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		stream.readBodyData(baos);
		Struct fileStruct = new StructImpl();
		fileStruct.set(KeyConstants._content, baos.toByteArray());
		fileStruct.set(KeyConstants._headers, headers);
		IOUtil.closeEL(baos);
		return fileStruct;
	}

	private static Struct extractHeaders(String rawHeaders) throws PageException {
		Struct result = new StructImpl();
		String[] headers = ListUtil.listToStringArray(rawHeaders,'\n');
		for(String rawHeader :headers) {
			String[] headerArray = ListUtil.listToStringArray(rawHeader,':');
			String headerName = headerArray[0];
			if (!StringUtil.isEmpty(headerName,true)) {
				String value = StringUtils.join(Arrays.copyOfRange(headerArray, 1, headerArray.length),":").trim();
				result.set(headerName, value);
			}
		}
		return result;
	}

	private static byte[] getBytes(String string, String charset) {
		byte[] bytes;
		try {
			bytes = string.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			bytes = string.getBytes();
		}
		return bytes;
	}


}
