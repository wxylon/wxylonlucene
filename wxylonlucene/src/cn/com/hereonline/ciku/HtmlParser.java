package cn.com.hereonline.ciku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

public class HtmlParser {
	public final static String clear_tran = "&nbsp;";
	public static Map<Integer, String> keyWords = new HashMap<Integer, String>();
	public static Map<Integer, String> ch = new HashMap<Integer, String>();
	public static Map<Integer, String> en = new HashMap<Integer, String>();
	
	public static void queryJuKuu(String word){
		ch.clear();
		en.clear();
		String en_value = "";
		String ch_value = ""; 
		String url = "";
		int i = 0;
		boolean check = true;
		try {
			while(check){
				url = URLEncoder.encode(word,"utf-8");
				Parser parser = new Parser("http://www.jukuu.com/show-"+url+"-"+i+".html");
				parser.setEncoding("utf-8");
				
				NodeFilter en_node = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("class","e"));
				NodeList en_nodeList = parser.parse(en_node);
				SimpleNodeIterator en_iterator = en_nodeList.elements();
				
				parser.reset();
				NodeFilter ch_node = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("class","c"));
				NodeList ch_nodeList = parser.parse(ch_node);
				SimpleNodeIterator ch_iterator = ch_nodeList.elements();
				
				while(en_iterator.hasMoreNodes()){
					Node en_son_node_1 = en_iterator.nextNode();
					SimpleNodeIterator en_son_nodelist = en_son_node_1.getChildren().elements();
					while(en_son_nodelist.hasMoreNodes()){
						en_value += en_son_nodelist.nextNode().getFirstChild().toHtml().trim();
						// 去掉特殊转义字符
						int line;
						while((line = en_value.indexOf(clear_tran)) != -1){
							en_value = en_value.substring(0, line) + en_value.substring(line+6);
						}
						
						//去除起始数字数字
						en_value = StringUtils.stripStartNum(en_value);
					}
					
					if(en.containsValue(en_value)){
						check = false;
						break;
					}
					en.put(en.size(), en_value);
					System.out.println(en_value);
					en_value = "";
					
					Node ch_son_node_1 = ch_iterator.nextNode();
					ch_value = StringUtils.stripHTMLTags(ch_son_node_1.toHtml());
					// 去掉特殊转义字符
					int line;
					while((line = ch_value.indexOf(clear_tran)) != -1){
						ch_value = ch_value.substring(0, line) + ch_value.substring(line+6);
					}
					ch.put(ch.size(), ch_value.trim());
					System.out.println(ch_value.trim());
					ch_value = "";
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void queryWordsFromTxt(){
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File("D:\\ciku\\words.txt")));
			while((line = br.readLine()) != null){
				if(!"".equals(line) && line.length() > 0){
					if(!keyWords.containsValue(line)){
						keyWords.put(keyWords.size(), line);
						queryJuKuu(line);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		HtmlParser.queryWordsFromTxt();
	}
	
}
