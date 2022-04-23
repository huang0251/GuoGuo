package top.designerSH.guoguo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import io.rong.imkit.RongIM;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.RongIMClient;
import top.designerSH.guoguo.instance.UserInformationManager;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RongIM.connect("pYVzEL7ZQUYEg/lE/PpzVbWtGTsXotOgj3oXNihcx7A=@gk5p.cn.rongnav.com;gk5p.cn.rongcfg.com", new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                // 登录成功，跳转到默认会话列表页。
                RouteUtils.routeToConversationListActivity(MainActivity.this, "会话列表");
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

            }
        });
        /*if (UserInformationManager.detectLoginDuration(48))//判定距上次登录是否过期；
        {
            //展示登录界面
            new Thread(() -> {
                //UserInformationManager.login("exply","");
                //UserInformationManager.register("hdy_hdy","025177");
            }).start();
        }else {
            RongIM.connect(UserInformationManager.userToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String userId) {
                    // 登录成功，跳转到默认会话列表页。
                    RouteUtils.routeToConversationListActivity(MainActivity.this, "text");
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

                }
            });
        }*/
    }
}