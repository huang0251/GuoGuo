package top.designerSH.guoguo;

import android.app.Application;

import io.rong.imkit.RongIM;

public class ApplicationManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(ApplicationManager.this,"vnroth0kvomlo",true);
    }
}
