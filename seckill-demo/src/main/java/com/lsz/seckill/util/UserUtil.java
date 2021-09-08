package com.lsz.seckill.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.String;

/**
 * 生成用户工具类
 */
public class UserUtil {

    public static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<User>(count);
        for(int i = 0;i < count;i++){
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");

        //加入数据库
        //获取连接
//        Connection conn = getConn();
//        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id)" +
//                " values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for (int i = 0; i < users.size(); i++) {
//            User user = users.get(i);
//            pstmt.setInt(1,user.getLoginCount());
//            pstmt.setString(2,user.getNickname());
//            pstmt.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(4,user.getSalt());
//            pstmt.setString(5,user.getPassword());
//            pstmt.setLong(6,user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.clearParameters();
//        conn.close();
//        System.out.println("insert into db");

        //登录，生成userTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\asus\\Desktop\\config.txt");
        //文件存在，删掉文件
        if(file.exists()){
            file.delete();
        }
        //创建文件
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            //获取输出流，写文件
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId()+"&password=" + MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            //获取输入流，读文件
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0){
                bout.write(buff,0,len);
            }
            //关闭输入输出流
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response,RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket :" + user.getId());
            //文件中的一行
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "lsz123";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
