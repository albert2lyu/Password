package com.daggerstudio.password.assets;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.daggerstudio.password.dao.DaoMaster;
import com.daggerstudio.password.dao.DaoSession;
import com.daggerstudio.password.dao.RecDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by alex on 15/5/29.
 */
public class ProgressDialogAsyncTaskForBackup extends AsyncTask {

    private ProgressDialog pd;

    /**
     *
     * @param params Fill Params with Context, FileName, ProgressBar, handler
     * @return return true if progress success
     */
    @Override
    protected Boolean doInBackground(Object[] params) {
        Context context = (Context) params[0];
        File backupFile = (File) params[1];
        this.pd = (ProgressDialog) params[2];
        Handler handler = (Handler) params[3];
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context, "rec-db", null).getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        RecDao recDao = daoSession.getRecDao();
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            Object[] allRec = recDao.loadAll().toArray();
            fos = new FileOutputStream(backupFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(allRec);
            oos.flush();
            fos.close();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_BACKUP_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_BACKUP_STATUS_SUCCESS;
            msg.obj = backupFile.getPath();
            handler.sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_BACKUP_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_BACKUP_STATUS_FAIL;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = GlobalConstants.ASYNCTASK_FROM_BACKUP_TAG;
            msg.arg1 = GlobalConstants.ASYNCTASK_FROM_BACKUP_STATUS_FAIL;
        }finally {
            try {
                if(null != fos) {
                    fos.close();
                }
                if(null != oos){
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}