package org.apache.lucene.util;

/**
 * Methods for manipulating strings.
 * 操作字符串方法
 * $Id: StringHelper.java 641303 2008-03-26 13:39:25Z mikemccand $
 */
public abstract class StringHelper {
	
	private StringHelper() {}
	/**
	 * Compares two byte[] arrays, element by element, and returns the number of
	 * elements common to both arrays.
	 * 比较俩个数组的中 对应位置的值 是否相等， 返回最后一次匹配相等的位置
	 *
	 * @param bytes1 The first byte[] to compare
	 * @param bytes2 The second byte[] to compare
	 * @return The number of common elements.
	 */
	public static final int bytesDifference(byte[] bytes1, int len1, byte[] bytes2, int len2) {
		int len = len1 < len2 ? len1 : len2;
		for (int i = 0; i < len; i++)
			if (bytes1[i] != bytes2[i])
				return i;
		return len;
	}

	/**
	 * Compares two strings, character by character, and returns the first
	 * position where the two strings differ from one another.
	 * 比较俩个字符串的中 对应位置的值 是否相等， 返回最后一次匹配相等的位置
	 * @param s1 The first string to compare
	 * @param s2 The second string to compare
	 * @return The first position where the two strings differ.
	 */
	public static final int stringDifference(String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		int len = len1 < len2 ? len1 : len2;
		for (int i = 0; i < len; i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				return i;
			}
		}
		return len;
	}
}
