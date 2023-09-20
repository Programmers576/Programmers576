import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SimHashPlagiarismChecker {
   public static void main(String []args){
      String originalFile = "d:\\Users\\LENOVO\\Desktop\\工程概论作业\\第一项程序\\orig.txt";
      String original_addFile = "d:\\Users\\LENOVO\\Desktop\\工程概论作业\\第一项程序\\orig_add.txt";
      String answerFile = "d:\\Users\\LENOVO\\Desktop\\工程概论作业\\第一项程序\\answer.txt";
      try{
         String originalText = readTextFromFile(originalFile);
         String original_addText = readTextFromFile(original_addFile);
         double similarity = calculateSimilarity(originalText,original_addText);
         writeResultToFile(answerFile,similarity);
      }catch(IOException e){
         e.printStackTrace();
      }
   }
   //读取文件内容
   private static String readTextFromFile(String filepath) throws IOException {
      StringBuilder stringbuilder = new StringBuilder();
      try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
         String line;
         while((line = reader.readLine()) != null){
            stringbuilder.append(line);
         }
      }
      return stringbuilder.toString();
   }
   //计算SimHash值
   private static BigInteger calculateSimhash(String text){
      Map<String, Integer> wordCountMap = new HashMap<>();
      //分词并统计词频
      String []words = text.split("\\s+");//按空格分割文本为单词
      for(String word:words){
         wordCountMap.put(word,wordCountMap.getOrDefault(word,0)+1);//统计词频
      }
      int []hashBits = new int[64];
      for(Map.Entry<String,Integer> entry: wordCountMap.entrySet()){
         String word = entry.getKey();
         int count = entry.getValue();
         BigInteger wordHash = calculateWordHash(word);
         for(int i = 0;i<64;i++){
            BigInteger mask = BigInteger.ONE.shiftLeft(i);
            if(wordHash.and(mask).signum()!=0){
               //判断哈希值当前位是否为1
               hashBits[i] += count;//如果是1则增加词频
            }
            else{
               hashBits[i] -= count;//如果是0则减少词频
            }
         }
      }
      BigInteger simhash = BigInteger.ZERO;
      for(int i = 0;i<64;i++){
         if(hashBits[i]>0){
            simhash = simhash.setBit(i);
         }
      }
      return simhash;
   }

   private static BigInteger calculateWordHash(String word) {
      //实现具体单词的哈希算法,使用Java内置的hashCode方法
      return BigInteger.valueOf(word.hashCode());
   }
   //计算重复率
   private static double calculateSimilarity(String originalText,String original_addText){
      BigInteger originalsimhash = calculateSimhash(originalText);
      BigInteger original_addsimhash = calculateSimhash(original_addText);
      int haimingDistance = calculateHaiMingDistance(originalsimhash,original_addsimhash);
      int maxdistance = 128;//SimHash值的位数
      double similarity = 1-(double)haimingDistance/maxdistance;
      return similarity;
   }
   //计算海明距离
   private static int calculateHaiMingDistance(BigInteger simhash1, BigInteger simhash2) {
      BigInteger xorResult = simhash1.xor(simhash2);
      int haimingdistance = xorResult.bitCount();
      return haimingdistance;
   }
   //将结果写入文件
   private static void writeResultToFile(String filepath,double similarity) throws IOException{
      try(PrintWriter writer = new PrintWriter(new FileWriter(filepath))){
         writer.printf("%.2f",similarity);
      }
   }
}
