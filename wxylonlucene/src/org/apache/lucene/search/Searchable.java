package org.apache.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import java.io.IOException;
/**
 * 此类描述的是：  实现该接口就视为可搜索的
 * @version 创建时间：Sep 26, 2009 8:21:01 AM
 */
//java.rmi.Remote  接口用于标识其方法可以从非本地虚拟机上调用的接口
public interface Searchable extends java.rmi.Remote {
	//search 的重载方法
	void search(Weight weight, Filter filter, HitCollector results)throws IOException;

	//关闭searcher ，同时也关闭了IndexReader
	void close() throws IOException;

	/**
	 * 计算索引中包含有指定的term信息的文档量
	 * @see IndexReader#docFreq(Term)
	 */
	int docFreq(Term term) throws IOException;

	//计算索引中有多少个文档包哟包含有指定的一组term信息
	int[] docFreqs(Term[] terms) throws IOException;

	/**
	 * 返回索引中有最大可能具有的Document的数量(在加1)
	 * @see IndexReader#maxDoc()
	 */
	int maxDoc() throws IOException;

	/**
	 * search 的重载方法
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
	 * 取出索引中的ID号为i的文档
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

	//对查询进行rewrite，使之成为原子查询
	Query rewrite(Query query) throws IOException;

	//对查询结果的权重进行翻译
	Explanation explain(Weight weight, int doc) throws IOException;

	//rearch 的重载方法
	TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort)
			throws IOException;

}
