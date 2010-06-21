package org.apache.lucene.index;

/**
 * 此类描述的是：Term(理解为词条)类lucene 为查询条件(相当sql查询中的条件 eg.id = 001) 在 lucene 中
 * 
 * @version 创建时间：Sep 25, 2009 1:44:07 PM
 */
// Comparable接口 此接口强行对实现它的每个类的对象进行整体排序。此排序被称为该类的自然排序，类的 compareTo 方法被称为它的自然比较方法
public final class Term implements Comparable, java.io.Serializable {
	// key
	String field;
	// value
	String text;
	
	public Term(String fld, String txt) {
		this(fld, txt, true);
	}

	
	public Term(String fld) {
		this(fld, "", true);
	}

	Term(String fld, String txt, boolean intern) {
		field = intern ? fld.intern() : fld; // String.intern();// 字符串规范化形式，无明显区别
		text = txt; // unless already known to be
	}

	//返回指定的 key
	public final String field() {
		return field;
	}

	//返回指定的 value
	public final String text() {
		return text;
	}

	//创建一个 指定value 的查询条件
	public Term createTerm(String text) {
		return new Term(field, text, false);
	}

	// 俩 term field和text 是否相同，判断 field 和 text 是否相同
	public final boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof Term))
			return false;
		Term other = (Term) o;
		return field == other.field && text.equals(other.text);
	}

	//hashCode
	public final int hashCode() {
		return field.hashCode() + text.hashCode();
	}
	
	// 比较俩个 term 是否为同一个 term
	public int compareTo(Object other) {
		return compareTo((Term) other);
	}

	// term 比较也是标胶 field 和 text 是否相同 返回的是int差 只有返回0 才相等
	public final int compareTo(Term other) {
		if (field == other.field) // fields are interned
			return text.compareTo(other.text);
		else
			return field.compareTo(other.field);
	}

	//设置field 和 text 的值
	final void set(String fld, String txt) {
		field = fld;
		text = txt;
	}
	
	public final String toString() {
		return field + ":" + text;
	}

	private void readObject(java.io.ObjectInputStream in)
			throws java.io.IOException, ClassNotFoundException {
		in.defaultReadObject();
		field = field.intern();
	}
}
