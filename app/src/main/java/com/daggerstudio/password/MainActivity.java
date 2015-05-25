package com.daggerstudio.password;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daggerstudio.dao.DaoMaster;
import com.daggerstudio.dao.DaoSession;
import com.daggerstudio.dao.Rec;
import com.daggerstudio.dao.RecDao;

import org.w3c.dom.Text;

import java.util.List;

import de.greenrobot.dao.query.WhereCondition;


public class MainActivity extends ActionBarActivity {

    private ListView mainListView;
    private Button mainNewBtn;
    private MainListViewAdapter mainAdapter;
    private SharedPreferences sharedPreferences;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private RecDao recDao;
    private List<Rec> allRecs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = (ListView)findViewById(R.id.main_listView);
        mainNewBtn = (Button)findViewById(R.id.main_new_btn);

        //加载SharedPreference和数据库对象
        if (sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        if(daoMaster == null || db == null || recDao == null || daoSession == null){
            db = new DaoMaster.DevOpenHelper(this, "rec-db", null).getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            recDao = daoSession.getRecDao();
        }

        //TODO 事成之后，删掉这段测试数据注入部分
        if(sharedPreferences.getBoolean("firsttime", true)){
            //        public Rec(String site_url, String site_brief, String user_name, byte[] encypted_content, String note, Long id) {
            Rec rec = new Rec("site_url1", "brief1", "usr_name_1", "password1".getBytes(), null, null);
//        rec.setSite_url("site_url1");
//        rec.setUser_name("usr_name_1");
//        rec.setSite_brief("brief1");
//        rec.setEncypted_content("password1".getBytes());
            recDao.insert(rec);
            rec = new Rec("site_url2", "brief2", "usr_name_2", "password2".getBytes(), null, null);
//        rec.setSite_url("site_url2");
//        rec.setUser_name("usr_name_2");
//        rec.setSite_brief("brief2");
//        rec.setEncypted_content("password2".getBytes());
            recDao.insert(rec);
            sharedPreferences.edit().putBoolean("firsttime", false).commit();
        }



        //下面把数据放入内存进行缓存
        //TODO 从数据库读数据部分放入异步线程里！现在先懒得弄
        allRecs = (List<Rec>)recDao.loadAll();

        mainAdapter = new MainListViewAdapter(this);
        mainListView.setAdapter(mainAdapter);
    }


    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }


    private class MainListViewAdapter extends BaseAdapter{

        Context context;

        public MainListViewAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return allRecs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LinearLayout lv = (LinearLayout) LinearLayout.inflate(context, R.layout.card_view_main, null);
                TextView tv = (TextView)lv.findViewById(R.id.main_textview);
                tv.setText(allRecs.get(position).getSite_url());
                lv.setTag(allRecs.get(position).getId());
                lv.setOnClickListener(new MainLvOnClickListener());
                return lv;
            }else{
                TextView tv = (TextView)convertView.findViewById(R.id.main_textview);
                tv.setText(allRecs.get(position).getSite_url());
                return convertView;
            }
        }
    }


    private class MainLvOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "click on "+(v.getTag().toString()), Toast.LENGTH_SHORT).show();
        }
    }


}
