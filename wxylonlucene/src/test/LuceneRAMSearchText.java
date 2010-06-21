package test;


import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneRAMSearchText {
	
	private static String Dest_Index_Path = "D:\\workshop\\TextIndex";
	
	static protected String[] keywords = {"001","002","003"};
	static protected String[] textdetail = {"��¼ һ","��¼ ��", "��¼ ��"} ;
	
	/*================================================================
	 * �� �ƣ�QueryRAMIndex
	 * �� �ܣ���������ѯ���ָ����Ŀ¼���в�ѯ���ҵ�ָ����ֵ���������Ӧ���
	 ===============================================================*/
	public static void QueryRAMIndex(){
		
		try {
			
			Directory fsDir = FSDirectory.getDirectory(Dest_Index_Path, false); 
			Directory ramDir = new RAMDirectory(fsDir); 
			
			IndexSearcher searcher = new IndexSearcher(ramDir);
			Term term = new Term("id","002");
			//Term term = new Term("content","��¼");
			Query query = new TermQuery(term);
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
	 * �� �ƣ�IndexBuilder
	 * �� �ܣ������������������ݵ�ָ��Ŀ¼��Ϊ��������ѯ���׼����
	 ===============================================================*/
	public static void IndexBuilder(){
		
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
	 * �� �ܣ�����Lucene����b���ڴ�����ѯ���ܡ�
	 ===============================================================*/
	public static void main(String[] args) {
		
		IndexBuilder();
		QueryRAMIndex();
		
		System.out.println("Test success");
	}
}
