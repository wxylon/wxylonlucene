package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.ToStringUtils;

/**
 * Matches spans which are near one another. One can specify <i>slop</i>, the
 * maximum number of intervening unmatched positions, as well as whether matches
 * are required to be in-order.
 */
public class SpanNearQuery extends SpanQuery {
	//SpanQuery �ļ���
	private List clauses;
	
	//SpanQuery �ļ����� ���� ֮���������ʿ���; eg. 1&nbsp;2&nubsp;  ֮��Ĵʿ�Ϊ1
	private int slop;
	
	//�Ƿ�ȫ��ƥ��
	private boolean inOrder;
	
	// ƥ���key
	private String field;

	/**
	 * ����һ�� SpanNearQuery.
	 * @param clauses	SpanQuery����
	 * @param slop		SpanQuery֮��ļ�����ʿ���
	 * @param inOrder	�Ƿ�ȫ��ƥ��
	 */
	public SpanNearQuery(SpanQuery[] clauses, int slop, boolean inOrder) {

		// ����ת��ΪArrayList
		this.clauses = new ArrayList(clauses.length);
		for (int i = 0; i < clauses.length; i++) {
			SpanQuery clause = clauses[i];
			if (i == 0) { // check field
				field = clause.getField();
			} else if (!clause.getField().equals(field)) {
				throw new IllegalArgumentException(
						"Clauses must have same field.");
			}
			this.clauses.add(clause);
		}

		this.slop = slop;
		this.inOrder = inOrder;
	}

	//���������ʽ�� SpanQuery
	public SpanQuery[] getClauses() {
		return (SpanQuery[]) clauses.toArray(new SpanQuery[clauses.size()]);
	}

	/** SpanQuery֮��ļ�����ʿ���. */
	public int getSlop() {
		return slop;
	}

	/** inOrder��ֵ */
	public boolean isInOrder() {
		return inOrder;
	}
	/** field��ֵ key */
	public String getField() {
		return field;
	}

	/**
	 * �ɸò�ѯƥ���������������һ�����ϡ�
	 * @deprecated use extractTerms instead
	 */
	public Collection getTerms() {
		Collection terms = new ArrayList();
		Iterator i = clauses.iterator();
		while (i.hasNext()) {
			SpanQuery clause = (SpanQuery) i.next();
			terms.addAll(clause.getTerms());
		}
		return terms;
	}
	//��SpanQuery����clauses װ�� �ü���terms��
	public void extractTerms(Set terms) {
		Iterator i = clauses.iterator();
		while (i.hasNext()) {
			SpanQuery clause = (SpanQuery) i.next();
			clause.extractTerms(terms);
		}
	}

	public String toString(String field) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("spanNear([");
		Iterator i = clauses.iterator();
		while (i.hasNext()) {
			SpanQuery clause = (SpanQuery) i.next();
			buffer.append(clause.toString(field));
			if (i.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append("], ");
		buffer.append(slop);
		buffer.append(", ");
		buffer.append(inOrder);
		buffer.append(")");
		buffer.append(ToStringUtils.boost(getBoost()));
		return buffer.toString();
	}

	public Spans getSpans(final IndexReader reader) throws IOException {
		if (clauses.size() == 0) // optimize 0-clause case
			return new SpanOrQuery(getClauses()).getPayloadSpans(reader);

		if (clauses.size() == 1) // optimize 1-clause case
			return ((SpanQuery) clauses.get(0)).getPayloadSpans(reader);

		return inOrder ? (PayloadSpans) new NearSpansOrdered(this, reader)
				: (PayloadSpans) new NearSpansUnordered(this, reader);
	}

	public PayloadSpans getPayloadSpans(IndexReader reader) throws IOException {
		return (PayloadSpans) getSpans(reader);
	}

	public Query rewrite(IndexReader reader) throws IOException {
		SpanNearQuery clone = null;
		for (int i = 0; i < clauses.size(); i++) {
			SpanQuery c = (SpanQuery) clauses.get(i);
			SpanQuery query = (SpanQuery) c.rewrite(reader);
			if (query != c) { // clause rewrote: must clone
				if (clone == null)
					clone = (SpanNearQuery) this.clone();
				clone.clauses.set(i, query);
			}
		}
		if (clone != null) {
			return clone; // some clauses rewrote
		} else {
			return this; // no clauses rewrote
		}
	}

	// �Ƚ��Ƿ����
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SpanNearQuery))
			return false;

		final SpanNearQuery spanNearQuery = (SpanNearQuery) o;

		if (inOrder != spanNearQuery.inOrder)
			return false;
		if (slop != spanNearQuery.slop)
			return false;
		if (!clauses.equals(spanNearQuery.clauses))
			return false;

		return getBoost() == spanNearQuery.getBoost();
	}

	public int hashCode() {
		int result;
		result = clauses.hashCode();
		// Mix bits before folding in things like boost, since it could cancel
		// the
		// last element of clauses. This particular mix also serves to
		// differentiate SpanNearQuery hashcodes from others.
		result ^= (result << 14) | (result >>> 19); // reversible
		result += Float.floatToRawIntBits(getBoost());
		result += slop;
		result ^= (inOrder ? 0x99AFD3BD : 0);
		return result;
	}
}
