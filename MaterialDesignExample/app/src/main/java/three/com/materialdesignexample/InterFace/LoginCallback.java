package three.com.materialdesignexample.InterFace;

/**
 * Created by Zj on 2016/1/6.
 */
public interface LoginCallback {
    void beforeStart();
    void onLoginFinshed();
    void onError(Exception e);
}
