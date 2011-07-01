package railo.commons.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.Inet6Address;
import java.net.InetAddress;

import railo.commons.lang.StringUtil;
import railo.runtime.type.List;

public class IPRange implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4427999443422764L;
	private static final short N256 = 256;
	private static final int SIZE = 4;
	
	private Range[] ranges=new Range[SIZE];
	int max=0;
	
	
	private static class Range {
		private final short[] from;
		private final short[] to;
		private final boolean equality;

		private Range(short[] from, short[] to) throws IOException{
			if(from.length!=to.length)
				throw new IOException("both ip address must be from same type, IPv4 or IPv6");
			
			this.from=from;
			this.to=to;
			this.equality=equal(from,to);
		}
		private Range(short[] from){
			this.from=from;
			this.to=from;
			this.equality=true;
		}
		private boolean inRange(short[] sarr) {
			if(from.length!=sarr.length) return false;
			
			if(equality) return equal(from, sarr);
			
			for(int i=0;i<from.length;i++){
				if(from[i]>sarr[i] ||  to[i]<sarr[i])
					return false;
			}
			return true;
		}
		
		public String toString(){
			if(equality) return toString(from);
			return toString(from)+"-"+toString(to);
		}
		
		private String toString(short[] sarr) {
			if(sarr.length==4)
				return new StringBuilder().append(sarr[0]).append(".").append(sarr[1]).append(".").append(sarr[2]).append(".").append(sarr[3]).toString();
			
			return new StringBuilder()
			.append(toHex(sarr[0],sarr[1],false)).append(":")
			.append(toHex(sarr[2],sarr[3],true)).append(":")
			.append(toHex(sarr[4],sarr[5],true)).append(":")
			.append(toHex(sarr[6],sarr[7],true)).append(":")
			.append(toHex(sarr[8],sarr[9],true)).append(":")
			.append(toHex(sarr[10],sarr[11],true)).append(":")
			.append(toHex(sarr[12],sarr[13],true)).append(":")
			.append(toHex(sarr[14],sarr[15],false)).toString();
			
			
		}
		
		
		
		private String toHex(int first, int second, boolean allowEmpty) {
			String str1=Integer.toString(first,16);
			while(str1.length()<2)str1="0"+str1;
			String str2=Integer.toString(second,16);
			while(str2.length()<2)str2="0"+str2;
			str1+=str2;
			if(allowEmpty && str1.equals("0000")) return "";
			
			while(str1.length()>1 && str1.charAt(0)=='0')str1=str1.substring(1);
			
			
			
			return str1;
		}
		private boolean equal(short[] left, short[] right) {
			for(int i=0;i<left.length;i++){
				if(left[i]!=right[i]) return false;
			}
			return true;
		}
		
		
	}
	

	public void add(String ip) throws IOException {
		// no wildcard defined
		if(ip.indexOf('*')==-1) {
			add(new Range(toShortArray(InetAddress.getByName(ip))));
			return;
		}
		
		String from = ip.replace('*', '0');
		String to;
		InetAddress addr1 = InetAddress.getByName(from);
		if(addr1 instanceof Inet6Address) 
			to=StringUtil.replace(ip, "*","ffff",false);
		else 
			to=StringUtil.replace(ip, "*","255",false);
		add(new Range(toShortArray(addr1),toShortArray(InetAddress.getByName(to))));
	}

	public void add(String ip1,String ip2) throws IOException {
		add(new Range(toShortArray(InetAddress.getByName(ip1)),toShortArray(InetAddress.getByName(ip2))));
	}
	public static IPRange getInstance(String raw) throws IOException {
		return getInstance(List.listToStringArray(raw, ','));
	}

	public static IPRange getInstance(String[] raw) throws IOException {
		IPRange range=new IPRange();
		String[] arr = List.trimItems(List.trim(raw));
		String str;
		int index;
		for(int i=0;i<arr.length;i++){
			str=arr[i];
			if(str.length()>0) {
				index=str.indexOf('-');
				if(index!=-1) range.add(str.substring(0,index), str.substring(index+1));
				else range.add(str);
			}
		}
		return range;
		
	}
	
	
	private synchronized void add(Range range) {
		if(max>=ranges.length) {
			Range[] tmp=new Range[ranges.length+SIZE];
			for(int i=0;i<ranges.length;i++){
				tmp[i]=ranges[i];
			}
			ranges=tmp;
		}
		ranges[max++]=range;
	}

	public boolean inRange(String ip) throws IOException {
		InetAddress addr = InetAddress.getByName(ip);
		short[] sarr = toShortArray(addr);
		
		for(int i=0;i<max;i++){
			if(ranges[i].inRange(sarr)) return true;
		}
		return false;
	}
	
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<max;i++){
			if(i>0)sb.append(",");
			sb.append(ranges[i].toString());
		}
		return sb.toString();
	}

	private static short[] toShortArray(InetAddress ia){
		byte[] addr = ia.getAddress();
		short[] sarr=new short[addr.length];
		for(int i=0;i<addr.length;i++){
			sarr[i]=byte2short(addr[i]);
		}
		return sarr;
	}
	
	private static short byte2short(byte b){
		if(b<0) return (short)(b+N256);
		return b;
	}

	/*public static void main(String[] args) throws IOException {
		IPRange r=IPRange.getInstance("127.0.0.1-127.0.0.10,128.0.*.*,,129.0.0.1,ff:db8::8d3:*:8a2e:70:7344,::");
		//IPRange r=IPRange.getInstance("::");
		print.o(r);
		
		
		
		if(true) return;
		IPRange range=new IPRange();
		range.add("127.128.255.0");
		range.add("::","0:0:0:0:0:0:0:1%0");
		range.add("0:0:0:0:0:0:0:1%0");
		range.add("2001:db8::8d3:*:8a2e:70:7344");
		range.add("127.0.0.1","127.0.0.10");
		range.add("127.0.0.20");
		range.add("127.0.0.23");
		range.add("127.0.0.25");
		range.add("127.0.*.*");

		print.o(range.inRange("127.0.0.1"));
		print.o(range.inRange("127.0.0.4"));
		print.o(range.inRange("127.0.0.10"));
		print.o(range.inRange("127.0.0.23"));
		print.o(range.inRange("0:0:0:0:0:0:0:1%0"));

		print.o(range.inRange("127.0.0.24"));
		
		
	}*/
}
