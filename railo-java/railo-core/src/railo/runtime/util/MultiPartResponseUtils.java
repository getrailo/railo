package railo.runtime.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import railo.commons.io.IOUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;


public class MultiPartResponseUtils {

	public static boolean isMultipart(String mimetype) {
		boolean hasBoundary = !extractBoundary(mimetype).equals("");
		return hasBoundary && mimetype.toLowerCase().startsWith("multipart/");
	}

	public static Object getParts(byte[] barr,String contentTypeHeader) throws IOException, PageException {
		
		String boundary = extractBoundary(contentTypeHeader);
		ByteArrayInputStream bis = new ByteArrayInputStream(barr);
		MultipartStream stream;
		ArrayImpl result = new ArrayImpl();
		stream = new MultipartStream(bis,getBytes(boundary));
		
		boolean hasNextPart;

		hasNextPart = stream.skipPreamble();
		
		while (hasNextPart) {
			result.append(getPartData(stream));
			hasNextPart = stream.readBoundary();
		}
		return result;
	}

	private static String extractBoundary(String contentTypeHeader) {
		String[] headerSections = contentTypeHeader.split(";");
		for (String section: headerSections) {
			String[] subHeaderSections = section.split("=");
			String headerName = subHeaderSections[0];
			if (headerName.toLowerCase().equals("boundary")) {
				return subHeaderSections[1];
			}
		
		}
		return "";
	}

	private static StructImpl getPartData(MultipartStream stream) throws IOException, PageException {
		StructImpl headers = extractHeaders(stream.readHeaders());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		stream.readBodyData(baos);
		StructImpl fileStruct = new StructImpl();
		fileStruct.set("content", baos.toString());
		fileStruct.set("headers", headers);
		baos.close();
		return fileStruct;
	}

	private static StructImpl extractHeaders(String rawHeaders) throws PageException {
		StructImpl result = new StructImpl();
		String[] headers = rawHeaders.split("\n");
		for(String rawHeader :headers) {
			String[] headerArray = rawHeader.split(":");
			String headerName = headerArray[0];
			if (!headerName.trim().equals("")) {
				String value = StringUtils.join(Arrays.copyOfRange(headerArray, 1, headerArray.length),":").trim();
				result.set(headerName, value.trim());
			}
		}
		return result;
	}

	private static byte[] getBytes(String string) {
		byte[] bytes;
		try {
			bytes = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			bytes = string.getBytes();
		}
		return bytes;
	}


}
