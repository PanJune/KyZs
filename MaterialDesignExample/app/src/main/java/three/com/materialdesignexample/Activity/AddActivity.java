package three.com.materialdesignexample.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import three.com.materialdesignexample.Db.DbOpenHelper;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.widget.ProgressDialogHelper;

/**
 * Created by Administrator on 2015/10/26.
 */
public class AddActivity extends AppCompatActivity {
    private EditText content_et;
    private AVObject editObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        content_et= (EditText) findViewById(R.id.content_et);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("你的状态");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar   if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send) {
            hideSoftKeyboard(this,content_et);
            uploadStatus(AddActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 隐藏软键盘
     * @param context
     * @param view
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 上传数据
     *
     * @param context
     */

    private void uploadStatus(final Context context) {

        if (TextUtils.isEmpty(content_et.getText().toString().trim())) {
            Toast.makeText(context, "内容不可为空", Toast.LENGTH_SHORT).show();

            return;
        }
        ProgressDialogHelper.showProgressDialog(context, "发送中...");

        AVObject avObject = editObject;
        if(avObject == null) {
            avObject = new AVObject(DbOpenHelper.STATUS_TABLE);
        }

        //内容
        avObject.put(DbOpenHelper.STATUS_CONTETT, content_et.getText().toString().trim());
        //发布人(学生)
        avObject.put(DbOpenHelper.STATUS_USER, HttpUtil.userName);


        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                ProgressDialogHelper.closeProgressDialog();
                if (e == null) {
                    Toast.makeText(context, "哇，成功了", Toast.LENGTH_SHORT).show();
                    AddActivity.this.finish();
                    Log.d("winson", "上传对象成功");
                } else {
                    Toast.makeText(context, "唉，失败了，再试试", Toast.LENGTH_SHORT).show();
                    Log.d("winson", "上传对象失败：" + e.getMessage());
                }
            }
        });

    }


}
