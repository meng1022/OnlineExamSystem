package org.jeecg.common.util;
import java.io.*;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

@Slf4j
public class QuestUtils {
        public static final String filepath = "D://StopWordTable.txt";
        /**
       * 阈值
       */
        public static double THRESHOLD = 0;
        /**
       * 计算两个字符串的夹角余弦值
       * @param str1 字符串1
       * @param str2 字符串2
       * @return 夹角余弦值
       */
        public static double getSimilarity(String str1, String str2) throws Exception {
            List<String> vector1 = participle(str1);
            List<String> vector2 = participle(str2);
            log.info(vector1.toString());
            log.info(vector2.toString());
            int size = vector1.size();
            int size2 = vector2.size();
            double similar1 = 1.0-((double)Math.abs(size-size2)/(double)(size+size2));//句长相似度
            double similar2 = 0.0;//词形相似度
            double similar3 = 0.0;//词序相似度
            Map<String, double[]> map = new HashMap<String, double[]>();
            // 计算并集,两个句子中所有不重复的词，一个词str1中有，c[0]为1，没有为0.str2中同理
            String index = null;
            for (int i = 0; i < size; i++) {
               index = vector1.get(i);
               if (index != null) {
                    double[] c = map.get(index);
                    c = new double[2];
                    c[0] = 1; // vector1的语义分数
                    c[1] = THRESHOLD;// vector2的语义分数
                    map.put(index, c);
               }
            }
            for (int i = 0; i < size2; i++) {
               index = vector2.get(i);
               if (index != null) {
                    double[] c = map.get(index);
                    if (c != null && c.length == 2) {
                         c[1] = 1; // vector2中也存在，vector2的语义分数=1
                    } else {
                         c = new double[2];
                         c[0] = THRESHOLD; // vector1的语义分数
                         c[1] = 1; // vector2的语义分数
                         map.put(index, c);
                    }
               }
            }

            Iterator<String> it = map.keySet().iterator();
            double s1 = 0, s2 = 0, sum = 0;
            while (it.hasNext()) {
               double[] c = map.get(it.next());
               sum += c[0] * c[1];
               s1 += c[0] * c[0];
               s2 += c[1] * c[1];
            }
            similar2 = sum / Math.sqrt(s1 * s2);

            Map<String,Integer> wordcount1 = new HashMap<String, Integer>();
            Map<String,Integer> wordcount2 = new HashMap<String, Integer>();
            int [] adversenum2 = new int[999];
            for(int k=0;k<999;k++){
                adversenum2[k]=999;
            }
            Integer count = 1;
            for(int i=0;i<vector1.size();i++){
                if(wordcount1.get(vector1.get(i))==null){
                    wordcount1.put(vector1.get(i),1);
                }else{
                    count = wordcount1.get(vector1.get(i))+1;
                    wordcount1.put(vector1.get(i),count);
                }
            }
            int j=0;
            for(int i=0;i<vector2.size();i++){
                if(vector1.indexOf(vector2.get(i))!=-1){
                    adversenum2[j]=vector1.indexOf(vector2.get(i));
                    j++;
                    //log.info(adversenum2.toString());//为了计算逆序数
                }else{
                    adversenum2[j]=999;
                }
                if(wordcount2.get(vector2.get(i))==null){
                    wordcount2.put(vector2.get(i),1);
                }else{
                    count = wordcount2.get(vector2.get(i))+1;
                    wordcount2.put(vector2.get(i),count);
                }
            }
            int only1 = 0;//两个句子中都出现且只出现1次的词数
            Set<String> set = map.keySet();
            for(String word:set){
               double[]c = map.get(word);
               if(c[0]==1&&c[1]==1){
                   if(wordcount1.get(word)==1&&wordcount2.get(word)==1){
                       only1++;
                   }
               }
            }
            if(only1==0){
                similar3=0.0;
            }else if(only1==1){
                similar3=1.0;
            }else{
                count =0;
                int num = adversenum2.length;
                for(int i =0;i<num;i++){
                    for(j=i+1;j<num-i-1;j++){
                        if(adversenum2[i]>adversenum2[j]){
                            count++;
                        }
                    }
                }
                System.out.print(count);
                System.out.print(only1);
                similar3 = 1.0-(count/(only1-1.0));
            }
            double similar = 0.1*similar1+0.7*similar2+0.2*similar3;
            log.info("similar1:"+similar1+",similar2:"+similar2+",similar3:"+similar3);
            return similar;//计算
     }

     /**
       * 分词
       * @param str 字符串
       * @return 分次后的字符集合
       * @throws IOException
       */
     public static List<String> participle(String str) throws IOException {
         BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"UTF8"));
         Set<String> stopWordSet = new HashSet<String>();//中文停用词表
         String stopWord = null;
         for(; (stopWord = StopWordFileBr.readLine()) != null;) {
             stopWordSet.add(stopWord);
         }
             List<String> vector = new Vector<String>();// 对输入进行分词
          StringReader reader = new StringReader(str);
          IKSegmentation ik = new IKSegmentation(reader, true);// 当为true时，分词器进行最大词长切分
          Lexeme lexeme = null;
          while ((lexeme = ik.next()) != null) {
              if(stopWordSet.contains(lexeme.getLexemeText())){
                  continue;
              }
               vector.add(lexeme.getLexemeText());
          }
          StopWordFileBr.close();
          return vector;
     }

//     public static void main(String[] args) {
//          double same = 0;
//          try {
//               same = QuestUtils.getSimilarity("乌干达外交部就此事件可能对中国大使馆造成的负面影响感到抱歉。", "此事件对中国大使馆造成了一定的负面影响，乌干达外交部感到抱歉并公开道歉。");
//          } catch (Exception e) {
//               System.err.println(e.getMessage());
//          }
//          System.out.println("相似度：" + same);
//     }
    }


