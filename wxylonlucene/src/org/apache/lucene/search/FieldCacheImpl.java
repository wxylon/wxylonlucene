package org.apache.lucene.search;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Expert: The default cache implementation, storing all values in memory. A
 * WeakHashMap is used for storage.
 * 
 * <p>
 * Created: May 19, 2004 4:40:36 PM
 * 
 * @since lucene 1.4
 * @version $Id: FieldCacheImpl.java 695514 2008-09-15 15:42:11Z otis $
 */
class FieldCacheImpl implements FieldCache {

	/** Expert: Internal cache. */
	abstract static class Cache {
		private final Map readerCache = new WeakHashMap();

		protected abstract Object createValue(IndexReader reader, Object key)
				throws IOException;

		public Object get(IndexReader reader, Object key) throws IOException {
			Map innerCache;
			Object value;
			synchronized (readerCache) {
				innerCache = (Map) readerCache.get(reader);
				if (innerCache == null) {
					innerCache = new HashMap();
					readerCache.put(reader, innerCache);
					value = null;
				} else {
					value = innerCache.get(key);
				}
				if (value == null) {
					value = new CreationPlaceholder();
					innerCache.put(key, value);
				}
			}
			if (value instanceof CreationPlaceholder) {
				synchronized (value) {
					CreationPlaceholder progress = (CreationPlaceholder) value;
					if (progress.value == null) {
						progress.value = createValue(reader, key);
						synchronized (readerCache) {
							innerCache.put(key, progress.value);
						}
					}
					return progress.value;
				}
			}
			return value;
		}
	}

	static final class CreationPlaceholder {
		Object value;
	}

	/** Expert: Every composite-key in the internal cache is of this type. */
	static class Entry {
		final String field; // which Fieldable
		final int type; // which SortField type
		final Object custom; // which custom comparator
		final Locale locale; // the locale we're sorting (if string)

		/** Creates one of these objects. */
		Entry(String field, int type, Locale locale) {
			this.field = field.intern();
			this.type = type;
			this.custom = null;
			this.locale = locale;
		}

		/** Creates one of these objects for a custom comparator. */
		Entry(String field, Object custom) {
			this.field = field.intern();
			this.type = SortField.CUSTOM;
			this.custom = custom;
			this.locale = null;
		}

		/** Two of these are equal iff they reference the same field and type. */
		public boolean equals(Object o) {
			if (o instanceof Entry) {
				Entry other = (Entry) o;
				if (other.field == field && other.type == type) {
					if (other.locale == null ? locale == null : other.locale
							.equals(locale)) {
						if (other.custom == null) {
							if (custom == null)
								return true;
						} else if (other.custom.equals(custom)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		/** Composes a hashcode based on the field and type. */
		public int hashCode() {
			return field.hashCode() ^ type
					^ (custom == null ? 0 : custom.hashCode())
					^ (locale == null ? 0 : locale.hashCode());
		}
	}

	private static final ByteParser BYTE_PARSER = new ByteParser() {
		public byte parseByte(String value) {
			return Byte.parseByte(value);
		}
	};

	private static final ShortParser SHORT_PARSER = new ShortParser() {
		public short parseShort(String value) {
			return Short.parseShort(value);
		}
	};

	private static final IntParser INT_PARSER = new IntParser() {
		public int parseInt(String value) {
			return Integer.parseInt(value);
		}
	};

	private static final FloatParser FLOAT_PARSER = new FloatParser() {
		public float parseFloat(String value) {
			return Float.parseFloat(value);
		}
	};

	// inherit javadocs
	public byte[] getBytes(IndexReader reader, String field) throws IOException {
		return getBytes(reader, field, BYTE_PARSER);
	}

	// inherit javadocs
	public byte[] getBytes(IndexReader reader, String field, ByteParser parser)
			throws IOException {
		return (byte[]) bytesCache.get(reader, new Entry(field, parser));
	}

	Cache bytesCache = new Cache() {

		protected Object createValue(IndexReader reader, Object entryKey)
				throws IOException {
			/*获得需要处理的域的信息*/ 
			Entry entry = (Entry) entryKey;
			/*获得需要处理的域名*/     
			String field = entry.field;
			/*获得类型转换器，将String转成byte*/  
			ByteParser parser = (ByteParser) entry.custom;
			/*声明缓存的数据，注意该数据声明的大小为reader。maxDoc(),即最大Doc*/  
			final byte[] retArray = new byte[reader.maxDoc()];
			/*获得通过term查找包含其的doc号*/ 
			TermDocs termDocs = reader.termDocs();
			/*term的循环器，参数为new Term (field, "")，即从该域开始循环*/  
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					/*开始loop*/  
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					/*将term中的text转换成byte*/  
					byte termval = parser.parseByte(term.text());
					/*寻找包含该term的doc*/  
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						/*  
					       并把retArray数组该doc号index下的值赋值。  
					       在此处注意！！如果一个doc包含多个term，  
					       那么意味着该retArray[termDocs.doc()]会被覆盖，  
					       值将是最后次赋的值，也就是说cache的数据是限制了doc包含的数据个数据的，  
					       doc包含1个以上，cache便起不到真正的效果了，所以使用该cache的场景有限，  
					       我同样也考虑过改写cache，让其支持doc包含多个数据的情况，  
					       但突然发现这个跟把Index的数据全缓存在内存中没有什么区别！ 矛盾啊！  
					       可能我的思路 还是受了限制阿！希望有人能给个思路 :D   
					    */  
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			 /*返回缓存的数据*/  
			return retArray;
		}
	};

	// inherit javadocs
	public short[] getShorts(IndexReader reader, String field)
			throws IOException {
		return getShorts(reader, field, SHORT_PARSER);
	}

	// inherit javadocs
	public short[] getShorts(IndexReader reader, String field,
			ShortParser parser) throws IOException {
		return (short[]) shortsCache.get(reader, new Entry(field, parser));
	}

	Cache shortsCache = new Cache() {

		protected Object createValue(IndexReader reader, Object entryKey)
				throws IOException {
			Entry entry = (Entry) entryKey;
			String field = entry.field;
			ShortParser parser = (ShortParser) entry.custom;
			final short[] retArray = new short[reader.maxDoc()];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					short termval = parser.parseShort(term.text());
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			return retArray;
		}
	};

	// inherit javadocs
	public int[] getInts(IndexReader reader, String field) throws IOException {
		return getInts(reader, field, INT_PARSER);
	}

	// inherit javadocs
	public int[] getInts(IndexReader reader, String field, IntParser parser)
			throws IOException {
		return (int[]) intsCache.get(reader, new Entry(field, parser));
	}

	Cache intsCache = new Cache() {

		protected Object createValue(IndexReader reader, Object entryKey)
				throws IOException {
			Entry entry = (Entry) entryKey;
			String field = entry.field;
			IntParser parser = (IntParser) entry.custom;
			final int[] retArray = new int[reader.maxDoc()];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					int termval = parser.parseInt(term.text());
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			return retArray;
		}
	};

	// inherit javadocs
	public float[] getFloats(IndexReader reader, String field)
			throws IOException {
		return getFloats(reader, field, FLOAT_PARSER);
	}

	// inherit javadocs
	public float[] getFloats(IndexReader reader, String field,
			FloatParser parser) throws IOException {
		return (float[]) floatsCache.get(reader, new Entry(field, parser));
	}

	Cache floatsCache = new Cache() {

		protected Object createValue(IndexReader reader, Object entryKey)
				throws IOException {
			Entry entry = (Entry) entryKey;
			String field = entry.field;
			FloatParser parser = (FloatParser) entry.custom;
			final float[] retArray = new float[reader.maxDoc()];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					float termval = parser.parseFloat(term.text());
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			return retArray;
		}
	};

	// inherit javadocs
	public String[] getStrings(IndexReader reader, String field)
			throws IOException {
		return (String[]) stringsCache.get(reader, field);
	}

	Cache stringsCache = new Cache() {

		protected Object createValue(IndexReader reader, Object fieldKey)
				throws IOException {
			String field = ((String) fieldKey).intern();
			final String[] retArray = new String[reader.maxDoc()];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					String termval = term.text();
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			return retArray;
		}
	};

	// inherit javadocs
	public StringIndex getStringIndex(IndexReader reader, String field)
			throws IOException {
		return (StringIndex) stringsIndexCache.get(reader, field);
	}

	Cache stringsIndexCache = new Cache() {

		protected Object createValue(IndexReader reader, Object fieldKey)
				throws IOException {
			String field = ((String) fieldKey).intern();
			final int[] retArray = new int[reader.maxDoc()];
			String[] mterms = new String[reader.maxDoc() + 1];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			int t = 0; // current term number

			// an entry for documents that have no terms in this field
			// should a document with no terms be at top or bottom?
			// this puts them at the top - if it is changed,
			// FieldDocSortedHitQueue
			// needs to change as well.
			mterms[t++] = null;

			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;

					// store term text
					// we expect that there is at most one term per document
					if (t >= mterms.length)
						throw new RuntimeException("there are more terms than "
								+ "documents in field \"" + field
								+ "\", but it's impossible to sort on "
								+ "tokenized fields");
					mterms[t] = term.text();

					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = t;
					}

					t++;
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}

			if (t == 0) {
				// if there are no terms, make the term array
				// have a single null entry
				mterms = new String[1];
			} else if (t < mterms.length) {
				// if there are less terms than documents,
				// trim off the dead array space
				String[] terms = new String[t];
				System.arraycopy(mterms, 0, terms, 0, t);
				mterms = terms;
			}

			StringIndex value = new StringIndex(retArray, mterms);
			return value;
		}
	};

	/** The pattern used to detect integer values in a field */
	/**
	 * removed for java 1.3 compatibility protected static final Pattern
	 * pIntegers = Pattern.compile ("[0-9\\-]+");
	 */

	/** The pattern used to detect float values in a field */
	/**
	 * removed for java 1.3 compatibility protected static final Object pFloats =
	 * Pattern.compile ("[0-9+\\-\\.eEfFdD]+");
	 */

	// inherit javadocs
	public Object getAuto(IndexReader reader, String field) throws IOException {
		return autoCache.get(reader, field);
	}

	Cache autoCache = new Cache() {

		protected Object createValue(IndexReader reader, Object fieldKey)
				throws IOException {
			String field = ((String) fieldKey).intern();
			TermEnum enumerator = reader.terms(new Term(field));
			try {
				Term term = enumerator.term();
				if (term == null) {
					throw new RuntimeException("no terms in field " + field
							+ " - cannot determine sort type");
				}
				Object ret = null;
				if (term.field() == field) {
					String termtext = term.text().trim();

					/**
					 * Java 1.4 level code:
					 * 
					 * if (pIntegers.matcher(termtext).matches()) return
					 * IntegerSortedHitQueue.comparator (reader, enumerator,
					 * field);
					 * 
					 * else if (pFloats.matcher(termtext).matches()) return
					 * FloatSortedHitQueue.comparator (reader, enumerator,
					 * field);
					 */

					// Java 1.3 level code:
					try {
						Integer.parseInt(termtext);
						ret = getInts(reader, field);
					} catch (NumberFormatException nfe1) {
						try {
							Float.parseFloat(termtext);
							ret = getFloats(reader, field);
						} catch (NumberFormatException nfe3) {
							ret = getStringIndex(reader, field);
						}
					}
				} else {
					throw new RuntimeException("field \"" + field
							+ "\" does not appear to be indexed");
				}
				return ret;
			} finally {
				enumerator.close();
			}
		}
	};

	// inherit javadocs
	public Comparable[] getCustom(IndexReader reader, String field,
			SortComparator comparator) throws IOException {
		return (Comparable[]) customCache.get(reader, new Entry(field,
				comparator));
	}

	Cache customCache = new Cache() {

		protected Object createValue(IndexReader reader, Object entryKey)
				throws IOException {
			Entry entry = (Entry) entryKey;
			String field = entry.field;
			SortComparator comparator = (SortComparator) entry.custom;
			final Comparable[] retArray = new Comparable[reader.maxDoc()];
			TermDocs termDocs = reader.termDocs();
			TermEnum termEnum = reader.terms(new Term(field));
			try {
				do {
					Term term = termEnum.term();
					if (term == null || term.field() != field)
						break;
					Comparable termval = comparator.getComparable(term.text());
					termDocs.seek(termEnum);
					while (termDocs.next()) {
						retArray[termDocs.doc()] = termval;
					}
				} while (termEnum.next());
			} finally {
				termDocs.close();
				termEnum.close();
			}
			return retArray;
		}
	};

}
