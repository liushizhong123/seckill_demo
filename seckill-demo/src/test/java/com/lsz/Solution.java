package com.lsz;

public class Solution {


    public static void main(String[] args) {
        int[][] scores = {{5,3,4,2,3},{1,5,2,2,3},{3,2,5,2,1},{1,4,2,5,2},{1,2,3,1,5}};
        Solution.getIndexScore(scores);
    }
    public static int getIndexScore(int [][] scores){
        //判空处理
        if(scores == null || scores.length ==0 || scores[0].length == 0){
            return -1;
        }
           int index = -1, score = 0;
           for(int i = 0;i < scores.length;i++){
               for(int j = 0;j < scores[0].length;j++){
                   if(j != i && scores[i][j] > score || j != i && scores[i][j] == score && j < index){
                       index = j;
                       score = scores[i][j];
                   }
               }
           }
           System.out.println("index = " + index + " score = " + score);
           return index;
    }
}
