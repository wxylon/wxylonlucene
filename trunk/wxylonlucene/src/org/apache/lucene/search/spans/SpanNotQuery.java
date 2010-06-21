package org.apache.lucene.search.spans;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * 此类描述的是：&& 匹配 include SpanQuery 的关键字中不包含 exclude SpanQuery 的关键字的情況
 * @version 创建时间：Sep 25, 2009 3:24:20 PM
 */
public class SpanNotQuery extends SpanQuery {
	//匹配该 SpanQuery
	private SpanQuery include;
	// 不匹配该 SpanQuery
	private SpanQuery exclude;

	/**
	 * 创建一个新的实例 SpanNotQuery
	 * 					include 的value 中不包含 exclude 的 value
	 * @param include	SpanQuery
	 * @param exclude	SpanQuery
	 */
	public SpanNotQuery(SpanQuery include, SpanQuery exclude) {
		this.include = include;
		this.exclude = exclude;

		if (!include.getField().equals(exclude.getField()))
			throw new IllegalArgumentException("Clauses must have same field.");
	}

	/** 返回 包含的SpanQuery*/
	public SpanQuery getInclude() {
		return include;
	}

	/** 返回 不包含的SpanQuery*/
	public SpanQuery getExclude() {
		return exclude;
	}
	/** 返回 SpanQuery 的 key 也就是索引的name*/
	public String getField() {
		return include.getField();
	}

	/**
	 *	返回Term的集合
	 * @deprecated use extractTerms instead
	 * @see #extractTerms(Set)
	 */
	public Collection getTerms() {
		return include.getTerms();
	}
	// include 装进terms集合中
	public void extractTerms(Set terms) {
		include.extractTerms(terms);
	}

	public String toString(String field) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("spanNot(");
		buffer.append(include.toString(field));
		buffer.append(", ");
		buffer.append(exclude.toString(field));
		buffer.append(")");
		buffer.append(ToStringUtils.boost(getBoost()));
		return buffer.toString();
	}

	public Spans getSpans(final IndexReader reader) throws IOException {
		return new PayloadSpans() {
			private PayloadSpans includeSpans = include.getPayloadSpans(reader);
			private boolean moreInclude = true;

			private Spans excludeSpans = exclude.getSpans(reader);
			private boolean moreExclude = excludeSpans.next();

			public boolean next() throws IOException {
				if (moreInclude) // move to next include
					moreInclude = includeSpans.next();

				while (moreInclude && moreExclude) {

					if (includeSpans.doc() > excludeSpans.doc()) // skip
																	// exclude
						moreExclude = excludeSpans.skipTo(includeSpans.doc());

					while (moreExclude // while exclude is before
							&& includeSpans.doc() == excludeSpans.doc()
							&& excludeSpans.end() <= includeSpans.start()) {
						moreExclude = excludeSpans.next(); // increment exclude
					}

					if (!moreExclude // if no intersection
							|| includeSpans.doc() != excludeSpans.doc()
							|| includeSpans.end() <= excludeSpans.start())
						break; // we found a match

					moreInclude = includeSpans.next(); // intersected: keep
														// scanning
				}
				return moreInclude;
			}

			public boolean skipTo(int target) throws IOException {
				if (moreInclude) // skip include
					moreInclude = includeSpans.skipTo(target);

				if (!moreInclude)
					return false;

				if (moreExclude // skip exclude
						&& includeSpans.doc() > excludeSpans.doc())
					moreExclude = excludeSpans.skipTo(includeSpans.doc());

				while (moreExclude // while exclude is before
						&& includeSpans.doc() == excludeSpans.doc()
						&& excludeSpans.end() <= includeSpans.start()) {
					moreExclude = excludeSpans.next(); // increment exclude
				}

				if (!moreExclude // if no intersection
						|| includeSpans.doc() != excludeSpans.doc()
						|| includeSpans.end() <= excludeSpans.start())
					return true; // we found a match

				return next(); // scan to next match
			}

			public int doc() {
				return includeSpans.doc();
			}

			public int start() {
				return includeSpans.start();
			}

			public int end() {
				return includeSpans.end();
			}

			// TODO: Remove warning after API has been finalizedb
			public Collection/* <byte[]> */getPayload() throws IOException {
				ArrayList result = null;
				if (includeSpans.isPayloadAvailable()) {
					result = new ArrayList(includeSpans.getPayload());
				}
				return result;
			}

			// TODO: Remove warning after API has been finalized
			public boolean isPayloadAvailable() {
				return includeSpans.isPayloadAvailable();
			}

			public String toString() {
				return "spans(" + SpanNotQuery.this.toString() + ")";
			}

		};
	}

	public PayloadSpans getPayloadSpans(IndexReader reader) throws IOException {
		return (PayloadSpans) getSpans(reader);
	}

	public Query rewrite(IndexReader reader) throws IOException {
		SpanNotQuery clone = null;

		SpanQuery rewrittenInclude = (SpanQuery) include.rewrite(reader);
		if (rewrittenInclude != include) {
			clone = (SpanNotQuery) this.clone();
			clone.include = rewrittenInclude;
		}
		SpanQuery rewrittenExclude = (SpanQuery) exclude.rewrite(reader);
		if (rewrittenExclude != exclude) {
			if (clone == null)
				clone = (SpanNotQuery) this.clone();
			clone.exclude = rewrittenExclude;
		}

		if (clone != null) {
			return clone; // some clauses rewrote
		} else {
			return this; // no clauses rewrote
		}
	}

	/** Returns true iff <code>o</code> is equal to this. */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SpanNotQuery))
			return false;

		SpanNotQuery other = (SpanNotQuery) o;
		return this.include.equals(other.include)
				&& this.exclude.equals(other.exclude)
				&& this.getBoost() == other.getBoost();
	}

	public int hashCode() {
		int h = include.hashCode();
		h = (h << 1) | (h >>> 31); // rotate left
		h ^= exclude.hashCode();
		h = (h << 1) | (h >>> 31); // rotate left
		h ^= Float.floatToRawIntBits(getBoost());
		return h;
	}

}
