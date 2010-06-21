package test;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;

public class LuceneSearchText {

	private static String Dest_Index_Path = "D:\\workshop\\TextIndex";

	static protected String[] keywords = { "001", "002", "003" };
	static protected String[] textdetail = { "记录 一", "记录 二", "记录 三" };

	/*
	 * ================================================================ 名
	 * 称：QueryIndex 功 能：构造检索查询器，对指定的目录进行查询，找到指定的值，并输出相应结果。
	 * ===============================================================
	 */
	public static void QueryIndex() {

		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
			// Term term = new Term("id","002");
			Term term = new Term("content", "记录");
			Query query = new TermQuery(term);
			System.out.println("" + query.toString());

			Hits hits = searcher.search(query);

			System.out.println("Search result:");

			for (int i = 0; i < hits.length(); i++) {
				System.out.println(hits.doc(i));
				System.out.println(hits.doc(i).getField("id"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Search success");

	}

	/*
	 * ================================================================ 名
	 * 称：IndexBuilder 功 能：构造磁盘索引，添加内容到指定目录，为后续检索查询做好准备。
	 * ===============================================================
	 */
	public static void IndexBuilder() {

		try {

			Analyzer TextAnalyzer = new SimpleAnalyzer();
			IndexWriter TextIndex = new IndexWriter(Dest_Index_Path,
					TextAnalyzer, true);
			TextIndex.setUseCompoundFile(true);
			for (int i = 0; i < 3; i++) {
				Document document = new Document();

				Field field_id = new Field("id", keywords[i], Field.Store.YES,
						Field.Index.UN_TOKENIZED);

				document.add(field_id);

				Field field_content = new Field("content", textdetail[i],
						Field.Store.YES, Field.Index.TOKENIZED);

				document.add(field_content);

				TextIndex.addDocument(document);

			}
			TextIndex.optimize();
			TextIndex.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Index success");
	}

	/*
	 * ================================================================ 名 称：main
	 * 功 能：测试Lucene索引建立和检索查询功能。
	 * ===============================================================
	 */
	public static void main(String[] args) {
		IndexBuilder();
		QueryIndex();
	}

}
