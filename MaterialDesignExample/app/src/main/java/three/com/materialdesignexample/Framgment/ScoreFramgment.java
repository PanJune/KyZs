package three.com.materialdesignexample.Framgment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import three.com.materialdesignexample.Adapter.ScoreAdapter;
import three.com.materialdesignexample.CallBack;
import three.com.materialdesignexample.Db.Db;
import three.com.materialdesignexample.Models.Score;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HandleResponseUtil;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.widget.ProgressDialogHelper;

/**
 * Created by Administrator on 2015/10/21.
 */
public class ScoreFramgment extends Fragment {

    private LinearLayout emptyLayout ;
    private LinearLayout scoreLayout;
    private View h_v;
    private Button importBtn;
    private ListView scoreLv;
    private ArrayList<Score> scoreData= HandleResponseUtil.scores;
    private TextView allScore_tv;
    private ScoreAdapter scoreAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.score_framgment, null);
        emptyLayout= (LinearLayout) view.findViewById(R.id.empty_layout);
        importBtn= (Button) view.findViewById(R.id.import_btn);
        scoreLv= (ListView) view.findViewById(R.id.score_lv);
        scoreLayout= (LinearLayout) view.findViewById(R.id.score_layout);
        h_v=view.findViewById(R.id.h_v);
        allScore_tv= (TextView) view.findViewById(R.id.allScore_tv);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        scoreAdapter=new ScoreAdapter(getActivity(),scoreData);

        findFromDb();   //先查数据库

        setRefreshListener();

        setImportBtnListener();

        return view;
    }

    private void setImportBtnListener() {
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.getScoreHtml(getActivity(), new CallBack() {
                    @Override
                    public void onStart() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogHelper.showProgressDialog(getActivity(),
                                        "正在导入，这可能需要一点时间...");
                            }
                        });

                    }

                    @Override
                    public void onFinsh(String response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveAllScore();
                                initView();
                                ProgressDialogHelper.closeProgressDialog();
                            }
                        });
                        Log.d("TAG", "handle score OK");
                    }
                });
            }
        });
    }

    private void setRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpUtil.getScoreHtml(getActivity(), new CallBack() {
                    @Override
                    public void onStart() {
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onFinsh(String response) {
                        saveAllScore();
                        initView();
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("TAG", "refresh score OK");
                    }
                });
            }
        });
    }

    private void findFromDb() {
        if(scoreData.size()==0){
            if(HandleResponseUtil.db==null){
                HandleResponseUtil.db= Db.getInstance(getActivity());
            }
            if(HandleResponseUtil.db!=null){
                if(HandleResponseUtil.db.loadScore()){
                    initView();
                }
            }
        }
        else
            initView();
    }

    private void initView(){
        setAllScore();
        scoreAdapter.notifyDataSetChanged();
        scoreLv.setAdapter(scoreAdapter);
        scoreLv.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        scoreLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        h_v.setVisibility(View.VISIBLE);
    }



    private void setAllScore() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        allScore_tv.setText(prefs.getString("allScore",null));
    }

    private void saveAllScore(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("allScore",HandleResponseUtil.allScore);
        editor.commit();
    }
}
