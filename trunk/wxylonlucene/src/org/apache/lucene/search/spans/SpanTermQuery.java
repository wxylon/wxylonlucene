package org.apache.lucene.search.spans;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * 此类描述的是： 块词条搜索
 * 
 * @version 创建时间：Sep 25, 2009 2:11:00 PM
 */
public class SpanTermQuery extends SpanQuery {

	protected Term term;
	
	/**
	 * 创建一个新的实例 SpanTermQuery
	 * @param term
	 */
	public SpanTermQuery(Term term) {
		this.term = term;
	}

	//获得 本身的 term
	public Term getTerm() {
		return term;
	}
	
	//获得 term 的 field(key);
 	public String getField() {
		return term.field();
	}

	/**
	 * @deprecated use extractTerms instead
	 */
 	// 过期
	public Collection getTerms() {
		Collection terms = new ArrayList();
		terms.add(term);
		return terms;
	}
	
	public void extractTerms(Set terms) {
		terms.add(term);
	}

	public String toString(String field) {
		StringBuffer buffer = new StringBuffer();
		if (term.field().equals(field))
			buffer.append(term.text());
		else
			buffer.append(term.toString());
		buffer.append(ToStringUtils.boost(getBoost()));
		return buffer.toString();
	}

	/** Returns true iff <code>o</code> is equal to this. */
	public boolean equals(Object o) {
		if (!(o instanceof SpanTermQuery))
			return false;
		SpanTermQuery other = (SpanTermQuery) o;
		return (this.getBoost() == other.getBoost())
				&& this.term.equals(other.term);
	}

	/** Returns a hash code value for this object. */
	public int hashCode() {
		return Float.floatToIntBits(getBoost()) ^ term.hashCode() ^ 0xD23FE494;
	}

	public Spans getSpans(final IndexReader reader) throws IOException {
		return new TermSpans(reader.termPositions(term), term);
	}

	public PayloadSpans getPayloadSpans(IndexReader reader) throws IOException {
		return (PayloadSpans) getSpans(reader);
	}

}
