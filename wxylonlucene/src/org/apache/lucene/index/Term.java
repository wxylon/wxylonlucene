package org.apache.lucene.index;

/**
 * �����������ǣ�Term(���Ϊ����)��lucene Ϊ��ѯ����(�൱sql��ѯ�е����� eg.id = 001) �� lucene ��
 * 
 * @version ����ʱ�䣺Sep 25, 2009 1:44:07 PM
 */
// Comparable�ӿ� �˽ӿ�ǿ�ж�ʵ������ÿ����Ķ�������������򡣴����򱻳�Ϊ�������Ȼ������� compareTo ��������Ϊ������Ȼ�ȽϷ���
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
		field = intern ? fld.intern() : fld; // String.intern();// �ַ����淶����ʽ������������
		text = txt; // unless already known to be
	}

	//����ָ���� key
	public final String field() {
		return field;
	}

	//����ָ���� value
	public final String text() {
		return text;
	}

	//����һ�� ָ��value �Ĳ�ѯ����
	public Term createTerm(String text) {
		return new Term(field, text, false);
	}

	// �� term field��text �Ƿ���ͬ���ж� field �� text �Ƿ���ͬ
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
	
	// �Ƚ����� term �Ƿ�Ϊͬһ�� term
	public int compareTo(Object other) {
		return compareTo((Term) other);
	}

	// term �Ƚ�Ҳ�Ǳ꽺 field �� text �Ƿ���ͬ ���ص���int�� ֻ�з���0 �����
	public final int compareTo(Term other) {
		if (field == other.field) // fields are interned
			return text.compareTo(other.text);
		else
			return field.compareTo(other.field);
	}

	//����field �� text ��ֵ
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
