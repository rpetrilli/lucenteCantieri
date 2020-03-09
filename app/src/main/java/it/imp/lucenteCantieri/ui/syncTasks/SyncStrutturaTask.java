package it.imp.lucenteCantieri.ui.syncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.AppService;

public class SyncStrutturaTask extends AsyncTask<String, Integer, String> {
    static private final int ITERATORS = 10;

    private ProgressDialog mProgress = null;
    private Context mContext = null;

    public SyncStrutturaTask(Context context){
        this.mContext = context;
    }


    @Override
    protected void onPreExecute() {
        mProgress =  new ProgressDialog(mContext);
        mProgress.setMessage(mContext.getString(R.string.msg_attendere_prego));
        mProgress.setTitle(R.string.action_get_struttura);

        mProgress.setCancelable(true);
        mProgress.setOnCancelListener(dialog-> cancel(true));

        mProgress.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            AppService.getInstance(mContext).downloadStruttura();
            return null;
        } catch (IOException e) {
            Log.e("SyncStrutturaTask", e.getMessage() );
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        mProgress.dismiss();
        if (StringUtils.isNotEmpty(errorMessage)) {
            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).showErrorMessage(errorMessage);
            }
        }
    }


}
