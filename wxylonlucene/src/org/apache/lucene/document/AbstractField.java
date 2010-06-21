package org.apache.lucene.document;

public abstract class AbstractField implements Fieldable {
	//表示该Filed的名称
	protected String name = "body";
	//表示是否存储词条向量
	protected boolean storeTermVector = false;
	//存储词条向量的偏移量
	protected boolean storeOffsetWithTermVector = false;
	//存储词条向量的位置
	protected boolean storePositionWithTermVector = false;
	protected boolean omitNorms = false;
	//是否存储
	protected boolean isStored = false;
	//是否是索引
	protected boolean isIndexed = true;
	//是否是分词
	protected boolean isTokenized = true;
	//是否是二进制数据
	protected boolean isBinary = false;
	//是否是压缩
	protected boolean isCompressed = false;
	//是否是延迟
	protected boolean lazy = false;
	protected boolean omitTf = false;
	//设置激励因子
	protected float boost = 1.0f;
	//Field的内部属性的具体内容
	protected Object fieldsData = null;
	//二进制数据长度
	protected int binaryLength;
	//二进制数据偏移量
	protected int binaryOffset;

	protected AbstractField() {}

	/**
	 * 创建一个新的实例 AbstractField
	 * @param name			name
	 * @param store			控制什么用的？
	 * @param index			控制什么用的？
	 * @param termVector	控制什么用的？
	 */
	protected AbstractField(String name, Field.Store store, Field.Index index,
			Field.TermVector termVector) {
		if (name == null)
			throw new NullPointerException("name cannot be null");
		this.name = name.intern(); //字符串的规范化形式，不懂什么是规范

		if (store == Field.Store.YES) {
			this.isStored = true;
			this.isCompressed = false;
		} else if (store == Field.Store.COMPRESS) {
			this.isStored = true;
			this.isCompressed = true;
		} else if (store == Field.Store.NO) {
			this.isStored = false;
			this.isCompressed = false;
		} else
			throw new IllegalArgumentException("unknown store parameter "
					+ store);

		if (index == Field.Index.NO) {
			this.isIndexed = false;
			this.isTokenized = false;
		} else if (index == Field.Index.ANALYZED) {
			this.isIndexed = true;
			this.isTokenized = true;
		} else if (index == Field.Index.NOT_ANALYZED) {
			this.isIndexed = true;
			this.isTokenized = false;
		} else if (index == Field.Index.NOT_ANALYZED_NO_NORMS) {
			this.isIndexed = true;
			this.isTokenized = false;
			this.omitNorms = true;
		} else if (index == Field.Index.ANALYZED_NO_NORMS) {
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
	 * Sets the boost factor hits on this field. This value will be multiplied
	 * into the score of all hits on this this field of this document.
	 * 
	 * <p>
	 * The boost is multiplied by
	 * {@link org.apache.lucene.document.Document#getBoost()} of the document
	 * containing this field. If a document has multiple fields with the same
	 * name, all such values are multiplied together. This product is then
	 * multipled by the value
	 * {@link org.apache.lucene.search.Similarity#lengthNorm(String,int)}, and
	 * rounded by {@link org.apache.lucene.search.Similarity#encodeNorm(float)}
	 * before it is stored in the index. One should attempt to ensure that this
	 * product does not overflow the range of that encoding.
	 * 
	 * @see org.apache.lucene.document.Document#setBoost(float)
	 * @see org.apache.lucene.search.Similarity#lengthNorm(String, int)
	 * @see org.apache.lucene.search.Similarity#encodeNorm(float)
	 */
	public void setBoost(float boost) {
		this.boost = boost;
	}

	/**
	 * Returns the boost factor for hits for this field.
	 * 
	 * <p>
	 * The default value is 1.0.
	 * 
	 * <p>
	 * Note: this value is not stored directly with the document in the index.
	 * Documents returned from
	 * {@link org.apache.lucene.index.IndexReader#document(int)} and
	 * {@link org.apache.lucene.search.Hits#doc(int)} may thus not have the same
	 * value present as when this field was indexed.
	 * 
	 * @see #setBoost(float)
	 */
	public float getBoost() {
		return boost;
	}

	/**
	 * Returns the name of the field as an interned string. For example "date",
	 * "title", "body", ...
	 */
	public String name() {
		return name;
	}

	protected void setStoreTermVector(Field.TermVector termVector) {
		if (termVector == Field.TermVector.NO) {
			this.storeTermVector = false;
			this.storePositionWithTermVector = false;
			this.storeOffsetWithTermVector = false;
		} else if (termVector == Field.TermVector.YES) {
			this.storeTermVector = true;
			this.storePositionWithTermVector = false;
			this.storeOffsetWithTermVector = false;
		} else if (termVector == Field.TermVector.WITH_POSITIONS) {
			this.storeTermVector = true;
			this.storePositionWithTermVector = true;
			this.storeOffsetWithTermVector = false;
		} else if (termVector == Field.TermVector.WITH_OFFSETS) {
			this.storeTermVector = true;
			this.storePositionWithTermVector = false;
			this.storeOffsetWithTermVector = true;
		} else if (termVector == Field.TermVector.WITH_POSITIONS_OFFSETS) {
			this.storeTermVector = true;
			this.storePositionWithTermVector = true;
			this.storeOffsetWithTermVector = true;
		} else {
			throw new IllegalArgumentException("unknown termVector parameter "
					+ termVector);
		}
	}

	/**
	 * True iff the value of the field is to be stored in the index for return
	 * with search hits. It is an error for this to be true if a field is
	 * Reader-valued.
	 */
	public final boolean isStored() {
		return isStored;
	}

	/**
	 * True iff the value of the field is to be indexed, so that it may be
	 * searched on.
	 */
	public final boolean isIndexed() {
		return isIndexed;
	}

	/**
	 * True iff the value of the field should be tokenized as text prior to
	 * indexing. Un-tokenized fields are indexed as a single word and may not be
	 * Reader-valued.
	 */
	public final boolean isTokenized() {
		return isTokenized;
	}

	/** True if the value of the field is stored and compressed within the index */
	public final boolean isCompressed() {
		return isCompressed;
	}

	/**
	 * True iff the term or terms used to index this field are stored as a term
	 * vector, available from
	 * {@link org.apache.lucene.index.IndexReader#getTermFreqVector(int,String)}.
	 * These methods do not provide access to the original content of the field,
	 * only to terms used to index it. If the original content must be
	 * preserved, use the <code>stored</code> attribute instead.
	 * 
	 * @see org.apache.lucene.index.IndexReader#getTermFreqVector(int, String)
	 */
	public final boolean isTermVectorStored() {
		return storeTermVector;
	}

	/**
	 * True iff terms are stored as term vector together with their offsets
	 * (start and end positon in source text).
	 */
	public boolean isStoreOffsetWithTermVector() {
		return storeOffsetWithTermVector;
	}

	/**
	 * True iff terms are stored as term vector together with their token
	 * positions.
	 */
	public boolean isStorePositionWithTermVector() {
		return storePositionWithTermVector;
	}

	/** True iff the value of the filed is stored as binary */
	public final boolean isBinary() {
		return isBinary;
	}

	/**
	 * Return the raw byte[] for the binary field. Note that you must also call
	 * {@link #getBinaryLength} and {@link #getBinaryOffset} to know which range
	 * of bytes in this returned array belong to the field.
	 * 
	 * @return reference to the Field value as byte[].
	 */
	public byte[] getBinaryValue() {
		return getBinaryValue(null);
	}

	public byte[] getBinaryValue(byte[] result) {
		if (isBinary || fieldsData instanceof byte[])
			return (byte[]) fieldsData;
		else
			return null;
	}

	/**
	 * Returns length of byte[] segment that is used as value, if Field is not
	 * binary returned value is undefined
	 * 
	 * @return length of byte[] segment that represents this Field value
	 */
	public int getBinaryLength() {
		if (isBinary) {
			if (!isCompressed)
				return binaryLength;
			else
				return ((byte[]) fieldsData).length;
		} else if (fieldsData instanceof byte[])
			return ((byte[]) fieldsData).length;
		else
			return 0;
	}

	/**
	 * Returns offset into byte[] segment that is used as value, if Field is not
	 * binary returned value is undefined
	 * 
	 * @return index of the first character in byte[] segment that represents
	 *         this Field value
	 */
	public int getBinaryOffset() {
		return binaryOffset;
	}

	/** True if norms are omitted for this indexed field */
	public boolean getOmitNorms() {
		return omitNorms;
	}

	/** True if tf is omitted for this indexed field */
	public boolean getOmitTf() {
		return omitTf;
	}

	/**
	 * Expert:
	 * 
	 * If set, omit normalization factors associated with this indexed field.
	 * This effectively disables indexing boosts and length normalization for
	 * this field.
	 */
	public void setOmitNorms(boolean omitNorms) {
		this.omitNorms = omitNorms;
	}

	/**
	 * Expert:
	 * 
	 * If set, omit tf from postings of this indexed field.
	 */
	public void setOmitTf(boolean omitTf) {
		this.omitTf = omitTf;
	}

	public boolean isLazy() {
		return lazy;
	}

	/** Prints a Field for human consumption. */
	public final String toString() {
		StringBuffer result = new StringBuffer();
		if (isStored) {
			result.append("stored");
			if (isCompressed)
				result.append("/compressed");
			else
				result.append("/uncompressed");
		}
		if (isIndexed) {
			if (result.length() > 0)
				result.append(",");
			result.append("indexed");
		}
		if (isTokenized) {
			if (result.length() > 0)
				result.append(",");
			result.append("tokenized");
		}
		if (storeTermVector) {
			if (result.length() > 0)
				result.append(",");
			result.append("termVector");
		}
		if (storeOffsetWithTermVector) {
			if (result.length() > 0)
				result.append(",");
			result.append("termVectorOffsets");
		}
		if (storePositionWithTermVector) {
			if (result.length() > 0)
				result.append(",");
			result.append("termVectorPosition");
		}
		if (isBinary) {
			if (result.length() > 0)
				result.append(",");
			result.append("binary");
		}
		if (omitNorms) {
			result.append(",omitNorms");
		}
		if (omitTf) {
			result.append(",omitTf");
		}
		if (lazy) {
			result.append(",lazy");
		}
		result.append('<');
		result.append(name);
		result.append(':');

		if (fieldsData != null && lazy == false) {
			result.append(fieldsData);
		}

		result.append('>');
		return result.toString();
	}
}
