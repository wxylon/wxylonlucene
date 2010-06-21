package org.apache.lucene.analysis;

import java.io.Reader;
import java.io.IOException;
/**
 * 此类描述的是： Tokenizer是一个以Reader为输入的TokenStream
 * 被用来做初级的文本处理，它把从Reader读入的原始文本通过一些简单的办法处理成一个个初级的token
 * @version 创建时间：Oct 13, 2009 5:03:09 PM
 */
public abstract class Tokenizer extends TokenStream {
	// 一个Reader对象作为它的成员
	protected Reader input;

	/** Construct a tokenizer with null input. */
	protected Tokenizer() {
	}

	/** Construct a token stream processing the given input. */
	protected Tokenizer(Reader input) {
		this.input = input;
	}

	// 关闭输入流
	public void close() throws IOException {
		input.close();
	}

	/**
	 * Expert: Reset the tokenizer to a new reader. Typically, an analyzer (in
	 * its reusableTokenStream method) will use this to re-use a previously
	 * created tokenizer.
	 */
	public void reset(Reader input) throws IOException {
		this.input = input;
	}
}
