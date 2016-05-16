package three.com.materialdesignexample.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import three.com.materialdesignexample.CallBack;
import three.com.materialdesignexample.InterFace.LoginCallback;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.Util.SafeCodeUtil;
import three.com.materialdesignexample.widget.AlertDialogHelper;
import three.com.materialdesignexample.widget.ProgressDialogHelper;
import three.com.materialdesignexample.widget.SharedPreferencesHelper;

/**
 * Created by Administrator on 2015/10/14.
 */
public class   LoginActivity extends Activity{
    private EditText loginuser=null;
    private EditText loginpass=null;
    private EditText passported=null;
    private Button loginbtn=null;
    private Button safecodebtn=null;
    private ImageView codeimg=null;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        isCookieGet() ;    //判断是否登陆过

        initView();

        setLoginBtnListener();  //设置登录按钮监听

        setSafecodeBtnListener(); //设置获取验证码按钮监听
    }

    private void setSafecodeBtnListener() {
        safecodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = loginuser.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒", "请先填写学号");
                } else {
                    ProgressDialogHelper.showProgressDialog(LoginActivity.this, "正在加载...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fetchSafecodeImageHttpRequest();
                        }
                    }).start();
                }
            }
        });
    }

    private void fetchSafecodeImageHttpRequest() {

        SafeCodeUtil.getLoginCookie(user, new CallBack() {
            @Override
            public void onStart() {

                final Bitmap codemap = SafeCodeUtil.getSafeCodePic();  //获取验证码图片

                setImage(codemap);//设置验证码到界面上

            }

            @Override
            public void onFinsh(String response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                        AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒", "请确保在校园网环境使用，或者账号密码正确");
                    }
                });

            }
        });
    }

    private void setImage(final Bitmap codemap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialogHelper.closeProgressDialog();
                codeimg.setImageBitmap(codemap);
                codeimg.setVisibility(View.VISIBLE);
                safecodebtn.setVisibility(View.GONE);
                Log.d("TAG", "image over");
            }
        });
    }

    private void setLoginBtnListener() {
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = loginuser.getText().toString();
                pass = loginpass.getText().toString();
                passport = passported.getText().toString();
                saveUserAndPass();
            }
        });
    }

    private void initView() {
        loginuser= (EditText) findViewById(R.id.loginid_et);
        loginpass= (EditText) findViewById(R.id.loginpswd_et);
        passported=(EditText) findViewById(R.id.safecode_et);
        loginbtn= (Button) findViewById(R.id.login_ok_btn);
        safecodebtn=(Button) findViewById(R.id.safecode_btn);
        codeimg= (ImageView) findViewById(R.id.codeimg);
    }

    private void isCookieGet() {
        if (prefs==null){
            prefs= SharedPreferencesHelper.getSharePreferences(this);
        }
        if (prefs.getBoolean("cookie_OK", false)) {
            HttpUtil.cookie=prefs.getString("cookie",null);
            HttpUtil.userName=prefs.getString("username",null);
            HttpUtil.yourName=prefs.getString("yourname",null);
            if(TextUtils.isEmpty(HttpUtil.yourName))
                return;
            Intent intent = new Intent(this, DrawerLayoutActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private String user="";
    private String pass="";
    private String passport="";

    private void saveUserAndPass() {


        if(TextUtils.isEmpty(user)||TextUtils.isEmpty(pass)||TextUtils.isEmpty(passport)){
            AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒", "请填写完整的学号,密码和验证码");
        }
        else{

            HttpUtil.userName=user;
            HttpUtil.password=pass;
            HttpUtil.passport=passport;

            login();
        }
    }

    private void login() {
        ProgressDialogHelper.showProgressDialog(this, "正在登陆...");
        HttpUtil.login(new LoginCallback(){

            @Override
            public void beforeStart() {

            }

            @Override
            public void onLoginFinshed() {
                saveCookieAndStartMainActivity();
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                        AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒", "登陆失败,请确保账号密码输入正确,和在校园网下登陆");
                    }
                });

            }
        });


    }

    private void saveCookieAndStartMainActivity() {
        saveCookie();
        Log.d("TAG", "获取cookie成功");
        ProgressDialogHelper.closeProgressDialog();
        Intent intent = new Intent(LoginActivity.this, DrawerLayoutActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveCookie(){
        if (prefs==null){
            prefs= SharedPreferencesHelper.getSharePreferences(this);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cookie_OK", true);
        editor.putString("cookie", HttpUtil.cookie);
        editor.putString("username",HttpUtil.userName);
        editor.putString("yourname",HttpUtil.yourName);
        editor.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
