package it.imp.lucenteCantieri.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.utils.Constants;

public class UploadWorker extends Worker {
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Long idTaskCantiere = getInputData().getLong(Constants.ID_TASK_CANTIERE , -1L);
        String note = getInputData().getString(Constants.NOTE);

        AppService service = AppService.getInstance(getApplicationContext());

        try {
            service.sendTaskCantiere(idTaskCantiere, note);
        } catch (IOException e) {
            return Result.retry();
        }


        return Result.success();
    }
}
