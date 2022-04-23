package top.designerSH.guoguo.instance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewDebug;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public UserInformationManager(){}
    /**
     *
     * @param _name 用户名
     * @param _password 密码
     * @return 0：服务器连接失败 1：用户名已存在 2：服务器空间已满 3：注册成功 4：长度需>=7 <=17 5：需同时包含数字和字母 6：不允许特殊字符
     */
    public static int register(String _name,String _password) {
        int msg = 0;
        int p = registerPasswordDetect(_password);
        if (p != 3) return p;
        if(_name.equals("") || _name.equals(" "))return 1;
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
                if (canResist) {
                    sql = "UPDATE guoguo_users SET nick_name = '" + _name + "',password = '" + _password + "' WHERE nick_name='' LIMIT 1";//查找是否有可用空间，修改表内容
                    PreparedStatement ps1 = connection.prepareStatement(sql);
                    if (ps1 != null) {
                        int effectLine = ps1.executeUpdate(sql);
                        if (effectLine == 0) {
                            canResist = false;
                            msg = 2;//未查询到可用空间,不可注册
                        }
                        ps1.close();
                    } else {
                        msg = 0;
                    }
                }
                if (canResist) {
                    sql = "select * from guoguo_users where nick_name = '" + _name + "' limit 1";//同步所有内容到本地
                    PreparedStatement ps2 = connection.prepareStatement(sql);
                    if (ps2 != null) {
                        ResultSet rs2 = ps2.executeQuery(sql);
                        int count = rs2.getMetaData().getColumnCount();//select到的每行的的所有内容——列数
                        while (rs2.next()) {//存在下一行时
                            for (int i = 1; i <= count; i++) {//遍历每一列
                                String field = rs2.getMetaData().getColumnName(i);
                                //Log.e("----------列表---------",field);
                                if (field.equals("user_id")) userID = Integer.valueOf(rs2.getString(field));
                                if (field.equals("user_token")) userToken = rs2.getString(field);
                                if (field.equals("nick_name")) userNickName = rs2.getString(field);
                                if (field.equals("password")) userPassword = rs2.getString(field);
                            }
                        }
                        saveLocalInformationData();
                        msg = 3;//注册成功
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
        return msg;
    }

    /**
     *
     * @param pass 要检测的密码
     * @return 3：正确 4：长度需>=7 <=17 5：需同时包含数字和字母 6：不允许特殊字符
     */
    private static int registerPasswordDetect(String pass) {
        boolean length = pass.length() >= 7 && pass.length() <= 17 ? true : false;//是否达到长度
        boolean isDigit = false;//用来表示是否包含数字
        boolean isLetter = false;//用来表示是否包含字母
        for (int i = 0; i < pass.length(); i++) {
            if (Character.isDigit(pass.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(pass.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        //String regex = "^[a-zA-Z0-9]+$";
        String match = "[^0-9a-zA-Z]+";
        //pass.matches(regex);
        if (!length) return 4;
        if (!isDigit || !isLetter) return 5;
        if (pass.matches(match)) return 6;
        return 3;
    }

    /**
     *
     * @param _name 账户名
     * @param _password 密码
     * @return 0：服务器连接失败 1：成功 2：用户名不存在或密码错误
     */
    public static int login(String _name,String _password){
        Connection connection = getConn();
        int msg = 0;
        try {
            String sql = "select * from guoguo_users where nick_name = '"+_name+"',password = '"+_password+"'";
            if (connection != null){
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    ResultSet rs = ps.executeQuery(sql);
                    if (!rs.next()){//未查询到
                        msg=2;
                    }
                    else {
                        int count = rs.getMetaData().getColumnCount();//select到的每行的的所有内容——列数
                        while (rs.next()){//存在下一行时
                            for (int i = 1;i <= count;i++){//遍历每一列
                                String field = rs.getMetaData().getColumnName(i);
                                if (field.equals("user_id")) userID = Integer.valueOf(rs.getString(field));
                                if (field.equals("user_token")) userToken = rs.getString(field);
                                if (field.equals("nick_name")) userNickName = rs.getString(field);
                                if (field.equals("password")) userPassword = rs.getString(field);
                            }
                        }
                        saveLocalInformationData();
                        msg=1;
                    }
                    ps.close();
                }else {
                    msg = 0;
                }
                connection.close();
            }else {
                msg = 0;
            }
        }
        catch (Exception e){
            e.printStackTrace();
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

    /**
     *  查询目标昵称的用户userID
     * @param targetName userNickName
     * @return String：查询到 Null：未查询到
     */
    public static String getTargetID(String targetName){
        String ID=null;
        Connection connection = getConn();
        try {
            String sql = "select * from guoguo_users where nick_name = '"+targetName+"'";
            if (connection != null){
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    ResultSet rs = ps.executeQuery(sql);
                    int count = rs.getMetaData().getColumnCount();//select到的每行的的所有内容——列数
                    while (rs.next()){//存在下一行时
                        for (int i = 1;i <= count;i++){//遍历每一列
                            String field = rs.getMetaData().getColumnName(i);
                            if (field.equals("user_id")) {
                                ID = rs.getString(field);
                            }
                        }
                    }
                    ps.close();
                }
                connection.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ID;
    }
    /**
     *
     * @param hours 检测两次间隔hours小时
     * @return true:需要重新登录 false:可以自动登录
     */
    public static boolean detectLoginDuration(int hours) {
        SharedPreferences sharedPreferences = mainContext.getSharedPreferences("userInf", Context.MODE_PRIVATE);
        Date d1 = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (sharedPreferences.getString("userNickName", null) == null) {//还没有登录过;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastLoginTime", dateFormat.format(d1));
            editor.apply();
            return true;
        }
        try {
            String lastTime = sharedPreferences.getString("lastLoginTime", null);
            Date d2 = dateFormat.parse(lastTime);
            long diff = d1.getTime() - d2.getTime();
            if (diff / 1000 / 60 / 60 > hours) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        //return true;
    }
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
}
