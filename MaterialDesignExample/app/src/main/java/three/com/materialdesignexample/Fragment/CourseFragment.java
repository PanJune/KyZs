package three.com.materialdesignexample.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Calendar;
import java.util.List;

import three.com.materialdesignexample.Activity.LoginActivity;
import three.com.materialdesignexample.Adapter.CourseAdapter;
import three.com.materialdesignexample.CallBack;
import three.com.materialdesignexample.Db.Db;
import three.com.materialdesignexample.Models.Course;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HandleResponseUtil;
import three.com.materialdesignexample.Util.HttpUtil;
import three.com.materialdesignexample.widget.ProgressDialogHelper;
import three.com.materialdesignexample.widget.SharedPreferencesHelper;


public class CourseFragment extends Fragment {

    private ViewPager pager=null;
    private PagerSlidingTabStrip tabs=null;
    private ViewPagerAdapter vpAdapter=null;
    private Button requestCourse=null;
    private List<List<Course>> data= HandleResponseUtil.courseData;
    private LinearLayout emptyLayout=null;
    private SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_framgment, null);
        initView(view);

        findFromDb();

        setImprotCourseBtnListener();

        return view;
    }

    private void setImprotCourseBtnListener() {
        requestCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.getCourseHtml(getActivity(), new CallBack() {
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

                                initViewPage();
                                ProgressDialogHelper.closeProgressDialog();
                            }
                        });
                        Log.d("TAG", "handle course OK");
                    }
                });
            }
        });
    }

    private void initView(View view) {
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tab_indicator);
        requestCourse= (Button) view.findViewById(R.id.import_btn);
        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_layout);

        setHasOptionsMenu(true);
    }

    private void findFromDb() {
        if(data.size()==0){
            if(HandleResponseUtil.db==null){
                HandleResponseUtil.db= Db.getInstance(getActivity());
            }
            if(HandleResponseUtil.db!=null){
                if(HandleResponseUtil.db.loadCourse()){
                    initViewPage();
                }
            }
        }
        else
            initViewPage();
    }

    private void initViewPage(){
        //ViewPager init
        vpAdapter= new ViewPagerAdapter(getActivity(),data);

        pager.setAdapter(vpAdapter);
        // Bind the tabs to the ViewPager
        tabs.setViewPager(pager);

        tabs.setVisibility(View.VISIBLE);
        pager.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);

        //根据星期几选定课程表
        whatDayIs();

    }

    //根据星期几选定课程表
    private void whatDayIs() {
        Calendar calendar = Calendar.getInstance();
        int date = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                date = 0;
                break;
            case Calendar.TUESDAY:
                date = 1;
                break;
            case Calendar.WEDNESDAY:
                date = 2;
                break;
            case Calendar.THURSDAY:
                date = 3;
                break;
            case Calendar.FRIDAY:
                date = 4;
                break;
            case Calendar.SATURDAY:
                date = 5;
                break;
            case Calendar.SUNDAY:
                date = 6;
                break;
        }
        pager.setCurrentItem(date);
    }

    public static CourseAdapter courseAdapter;

    private ListView getDateListView(Context context, List<Course> list) {

        courseAdapter=new CourseAdapter(context, list);
        ListView coursesList = new ListView(context);
        coursesList.setDivider(getResources().getDrawable(android.R.color.transparent));
        coursesList.setDividerHeight(0);
        coursesList.setAdapter(courseAdapter);

        return coursesList;
    }


    private class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private List<List<Course>> data;

        private ViewPagerAdapter(Context context, List<List<Course>> data) {
            this.context = context;
            this.data = data;

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "星期一";
            } else if (position == 1) {
                return "星期二";
            }
            else if (position == 2) {
                return "星期三";
            } else if (position == 3) {
                return "星期四";
            }
            else if (position == 4) {
                return "星期五";
            } else if (position == 5) {
                return "星期六";
            } else {
                return "星期天";
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if( data.get(position).size() < 1) {
                TextView textView = (TextView) View.inflate(context, R.layout.course_empty_textview, null);
                container.addView(textView);
                return textView;
            }
            else{
                ListView listView = getDateListView(context, data.get(position));
                container.addView(listView);
                return listView;
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.logout_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if(id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        if(sharedPreferences==null){
            sharedPreferences= SharedPreferencesHelper.getSharePreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
        }
    }
}
