package com.xjd.io.util;

import java.io.IOException;

/**
 * <pre>
 * byte与各种类型间的转换
 * 
 * </pre>
 * @author  elvis.xu
 * @version	2013-9-29 下午4:04:38
 */
public abstract class ByteUtils {
	public static int LEN_BYTES_FOR_LONG = 8;
	public static int LEN_BYTES_FOR_INT = 4;

	public static byte[] long2Bytes(long l) {
		return longs2Bytes(new long[]{l});
	}

	public static long bytes2Long(byte[] bs) {
		return bytes2Longs(bs)[0];
	}

	/**
	 * long数组转化为byte数组
	 * @param longArray
	 * @return
	 */
	public static byte[] longs2Bytes(long[] longArray) {
		byte[] byteArray = new byte[longArray.length * 8];
		for (int i = 0; i < longArray.length; i++) {
			byteArray[0 + 8 * i] = (byte) (longArray[i] >> 56);
			byteArray[1 + 8 * i] = (byte) (longArray[i] >> 48);
			byteArray[2 + 8 * i] = (byte) (longArray[i] >> 40);
			byteArray[3 + 8 * i] = (byte) (longArray[i] >> 32);
			byteArray[4 + 8 * i] = (byte) (longArray[i] >> 24);
			byteArray[5 + 8 * i] = (byte) (longArray[i] >> 16);
			byteArray[6 + 8 * i] = (byte) (longArray[i] >> 8);
			byteArray[7 + 8 * i] = (byte) (longArray[i] >> 0);
		}
		return byteArray;
	}

	/**
	 * byte数组转化为long数组
	 * 
	 * @param byteArray
	 * @return
	 */
	public static long[] bytes2Longs(byte[] byteArray) {
		long[] longArray = new long[byteArray.length / 8];
		for (int i = 0; i < longArray.length; i++) {
			longArray[i] = (((long) (byteArray[0 + 8 * i] & 0xff)) << 56) 
					| (((long) (byteArray[1 + 8 * i] & 0xff)) << 48) 
					| (((long) (byteArray[2 + 8 * i] & 0xff)) << 40)
					| (((long) (byteArray[3 + 8 * i] & 0xff)) << 32) 
					| (((long) (byteArray[4 + 8 * i] & 0xff)) << 24) 
					| (((long) (byteArray[5 + 8 * i] & 0xff)) << 16)
					| (((long) (byteArray[6 + 8 * i] & 0xff)) << 8) 
					| (((long) (byteArray[7 + 8 * i] & 0xff)) << 0);

		}
		return longArray;
	}
	
	public static byte[] int2Bytes(int i) {
		return ints2Bytes(new int[]{i});
	}
	
	public static int bytes2Int(byte[] byteArray) {
		return bytes2Ints(byteArray)[0];
	}
	
	public static byte[] ints2Bytes(int[] intArray) {
		byte[] byteArray = new byte[intArray.length * 4];
		for (int i = 0; i < intArray.length; i++) {
			byteArray[0 + 8 * i] = (byte) (intArray[i] >> 24);
			byteArray[1 + 8 * i] = (byte) (intArray[i] >> 16);
			byteArray[2 + 8 * i] = (byte) (intArray[i] >> 8);
			byteArray[3 + 8 * i] = (byte) (intArray[i] >> 0);
		}
		return byteArray;
	}
	
	public static int[] bytes2Ints(byte[] byteArray) {
		int[] intArray = new int[byteArray.length / 4];
		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = (((int) (byteArray[0 + 8 * i] & 0xff)) << 24) 
					| (((int) (byteArray[1 + 8 * i] & 0xff)) << 16) 
					| (((int) (byteArray[2 + 8 * i] & 0xff)) << 8)
					| (((int) (byteArray[3 + 8 * i] & 0xff)) << 0);

		}
		return intArray;
	}
}
