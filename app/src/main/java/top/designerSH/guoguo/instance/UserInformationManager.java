package top.designerSH.guoguo.instance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewDebug;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class UserInformationManager {
    public static String userNickName;
    public static int userID;
    //public static String userName;
    public static String userToken;
    public static String userPassword;

    public static Context mainContext;

    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动

    private static String dbName = "designerly";// 数据库名称

    private static String user = "designerly";// 用户名

    private static String password = "hdy025177";// 密码

    public UserInformationManager(){};
    public static void saveLocalInformationData(){
        SharedPreferences sharedPreferences= mainContext.getSharedPreferences("userInf",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userNickName",userNickName);
        editor.putInt("userID",userID);
        //editor.putString("userName",userName);
        editor.putString("userToken",userToken);
        editor.putString("userPassword",userPassword);
        editor.apply();
    }
    public static void getLocalInformationData(){
        SharedPreferences sharedPreferences= mainContext.getSharedPreferences("userInf",Context.MODE_PRIVATE);
        userNickName=sharedPreferences.getString("userNickName",userNickName);
        userID=sharedPreferences.getInt("userID",userID);
        //userName=sharedPreferences.getString("userName",userName);
        userToken=sharedPreferences.getString("userToken",userToken);
        userPassword=sharedPreferences.getString("userPassword",userPassword);
    }
    public static int register(String _name,String _password) {
        int msg = 0;//0：服务器链接失败 1：用户名已存在 2：密码不符合要求 3：服务器空间已满 4：注册成功
        if (registerPasswordDetect(_password)) {

        }
        boolean canResist = false;
        Connection connection = getConn();
        if (connection != null) {// connection不为null表示与数据库建立了连接
            try {
                String sql = "select * from guoguo_users where nick_name = '" + _name + "' limit 1";//查找是否有重复用户名
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ResultSet rs = ps.executeQuery(sql);
                    if (!rs.next()) {
                        canResist = true;
                    } else {
                        msg = 1;//用户名已存在
                    }
                    ps.close();
                } else {
                    msg = 0;
                }
                if (canResist){
                    sql = "UPDATE guoguo_users SET nick_name = '"+_name+"',password = '"+_password+"' WHERE nick_name='' LIMIT 1";//查找是否有可用空间，修改表内容
                    PreparedStatement ps1 = connection.prepareStatement(sql);
                    if (ps1 != null) {
                        int effectLine = ps1.executeUpdate(sql);
                        if (effectLine==0) {
                            canResist=false;
                            msg=3;//未查询到可用空间,不可注册
                        }
                        ps1.close();
                    } else {
                        msg = 0;
                    }
                }
                if (canResist){
                    sql = "select * from guoguo_users where nick_name = '"+_name+"' limit 1";//同步所有内容到本地
                    PreparedStatement ps2 = connection.prepareStatement(sql);
                    if (ps2 != null) {
                        ResultSet rs2 = ps2.executeQuery(sql);
                        int count = rs2.getMetaData().getColumnCount();//select到的每行的的所有内容——列数
                        while (rs2.next()){//存在下一行时
                            for (int i = 1;i <= count;i++){//遍历每一列
                                String field = rs2.getMetaData().getColumnName(i);
                                //Log.e("----------列表---------",field);
                                if(field.equals("user_id"))userID=Integer.valueOf(rs2.getString(field));
                                if(field.equals("user_token"))userToken=rs2.getString(field);
                                if(field.equals("nick_name"))userNickName=rs2.getString(field);
                                if(field.equals("password"))userPassword=rs2.getString(field);
                            }
                        }
                        saveLocalInformationData();
                        msg=4;
                        ps2.close();
                    } else {
                        msg = 0;
                    }
                }
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                msg = 0;
            }
        } else {
            msg = 0;
        }
        Log.e("----------msg---------",Integer.toString(msg));
        return msg;
    }
    private static boolean registerPasswordDetect(String pass){
        //填写密码格式要求
        return false;
    }
    public static int login(String _name,String _password){
        Connection connection = getConn();
        int msg = 0;
        try {
            // mysql简单的查询语句。这里是根据user表的userAccount字段来查询某条记录
            String sql = "select * from guoguo_users where nick_name = 'expl' limit 1";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    ResultSet rs = ps.executeQuery(sql);
                    Log.e("-------wrong------",Integer.toString(rs.getMetaData().getColumnCount())+"000");
                    if (!rs.next())//未查询到
                        Log.e("-------wrong------","4");
                    int count = rs.getMetaData().getColumnCount();//select到的每行的的所有内容——列数
                    while (rs.next()){//存在下一行时
                        for (int i = 1;i <= count;i++){//遍历每一列
                            String field = rs.getMetaData().getColumnName(i);
                            Log.e("----------列表---------",rs.getString(field));
                        }
                    }
                    ps.close();
                    /*
                    if (map.size()!=0){
                        StringBuilder s = new StringBuilder();
                        //寻找密码是否匹配
                        for (String key : map.keySet()){
                            if(key.equals("userpassword")){
                                if(userPassword.equals(map.get(key))){
                                    msg = 1;            //密码正确
                                }
                                else
                                    msg = 2;            //密码错误
                                break;
                            }
                        }
                    }else {
                        Log.e("", "查询结果为空");
                        msg = 3;
                    }*/
                }else {
                    msg = 0;
                }
                connection.close();
            }else {
                msg = 0;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("", "异常login：" + e.getMessage());
            msg = 0;
        }
        return msg;
    }
    public static Connection getConn(){

        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            //String ip = "120.27.26.138";// 写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个

            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://120.27.26.138:3306/" + dbName,user, password);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
}
