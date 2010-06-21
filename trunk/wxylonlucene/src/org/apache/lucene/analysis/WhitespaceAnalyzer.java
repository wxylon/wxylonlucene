package org.apache.lucene.analysis;

import java.io.Reader;
import java.io.IOException;

/**
 * 此类描述的是： 仅仅是去除空格，对字符没有lowcase化,不支持中文
 * @version 创建时间：Oct 13, 2009 5:17:42 PM
 */
public final class WhitespaceAnalyzer extends Analyzer {
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new WhitespaceTokenizer(reader);
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
		if (tokenizer == null) {
			tokenizer = new WhitespaceTokenizer(reader);
			setPreviousTokenStream(tokenizer);
		} else
			tokenizer.reset(reader);
		return tokenizer;
	}
}
