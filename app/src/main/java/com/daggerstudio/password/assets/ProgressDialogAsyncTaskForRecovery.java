package com.daggerstudio.password.assets;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.daggerstudio.password.dao.DaoMaster;
import com.daggerstudio.password.dao.DaoSession;
import com.daggerstudio.password.dao.Rec;
import com.daggerstudio.password.dao.RecDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

/**
 * Created by alex on 15/5/29.
 */
public class ProgressDialogAsyncTaskForRecovery extends AsyncTask {
    private ProgressDialog pd;

    /**
     *
     * @param params Fill Params with Context, FileName, ProgressBar, Handler
     * @return return true if progress success
     */
    @Override
    protected Boolean doInBackground(Object[] params) {
        SQLiteDatabase db = null;
        DaoMaster daoMaster = null;
        DaoSession daoSession = null;
        RecDao recDao = null;
        Context context = (Context) params[0];
        File file = (File) params[1];
        this.pd = (ProgressDialog) params[2];
        Handler handler = (Handler) params[3];
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);

            Object[] allRec = (Object[])ois.readObject();

            if(daoMaster == null || db == null || recDao == null || daoSession == null){
                db = new DaoMaster.DevOpenHelper(context, "rec-db", null).getReadableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                recDao = daoSession.getRecDao();
            }
            for(Object each : allRec){
                recDao.insertOrReplace((Rec)each);
            }
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_SUCCESS;
            handler.sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_FAIL;
            handler.sendMessage(msg);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_FAIL;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_FAIL;
            handler.sendMessage(msg);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_FAIL;
            handler.sendMessage(msg);
        }finally {
            try {
                if(null != fis) {
                    fis.close();
                }
                if(null != ois){
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
