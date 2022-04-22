package top.designerSH.guoguo;

import android.app.Application;

import io.rong.imkit.RongIM;
import top.designerSH.guoguo.instance.UserInformationManager;

public class ApplicationManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UserInformationManager.mainContext=this;
        RongIM.init(ApplicationManager.this,"vnroth0kvomlo",true);
    }
}
