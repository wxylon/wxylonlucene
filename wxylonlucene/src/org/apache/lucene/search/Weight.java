package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
/**
 * �����������ǣ��û���һ�μ���������һ��Weight��Ӧ�ģ���Ȼ���Բ����汾�μ�����ص�IndexSearcher��������״̬��Ϣ��һ��Weight�У�
 * 			�����Ļ�������Query�������ã�ÿ�ζ�Ҫ����ʵ����һ����
 * @version ����ʱ�䣺Sep 25, 2009 4:55:46 PM
 */
// java.io.Serializable �����������л�����
//���ǡ�Ȩ�ء�����ʾһ�β�ѯʱ�������е�ĳ���ĵ�����Ҫ��
public interface Weight extends java.io.Serializable {
	
	// ͨ��һ��Weight���Ի�ȡ��һ��Queryʵ��
	Query getQuery();

	// Weight��ص�Query��Ȩ��ֵ
	float getValue();

	// һ��Query�����кܶ��Ӿ�(����һ��BooleanQuery���԰������TermQuery�Ӿ�)����ȡ�������Ӿ��Ȩ��ֵ��ƽ��
	float sumOfSquaredWeights() throws IOException;

	// ָ�ɲ�ѯ�ı�׼������
	void normalize(float norm);

	// ����һ��IndexReader��ͨ��Weight��ȡ�÷�
	Scorer scorer(IndexReader reader) throws IOException;

	// Ϊ���Ϊdoc��Document���ü���÷ֵ�������Ϣ
	Explanation explain(IndexReader reader, int doc) throws IOException;
}
