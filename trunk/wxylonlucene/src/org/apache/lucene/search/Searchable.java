package org.apache.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import java.io.IOException;
/**
 * �����������ǣ�  ʵ�ָýӿھ���Ϊ��������
 * @version ����ʱ�䣺Sep 26, 2009 8:21:01 AM
 */
//java.rmi.Remote  �ӿ����ڱ�ʶ�䷽�����ԴӷǱ���������ϵ��õĽӿ�
public interface Searchable extends java.rmi.Remote {
	//search �����ط���
	void search(Weight weight, Filter filter, HitCollector results)throws IOException;

	//�ر�searcher ��ͬʱҲ�ر���IndexReader
	void close() throws IOException;

	/**
	 * ���������а�����ָ����term��Ϣ���ĵ���
	 * @see IndexReader#docFreq(Term)
	 */
	int docFreq(Term term) throws IOException;

	//�����������ж��ٸ��ĵ���Ӵ������ָ����һ��term��Ϣ
	int[] docFreqs(Term[] terms) throws IOException;

	/**
	 * �����������������ܾ��е�Document������(�ڼ�1)
	 * @see IndexReader#maxDoc()
	 */
	int maxDoc() throws IOException;

	/**
	 * search �����ط���
	 * <p>
	 * Called by {@link Hits}.
	 * <p>
	 * Applications should usually call {@link Searcher#search(Query)} or
	 * {@link Searcher#search(Query,Filter)} instead.
	 * 
	 * @throws BooleanQuery.TooManyClauses
	 */
	TopDocs search(Weight weight, Filter filter, int n) throws IOException;

	/**
	 * ȡ�������е�ID��Ϊi���ĵ�
	 * @see IndexReader#document(int)
	 * @throws CorruptIndexException
	 *             if the index is corrupt
	 * @throws IOException
	 *             if there is a low-level IO error
	 */
	Document doc(int i) throws CorruptIndexException, IOException;

	/**
	 * Get the {@link org.apache.lucene.document.Document} at the <code>n</code><sup>th</sup>
	 * position. The {@link org.apache.lucene.document.FieldSelector} may be
	 * used to determine what {@link org.apache.lucene.document.Field}s to load
	 * and how they should be loaded.
	 * 
	 * <b>NOTE:</b> If the underlying Reader (more specifically, the underlying
	 * <code>FieldsReader</code>) is closed before the lazy
	 * {@link org.apache.lucene.document.Field} is loaded an exception may be
	 * thrown. If you want the value of a lazy
	 * {@link org.apache.lucene.document.Field} to be available after closing
	 * you must explicitly load it or fetch the Document again with a new
	 * loader.
	 * 
	 * 
	 * @param n
	 *            Get the document at the <code>n</code><sup>th</sup>
	 *            position
	 * @param fieldSelector
	 *            The {@link org.apache.lucene.document.FieldSelector} to use to
	 *            determine what Fields should be loaded on the Document. May be
	 *            null, in which case all Fields will be loaded.
	 * @return The stored fields of the
	 *         {@link org.apache.lucene.document.Document} at the nth position
	 * @throws CorruptIndexException
	 *             if the index is corrupt
	 * @throws IOException
	 *             if there is a low-level IO error
	 * 
	 * @see IndexReader#document(int, FieldSelector)
	 * @see org.apache.lucene.document.Fieldable
	 * @see org.apache.lucene.document.FieldSelector
	 * @see org.apache.lucene.document.SetBasedFieldSelector
	 * @see org.apache.lucene.document.LoadFirstFieldSelector
	 */
	Document doc(int n, FieldSelector fieldSelector)
			throws CorruptIndexException, IOException;

	//�Բ�ѯ����rewrite��ʹ֮��Ϊԭ�Ӳ�ѯ
	Query rewrite(Query query) throws IOException;

	//�Բ�ѯ�����Ȩ�ؽ��з���
	Explanation explain(Weight weight, int doc) throws IOException;

	//rearch �����ط���
	TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort)
			throws IOException;

}
