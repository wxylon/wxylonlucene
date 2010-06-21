package org.apache.lucene.analysis;

import java.io.Reader;
import java.io.IOException;

/**
 * 此类描述的是：功能强于WhitespaceAnalyzer,将除去letter之外的符号全部过滤掉,并且将所有的字符lowcase化,不支持中文
 * @version 创建时间：Oct 13, 2009 4:58:57 PM
 */
public final class SimpleAnalyzer extends Analyzer {
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new LowerCaseTokenizer(reader);
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
		if (tokenizer == null) {
			tokenizer = new LowerCaseTokenizer(reader);
			setPreviousTokenStream(tokenizer);
		} else
			tokenizer.reset(reader);
		return tokenizer;
	}
}
