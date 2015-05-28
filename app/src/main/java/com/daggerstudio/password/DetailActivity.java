package com.daggerstudio.password;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daggerstudio.password.dao.Rec;
import com.daggerstudio.password.utils.EncDecUtil;


public class DetailActivity extends ActionBarActivity {
    private static final String REC_BUNDLE_TAG = "REC_BUNDLE_TAG";
    private static final String PARCEL_TAG = "PARCEL_TAG";
    private static final String SHAREDPREFERENCE_TAG = "SHAREDPREFERENCE_TAG";
    private static final String MAIN_PWD_TAG = "MAIN_PWD_TAG";
    private static final String CLIPBOARD_PWD_LABEL = "CLIPBOARD_PWD_LABEL";

    //TODO 修改字符常量
    private static final String ERR_MSG_1 = "啊哦,找不到信息😢";
    private static final String MSG1 = "请先登录并设置主密码";
    private static final String SHOW_BTN_TAG_SHOW = "显示密码";
    private static final String SHOW_BTN_TAG_HIDE = "隐藏密码";
    private static final String COPY_TOAST_MSG = "密码已复制到剪贴板";


    TextView brief;
    TextView usrName;
    TextView pwd;
    TextView url;
    TextView note;
    Button copy;
    Button show;
    Button edit;
    Rec recTrans;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        brief = (TextView)findViewById(R.id.detail_tv_brief);
        usrName = (TextView)findViewById(R.id.detail_tv_usrname);
        pwd = (TextView)findViewById(R.id.detail_tv_pwd);
        url = (TextView)findViewById(R.id.detail_tv_url);
        note = (TextView)findViewById(R.id.detail_tv_note);
        copy = (Button)findViewById(R.id.detail_btn_copy);
        copy.setOnClickListener(new CopyOnClickListener());
        show = (Button)findViewById(R.id.detail_btn_show);
        show.setOnClickListener(new ShowOnClickListener());
        edit = (Button)findViewById(R.id.detail_btn_edit);
        edit.setOnClickListener(new EditOnClickListener());
        //加载控件


        //读取传入信息,如果没拿到就卖个萌然后退出
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            recTrans = bundle.getParcelable(REC_BUNDLE_TAG);
            if(null == recTrans){
                Toast.makeText(this, ERR_MSG_1, Toast.LENGTH_SHORT).show();
                DetailActivity.this.finish();
                return;
            }
        }else{
            Toast.makeText(this, ERR_MSG_1, Toast.LENGTH_SHORT).show();
            DetailActivity.this.finish();
            return;
        }


        //加载数据
        sp = getSharedPreferences(SHAREDPREFERENCE_TAG, MODE_PRIVATE);

        String tmp;
        tmp = recTrans.getSite_brief();
        brief.setText(null == tmp ? "" : tmp);
        tmp = recTrans.getUser_name();
        usrName.setText(null == tmp ? "" : tmp);
        tmp = recTrans.getSite_url();
        url.setText(null == tmp ? "" : tmp);
        tmp = recTrans.getNote();
        note.setText(null == tmp ? "" : tmp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(intent);
            this.finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private class CopyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String tmp = sp.getString(MAIN_PWD_TAG, "");
            if("".equals(tmp)){
                Toast.makeText(getApplicationContext(), MSG1, Toast.LENGTH_SHORT).show();
                DetailActivity.this.finish();
            }else{
                String pwdStr = EncDecUtil.decrypt(recTrans.getEncypted_content(), tmp);
                ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData cp = ClipData.newPlainText(CLIPBOARD_PWD_LABEL, pwdStr);
                cm.setPrimaryClip(cp);
                Toast.makeText(getApplicationContext(), COPY_TOAST_MSG, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ShowOnClickListener implements View.OnClickListener{
        boolean hidden = true;

        @Override
        public void onClick(View v) {
            String tmp = sp.getString(MAIN_PWD_TAG, "");
            if("".equals(tmp)){
                Toast.makeText(getApplicationContext(), MSG1, Toast.LENGTH_SHORT).show();
                DetailActivity.this.finish();
            }else{
                if(hidden) {
                    String pwdStr = EncDecUtil.decrypt(recTrans.getEncypted_content(), tmp);
                    pwd.setText(pwdStr);
                    show.setText(SHOW_BTN_TAG_HIDE);
                    hidden = !hidden;
                }else{
                    pwd.setText("**********");
                    show.setText(SHOW_BTN_TAG_SHOW);
                    hidden = !hidden;
                }
            }
        }
    }


    private class EditOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(REC_BUNDLE_TAG, recTrans);
            Intent intent = new Intent(DetailActivity.this, EditActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            DetailActivity.this.finish();
        }
    }
}
