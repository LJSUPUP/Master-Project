import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    class Trie{
        int val = 0;
        Trie[] child = new Trie[26];
    }
    public String findLongestWord(String s, List<String> d) {
        int len = s.length();
        Trie root = new Trie();
        Trie curr = root;
        for(int i=0;i<len;i++){

            for(int j=i+1;j<len;j++){

            }
        }

        int[] rec= new int[26];
        String res="";
        len = 0;
        for(char ch:s.toCharArray()){
            rec[ch-'a']++;
        }
        for(String str:d){
            int[] tmp = new int[26];
            int i,j;
            if(str.length()==0) continue;
            for(i =0,j=0;i<s.length()&&j<str.length();i++){
                char c1=s.charAt(i);
                char c2=str.charAt(j);
                tmp[c2-'a']++;
                if(rec[c2-'a']==0||tmp[c2-'a']>rec[c2-'a']){
                    break;
                }
                if(s.charAt(i)==str.charAt(j)){
                    j++;
                }
            }
            if(j==str.length()){
                if(len<=str.length()){
                    if(len==str.length()){
                        res = str.compareTo(res)>0?res:str;
                    }
                    else{
                        res=str;
                    }
                }
                len=res.length();
            }
        }
        return res;

    }
}
