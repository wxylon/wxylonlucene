package org.apache.lucene.analysis;

import java.io.IOException;

/**
 * 此类描述的是：TokenFilter则以Tokenizer为输入（因为Tokenizer继承自TokenStream），
 * 用一些规则过滤掉不符合要求的token（像StopFilter中的停用词），产生最终的token stream
 * @version 创建时间：Oct 13, 2009 5:05:29 PM
 */
public abstract class TokenFilter extends TokenStream {
	
	// 它以一个TokenStream对象作为成员
	protected TokenStream input;

	/** Construct a token stream filtering the given input. */
	protected TokenFilter(TokenStream input) {
		this.input = input;
	}

	/** Close the input TokenStream. */
	public void close() throws IOException {
		input.close();
	}

	/** Reset the filter as well as the input TokenStream. */
	public void reset() throws IOException {
		super.reset();
		input.reset();
	}
}
