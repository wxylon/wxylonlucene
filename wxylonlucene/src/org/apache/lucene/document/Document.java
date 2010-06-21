package org.apache.lucene.document;

import java.util.*; // for javadoc
/**
 * 此类描述的是： 对象 Field对象和 实现Fieldable接口的对象的操作，以及属性的获取..
 * @version 创建时间：Sep 23, 2009 4:32:49 PM
 */
public final class Document implements java.io.Serializable {
	
	List fields = new ArrayList();
	private float boost = 1.0f;

	public Document() {}
	
	/*
	 * setter
	 */
	public void setBoost(float boost) {
		this.boost = boost;
	}
	/*
	 * getter
	 */
	public float getBoost() {
		return boost;
	}
	
	/**
	 * 此方法描述的是：fields(List 类型) 中添加一个 (Fieldable接口的) 对象 
	 * @version 创建时间：Sep 23, 2009 4:04:51 PM
	 * @param field 
	 * void
	 */
	public final void add(Fieldable field) {
		fields.add(field);
	}

	/**
	 * 此方法描述的是：删除指定name 的 第一个 对象
	 * @version 创建时间：Sep 23, 2009 4:14:49 PM
	 * @param name 对象的name属性
	 * void
	 */
	public final void removeField(String name) {
		//对集合进行迭代的迭代器,迭代器允许调用方利用定义良好的语义在迭代期间从迭代器所指向的集合移除元素。
		Iterator it = fields.iterator();
		while (it.hasNext()) {
			Fieldable field = (Fieldable) it.next();
			if (field.name().equals(name)) {
				// 从迭代器指向的集合中移除迭代器返回的最后一个元素（可选操作）。
				it.remove();
				return;
			}
		}
	}

	/**
	 * 此方法描述的是：删除指定name 的 所有 对象
	 * @version 创建时间：Sep 23, 2009 4:14:49 PM
	 * @param name  对象的name属性
	 * void
	 */
	public final void removeFields(String name) {
		Iterator it = fields.iterator();
		while (it.hasNext()) {
			Fieldable field = (Fieldable) it.next();
			if (field.name().equals(name)) {
				it.remove();
			}
		}
	}

	/**
	 * 此方法描述的是：查找 指定 name 的 Field 对象
	 * @version 创建时间：Sep 23, 2009 4:17:10 PM
	 * @param name 对象的name属性
	 * @return 
	 * Field
	 */
	public final Field getField(String name) {
		for (int i = 0; i < fields.size(); i++) {
			Field field = (Field) fields.get(i);
			if (field.name().equals(name))
				return field;
		}
		return null;
	}

	/**
	 * 此方法描述的是：查找 指定 name 的 Fieldable接口的 对象
	 * @version 创建时间：Sep 23, 2009 4:18:03 PM
	 * @param name	 对象的name属性
	 * @return 
	 * Fieldable
	 */
	public Fieldable getFieldable(String name) {
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name))
				return field;
		}
		return null;
	}

	/**
	 * 此方法描述的是：查找 指定 name 的 Fieldable接口的 对象 并且 field.isBinary()为false
	 * @version 创建时间：Sep 23, 2009 4:21:37 PM
	 * @param name
	 * @return 
	 * String
	 */
	public final String get(String name) {
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name) && (!field.isBinary()))
				return field.stringValue();
		}
		return null;
	}

	
	public final Enumeration fields() {
		return new Enumeration() {
			final Iterator iter = fields.iterator();

			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			public Object nextElement() {
				return iter.next();
			}
		};
	}

	
	public final List getFields() {
		return fields;
	}

	private final static Field[] NO_FIELDS = new Field[0];

	/**
	 * 此方法描述的是：Field 数组
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public final Field[] getFields(String name) {
		List result = new ArrayList();
		for (int i = 0; i < fields.size(); i++) {
			Field field = (Field) fields.get(i);
			if (field.name().equals(name)) {
				result.add(field);
			}
		}

		if (result.size() == 0)
			return NO_FIELDS;

		return (Field[]) result.toArray(new Field[result.size()]);
	}

	private final static Fieldable[] NO_FIELDABLES = new Fieldable[0];

	/**
	 * 此方法描述的是：Fieldable 数组
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public Fieldable[] getFieldables(String name) {
		List result = new ArrayList();
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name)) {
				result.add(field);
			}
		}

		if (result.size() == 0)
			return NO_FIELDABLES;

		return (Fieldable[]) result.toArray(new Fieldable[result.size()]);
	}

	private final static String[] NO_STRINGS = new String[0];

	/**
	 * 此方法描述的是：field.stringValue() 数组
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public final String[] getValues(String name) {
		List result = new ArrayList();
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name) && (!field.isBinary()))
				result.add(field.stringValue());
		}

		if (result.size() == 0)
			return NO_STRINGS;

		return (String[]) result.toArray(new String[result.size()]);
	}

	private final static byte[][] NO_BYTES = new byte[0][];

	/**
	 * 此方法描述的是：field.binaryValue() 2维数组
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public final byte[][] getBinaryValues(String name) {
		List result = new ArrayList();
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name) && (field.isBinary()))
				result.add(field.binaryValue());
		}

		if (result.size() == 0)
			return NO_BYTES;

		return (byte[][]) result.toArray(new byte[result.size()][]);
	}

	/**
	 * 此方法描述的是：指定名字的 binaryValue
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public final byte[] getBinaryValue(String name) {
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			if (field.name().equals(name) && (field.isBinary()))
				return field.binaryValue();
		}
		return null;
	}

	/**
	 * 此方法描述的是：to String
	 * @version 创建时间：Sep 23, 2009 4:26:55 PM
	 * @param name
	 * @return 
	 * Field[]
	 */
	public final String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Document<");
		for (int i = 0; i < fields.size(); i++) {
			Fieldable field = (Fieldable) fields.get(i);
			buffer.append(field.toString());
			if (i != fields.size() - 1)
				buffer.append(" ");
		}
		buffer.append(">");
		return buffer.toString();
	}
	
	public static void main(String[] args){
	}
	
}
