package cn.com.hereonline.ciku;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  字符串处理类
 */
public class StringUtils {

    private StringUtils() {}
    
    
    /**
     * 此方法描述的是：字符串的替换
     * @param string	需要替换的字符串
     * @param oldString	被替换的字符串
     * @param newString	新字符串
     * @author 作者 E-mail: wangxiongdx@163.com
     * @version 创建时间：2009-9-13 下午01:34:23
     */
     public static String replace(String string, String oldString, String newString) {
         if (string == null) {
             return null;
         }
         int i = 0;	//初始位置，从0开始
         
         //从指定位置开始查找oldString在string中的起始位置
         if ((i = string.indexOf(oldString, i)) >= 0) {
             // Use char []'s, as they are more efficient to deal with.
             char[] string2 = string.toCharArray();
             char[] newString2 = newString.toCharArray();
             int oLength = oldString.length();
             //StringBuilder 简易替换 比 StringBuffer 要快
             StringBuilder buf = new StringBuilder(string2.length);
             //string2从起始位置到  i 的部分... 继续追加新串 newString2
             buf.append(string2, 0, i).append(newString2);
             //string中旧字符串的结束位置
             i += oLength;
             int j = i;
             // Replace all remaining instances of oldString with newString.
             //用新串替换所有出现的旧串
             while ((i = string.indexOf(oldString, i)) > 0) {
                 buf.append(string2, j, i - j).append(newString2);
                 i += oLength;
                 j = i;
             }
             //用新串替换后的string2中截获最后一次出现新串的结束位置到结尾
             buf.append(string2, j, string2.length - j);
             return buf.toString();
         }
         return string;
     }
    
     /**
      * 此方法描述的是：使用正则替换字符串<br>tt</br>中含有的html标签和js标签
      * @param string	传入字符串
      * @author 作者 E-mail: wangxiongdx@163.com
      * @version 创建时间：2009-9-29 下午03:34:23
      */
    public static String stripHTMLTags(String tt){
    	//匹配所有带字母的标签(包括js标签)
    	String tag = "</?[a-zA-Z]*([\\s]*([A-Za-z]+[\\s]?=[\\s]?(\"|')?[\\w]+(\"|')?))*>";
    	//匹配js标签
    	String javascriptTag = "<script[\\s]*(.)*>";
    	Pattern p = Pattern.compile(tag);
		//起始
    	int index = 0;
    	//结束
		int last;
		while((index = tt.indexOf("<",index)) != -1){
			if((last = tt.indexOf(">",index)) != -1){
				//目标字符串
				String oldString = tt.substring(index, last+1);
				Matcher m = p.matcher(oldString);
				//首先判断是否为js标签
				if(Pattern.compile(javascriptTag).matcher(oldString).matches()){
					if((last = tt.indexOf("</script>",last)) != -1){
						tt = replace(tt, tt.substring(index, last+9), "");
					}
				//再判断是否为html标签(html正则考虑去除js标签正则？？？后补)
				}else if(m.matches()){
					tt = replace(tt, oldString, "");
				}
			}else{
				break;
			}
		}
		return tt;
    }

    public static String stripTags(String in) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len * 1.3));
        for (; i < len; i++) {
        	//System.out.println("i="+i);
        	//System.out.println("last="+last);
            ch = input[i];
            if (ch > '>') {
            	//do nothing
            }else if (ch == '<') {
                if (i + 3 < len && input[i + 1] == 't' && input[i + 2] == 'r' && input[i + 3] == '>') {
                    i += 3;
                    continue;
                }else if(i + 3 < len && input[i + 1] == '/' && input[i + 2] == 't' && input[i + 3] == 'r' && input[i + 4] == '>'){
                	i += 4;
                    continue;
                }
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
            }else if (ch == '>') {
                last = i + 1;
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }
    
    public static boolean isPreTagIn(String in) {
    	if(null == in || "".equals(in)){
    		return false;
    	}
    	if(in.indexOf("pre") != -1){
    		return true;
    	}
    	return false;
    }
    
    public static String stripStartNum(String in){
    	if(null == in || "".equals(in)){
    		return "";
    	}
    	StringTokenizer tokenizer = new StringTokenizer(in, ".");
    	while(tokenizer.hasMoreTokens()){
    		String first = tokenizer.nextToken();
    		if(Pattern.compile("[0-9]*").matcher(first).matches()){
    			return in.substring(first.length()+1);
    		}
    	}
    	return in;
    }

    public static void main(String[] args){
//    	//System.out.println("---->"+StringUtils.replaceIgnoreCase("我是。好m人", "M", "坏哈哈"));
//    	//System.out.println("---->"+StringUtils.isValidEmailAddress("011.com"));
////    	System.out.println("---->"+StringUtils.stripTags("<br>aaaaaadfdfsds"));
////    	List<String> lsit = new ArrayList<String>();
////    	for(int i = 0; i < 10; i++){
////    		lsit.add("哇"+i);
////    	}
    	System.out.println("---->"+StringUtils.stripHTMLTags("fsdfsdfsdfsdsfds<br>"));
//    	
    }

}