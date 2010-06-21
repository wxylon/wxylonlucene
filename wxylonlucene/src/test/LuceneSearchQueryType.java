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
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.MultiSearcher;

public class LuceneSearchQueryType {
	
	private static String Dest_Index_Path = "D:\\workshop\\TextIndex";
	
	static protected String[] keywords = {"001","002","003","004","005"};
	static protected String[] textdetail = {"��¼ һ","��¼ ��", "��¼ ��", "һ 2345 ��¼", "��¼ �� һ"} ;
	private static String Dest_Index_Path2 = "D:\\workshop\\TextIndex2";
	
	static protected String[] keywords2 = {"001","002","003","004","005"};
	static protected String[] textdetail2 = {"Record һ","Record ��", "Record ��", "һ 2345 Record", "Record �� һ"} ;
	
	/*================================================================
	 * �� �ƣ�TermQueryTest
	 * �� �ܣ���������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void TermQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "��¼һ";
		    Term t = new Term("id","002");
		    Query query = new TermQuery(t);
			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
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
	 * �� �ƣ�RangeQueryTest
	 * �� �ܣ����췶Χ�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void RangeQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term termStart = new Term("numval","150");
		    Term termEnd   = new Term("numval","155");
		    
		    Query query = new RangeQuery(termStart,termEnd,true);
			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�RangeQueryParserTest
	 * �� �ܣ�����QueryParser���RangeQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void RangeQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "{150 TO 155}";
		    Analyzer analyzer = new SimpleAnalyzer();
		    QueryParser parser = new QueryParser("numval",analyzer); 
		    try{
			    Query query = parser.parse(searchWords);
				System.out.println(query.toString());	
				System.out.println(query.getClass());	
	
				Hits hits = searcher.search(query);
				
				System.out.println("Search result:");		
				
				for(int i=0; i < hits.length(); i++)
				{
					System.out.println(hits.doc(i));
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
	 * �� �ƣ�BooleanQueryTest
	 * �� �ܣ����첼������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void BooleanQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term term1 = new Term("content","��¼");
		    Term term2 = new Term("content","��");
		    
		    TermQuery query1 = new TermQuery(term1);
		    TermQuery query2 = new TermQuery(term2);

		    BooleanQuery query = new BooleanQuery();
		    query.add(query1,BooleanClause.Occur.MUST);
		    query.add(query2,BooleanClause.Occur.MUST);
		    
			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�BooleanQueryParserTest
	 * �� �ܣ�����QueryParser���BooleanQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void BooleanQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "(��¼ AND һ)";
//		    String searchWords = "((һ OR ��)AND ��¼)";
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
	 * �� �ƣ�PhraseQueryTest
	 * �� �ܣ������������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void PhraseQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term term1 = new Term("content","��¼");
		    Term term2 = new Term("content","һ");
		    
		    PhraseQuery query = new PhraseQuery();
		    query.add(term1);
		    query.add(term2);
		    query.setSlop(2);
			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�PhraseQueryParserTest
	 * �� �ܣ�����QueryParser���PhraseQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void PhraseQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "\"��¼ һ\"";
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
	 * �� �ƣ�PrefixQueryTest
	 * �� �ܣ�����ǰ׺�����ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void PrefixQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

		    Term term1 = new Term("content","��¼");
		    
		    PrefixQuery query = new PrefixQuery(term1);
			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
		
	/*================================================================
	 * �� �ƣ�PrefixQueryParserTest
	 * �� �ܣ�����QueryParser���PrefixQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void PrefixQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "��¼*";
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
	 * �� �ƣ�IndexBuilder
	 * �� �ܣ������������������ݵ�ָ��Ŀ¼��Ϊ��������ѯ���׼����
	 ===============================================================*/
	public static void IndexBuilder(){
		try {
			Analyzer TextAnalyzer = new SimpleAnalyzer();
			IndexWriter TextIndex = new IndexWriter(Dest_Index_Path,TextAnalyzer,true);
	        TextIndex.setUseCompoundFile(true);
			for(int i = 0; i < 5 ; i++){
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
	 * �� �ƣ�IndexBuilder2
	 * �� �ܣ������������������ݵ�ָ��Ŀ¼��Ϊ��������ѯ���׼����
	 ===============================================================*/
	public static void IndexBuilder2(){
		try {
			Analyzer TextAnalyzer = new SimpleAnalyzer();
			IndexWriter TextIndex = new IndexWriter(Dest_Index_Path2,TextAnalyzer,true);
	        TextIndex.setUseCompoundFile(true);
			for(int i = 0; i < 5 ; i++){
				Document document = new Document();
				
				Field field_id = new Field("id", keywords2[i], 
						Field.Store.YES,Field.Index.UN_TOKENIZED);
				
				document.add(field_id);
				
				Field field_content = new Field("content", textdetail2[i], 
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
	 * �� �ƣ�DataIndexBuilder
	 * �� �ܣ������������ֵ�Ĵ�������������ݵ�ָ��Ŀ¼��Ϊ��������ѯ���׼����
	 ===============================================================*/
	public static void DataIndexBuilder(){
		
		try {
			Integer nNum;
			
			Analyzer TextAnalyzer = new SimpleAnalyzer();
			IndexWriter TextIndex = new IndexWriter(Dest_Index_Path,TextAnalyzer,true);
	        TextIndex.setUseCompoundFile(true);
			System.out.println("Index Value:");
			for(int i = 100; i < 160 ; i++){
				Document document = new Document();

				nNum = i;
				String sortvalue = nNum.toString();
				System.out.print(sortvalue);		
				System.out.print(" ");
				if( i%20 == 19){
					System.out.println("");
				}
				Field field_data = new Field("numval", sortvalue, 
						Field.Store.YES,Field.Index.UN_TOKENIZED);
				document.add(field_data);
		
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
	 * �� �ƣ�FuzzyQueryTest
	 * �� �ܣ�����ģ������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void FuzzyQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);

//	    	Term term = new Term("content","��¼");
//			Term term = new Term("content","����");
			Term term = new Term("content","��¼");
			
		    FuzzyQuery query = new FuzzyQuery(term,0.1f,1);

			System.out.println(query.toString());	

			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			
			for(int i=0; i < hits.length(); i++)
			{
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�FuzzyQueryParserTest
	 * �� �ܣ�����QueryParser���FuzzyQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void FuzzyQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "��¼һ~0.1";
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
	 * �� �ƣ�WildcardQueryTest
	 * �� �ܣ�����ͨ�������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void WildcardQueryTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
			Term term = new Term("content","��*");
			WildcardQuery query = new WildcardQuery(term);
			System.out.println(query.toString());	
			Hits hits = searcher.search(query);
			System.out.println("Search result:");		
			for(int i=0; i < hits.length(); i++){
				System.out.println(hits.doc(i));
			}
		}catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("Search success");		
	}
	
	/*================================================================
	 * �� �ƣ�WildcardQueryParserTest
	 * �� �ܣ�����QueryParser���WildcardQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void WildcardQueryParserTest(){
	
		try {
			IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
		    String searchWords = "0*1";
		    Analyzer analyzer = new SimpleAnalyzer();
		    QueryParser parser = new QueryParser("id",analyzer); 
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
	 * �� �ƣ�MultiFeildQueryParserTest
	 * �� �ܣ�����QueryParser���MultiFeildQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void MultiFeildQueryParserTest(){
	
		try {
			String word_list[] ={"002","��¼"};
			String feild_list[] ={"id","content"};
		    try{
		    	
				IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
			    BooleanQuery boolquery = new BooleanQuery();
			    Analyzer analyzer = new SimpleAnalyzer();
			    for(int m=0;m<2;m++)
			    {
			       QueryParser parser = new QueryParser(feild_list[m],analyzer);
			       Query query = parser.parse(word_list[m]);
			       boolquery.add(query, BooleanClause.Occur.SHOULD);
			    }

			    //Query query = parser.parse(searchWords);
				System.out.println(boolquery.toString());	
				System.out.println(boolquery.getClass());	
	
				Hits hits = searcher.search(boolquery);
				
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
	 * �� �ƣ�MultiFeildQueryParserTest
	 * �� �ܣ�����QueryParser���MultiFeildQuery���󣬲����ü����ѯ���ָ����Ŀ¼���в�ѯ��
	 * �ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void MultiFeildTermTest(){
	
		try {
			String word_list[] ={"002","��¼"};
			String feild_list[] ={"id","content"};
		    try{
		    	
				IndexSearcher searcher = new IndexSearcher(Dest_Index_Path);
			    BooleanQuery boolquery = new BooleanQuery();
			    Analyzer analyzer = new SimpleAnalyzer();
			    for(int m=0;m<2;m++)
			    {
			       QueryParser parser = new QueryParser(feild_list[m],analyzer);
			       Query query = parser.parse(word_list[m]);
			       boolquery.add(query, BooleanClause.Occur.SHOULD);
			    }
		    

			    //Query query = parser.parse(searchWords);
				System.out.println(boolquery.toString());	
				System.out.println(boolquery.getClass());	
	
				Hits hits = searcher.search(boolquery);
				
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
	 * �� �ƣ�MultiSearcherQueryTest
	 * �� �ܣ���������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void MultiSearcherQueryTest(){
	
		try {
			IndexSearcher searcher1 = new IndexSearcher(Dest_Index_Path);
			IndexSearcher searcher2 = new IndexSearcher(Dest_Index_Path2);
			
			IndexSearcher[] searchers = {searcher1,searcher2};
			MultiSearcher multisearcher = new MultiSearcher(searchers);
			
		    String searchWords = "��";
		    Term t = new Term("id","002");
		    Query query = new TermQuery(t);
			System.out.println(query.toString());	

			Hits hits = multisearcher.search(query);
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
	 * �� �ƣ�main
	 * �� �ܣ�����Lucene����b�ͼ����ѯ���ܡ�
	 ===============================================================*/
	public static void main(String[] args) {
		
		//IndexBuilder();
		//TermQueryTest();
		//TermQueryParserTest();
		
		//DataIndexBuilder();
		//RangeQueryTest();
		//RangeQueryParserTest();
		
		//IndexBuilder();
		//BooleanQueryTest();
		//BooleanQueryParserTest();
		
		//IndexBuilder();
		//PrefixQueryTest();
		//PrefixQueryParserTest();
		
		
		//IndexBuilder();
		//PhraseQueryTest();
		//PhraseQueryParserTest();
		
		//IndexBuilder();
		//FuzzyQueryTest();
		//FuzzyQueryParserTest();
		
		//IndexBuilder();
		//WildcardQueryTest();
		//WildcardQueryParserTest();
		
		//IndexBuilder();
		//MultiFeildQueryParserTest();
		
		IndexBuilder();
		IndexBuilder2();
		MultiSearcherQueryTest();

		System.out.println("Test success");
	}

}
