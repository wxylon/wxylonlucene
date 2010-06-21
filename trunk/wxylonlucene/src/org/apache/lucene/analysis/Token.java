package org.apache.lucene.analysis;

import org.apache.lucene.index.Payload;
import org.apache.lucene.util.ArrayUtil;

/**
 * 此类描述的是： 经过分词器得到的词条
 * @version 创建时间：Oct 20, 2009 1:53:49 PM
 */
public class Token implements Cloneable {

	/**
	 * 默认类型
	 */
	public static final String DEFAULT_TYPE = "word";
	
	/**
	 * 数组最小的size值
	 */
	private static int MIN_BUFFER_SIZE = 10;

	/**
	 * 文本
	 * @deprecated We will remove this when we remove the deprecated APIs
	 */
	private String termText;

	/**
	 * 文本转换后的字节数组
	 * @deprecated This will be made private. Instead, use: {@link termBuffer()},
	 *             {@link #setTermBuffer(char[], int, int)},
	 *             {@link #setTermBuffer(String)}, or
	 *             {@link #setTermBuffer(String, int, int)}
	 */
	char[] termBuffer;

	/**
	 * termText 的长度
	 * @deprecated This will be made private. Instead, use: {@link termLength()},or @{link setTermLength(int)}.
	 */
	int termLength;

	/**
	 *	起始位置
	 * @deprecated This will be made private. Instead, use:{@link startOffset()}, or
	 * @{link setStartOffset(int)}.
	 */
	int startOffset;

	/**
	 * 结束位置
	 * @deprecated This will be made private. Instead, use: {@link endOffset()},or @{link setEndOffset(int)}.
	 */
	int endOffset;

	/**
	 * 文本 名字 key
	 * @deprecated This will be made private. Instead, use: {@link type()}, or @{link setType(String)}.
	 */
	String type = DEFAULT_TYPE;

	private int flags;

	/**
	 * @deprecated This will be made private. Instead, use: {@link getPayload()},or @{link setPayload(Payload)}.
	 */
	Payload payload;

	/**
	 *	位置增量
	 * @deprecated This will be made private. Instead, use: {@link getPositionIncrement()}, or@{link setPositionIncrement(String)}.
	 */
	int positionIncrement = 1;

	/**
	 * 创建一个新的实例 Token
	 */
	public Token() {
	}

	/**
	 * 创建一个新的实例 Token
	 * @param start  start offset in the source text
	 * @param end	 end offset in the source text
	 */
	public Token(int start, int end) {
		startOffset = start;
		endOffset = end;
	}

	/**
	 * 创建一个新的实例 Token
	 * @param start		start offset in the source text
	 * @param end		end offset in the source text
	 * @param typ		the lexical type of this Token
	 */
	public Token(int start, int end, String typ) {
		startOffset = start;
		endOffset = end;
		type = typ;
	}

	/**
	 * 创建一个新的实例 Token
	 * @param start		start offset in the source text
	 * @param end		end offset in the source text
	 * @param flags		 The bits to set for this token
	 *           
	 */
	public Token(int start, int end, int flags) {
		startOffset = start;
		endOffset = end;
		this.flags = flags;
	}
	/**
	 * 创建一个新的实例 Token
	 * @param text		term text
	 * @param start		start offset
	 * @param end		end offset
	 * @deprecated          
	 */
	public Token(String text, int start, int end) {
		termText = text;
		startOffset = start;
		endOffset = end;
	}

	/**
	 * Constructs a Token with the given text, start and end offsets, & type.
	 * <b>NOTE:</b> for better indexing speed you should instead use the char[]
	 * termBuffer methods to set the term text.
	 * 
	 * @param text
	 *            term text
	 * @param start
	 *            start offset
	 * @param end
	 *            end offset
	 * @param typ
	 *            token type
	 * @deprecated
	 */
	public Token(String text, int start, int end, String typ) {
		termText = text;
		startOffset = start;
		endOffset = end;
		type = typ;
	}

	/**
	 * Constructs a Token with the given text, start and end offsets, & type.
	 * <b>NOTE:</b> for better indexing speed you should instead use the char[]
	 * termBuffer methods to set the term text.
	 * 
	 * @param text
	 * @param start
	 * @param end
	 * @param flags
	 *            token type bits
	 * @deprecated
	 */
	public Token(String text, int start, int end, int flags) {
		termText = text;
		startOffset = start;
		endOffset = end;
		this.flags = flags;
	}

	/**
	 * Constructs a Token with the given term buffer (offset & length), start
	 * and end offsets
	 * 
	 * @param startTermBuffer
	 * @param termBufferOffset
	 * @param termBufferLength
	 * @param start
	 * @param end
	 */
	public Token(char[] startTermBuffer, int termBufferOffset,
			int termBufferLength, int start, int end) {
		setTermBuffer(startTermBuffer, termBufferOffset, termBufferLength);
		startOffset = start;
		endOffset = end;
	}

	/**
	 * Set the position increment. This determines the position of this token
	 * relative to the previous Token in a {@link TokenStream}, used in phrase
	 * searching.
	 * 
	 * <p>
	 * The default value is one.
	 * 
	 * <p>
	 * Some common uses for this are:
	 * <ul>
	 * 
	 * <li>Set it to zero to put multiple terms in the same position. This is
	 * useful if, e.g., a word has multiple stems. Searches for phrases
	 * including either stem will match. In this case, all but the first stem's
	 * increment should be set to zero: the increment of the first instance
	 * should be one. Repeating a token with an increment of zero can also be
	 * used to boost the scores of matches on that token.
	 * 
	 * <li>Set it to values greater than one to inhibit exact phrase matches.
	 * If, for example, one does not want phrases to match across removed stop
	 * words, then one could build a stop word filter that removes stop words
	 * and also sets the increment to the number of stop words removed before
	 * each non-stop word. Then exact phrase queries will only match when the
	 * terms occur with no intervening stop words.
	 * 
	 * </ul>
	 * 
	 * @param positionIncrement
	 *            the distance from the prior term
	 * @see org.apache.lucene.index.TermPositions
	 */
	public void setPositionIncrement(int positionIncrement) {
		if (positionIncrement < 0)
			throw new IllegalArgumentException(
					"Increment must be zero or greater: " + positionIncrement);
		this.positionIncrement = positionIncrement;
	}

	/**
	 * Returns the position increment of this Token.
	 * 
	 * @see #setPositionIncrement
	 */
	public int getPositionIncrement() {
		return positionIncrement;
	}

	/**
	 * Sets the Token's term text. <b>NOTE:</b> for better indexing speed you
	 * should instead use the char[] termBuffer methods to set the term text.
	 * 
	 * @deprecated use {@link #setTermBuffer(char[], int, int)} or
	 *             {@link #setTermBuffer(String)} or
	 *             {@link #setTermBuffer(String, int, int)}.
	 */
	public void setTermText(String text) {
		termText = text;
		termBuffer = null;
	}

	/**
	 * termText
	 * @deprecated This method now has a performance penalty because the text is
	 *             stored internally in a char[]. If possible, use
	 *             {@link #termBuffer()} and {@link #termLength()} directly
	 *             instead. If you really need a String, use {@link #term()}</b>
	 */
	public final String termText() {
		if (termText == null && termBuffer != null)
			termText = new String(termBuffer, 0, termLength);
		return termText;
	}

	/**
	 * 将 termBuffer 转换为 String
	 * This method has a performance penalty because the text is stored
	 * internally in a char[]. If possible, use {@link #termBuffer()} and {@link
	 * #termLength()} directly instead. If you really need a String, use this
	 * method, which is nothing more than a convenience call to <b>new
	 * String(token.termBuffer(), 0, token.termLength())</b>
	 */
	public final String term() {
		if (termText != null)
			return termText;
		initTermBuffer();
		return new String(termBuffer, 0, termLength);
	}

	/**
	 * 复制	buffer 的内容,从 offset 开始 length 长度,到 termBuffer 中
	 * @param buffer		the buffer to copy
	 * @param offset		the index in the buffer of the first character to copy
	 * @param length		the number of characters to copy
	 */
	public final void setTermBuffer(char[] buffer, int offset, int length) {
		termText = null;
		char[] newCharBuffer = growTermBuffer(length);
		if (newCharBuffer != null) {
			termBuffer = newCharBuffer;
		}
		System.arraycopy(buffer, offset, termBuffer, 0, length);
		termLength = length;
	}

	/**
	 * String 转换为 字节数组  termBuffer
	 * @param buffer the buffer to copy
	 */
	public final void setTermBuffer(String buffer) {
		termText = null;
		int length = buffer.length();
		char[] newCharBuffer = growTermBuffer(length);
		if (newCharBuffer != null) {
			termBuffer = newCharBuffer;
		}
		buffer.getChars(0, length, termBuffer, 0);
		//字符串中要复制的第一个字符的索引。getChars()参数
		//字符串中要复制的最后一个字符之后的索引。
		//目标数组。
		//目标数组中的起始偏移量。
		termLength = length;
	}

	/**
	 * 指定 起始位置 和长度 将 buffer 复制进	termBuffer 中
	 * @param buffer  the buffer to copy
	 * @param offset  the index in the buffer of the first character to copy
	 * @param length  the number of characters to copy
	 */
	public final void setTermBuffer(String buffer, int offset, int length) {
		assert offset <= buffer.length();
		assert offset + length <= buffer.length();
		termText = null;
		char[] newCharBuffer = growTermBuffer(length);
		if (newCharBuffer != null) {
			termBuffer = newCharBuffer;
		}
		buffer.getChars(offset, offset + length, termBuffer, 0);
		termLength = length;
	}

	/**
	 * 返回数组termBuffer;
	 * Returns the internal termBuffer character array which you can then
	 * directly alter. If the array is too small for your token, use {@link
	 * #resizeTermBuffer(int)} to increase it. After altering the buffer be sure
	 * to call {@link #setTermLength} to record the number of valid characters
	 * that were placed into the termBuffer.
	 */
	public final char[] termBuffer() {
		initTermBuffer();
		return termBuffer;
	}

	/**
	 * Grows the termBuffer to at least size newSize, preserving the existing
	 * content. Note: If the next operation is to change the contents of the
	 * term buffer use {@link #setTermBuffer(char[], int, int)},
	 * {@link #setTermBuffer(String)}, or
	 * {@link #setTermBuffer(String, int, int)} to optimally combine the resize
	 * with the setting of the termBuffer.
	 * 
	 * @param newSize minimum size of the new termBuffer
	 * @return newly created termBuffer with length >= newSize
	 */
	public char[] resizeTermBuffer(int newSize) {
		char[] newCharBuffer = growTermBuffer(newSize);
		if (termBuffer == null) {
			// If there were termText, then preserve it.
			// note that if termBuffer is null then newCharBuffer cannot be null
			assert newCharBuffer != null;
			if (termText != null) {
				termText.getChars(0, termText.length(), newCharBuffer, 0);
			}
			termBuffer = newCharBuffer;
		} else if (newCharBuffer != null) {
			// Note: if newCharBuffer != null then termBuffer needs to grow.
			// If there were a termBuffer, then preserve it
			System.arraycopy(termBuffer, 0, newCharBuffer, 0, termBuffer.length);
			termBuffer = newCharBuffer;
		}
		termText = null;
		return termBuffer;
	}

	/**
	 * 构造具有指定大小的空字节数组
	 * @param newSize  minimum size of the buffer
	 * @return newly created buffer with length >= newSize or null if the current termBuffer is big enough
	 */
	private char[] growTermBuffer(int newSize) {
		if (termBuffer != null) {
			if (termBuffer.length >= newSize)
				// Already big enough
				return null;
			else
				// Not big enough; create a new array with slight
				// over allocation:
				return new char[ArrayUtil.getNextSize(newSize)];
		} else {

			// determine the best size
			// The buffer is always at least MIN_BUFFER_SIZE
			if (newSize < MIN_BUFFER_SIZE) {
				newSize = MIN_BUFFER_SIZE;
			}

			// If there is already a termText, then the size has to be at least
			// that big
			if (termText != null) {
				int ttLength = termText.length();
				if (newSize < ttLength) {
					newSize = ttLength;
				}
			}

			return new char[newSize];
		}
	}

	// TODO: once we remove the deprecated termText() method
	// and switch entirely to char[] termBuffer we don't need
	// to use this method anymore
	// 初始化 termBuffer
	private void initTermBuffer() {
		if (termBuffer == null) {
			if (termText == null) {
				termBuffer = new char[MIN_BUFFER_SIZE];
				termLength = 0;
			} else {
				int length = termText.length();
				if (length < MIN_BUFFER_SIZE)
					length = MIN_BUFFER_SIZE;
				termBuffer = new char[length];
				termLength = termText.length();
				//将字符从此字符串复制到目标字符数组
				termText.getChars(0, termText.length(), termBuffer, 0);
				termText = null;
			}
		} else if (termText != null)
			termText = null;
	}

	/**
	 * 此方法描述的是：termBuffer 的大小
	 * @version 创建时间：Oct 15, 2009 9:40:43 AM
	 * @return 
	 * int
	 */
	public final int termLength() {
		initTermBuffer();
		return termLength;
	}

	/**
	 * 设置 termLength 的长度
	 * Set number of valid characters (length of the term) in the termBuffer
	 * array. Use this to truncate the termBuffer or to synchronize with
	 * external manipulation of the termBuffer. Note: to grow the size of the
	 * array, use {@link #resizeTermBuffer(int)} first.
	 * @param length   the truncated length
	 */
	public final void setTermLength(int length) {
		initTermBuffer();
		if (length > termBuffer.length)
			throw new IllegalArgumentException("length " + length
					+ " exceeds the size of the termBuffer ("
					+ termBuffer.length + ")");
		termLength = length;
	}

	/**
	 * Returns this Token's starting offset, the position of the first character
	 * corresponding to this token in the source text.
	 * 
	 * Note that the difference between endOffset() and startOffset() may not be
	 * equal to termText.length(), as the term text may have been altered by a
	 * stemmer or some other filter.
	 */
	public final int startOffset() {
		return startOffset;
	}

	/**
	 * Set the starting offset.
	 * 
	 * @see #startOffset()
	 */
	public void setStartOffset(int offset) {
		this.startOffset = offset;
	}

	/**
	 * Returns this Token's ending offset, one greater than the position of the
	 * last character corresponding to this token in the source text. The length
	 * of the token in the source text is (endOffset - startOffset).
	 */
	public final int endOffset() {
		return endOffset;
	}

	/**
	 * Set the ending offset.
	 * 
	 * @see #endOffset()
	 */
	public void setEndOffset(int offset) {
		this.endOffset = offset;
	}

	/** Returns this Token's lexical type. Defaults to "word". */
	public final String type() {
		return type;
	}

	/**
	 * Set the lexical type.
	 * 
	 * @see #type()
	 */
	public final void setType(String type) {
		this.type = type;
	}

	/**
	 * EXPERIMENTAL: While we think this is here to stay, we may want to change
	 * it to be a long. <p/>
	 * 
	 * Get the bitset for any bits that have been set. This is completely
	 * distinct from {@link #type()}, although they do share similar purposes.
	 * The flags can be used to encode information about the token for use by
	 * other {@link org.apache.lucene.analysis.TokenFilter}s.
	 * 
	 * 
	 * @return The bits
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @see #getFlags()
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * Returns this Token's payload.
	 */
	public Payload getPayload() {
		return this.payload;
	}

	/**
	 * Sets this Token's payload.
	 */
	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	//转换为String
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		initTermBuffer();
		if (termBuffer == null)
			sb.append("null");
		else
			sb.append(termBuffer, 0, termLength);
		sb.append(',').append(startOffset).append(',').append(endOffset);
		if (!type.equals("word"))
			sb.append(",type=").append(type);
		if (positionIncrement != 1)
			sb.append(",posIncr=").append(positionIncrement);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Resets the term text, payload, flags, and positionIncrement to default.
	 * Other fields such as startOffset, endOffset and the token type are not
	 * reset since they are normally overwritten by the tokenizer.
	 */
	public void clear() {
		payload = null;
		// Leave termBuffer to allow re-use
		termLength = 0;
		termText = null;
		positionIncrement = 1;
		flags = 0;
		// startOffset = endOffset = 0;
		// type = DEFAULT_TYPE;
	}

	public Object clone() {
		try {
			Token t = (Token) super.clone();
			// Do a deep clone
			if (termBuffer != null) {
				t.termBuffer = (char[]) termBuffer.clone();
			}
			if (payload != null) {
				t.setPayload((Payload) payload.clone());
			}
			return t;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e); // shouldn't happen
		}
	}

	/**
	 * Makes a clone, but replaces the term buffer & start/end offset in the
	 * process. This is more efficient than doing a full clone (and then calling
	 * setTermBuffer) because it saves a wasted copy of the old termBuffer.
	 */
	public Token clone(char[] newTermBuffer, int newTermOffset,
			int newTermLength, int newStartOffset, int newEndOffset) {
		final Token t = new Token(newTermBuffer, newTermOffset, newTermLength,
				newStartOffset, newEndOffset);
		t.positionIncrement = positionIncrement;
		t.flags = flags;
		t.type = type;
		if (payload != null)
			t.payload = (Payload) payload.clone();
		return t;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof Token) {
			Token other = (Token) obj;

			initTermBuffer();
			other.initTermBuffer();

			if (termLength == other.termLength
					&& startOffset == other.startOffset
					&& endOffset == other.endOffset && flags == other.flags
					&& positionIncrement == other.positionIncrement
					&& subEqual(type, other.type)
					&& subEqual(payload, other.payload)) {
				for (int i = 0; i < termLength; i++)
					if (termBuffer[i] != other.termBuffer[i])
						return false;
				return true;
			} else
				return false;
		} else
			return false;
	}

	private boolean subEqual(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		else
			return o1.equals(o2);
	}

	public int hashCode() {
		initTermBuffer();
		int code = termLength;
		code = code * 31 + startOffset;
		code = code * 31 + endOffset;
		code = code * 31 + flags;
		code = code * 31 + positionIncrement;
		code = code * 31 + type.hashCode();
		code = (payload == null ? code : code * 31 + payload.hashCode());
		code = code * 31 + ArrayUtil.hashCode(termBuffer, 0, termLength);
		return code;
	}

	// like clear() but doesn't clear termBuffer/text
	private void clearNoTermBuffer() {
		payload = null;
		positionIncrement = 1;
		flags = 0;
	}

	/**
	 * Shorthand for calling {@link #clear},
	 * {@link #setTermBuffer(char[], int, int)}, {@link #setStartOffset},
	 * {@link #setEndOffset}, {@link #setType}
	 * 
	 * @return this Token instance
	 */
	public Token reinit(char[] newTermBuffer, int newTermOffset,
			int newTermLength, int newStartOffset, int newEndOffset,
			String newType) {
		clearNoTermBuffer();
		payload = null;
		positionIncrement = 1;
		setTermBuffer(newTermBuffer, newTermOffset, newTermLength);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = newType;
		return this;
	}

	/**
	 * Shorthand for calling {@link #clear},
	 * {@link #setTermBuffer(char[], int, int)}, {@link #setStartOffset},
	 * {@link #setEndOffset} {@link #setType} on Token.DEFAULT_TYPE
	 * 
	 * @return this Token instance
	 */
	public Token reinit(char[] newTermBuffer, int newTermOffset,
			int newTermLength, int newStartOffset, int newEndOffset) {
		clearNoTermBuffer();
		setTermBuffer(newTermBuffer, newTermOffset, newTermLength);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = DEFAULT_TYPE;
		return this;
	}

	/**
	 * Shorthand for calling {@link #clear}, {@link #setTermBuffer(String)},
	 * {@link #setStartOffset}, {@link #setEndOffset} {@link #setType}
	 * 
	 * @return this Token instance
	 */
	public Token reinit(String newTerm, int newStartOffset, int newEndOffset,
			String newType) {
		clearNoTermBuffer();
		setTermBuffer(newTerm);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = newType;
		return this;
	}

	/**
	 * Shorthand for calling {@link #clear},
	 * {@link #setTermBuffer(String, int, int)}, {@link #setStartOffset},
	 * {@link #setEndOffset} {@link #setType}
	 * 
	 * @return this Token instance
	 */
	public Token reinit(String newTerm, int newTermOffset, int newTermLength,
			int newStartOffset, int newEndOffset, String newType) {
		clearNoTermBuffer();
		setTermBuffer(newTerm, newTermOffset, newTermLength);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = newType;
		return this;
	}

	/**
	 * Shorthand for calling {@link #clear}, {@link #setTermBuffer(String)},
	 * {@link #setStartOffset}, {@link #setEndOffset} {@link #setType} on
	 * Token.DEFAULT_TYPE
	 * 
	 * @return this Token instance
	 */
	public Token reinit(String newTerm, int newStartOffset, int newEndOffset) {
		clearNoTermBuffer();
		setTermBuffer(newTerm);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = DEFAULT_TYPE;
		return this;
	}

	/**
	 * Shorthand for calling {@link #clear},
	 * {@link #setTermBuffer(String, int, int)}, {@link #setStartOffset},
	 * {@link #setEndOffset} {@link #setType} on Token.DEFAULT_TYPE
	 * 
	 * @return this Token instance
	 */
	public Token reinit(String newTerm, int newTermOffset, int newTermLength,
			int newStartOffset, int newEndOffset) {
		clearNoTermBuffer();
		setTermBuffer(newTerm, newTermOffset, newTermLength);
		startOffset = newStartOffset;
		endOffset = newEndOffset;
		type = DEFAULT_TYPE;
		return this;
	}

	/**
	 * Copy the prototype token's fields into this one. Note: Payloads are
	 * shared.
	 * 
	 * @param prototype
	 */
	public void reinit(Token prototype) {
		prototype.initTermBuffer();
		setTermBuffer(prototype.termBuffer, 0, prototype.termLength);
		positionIncrement = prototype.positionIncrement;
		flags = prototype.flags;
		startOffset = prototype.startOffset;
		endOffset = prototype.endOffset;
		type = prototype.type;
		payload = prototype.payload;
	}

	/**
	 * Copy the prototype token's fields into this one, with a different term.
	 * Note: Payloads are shared.
	 * 
	 * @param prototype
	 * @param newTerm
	 */
	public void reinit(Token prototype, String newTerm) {
		setTermBuffer(newTerm);
		positionIncrement = prototype.positionIncrement;
		flags = prototype.flags;
		startOffset = prototype.startOffset;
		endOffset = prototype.endOffset;
		type = prototype.type;
		payload = prototype.payload;
	}

	/**
	 * Copy the prototype token's fields into this one, with a different term.
	 * Note: Payloads are shared.
	 * 
	 * @param prototype
	 * @param newTermBuffer
	 * @param offset
	 * @param length
	 */
	public void reinit(Token prototype, char[] newTermBuffer, int offset,
			int length) {
		setTermBuffer(newTermBuffer, offset, length);
		positionIncrement = prototype.positionIncrement;
		flags = prototype.flags;
		startOffset = prototype.startOffset;
		endOffset = prototype.endOffset;
		type = prototype.type;
		payload = prototype.payload;
	}
}
