package test;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.cache.Cache;
import org.apache.lucene.util.cache.SimpleLRUCache;
import org.apache.lucene.util.cache.SimpleMapCache;

public class Test {

	public static void testToken() {
		String string = new String("我爱天大,但我更爱中国");
		Analyzer analyzer = new StandardAnalyzer();
//		 Analyzer analyzer= new StopAnalyzer();
		TokenStream ts = analyzer.tokenStream("dummy", new StringReader(string));
		Token token;
		try{
			int n = 0;
			while ((token = ts.next()) != null){
				System.out.println((n++) + "->" + token.toString());
			}
		}
		catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public static void testSimpleMapCache(){
			SimpleMapCache cache = new SimpleMapCache();
			Cache ssmc = cache.synchronizedCache(cache);
			ssmc.put(1, 1);
			ssmc.put(2, 2);
			
			System.out.println(ssmc.get(1));
	}
	
	public static void testSimpleLRUCache(){
		SimpleLRUCache cache = new SimpleLRUCache(10);
		Cache ssmc = cache.synchronizedCache(cache);
		ssmc.put(1, 1);
		ssmc.put(2, 2);
		
		System.out.println(ssmc.get(1));
	}

	public static void main(String[] args) {
//		Test.testSimpleMapCache();
//		Test.testSimpleLRUCache();
	}
}
