package three.com.materialdesignexample.Framgment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;

import java.util.ArrayList;

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



        findFromDb();

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.getHtmlUtil(getActivity(), "http://Xg.ndky.edu.cn/android/GetAllstudenttelephone.aspx", new CallBack() {
                            @Override
                            public void onStart() {
                                ProgressDialogHelper.showProgressDialog(getActivity(),"正在载入...");
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
                                                        ProgressDialogHelper.closeProgressDialog();
                                                        initView();
                                                    }
                                                });
                                            }
                                        },phoneInfos);
                                    }
                                }).start();
                            }
                        },
                        Request.Method.GET, null);
            }
        });

        return view;
    }

    private void initView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                phoneAdapter.notifyDataSetChanged();
                phone_lv.setAdapter(phoneAdapter);
                phone_lv.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

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
}
