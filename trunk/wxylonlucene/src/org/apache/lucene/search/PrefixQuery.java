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
//前缀搜索
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
		// BooleanQuery ,用于保存查询重写后的结果
		BooleanQuery query = new BooleanQuery(true);
		//使用IndexReader的terms方法取得以当前prefix开头的所有词条
		TermEnum enumerator = reader.terms(prefix);
		try {
			//取出词条文本
			String prefixText = prefix.text();
			String prefixField = prefix.field();
			do {
				Term term = enumerator.term();
				//若是当前词条以prefixText开头，则说明该词条应当符合查询条件
				if (term != null && term.text().startsWith(prefixText)
						&& term.field() == prefixField) // interned comparison
				{
					//构建term并加入BooleanQuery
					TermQuery tq = new TermQuery(term); // found a match
					tq.setBoost(getBoost()); // set the boost
					query.add(tq, BooleanClause.Occur.SHOULD); // add to query
					// System.out.println("added " + term);
				} else {
					//当遇到第一个不符合的，就结束
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
