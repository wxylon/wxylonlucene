package org.apache.lucene.util;

import java.lang.reflect.Array;

public final class ArrayUtil {

	public static int getNextSize(int targetSize) {
		/*
		 * This over-allocates proportional to the list size, making room for
		 * additional growth. The over-allocation is mild, but is enough to give
		 * linear-time amortized behavior over a long sequence of appends() in
		 * the presence of a poorly-performing system realloc(). The growth
		 * pattern is: 0, 4, 8, 16, 25, 35, 46, 58, 72, 88, ...
		 */
		//  >>  右移
		return (targetSize >> 3) + (targetSize < 9 ? 3 : 6) + targetSize;
	}

	public static int getShrinkSize(int currentSize, int targetSize) {
		final int newSize = getNextSize(targetSize);
		// Only reallocate if we are "substantially" smaller.
		// This saves us from "running hot" (constantly making a
		// bit bigger then a bit smaller, over and over):
		if (newSize < currentSize / 2)
			return newSize;
		else
			return currentSize;
	}

	public static int[] grow(int[] array, int minSize) {
		if (array.length < minSize) {
			int[] newArray = new int[getNextSize(minSize)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static int[] grow(int[] array) {
		return grow(array, 1 + array.length);
	}

	public static int[] shrink(int[] array, int targetSize) {
		final int newSize = getShrinkSize(array.length, targetSize);
		if (newSize != array.length) {
			int[] newArray = new int[newSize];
			System.arraycopy(array, 0, newArray, 0, newSize);
			return newArray;
		} else
			return array;
	}

	public static long[] grow(long[] array, int minSize) {
		if (array.length < minSize) {
			long[] newArray = new long[getNextSize(minSize)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static long[] grow(long[] array) {
		return grow(array, 1 + array.length);
	}

	public static long[] shrink(long[] array, int targetSize) {
		final int newSize = getShrinkSize(array.length, targetSize);
		if (newSize != array.length) {
			long[] newArray = new long[newSize];
			System.arraycopy(array, 0, newArray, 0, newSize);
			return newArray;
		} else
			return array;
	}

	public static byte[] grow(byte[] array, int minSize) {
		if (array.length < minSize) {
			byte[] newArray = new byte[getNextSize(minSize)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static byte[] grow(byte[] array) {
		return grow(array, 1 + array.length);
	}

	public static byte[] shrink(byte[] array, int targetSize) {
		final int newSize = getShrinkSize(array.length, targetSize);
		if (newSize != array.length) {
			byte[] newArray = new byte[newSize];
			System.arraycopy(array, 0, newArray, 0, newSize);
			return newArray;
		} else
			return array;
	}

	/**
	 * Returns hash of chars in range start (inclusive) to end (inclusive)
	 */
	public static int hashCode(char[] array, int start, int end) {
		int code = 0;
		for (int i = end - 1; i >= start; i--)
			code = code * 31 + array[i];
		return code;
	}

	/**
	 * Returns hash of chars in range start (inclusive) to end (inclusive)
	 */
	public static int hashCode(byte[] array, int start, int end) {
		int code = 0;
		for (int i = end - 1; i >= start; i--){
			System.out.println(code);
			code = code * 31 + array[i];
		}
		return code;
	}
	
	public static void main(String[] args){
//		byte[] aa = {1,1,1,1};
//		System.out.println(hashCode(aa,1,4));
		
		
		System.out.println(1+2>3);
	}
}