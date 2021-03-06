package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;

/**
 * 此类描述的是： 实现该类就视为可搜索的
 * 
 * @version 创建时间：Sep 26, 2009 8:21:01 AM
 */
public abstract class Searcher implements Searchable {

	//返回hits 对象
	//检索只返回得分最高的document method: public topDocs  ***  && public TopFieldDocs ***
	//传入HitCollector,将结果保存在HitCollector 中 method : public void search(*, HitCollector results);
	/**
	 * Returns the documents matching <code>query</code>.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 * @deprecated Hits will be removed in Lucene 3.0. Use
	 *             {@link #search(Query, Filter, int)} instead.
	 */
	public final Hits search(Query query) throws IOException {
		return search(query, (Filter) null);
	}

	/**
	 * Returns the documents matching <code>query</code> and
	 * <code>filter</code>.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 * @deprecated Hits will be removed in Lucene 3.0. Use
	 *             {@link #search(Query, Filter, int)} instead.
	 */
	public Hits search(Query query, Filter filter) throws IOException {
		return new Hits(this, query, filter);
	}

	/**
	 * Returns documents matching <code>query</code> sorted by
	 * <code>sort</code>.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 * @deprecated Hits will be removed in Lucene 3.0. Use
	 *             {@link #search(Query, Filter, int, Sort)} instead.
	 */
	public Hits search(Query query, Sort sort) throws IOException {
		return new Hits(this, query, null, sort);
	}

	/**
	 * Returns documents matching <code>query</code> and <code>filter</code>,
	 * sorted by <code>sort</code>.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 * @deprecated Hits will be removed in Lucene 3.0. Use
	 *             {@link #search(Query, Filter, int, Sort)} instead.
	 */
	public Hits search(Query query, Filter filter, Sort sort)
			throws IOException {
		return new Hits(this, query, filter, sort);
	}

	/**
	 * Search implementation with arbitrary sorting. Finds the top
	 * <code>n</code> hits for <code>query</code>, applying
	 * <code>filter</code> if non-null, and sorting the hits by the criteria
	 * in <code>sort</code>.
	 * 
	 * <p>
	 * Applications should usually call {@link
	 * Searcher#search(Query,Filter,Sort)} instead.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 */
	public TopFieldDocs search(Query query, Filter filter, int n, Sort sort)
			throws IOException {
		return search(createWeight(query), filter, n, sort);
	}

	/**
	 * Lower-level search API.
	 * 
	 * <p>
	 * {@link HitCollector#collect(int,float)} is called for every matching
	 * document.
	 * 
	 * <p>
	 * Applications should only use this if they need <i>all</i> of the
	 * matching documents. The high-level search API ({@link
	 * Searcher#search(Query)}) is usually more efficient, as it skips
	 * non-high-scoring hits.
	 * <p>
	 * Note: The <code>score</code> passed to this method is a raw score. In
	 * other words, the score will not necessarily be a float whose value is
	 * between 0 and 1.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 */
	public void search(Query query, HitCollector results) throws IOException {
		search(query, (Filter) null, results);
	}

	/**
	 * Lower-level search API.
	 * 
	 * <p>
	 * {@link HitCollector#collect(int,float)} is called for every matching
	 * document. <br>
	 * HitCollector-based access to remote indexes is discouraged.
	 * 
	 * <p>
	 * Applications should only use this if they need <i>all</i> of the
	 * matching documents. The high-level search API ({@link
	 * Searcher#search(Query, Filter, int)}) is usually more efficient, as it
	 * skips non-high-scoring hits.
	 * 
	 * @param query
	 *            to match documents
	 * @param filter
	 *            if non-null, used to permit documents to be collected.
	 * @param results
	 *            to receive hits
	 * @throws BooleanQuery.TooManyClauses
	 */
	public void search(Query query, Filter filter, HitCollector results)
			throws IOException {
		search(createWeight(query), filter, results);
	}

	/**
	 * Finds the top <code>n</code> hits for <code>query</code>, applying
	 * <code>filter</code> if non-null.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 */
	public TopDocs search(Query query, Filter filter, int n) throws IOException {
		return search(createWeight(query), filter, n);
	}

	/**
	 * Finds the top <code>n</code> hits for <code>query</code>.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 */
	public TopDocs search(Query query, int n) throws IOException {
		return search(query, null, n);
	}

	/**
	 * Returns an Explanation that describes how <code>doc</code> scored
	 * against <code>query</code>.
	 * 
	 * <p>
	 * This is intended to be used in developing Similarity implementations,
	 * and, for good performance, should not be displayed with every hit.
	 * Computing an explanation is as expensive as executing the query over the
	 * entire index.
	 */
	public Explanation explain(Query query, int doc) throws IOException {
		return explain(createWeight(query), doc);
	}

	/** The Similarity implementation used by this searcher. */
	private Similarity similarity = Similarity.getDefault();

	/**
	 * Expert: Set the Similarity implementation used by this Searcher.
	 * 
	 * @see Similarity#setDefault(Similarity)
	 */
	public void setSimilarity(Similarity similarity) {
		this.similarity = similarity;
	}

	/**
	 * Expert: Return the Similarity implementation used by this Searcher.
	 * 
	 * <p>
	 * This defaults to the current value of {@link Similarity#getDefault()}.
	 */
	public Similarity getSimilarity() {
		return this.similarity;
	}

	/**
	 * creates a weight for <code>query</code>
	 * 
	 * @return new weight
	 */
	protected Weight createWeight(Query query) throws IOException {
		return query.weight(this);
	}

	// inherit javadoc
	public int[] docFreqs(Term[] terms) throws IOException {
		int[] result = new int[terms.length];
		for (int i = 0; i < terms.length; i++) {
			result[i] = docFreq(terms[i]);
		}
		return result;
	}

	/*
	 * The following abstract methods were added as a workaround for GCJ bug
	 * #15411. http://gcc.gnu.org/bugzilla/show_bug.cgi?id=15411
	 */
	abstract public void search(Weight weight, Filter filter,
			HitCollector results) throws IOException;

	abstract public void close() throws IOException;

	abstract public int docFreq(Term term) throws IOException;

	abstract public int maxDoc() throws IOException;

	abstract public TopDocs search(Weight weight, Filter filter, int n)
			throws IOException;

	abstract public Document doc(int i) throws CorruptIndexException,
			IOException;

	abstract public Query rewrite(Query query) throws IOException;

	abstract public Explanation explain(Weight weight, int doc)
			throws IOException;

	abstract public TopFieldDocs search(Weight weight, Filter filter, int n,
			Sort sort) throws IOException;
	/* End patch for GCJ bug #15411. */
}
