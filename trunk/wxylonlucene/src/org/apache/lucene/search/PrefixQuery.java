package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ToStringUtils;

/**
 * A Query that matches documents containing terms with a specified prefix. A
 * PrefixQuery is built by QueryParser for input like <code>app*</code>.
 */
//ǰ׺����
public class PrefixQuery extends Query {
	private Term prefix;

	/** Constructs a query for terms starting with <code>prefix</code>. */
	public PrefixQuery(Term prefix) {
		this.prefix = prefix;
	}

	/** Returns the prefix of this query. */
	public Term getPrefix() {
		return prefix;
	}

	public Query rewrite(IndexReader reader) throws IOException {
		// BooleanQuery ,���ڱ����ѯ��д��Ľ��
		BooleanQuery query = new BooleanQuery(true);
		//ʹ��IndexReader��terms����ȡ���Ե�ǰprefix��ͷ�����д���
		TermEnum enumerator = reader.terms(prefix);
		try {
			//ȡ�������ı�
			String prefixText = prefix.text();
			String prefixField = prefix.field();
			do {
				Term term = enumerator.term();
				//���ǵ�ǰ������prefixText��ͷ����˵���ô���Ӧ�����ϲ�ѯ����
				if (term != null && term.text().startsWith(prefixText)
						&& term.field() == prefixField) // interned comparison
				{
					//����term������BooleanQuery
					TermQuery tq = new TermQuery(term); // found a match
					tq.setBoost(getBoost()); // set the boost
					query.add(tq, BooleanClause.Occur.SHOULD); // add to query
					// System.out.println("added " + term);
				} else {
					//��������һ�������ϵģ��ͽ���
					break;
				}
			} while (enumerator.next());
		} finally {
			enumerator.close();
		}
		return query;
	}

	/** Prints a user-readable version of this query. */
	public String toString(String field) {
		StringBuffer buffer = new StringBuffer();
		if (!prefix.field().equals(field)) {
			buffer.append(prefix.field());
			buffer.append(":");
		}
		buffer.append(prefix.text());
		buffer.append('*');
		buffer.append(ToStringUtils.boost(getBoost()));
		return buffer.toString();
	}

	/** Returns true iff <code>o</code> is equal to this. */
	public boolean equals(Object o) {
		if (!(o instanceof PrefixQuery))
			return false;
		PrefixQuery other = (PrefixQuery) o;
		return (this.getBoost() == other.getBoost())
				&& this.prefix.equals(other.prefix);
	}

	/** Returns a hash code value for this object. */
	public int hashCode() {
		return Float.floatToIntBits(getBoost()) ^ prefix.hashCode()
				^ 0x6634D93C;
	}
}
