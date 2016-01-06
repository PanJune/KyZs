package three.com.materialdesignexample.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import three.com.materialdesignexample.Adapter.CommentAdapter;
import three.com.materialdesignexample.Db.DbOpenHelper;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.widget.ProgressDialogHelper;
import three.com.materialdesignexample.widget.TimeHelper;

/**
 * Created by Administrator on 2015/10/30.
 */
public class StatusActivity extends BaseActivity {

    private  static String statusID;
    private ListView listView;
    private EditText commentEt;
    private Button sendBtn;
    private View headerView;
    private TextView nameTv;
    private TextView dateTv;
    private TextView contentTv;
    private LinearLayout imageLayout;
    private TextView commentBtn;
    private CommentAdapter commentAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View footerView;
    private AVObject staObject=null;

    public static void startStatusActivity(Context context, String objectId) {
        Intent intent = new Intent(context, StatusActivity.class);
        statusID=objectId;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        initView();

        initStatusData();
        initCommentData();
    }

    private void initView() {
        actionBar.setTitle("帖子");

        listView = (ListView) findViewById(R.id.comment_listView);
        commentEt = (EditText) findViewById(R.id.comment_et);
        sendBtn = (Button) findViewById(R.id.comment_send_btn);

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.status_swipe_container);

        setSwipeRefreshListener();

        //headerView
        headerView = View.inflate(this, R.layout.status_hearder, null);
        nameTv = (TextView) headerView.findViewById(R.id.status_name_tv);
        dateTv = (TextView) headerView.findViewById(R.id.status_time_tv);
        contentTv = (TextView) headerView.findViewById(R.id.status_content_tv);
        imageLayout = (LinearLayout) headerView.findViewById(R.id.status_image_layout);
        commentBtn = (TextView) headerView.findViewById(R.id.status_comment_btn);
        headerView.setVisibility(View.GONE);

        //FootView
        footerView = View.inflate(this, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);

        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);

        listView.addHeaderView(headerView, null, false);

        commentAdapter = new CommentAdapter(this, data);
        listView.setAdapter(commentAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionDialog(StatusActivity.this, data.get((int) id));
            }
        });
    }

    private void setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initStatusData();
                findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 显示选项dialog
     */
    private void showOptionDialog(final Context context, final AVObject avComment) {

        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] optionArray = null;

        //若为本人
        if (HttpUtil.yourName.equals(avComment.getString(DbOpenHelper.COMMENT_SENDUSER))) {
            optionArray = getResources().getStringArray(R.array.comment_option_0);
        }
        //若为普通用户
        else {
            optionArray = getResources().getStringArray(R.array.comment_option_1);
        }

        builder.setItems(optionArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //回复
                if (which == 0) {
                    receiveStudent = avComment.getString(DbOpenHelper.COMMENT_SENDUSER);
                    commentEt.setHint("回复 " + receiveStudent + " 的评论");
                }
                //删除
                else {
                    showDeleteDialog(context, avComment, new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                footerView.setVisibility(View.GONE);
                                Toast.makeText(StatusActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                addCommentCount(false);

                            } else {
                                Toast.makeText(StatusActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                Log.d("winson", "删除失败：" + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 显示删除dialog
     * @param context
     * @param avComment
     */
    public static void showDeleteDialog(Context context, final AVObject avComment, final DeleteCallback callback) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.deleteMsg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        avComment.deleteInBackground(callback);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void initStatusData(){
        findByObjectId(AVQuery.CachePolicy.CACHE_THEN_NETWORK, new GetCallback<AVObject>() {

            @Override
            public void done(AVObject avObject, AVException e) {

                if (e == null) {
                    staObject = avObject;
                    Log.d("winson", "更新帖子数据");
                    //名字
                    nameTv.setText(staObject.getString(DbOpenHelper.STATUS_USER));
                    //时间
                    dateTv.setText(TimeHelper.timeToFriendlyTime(staObject.getCreatedAt().toString()));
                    //内容
                    contentTv.setText(staObject.getString(DbOpenHelper.STATUS_CONTETT));
                    //评论
                    commentBtn.setText(staObject.getLong(DbOpenHelper.STATUS_COUNT) + "");
                    //显示headerView
                    headerView.setVisibility(View.VISIBLE);
                } else {
                    //帖子已被删除
                    if (e.getMessage().contains("java.lang.IndexOutOfBoundsException")) {
                        Toast.makeText(StatusActivity.this, "帖子应该已经被删除了，找不到它了", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("winson", "加载出错：" + e.getMessage());
                }

            }
        });
    }

    public void initCommentData(){

        //发送评论
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = commentEt.getText().toString().trim();
                hideSoftKeyboard(StatusActivity.this, sendBtn);
                if (TextUtils.isEmpty(commentStr)) {
                    Toast.makeText(StatusActivity.this, "评论不可以是空的哦", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment(commentStr);
                }
            }
        });

        findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
    }

    //接收回复者
    private String receiveStudent=null;

    private void sendComment(String commentStr) {
        ProgressDialogHelper.showProgressDialog(this,"正在发送...");
        AVObject avComment = new AVObject(DbOpenHelper.COMMENT_TABLE);
        //存帖子
        avComment.put(DbOpenHelper.COMMENT_STATUS, AVObject.createWithoutData(DbOpenHelper.STATUS_TABLE,
                statusID));
        //发送者
        avComment.put(DbOpenHelper.COMMENT_SENDUSER, HttpUtil.yourName);
        //接收者
        if (receiveStudent != null) {
            avComment.put(DbOpenHelper.COMMENT_RECEIVE_USER, receiveStudent);
        }
        //内容
        avComment.put(DbOpenHelper.COMMENT_CONTETT, commentStr);

        avComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                ProgressDialogHelper.closeProgressDialog();
                if (e != null) {
                    Log.d("winson", e.getMessage());
                    Toast.makeText(StatusActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StatusActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    //清空editText
                    commentEt.setHint("");
                    commentEt.setText("");
                    receiveStudent = null;

                    addCommentCount(true);
                }
            }
        });
    }

    private void addCommentCount(boolean isadded) {
        if(isadded){
            staObject.increment(DbOpenHelper.STATUS_COUNT);
            staObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    initStatusData();
                    findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
                }
            });

        }
        else {
            staObject.increment(DbOpenHelper.STATUS_COUNT, -1);
            staObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    initStatusData();
                    findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
                }
            });

        }
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void findNewData(AVQuery.CachePolicy cachePolicy) {
        swipeRefreshLayout.setRefreshing(true);

        findNewDataByObjectId(cachePolicy, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson,findNewData", list.size() + "个");

                    notifyDataSetChanged(list, true);

                } else {
                    if (!e.getMessage().contains("Cache"))
                        Toast.makeText(StatusActivity.this, "你的网络好像有点问题，刷新试试吧", Toast.LENGTH_SHORT).show();
                    Log.d("winson", "出错：" + e.getMessage());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    public void findNewDataByObjectId(AVQuery.CachePolicy cachePolicy,FindCallback callback) {

        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(DbOpenHelper.COMMENT_TABLE);

        avQuery.whereEqualTo(DbOpenHelper.COMMENT_STATUS, AVObject.createWithoutData(DbOpenHelper.STATUS_TABLE, statusID));
        //id降序
        avQuery.orderByAscending("createdAt");


        avQuery.setCachePolicy(cachePolicy);
        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10)条

        avQuery.findInBackground(callback);

    }


    public void findByObjectId(AVQuery.CachePolicy cachePolicy,GetCallback<AVObject> callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(DbOpenHelper.STATUS_TABLE);
        avQuery.setCachePolicy(cachePolicy);

        avQuery.getInBackground(statusID, callback);

    }

    private void notifyDataSetChanged(List<AVObject> list, boolean isRefresh) {
        if (isRefresh) {
            data.clear();
        }
        data.addAll(list);
        commentAdapter.notifyDataSetChanged();

    }
}
