package org.apache.lucene.analysis;

import java.io.Reader;
import java.io.IOException;
/** 
  *建立TokenStreams分析器，它分析文本。因此，它代表了政策提取文本指数。 
  *典型的实现首先建立一个标记生成器，它打破了流从阅读器字符的原始凭证。一个或多个TokenFilters便可
  *适用于对记号赋予器输出。 
  */
public abstract class Analyzer {
	
	//  通过Field的名称，和一个Reader对象，创建一个分词流，该方法是抽象方法
	public abstract TokenStream tokenStream(String fieldName, Reader reader);

	/** 
	 *创建一个允许重新TokenStream，从以前的使用时间，同样的线程调用此方法。调用者不需要
	 *使用一个以上的从这个分析应该同时TokenStream 更好的性能，使用此方法。 
	 */
	public TokenStream reusableTokenStream(String fieldName, Reader reader)throws IOException {
		return tokenStream(fieldName, reader);
	}

	private ThreadLocal tokenStreams = new ThreadLocal();

	/** 
	 *使用的分析仪，实现reusableTokenStream检索
	 *以前保存在同一线程重新TokenStreams使用。 
	 */
	protected Object getPreviousTokenStream() {
		return tokenStreams.get();
	}

	/** 
	 *使用的分析仪，实现reusableTokenStream保存
	 *TokenStream后重新由同一线程使用。 
	 */
	protected void setPreviousTokenStream(Object obj) {
		tokenStreams.set(obj);
	}

	/**
	 * Invoked before indexing a Fieldable instance if terms have already been
	 * added to that field. This allows custom analyzers to place an automatic
	 * position increment gap between Fieldable instances using the same field
	 * name. The default value position increment gap is 0. With a 0 position
	 * increment gap and the typical default token position increment of 1, all
	 * terms in a field, including across Fieldable instances, are in successive
	 * positions, allowing exact PhraseQuery matches, for instance, across
	 * Fieldable instance boundaries.
	 * 
	 * @param fieldName
	 *            Fieldable name being indexed.
	 * @return position increment gap, added to the next token emitted from
	 *         {@link #tokenStream(String,Reader)}
	 */
	public int getPositionIncrementGap(String fieldName) {
		return 0;
	}
}
