package three.com.materialdesignexample.Framgment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;

import java.util.ArrayList;

import three.com.materialdesignexample.Activity.SearchActivity;
import three.com.materialdesignexample.Adapter.PhoneAdapter;
import three.com.materialdesignexample.CallBack;
import three.com.materialdesignexample.Db.Db;
import three.com.materialdesignexample.Models.PhoneInfo;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HandleResponseUtil;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.widget.ProgressDialogHelper;

/**
 * Created by Administrator on 2015/10/27.
 */
public class PhoneFramgment extends android.support.v4.app.Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Button import_btn;
    private ListView phone_lv;
    private ArrayList<PhoneInfo> phoneInfos =new ArrayList<PhoneInfo>();
    private PhoneAdapter phoneAdapter;
    private LinearLayout emptyLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.phone_framgment,null);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        import_btn= (Button) view.findViewById(R.id.import_btn);
        phone_lv= (ListView) view.findViewById(R.id.phone_lv);
        phoneAdapter=new PhoneAdapter(getActivity(),phoneInfos);

        swipeRefreshLayout.setColorSchemeColors(R.color.mainColor);
        emptyLayout= (LinearLayout) view.findViewById(R.id.empty_layout);


        setHasOptionsMenu(true);

        findFromDb();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findFromHttp();
            }
        });

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogHelper.showProgressDialog(getActivity(), "正在载入...");
                findFromHttp();
            }
        });

        phone_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = ((PhoneInfo) parent.getItemAtPosition(position)).getPhoneNumber();
                Log.d("tag", number);
                String flag = number.substring(0, 2);
                if (!TextUtils.isEmpty(flag) && !flag.equals("  ")) {
                    showDeleteDialog(getActivity(), number);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("善意的提醒")
                            .setPositiveButton("确定", null)
                            .setMessage("未查询到电话号码")
                            .show();
                }
            }
        });
        return view;
    }

    public void showDeleteDialog(Context context, final String number) {
        android.app.AlertDialog alertDialog = null;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("是否要拨打电话?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Uri uri = Uri.parse("tel:"+number);
                        Intent it = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(it);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void findFromHttp() {
        HttpUtil.getHtmlUtil(getActivity(), "http://Xg.ndky.edu.cn/android/GetAllstudenttelephone.aspx", new CallBack() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinsh(final String response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HandleResponseUtil.handlePhoneHtmlStr(response, new CallBack() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onFinsh(String response) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initView();
                                                ProgressDialogHelper.closeProgressDialog();
                                            }
                                        });
                                    }
                                }, phoneInfos);
                            }
                        }).start();
                    }
                },
                Request.Method.GET, null);
    }

    private void initView(){
        Log.d("TAG","initview");
        Log.d("TAG",phoneInfos.get(0).getClassName().toString());
        phoneAdapter.notifyDataSetChanged();
        phone_lv.setAdapter(phoneAdapter);
        phone_lv.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }


    private void findFromDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(phoneInfos.size()==0){
                    if(HandleResponseUtil.db==null){
                        HandleResponseUtil.db= Db.getInstance(getActivity());
                    }
                    if(HandleResponseUtil.db!=null){

                        HandleResponseUtil.db.loadPhoneInfo(phoneInfos, new CallBack() {
                            @Override
                            public void onStart() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                            }
                                        });
                                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                                        emptyLayout.setVisibility(View.GONE);
                                    }
                                });
                            }

                            @Override
                            public void onFinsh(String response) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initView();
                                    }
                                });
                            }
                        });
                    }
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                }
            }
        }).start();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.phone_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if(id == R.id.action_search) {
            SearchActivity.startSearchStatusActivity(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
