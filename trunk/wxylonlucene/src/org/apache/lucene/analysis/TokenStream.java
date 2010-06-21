package org.apache.lucene.analysis;

import org.apache.lucene.index.Payload;
import java.io.IOException;

/**
 * 它最基本的是对分词流的状态进行管理。具体地，它如何对分析的对象处理，应该从继承该抽象类的子类的构造来看。
 * TokenStream用来分析文字流，按一定的规则罗列token,在lucene有字节流是即将要索引的文本，或者查询的关键字。
 * <p>
 * 它是一个抽象类，它的子类有如下两种:
 * <ul>
 * <li>分词器-Tokenizer，Tokenizer是以Reader对象做为输入;
 * <li>过滤器-TokenFilter,主要用来处理词汇单元的部分内容过滤功能。与分词器比较最大的区别是它的输入是另一个TokenStream.多个过滤器可以串接起来，形成管道型的流逝过滤器
 * </ul>
 * NOTE:子类必须要重写next(Token).
 */

public abstract class TokenStream {

	/**
	 * 返回字节流的下一个Token.
	 * @deprecated 被next(Token)所代替 .
	 */
	public Token next() throws IOException {
		final Token reusableToken = new Token();
		Token nextToken = next(reusableToken);

		if (nextToken != null) {
			Payload p = nextToken.getPayload();
			if (p != null) {
				nextToken.setPayload((Payload) p.clone());
			}
		}

		return nextToken;
	}

	/**
	 * 返回数据流中的下个Token或null、EOS. 通常情况下，为了有着更好的性能，返回的Token和输入的Token应该是同一个对象
	 * 但是这不是必须，也可以返回一个新的Token. .
	 * <p>
	 * 调用该方法的对象和实现该方法的对象有一种合约:
	 * <ul>
	 * <li>调用该方法的对象必须在再次调用该方法之前要使用完毕之前的Token.</li>
	 * <li>实现该方法的在每次设置之前都要先调用clear()。把之前的属性的清空</li>
	 * </ul>
	 * 当Token被返回之后，调用者可以随意改变该Token。所以producer如果想保存Token，必须要在返回Token之前Clone()克隆一个新的Token
	 * 
	 * @param reusableToken
	 *            该Token或许被返回，或许返回一个新的Token。reusableToken不能为null。
	 * @return
	 */
	public Token next(final Token reusableToken) throws IOException {
		// We don't actually use inputToken, but still add this assert
		assert reusableToken != null;
		return next();
	}

	/**
	 * 重置数据流的标记位置.这个方法这是可选的。 Reset()通常情况下不需要的.如果
	 * 但是如果Token要被使用多次，那就有重写实现reset()接口 .
	 * 如果TokenStream缓存了Token,调用rest()会返回缓存的Token
	 */
	public void reset() throws IOException {
	}

	// 关闭分词流，停止分词
	public void close() throws IOException {
	}
}
