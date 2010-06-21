package org.apache.lucene.util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

public abstract class Parameter implements Serializable {

	static Map allParameters = new HashMap();

	private String name;

	private Parameter() {}

	protected Parameter(String name) {
		this.name = name;
		String key = makeKey(name);

		if (allParameters.containsKey(key))
			throw new IllegalArgumentException("Parameter name " + key
					+ " already used!");

		allParameters.put(key, this);
	}

	private String makeKey(String name) {
		return getClass() + " " + name;
	}

	public String toString() {
		return name;
	}

	/**
	 * �˷���������ǣ�allParameters �л�øö����
	 * @version ����ʱ�䣺Sep 23, 2009 4:40:28 PM
	 * @return
	 * @throws ObjectStreamException 
	 * Object
	 */
	protected Object readResolve() throws ObjectStreamException {
		Object par = allParameters.get(makeKey(name));

		if (par == null)
			throw new StreamCorruptedException("Unknown parameter value: "
					+ name);

		return par;
	}
	
	public static void main(String[] args){
	}

}
