package three.com.materialdesignexample;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Administrator on 2015/10/26.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "uJqusUnLu4grSTMesgsvNE9z", "Ul3CrGblHrSktzEI0j5gpgQz");
    }
}
