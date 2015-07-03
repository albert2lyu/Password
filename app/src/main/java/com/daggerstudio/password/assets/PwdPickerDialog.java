package com.daggerstudio.password.assets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daggerstudio.password.R;
import com.daggerstudio.password.utils.PasswordUtil;

/**
 * Created by alex on 15/5/28.
 */
public class PwdPickerDialog{
    private static final String MAIN_PWD_TAG = "MAIN_PWD_TAG";
    private static final String PWD_CHANGE_SUCCESS_MSG = "密码胜利修改";
    private static final String PWD_CHANGE_FAIL_MSG = "密码未修改";

    private EditText et;
    private Context context;
    private SharedPreferences sp;

    public PwdPickerDialog(Context context, final SharedPreferences sp){
        this.context = context;
        this.sp = sp;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入密码");
        RelativeLayout rl = (RelativeLayout) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pwd_picker_layout, null);
        builder.setView(rl);
        et = (EditText)rl.findViewById(R.id.et_pwd_picker);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pwd = et.getText().toString();
                if(null == pwd || "".equals(pwd)){
                    Toast.makeText(context, PWD_CHANGE_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
                    return;
                }
                sp.edit().putString(MAIN_PWD_TAG, PasswordUtil.sha1ThenBase64Password(pwd)).commit();
                Toast.makeText(context, PWD_CHANGE_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}