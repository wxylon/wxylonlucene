package org.apache.lucene.util;

/**
 * @version ����ʱ�䣺Sep 26, 2009 4:03:09 PM
 */
public class ToStringUtils {
	/**
	 * 此方法描述的是：判断是否为浮点数<b>1.0f</b>如果不是 <b>"^bootst"</b>，否则 返回 ""
	 * @version 创建时间：Nov 14, 2009 12:53:58 PM
	 * @param boost	浮点数
	 * @return 
	 * String	
	 */
	public static String boost(float boost) {
		if (boost != 1.0f) {
			return "^" + Float.toString(boost);
		} else
			return "";
	}
  public static void main(String[] args){
	  System.out.println(boost(1.0f));
}
}
