package railo.runtime.functions.image;

import java.awt.image.Kernel;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class ImageFilterKernel {
	public static Object call(PageContext pc, double width, double height, Object oData) throws PageException {
		
		float[] data=null;
		if(oData instanceof float[])
			data=(float[]) oData;
		else if(Decision.isNativeArray(oData)) {
			data=toFloatArray(pc,oData);
		}
		else if(Decision.isArray(oData)) {
			data=toFloatArray(pc,Caster.toNativeArray(oData));
		}
		else 
			throw new FunctionException(pc, "", 3, "data", "cannot cast data to a float array");
		
		return new Kernel(Caster.toIntValue(width),Caster.toIntValue(height),data);
	}

	private static float[] toFloatArray(PageContext pc,Object oData) throws PageException {
		float[] data=null;
		// Object[]
		if(oData instanceof Object[]) {
			Object[] arr = ((Object[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// boolean[]
		else if(oData instanceof boolean[]) {
			boolean[] arr = ((boolean[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// byte[]
		else if(oData instanceof byte[]) {
			byte[] arr = ((byte[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// short[]
		else if(oData instanceof short[]) {
			short[] arr = ((short[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// long[]
		else if(oData instanceof long[]) {
			long[] arr = ((long[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// int[]
		else if(oData instanceof int[]) {
			int[] arr = ((int[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		// double[]
		else if(oData instanceof double[]) {
			double[] arr = ((double[])oData);
			data=new float[arr.length];
			for(int i=0;i<arr.length;i++){
				data[i]=Caster.toFloatValue(arr[i]);
			}
		}
		else 
			throw new FunctionException(pc, "", 3, "data", "cannot cast data to a float array");
		
		return data;
	}
}
