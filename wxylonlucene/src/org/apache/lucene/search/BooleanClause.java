package org.apache.lucene.search;

import org.apache.lucene.util.Parameter;

/** A clause in a BooleanQuery. */
public class BooleanClause implements java.io.Serializable {

	/** Specifies how clauses are to occur in matching documents. */
	public static final class Occur extends Parameter implements java.io.Serializable {

		private Occur(String name) {
			super(name);
		}

		public String toString() {
			if (this == MUST)
				return "+";
			if (this == MUST_NOT)
				return "-";
			return "";
		}

		// 出现
		public static final Occur MUST = new Occur("MUST");
		
		// MUST 与 MUST_NOT 组合相当于 异或
		// MUST 与 MUST 组合相当于 与
		// SHOULD 与 MUST 组合相当于 MUST
		// SHOULD 与 SHOULD  组合相当于 或关系
		
		public static final Occur SHOULD = new Occur("SHOULD");
		// 不出现
		public static final Occur MUST_NOT = new Occur("MUST_NOT");

	}

	/**
	 * The query whose matching documents are combined by the boolean query.
	 */
	private Query query;

	private Occur occur;

	/**
	 * Constructs a BooleanClause.
	 */
	public BooleanClause(Query query, Occur occur) {
		this.query = query;
		this.occur = occur;

	}

	public Occur getOccur() {
		return occur;
	}

	public void setOccur(Occur occur) {
		this.occur = occur;

	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public boolean isProhibited() {
		return Occur.MUST_NOT.equals(occur);
	}

	public boolean isRequired() {
		return Occur.MUST.equals(occur);
	}

	/** Returns true iff <code>o</code> is equal to this. */
	public boolean equals(Object o) {
		if (!(o instanceof BooleanClause))
			return false;
		BooleanClause other = (BooleanClause) o;
		return this.query.equals(other.query) && this.occur.equals(other.occur);
	}

	/** Returns a hash code value for this object. */
	public int hashCode() {
		return query.hashCode() ^ (Occur.MUST.equals(occur) ? 1 : 0)
				^ (Occur.MUST_NOT.equals(occur) ? 2 : 0);
	}

	public String toString() {
		return occur.toString() + query.toString();
	}
}
