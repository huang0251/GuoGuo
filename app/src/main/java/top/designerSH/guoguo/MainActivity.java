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
        new Thread(new Runnable() {
            @Override
            public void run() {
                //UserInformationManager.login("exply","");
                UserInformationManager.register("hdy_hdy","025177");
            }
        }).start();

        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            String token = "pYVzEL7ZQUYEg/lE/PpzVbWtGTsXotOgj3oXNihcx7A=@gk5p.cn.rongnav.com;gk5p.cn.rongcfg.com";
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
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
        });
    }
}