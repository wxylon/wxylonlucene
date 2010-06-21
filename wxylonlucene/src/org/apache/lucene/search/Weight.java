package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
/**
 * 此类描述的是：用户的一次检索，是与一个Weight对应的，当然可以不保存本次检索相关的IndexSearcher检索器的状态信息到一个Weight中，
 * 			这样的坏处就是Query不能重用，每次都要重新实例化一个。
 * @version 创建时间：Sep 25, 2009 4:55:46 PM
 */
// java.io.Serializable 以启用其序列化功能
//就是“权重”，表示一次查询时，索引中的某个文档的重要性
public interface Weight extends java.io.Serializable {
	
	// 通过一个Weight可以获取到一个Query实例
	Query getQuery();

	// Weight相关的Query的权重值
	float getValue();

	// 一个Query可以有很多子句(比如一个BooleanQuery可以包含多个TermQuery子句)，获取到所有子句的权重值的平方
	float sumOfSquaredWeights() throws IOException;

	// 指派查询的标准化因子
	void normalize(float norm);

	// 根据一个IndexReader，通过Weight获取得分
	Scorer scorer(IndexReader reader) throws IOException;

	// 为编号为doc的Document设置计算得分的描述信息
	Explanation explain(IndexReader reader, int doc) throws IOException;
}
