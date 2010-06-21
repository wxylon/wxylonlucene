package org.apache.lucene.document;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexWriter; // for javadoc
import org.apache.lucene.util.Parameter;
import java.io.Reader;
import java.io.Serializable;

public final class Field extends AbstractField implements Fieldable,Serializable {
	/**
	 * 此类描述的是：设置Field的存储属性
	 * @version 创建时间：Sep 23, 2009 4:44:41 PM
	 */
	public static final class Store extends Parameter implements Serializable {

		private Store(String name) {
			super(name);
		}

		//COMPRESS 对象	标识使用压缩方式来保存这个field的值
		public static final Store COMPRESS = new Store("COMPRESS");
		
		// YES 对象	表示该field需要存储
		public static final Store YES = new Store("YES");

		// NO 对象	表示该field不需要存储
		public static final Store NO = new Store("NO");
	}
	
	/**
	 * 此类描述的是：设置Field的索引属性
	 * @version 创建时间：2009-9-25 上午11:29:17
	 */
	public static final class Index extends Parameter implements Serializable {

		private Index(String name) {
			super(name);
		}

		//不对Field进行索引，所以这个Field就不能被检索到(一般来说，建立索引而使它不被检索，这是没有意义的)
		//如果对该Field还设置了Field.Store为Field.Store.YES或Field.Store.COMPRESS，则可以检索
		public static final Index NO = new Index("NO");

		//对Field进行索引，同时还要对其进行分词(由Analyzer来管理如何分词)
		public static final Index ANALYZED = new Index("ANALYZED");

		/**
		 * @deprecated
		 */
		//废弃的属性，使用ANALYZED 来替代
		public static final Index TOKENIZED = ANALYZED;
		
		//表示不对该field进行分词，但是要对它进行索引
		public static final Index NOT_ANALYZED = new Index("NOT_ANALYZED");
		/**
		 * @deprecated
		 */
		//废弃的属性，使用NOT_ANALYZED来替代
		public static final Index UN_TOKENIZED = NOT_ANALYZED;

		// 表示对该field进行索引，但是不使用analyzer，同时禁止它参加评分，主要是为了减少内存消耗
		public static final Index NOT_ANALYZED_NO_NORMS = new Index("NOT_ANALYZED_NO_NORMS");

		/**
		 * @deprecated
		 */
		//废弃的属性，有NOT_ANALYZED_NO_NORMS来替代
		public static final Index NO_NORMS = NOT_ANALYZED_NO_NORMS;

		//对Field属性，使用分词，但是不是使用Analyzer来分词
		public static final Index ANALYZED_NO_NORMS = new Index("ANALYZED_NO_NORMS");
	}

	/**
	 * 此类描述的是： 这是一个与词条有关的类。因为在检索的时候需要指定检索关键字，通过为一个Field添加一个TermVector，就可以在检索中把该Field检索到。
	 * @version 创建时间：Sep 23, 2009 4:48:50 PM
	 */
	public static final class TermVector extends Parameter implements Serializable {

		private TermVector(String name) {
			super(name);
		}

		//不存储
		public static final TermVector NO = new TermVector("NO");

		//为每个Document都存储一个TermVector
		public static final TermVector YES = new TermVector("YES");

		//存储，并且存在位置信息
		public static final TermVector WITH_POSITIONS = new TermVector("WITH_POSITIONS");

		//存储，并且存贮偏移量信息
		public static final TermVector WITH_OFFSETS = new TermVector("WITH_OFFSETS");

		//存储位置、偏移量等所有信息
		public static final TermVector WITH_POSITIONS_OFFSETS = new TermVector("WITH_POSITIONS_OFFSETS");
	}
	
	/**
	 * 取 fieldsData 的 String 值 
	 */
	public String stringValue() {
		return fieldsData instanceof String ? (String) fieldsData : null;
	}

	/**
	 * 取 binaryOffset 的 Reader 值 
	 */
	public Reader readerValue() {
		return fieldsData instanceof Reader ? (Reader) fieldsData : null;
	}

	/**取 fieldsData 的 byte[] 值 
	 * @deprecated This method must allocate a new byte[] if the
	 *             {@link AbstractField#getBinaryOffset()} is non-zero or
	 *             {@link AbstractField#getBinaryLength()} is not the full
	 *             length of the byte[]. Please use {@link
	 *             AbstractField#getBinaryValue()} instead, which simply returns
	 *             the byte[].
	 */
	public byte[] binaryValue() {
		if (!isBinary)
			return null;
		final byte[] data = (byte[]) fieldsData;
		if (binaryOffset == 0 && data.length == binaryLength)
			return data; // Optimization

		final byte[] ret = new byte[binaryLength];
		System.arraycopy(data, binaryOffset, ret, 0, binaryLength);
		return ret;
	}

	/**
	 * The value of the field as a TokesStream, or null. If null, the Reader
	 * value, String value, or binary value is used. Exactly one of
	 * stringValue(), readerValue(), getBinaryValue(), and tokenStreamValue()
	 * must be set.
	 */
	public TokenStream tokenStreamValue() {
		return fieldsData instanceof TokenStream ? (TokenStream) fieldsData
				: null;
	}

	/**
	 * <p>
	 * Expert: change the value of this field. This can be used during indexing
	 * to re-use a single Field instance to improve indexing speed by avoiding
	 * GC cost of new'ing and reclaiming Field instances. Typically a single
	 * {@link Document} instance is re-used as well. This helps most on small
	 * documents.
	 * </p>
	 * 
	 * <p>
	 * Note that you should only use this method after the Field has been
	 * consumed (ie, the {@link Document} containing this Field has been added
	 * to the index). Also, each Field instance should only be used once within
	 * a single {@link Document} instance. See <a
	 * href="http://wiki.apache.org/lucene-java/ImproveIndexingSpeed">ImproveIndexingSpeed</a>
	 * for details.
	 * </p>
	 */
	public void setValue(String value) {
		fieldsData = value;
	}

	/**
	 * Expert: change the value of this field. See <a
	 * href="#setValue(java.lang.String)">setValue(String)</a>.
	 */
	public void setValue(Reader value) {
		fieldsData = value;
	}

	/**
	 * Expert: change the value of this field. See <a
	 * href="#setValue(java.lang.String)">setValue(String)</a>.
	 */
	public void setValue(byte[] value) {
		fieldsData = value;
		binaryLength = value.length;
		binaryOffset = 0;
	}

	/**
	 * Expert: change the value of this field. See <a
	 * href="#setValue(java.lang.String)">setValue(String)</a>.
	 */
	public void setValue(byte[] value, int offset, int length) {
		fieldsData = value;
		binaryLength = length;
		binaryOffset = offset;
	}

	/**
	 * Expert: change the value of this field. See <a
	 * href="#setValue(java.lang.String)">setValue(String)</a>.
	 */
	public void setValue(TokenStream value) {
		fieldsData = value;
	}

	/**
	 * Create a field by specifying its name, value and how it will be saved in
	 * the index. Term vectors will not be stored in the index.
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The string to process
	 * @param store
	 *            Whether <code>value</code> should be stored in the index
	 * @param index
	 *            Whether the field should be indexed, and if so, if it should
	 *            be tokenized before indexing
	 * @throws NullPointerException
	 *             if name or value is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the field is neither stored nor indexed
	 */
	public Field(String name, String value, Store store, Index index) {
		this(name, value, store, index, TermVector.NO);
	}

	/**
	 * Create a field by specifying its name, value and how it will be saved in
	 * the index.
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The string to process
	 * @param store
	 *            Whether <code>value</code> should be stored in the index
	 * @param index
	 *            Whether the field should be indexed, and if so, if it should
	 *            be tokenized before indexing
	 * @param termVector
	 *            Whether term vector should be stored
	 * @throws NullPointerException
	 *             if name or value is <code>null</code>
	 * @throws IllegalArgumentException
	 *             in any of the following situations:
	 *             <ul>
	 *             <li>the field is neither stored nor indexed</li>
	 *             <li>the field is not indexed but termVector is
	 *             <code>TermVector.YES</code></li>
	 *             </ul>
	 */
	public Field(String name, String value, Store store, Index index,
			TermVector termVector) {
		if (name == null)
			throw new NullPointerException("name cannot be null");
		if (value == null)
			throw new NullPointerException("value cannot be null");
		if (name.length() == 0 && value.length() == 0)
			throw new IllegalArgumentException(
					"name and value cannot both be empty");
		if (index == Index.NO && store == Store.NO)
			throw new IllegalArgumentException(
					"it doesn't make sense to have a field that "
							+ "is neither indexed nor stored");
		if (index == Index.NO && termVector != TermVector.NO)
			throw new IllegalArgumentException(
					"cannot store term vector information "
							+ "for a field that is not indexed");

		this.name = name.intern(); // field names are interned
		this.fieldsData = value;

		if (store == Store.YES) {
			this.isStored = true;
			this.isCompressed = false;
		} else if (store == Store.COMPRESS) {
			this.isStored = true;
			this.isCompressed = true;
		} else if (store == Store.NO) {
			this.isStored = false;
			this.isCompressed = false;
		} else
			throw new IllegalArgumentException("unknown store parameter "
					+ store);

		if (index == Index.NO) {
			this.isIndexed = false;
			this.isTokenized = false;
		} else if (index == Index.ANALYZED) {
			this.isIndexed = true;
			this.isTokenized = true;
		} else if (index == Index.NOT_ANALYZED) {
			this.isIndexed = true;
			this.isTokenized = false;
		} else if (index == Index.NOT_ANALYZED_NO_NORMS) {
			this.isIndexed = true;
			this.isTokenized = false;
			this.omitNorms = true;
		} else if (index == Index.ANALYZED_NO_NORMS) {
			this.isIndexed = true;
			this.isTokenized = true;
			this.omitNorms = true;
		} else {
			throw new IllegalArgumentException("unknown index parameter "
					+ index);
		}

		this.isBinary = false;

		setStoreTermVector(termVector);
	}

	/**
	 * Create a tokenized and indexed field that is not stored. Term vectors
	 * will not be stored. The Reader is read only when the Document is added to
	 * the index, i.e. you may not close the Reader until
	 * {@link IndexWriter#addDocument(Document)} has been called.
	 * 
	 * @param name
	 *            The name of the field
	 * @param reader
	 *            The reader with the content
	 * @throws NullPointerException
	 *             if name or reader is <code>null</code>
	 */
	public Field(String name, Reader reader) {
		this(name, reader, TermVector.NO);
	}

	/**
	 * Create a tokenized and indexed field that is not stored, optionally with
	 * storing term vectors. The Reader is read only when the Document is added
	 * to the index, i.e. you may not close the Reader until
	 * {@link IndexWriter#addDocument(Document)} has been called.
	 * 
	 * @param name
	 *            The name of the field
	 * @param reader
	 *            The reader with the content
	 * @param termVector
	 *            Whether term vector should be stored
	 * @throws NullPointerException
	 *             if name or reader is <code>null</code>
	 */
	public Field(String name, Reader reader, TermVector termVector) {
		if (name == null)
			throw new NullPointerException("name cannot be null");
		if (reader == null)
			throw new NullPointerException("reader cannot be null");

		this.name = name.intern(); // field names are interned
		this.fieldsData = reader;

		this.isStored = false;
		this.isCompressed = false;

		this.isIndexed = true;
		this.isTokenized = true;

		this.isBinary = false;

		setStoreTermVector(termVector);
	}

	/**
	 * Create a tokenized and indexed field that is not stored. Term vectors
	 * will not be stored. This is useful for pre-analyzed fields. The
	 * TokenStream is read only when the Document is added to the index, i.e.
	 * you may not close the TokenStream until
	 * {@link IndexWriter#addDocument(Document)} has been called.
	 * 
	 * @param name
	 *            The name of the field
	 * @param tokenStream
	 *            The TokenStream with the content
	 * @throws NullPointerException
	 *             if name or tokenStream is <code>null</code>
	 */
	public Field(String name, TokenStream tokenStream) {
		this(name, tokenStream, TermVector.NO);
	}

	/**
	 * Create a tokenized and indexed field that is not stored, optionally with
	 * storing term vectors. This is useful for pre-analyzed fields. The
	 * TokenStream is read only when the Document is added to the index, i.e.
	 * you may not close the TokenStream until
	 * {@link IndexWriter#addDocument(Document)} has been called.
	 * 
	 * @param name
	 *            The name of the field
	 * @param tokenStream
	 *            The TokenStream with the content
	 * @param termVector
	 *            Whether term vector should be stored
	 * @throws NullPointerException
	 *             if name or tokenStream is <code>null</code>
	 */
	public Field(String name, TokenStream tokenStream, TermVector termVector) {
		if (name == null)
			throw new NullPointerException("name cannot be null");
		if (tokenStream == null)
			throw new NullPointerException("tokenStream cannot be null");

		this.name = name.intern(); // field names are interned
		this.fieldsData = tokenStream;

		this.isStored = false;
		this.isCompressed = false;

		this.isIndexed = true;
		this.isTokenized = true;

		this.isBinary = false;

		setStoreTermVector(termVector);
	}

	/**
	 * Create a stored field with binary value. Optionally the value may be
	 * compressed.
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The binary value
	 * @param store
	 *            How <code>value</code> should be stored (compressed or not)
	 * @throws IllegalArgumentException
	 *             if store is <code>Store.NO</code>
	 */
	public Field(String name, byte[] value, Store store) {
		this(name, value, 0, value.length, store);
	}

	/**
	 * Create a stored field with binary value. Optionally the value may be
	 * compressed.
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The binary value
	 * @param offset
	 *            Starting offset in value where this Field's bytes are
	 * @param length
	 *            Number of bytes to use for this Field, starting at offset
	 * @param store
	 *            How <code>value</code> should be stored (compressed or not)
	 * @throws IllegalArgumentException
	 *             if store is <code>Store.NO</code>
	 */
	public Field(String name, byte[] value, int offset, int length, Store store) {

		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		if (value == null)
			throw new IllegalArgumentException("value cannot be null");

		this.name = name.intern();
		fieldsData = value;

		if (store == Store.YES) {
			isStored = true;
			isCompressed = false;
		} else if (store == Store.COMPRESS) {
			isStored = true;
			isCompressed = true;
		} else if (store == Store.NO)
			throw new IllegalArgumentException(
					"binary values can't be unstored");
		else
			throw new IllegalArgumentException("unknown store parameter "
					+ store);

		isIndexed = false;
		isTokenized = false;

		isBinary = true;
		binaryLength = length;
		binaryOffset = offset;

		setStoreTermVector(TermVector.NO);
	}
}
