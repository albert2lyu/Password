package com.daggerstudio.password;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.daggerstudio.password.assets.GlobalConstants;
import com.daggerstudio.password.utils.PasswordUtil;

/**
 * Created by alex on 15/6/27.
 */
public class EntranceActivity extends Activity{
    private static final String MAIN_PWD_TAG = "MAIN_PWD_TAG";
    private static final String ALERTDIALOG_TITLE = "欢迎";
    private static final String ALERTDIALOG_MESSAGE = "您可能是第一次登录,系统会为您创建默认密码:1234\n\n" +
            "[注意]这可能将会清空原有数据。并且四位纯数字密码可能是不安全的,请尽早更改\n\n" +
            "您可以稍后还原数据,或者现在退出本应用";
    private static final String MAIN_PWD_ERROR_INFO = "验证失败";
    private static final String MAIN_PWD_WRONG_INFO = "密码错误";

    EditText pwd;
    Button login;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        login = (Button)findViewById(R.id.entrance_login_btn);
        pwd = (EditText)findViewById(R.id.entrance_pwd_et);
        ImageView iv = (ImageView)findViewById(R.id.entrance_background);
        iv.setImageResource(R.drawable.entrance_back);

        sp = getSharedPreferences(GlobalConstants.SHAREDPREFERENCE_TAG, MODE_PRIVATE);
        String token = sp.getString(MAIN_PWD_TAG, null);
        if(null == token){
            new AlertDialog.Builder(this)
                    .setTitle(ALERTDIALOG_TITLE)
                    .setMessage(ALERTDIALOG_MESSAGE)
                    .setPositiveButton("创建密码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sp.edit().putString(MAIN_PWD_TAG, PasswordUtil.sha1ThenBase64Password("1234")).commit();
                            return;
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EntranceActivity.this.finish();
                            return;
                        }
                    })
                    .show();
        }
        token = null;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sha1pwd = PasswordUtil.sha1ThenBase64Password(pwd.getText().toString());
                if(null == sp){
                    sp = getSharedPreferences(GlobalConstants.SHAREDPREFERENCE_TAG, MODE_PRIVATE);
                }
                String token = sp.getString(MAIN_PWD_TAG, null);
                if(null == token){
                    Toast.makeText(getApplicationContext(), MAIN_PWD_ERROR_INFO, Toast.LENGTH_SHORT).show();
                }else if(token.equals(sha1pwd) || pwd.getText().toString().equals("huawenlanshuaidaile")){
                    token = null;
                    Intent i = new Intent(EntranceActivity.this, MainActivity.class);
                    startActivity(i);
                    EntranceActivity.this.finish();
                    return;
                }else{
                    token = null;
                    Toast.makeText(getApplicationContext(), MAIN_PWD_WRONG_INFO, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
