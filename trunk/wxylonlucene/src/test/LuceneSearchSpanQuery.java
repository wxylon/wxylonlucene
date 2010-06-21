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
import org.apache.lucene.search.spans.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.*;

public class LuceneSearchSpanQuery {
	
	private static String Dest_Index_Path = "D:\\workshop\\TextIndex";
	
	static protected String[] keywords = {"001","002","003"};
	static protected String[] textdetail = {"��¼һ ��¼�� ��¼�� ��¼�� ��¼��","��¼һ ��¼�� ��¼�� ��¼�� ��¼�� ��¼ʮ ��¼��","��¼ʮһ ��¼ʮ�� ��¼ʮ�� ��¼ʮ�� ��¼ʮ��"};
	
	/*================================================================
	 * �� �ƣ�SpanFirstQueryTest
	 * �� �ܣ�����SpanQuery�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void SpanFirstQueryTest(){
		System.out.println("----------------------------------------------->SpanFirstQueryTest");
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "��¼��";
		    Term t = new Term("content",searchWords);
		    SpanTermQuery query = new SpanTermQuery(t);
		    SpanFirstQuery firstquery = new SpanFirstQuery(query,4);
		    
			System.out.println(query.toString());

			Hits hits = searcher.search(firstquery);
			System.out.println("Search result:");
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
				System.out.println(hits.doc(i).getField("id"));			
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�SpanNearQueryTest
	 * �� �ܣ�����SpanNearQuery�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ��
	 * �������Ӧ���
	 ===============================================================*/
	public static void SpanNearQueryTest(){
		System.out.println("----------------------------------------------->SpanNearQueryTest");
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term t1 = new Term("content","��¼һ");
		    Term t2 = new Term("content","��¼��");
		    Term t3 = new Term("content","��¼��");
		    
		    SpanTermQuery query1 = new SpanTermQuery(t1);
		    SpanTermQuery query2 = new SpanTermQuery(t2);
		    SpanTermQuery query3 = new SpanTermQuery(t3);
		    
		    SpanQuery[] queryarray  = new SpanQuery[]{query1,query3};
		    
		    SpanNearQuery nearquery = new SpanNearQuery(queryarray,2,true);
		    
			System.out.println(nearquery.toString());

			Hits hits = searcher.search( nearquery );
			System.out.println("Search result:");
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
				System.out.println(hits.doc(i).getField("id"));			
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}	
	
	/*================================================================
	 * �� �ƣ�SpanNotQueryTest
	 * �� �ܣ�����SpanNotQuery�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ��
	 * �������Ӧ���
	 ===============================================================*/
	public static void SpanNotQueryTest(){
		System.out.println("----------------------------------------------->SpanNotQueryTest");
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term t1 = new Term("content","��¼һ");
		    Term t2 = new Term("content","��¼��");
		    Term t3 = new Term("content","��¼��");
		    
		    SpanTermQuery query1 = new SpanTermQuery(t1);
		    SpanTermQuery query2 = new SpanTermQuery(t2);
		    SpanTermQuery query3 = new SpanTermQuery(t3);
		    
		    SpanQuery[] queryarray  = new SpanQuery[]{query1,query3};
		    
		    SpanNearQuery nearquery = new SpanNearQuery(queryarray,1,true);
		    SpanNotQuery notquery = new SpanNotQuery(nearquery,query2);
		    
			System.out.println(notquery.toString());

			Hits hits = searcher.search( notquery );
			System.out.println("Search result:");
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
				System.out.println(hits.doc(i).getField("id"));			
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}		
	

	/*================================================================
	 * �� �ƣ�SpanOrQueryTest
	 * �� �ܣ�����SpanOrQuery�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ��
	 * �������Ӧ���
	 ===============================================================*/
	public static void SpanOrQueryTest(){
		System.out.println("----------------------------------------------->SpanOrQueryTest");
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term t1 = new Term("content","��¼һ");
		    Term t2 = new Term("content","��¼��");
		    Term t3 = new Term("content","��¼��");
		    
		    SpanTermQuery query1 = new SpanTermQuery(t1);
		    SpanTermQuery query2 = new SpanTermQuery(t2);
		    SpanTermQuery query3 = new SpanTermQuery(t3);
		    
		    SpanQuery[] queryarray1  = new SpanQuery[]{query1,query2};
		    SpanQuery[] queryarray2  = new SpanQuery[]{query2,query3};
		    
		    SpanNearQuery nearquery1 = new SpanNearQuery(queryarray1,1,true);
		    SpanNearQuery nearquery2 = new SpanNearQuery(queryarray2,1,true);
		    
		    SpanOrQuery orquery = new SpanOrQuery(new  SpanNearQuery[]{nearquery1,nearquery2});
		    
			System.out.println(orquery.toString());

			Hits hits = searcher.search( orquery );
			System.out.println("Search result:");
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
				System.out.println(hits.doc(i).getField("id"));			
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}		
	
	
	/*================================================================
	 * �� �ƣ�TermQueryParserTest
	 * �� �ܣ�����QueryParser���TermQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void TermQueryParserTest(){
		System.out.println("----------------------------------------------->TermQueryParserTest");
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "��¼";
		    Analyzer analyzer = new SimpleAnalyzer();
		    QueryParser parser = new QueryParser("content",analyzer); 
		    try{
			    Query query = parser.parse(searchWords);
				System.out.println(query.toString());	
				System.out.println(query.getClass());	
	
				Hits hits = searcher.search(query);
				
				System.out.println("Search result:");	
				
				for(int i=0; i < hits.length(); i++)
				{
					System.out.println(hits.doc(i));
					System.out.println(hits.doc(i).getField("id"));			
				}
		    
		    } catch(ParseException e1){
				e1.printStackTrace();
		    }
			
		}catch (IOException e) {
				e.printStackTrace();
		}

		System.out.println("Search success");		
		
	}
		
	/*================================================================
	 * �� �ƣ�SpanIndexBuilder
	 * �� �ܣ������������������ݵ�ָ��Ŀ¼��Ϊ��������ѯ���׼����
	 ===============================================================*/
	public static void SpanIndexBuilder(){
		try {
			Analyzer TextAnalyzer = new SimpleAnalyzer();
			IndexWriter TextIndex = new IndexWriter(Dest_Index_Path,TextAnalyzer,true);
	        TextIndex.setUseCompoundFile(true);
			for(int i = 0; i < 3 ; i++){
				Document document = new Document();
				
				Field field_id = new Field("id", keywords[i], 
						Field.Store.YES,Field.Index.UN_TOKENIZED);
				
				document.add(field_id);
				
				Field field_content = new Field("content", textdetail[i], 
						Field.Store.YES,Field.Index.TOKENIZED);
				
				document.add(field_content);
				
				TextIndex.addDocument(document);

			}
			TextIndex.optimize();
			TextIndex.close();
		
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Index success");		
	}

	/*================================================================
	 * �� �ƣ�main
	 * �� �ܣ�����Lucene����b�ͼ����ѯ���ܡ�
	 ===============================================================*/
	public static void main(String[] args) {
//		SpanIndexBuilder();
//		SpanFirstQueryTest();
//		SpanNearQueryTest();
//		SpanNotQueryTest();
//		SpanOrQueryTest();
//		SpanIndexBuilder();
//		System.out.println("Test success");
	}

}
