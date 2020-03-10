package it.imp.lucenteCantieri.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.utils.Constants;

public class SegnalazioneWorker extends Worker {
    public SegnalazioneWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Long idSegnalazione = getInputData().getLong(Constants.ID_SEGNALAZIONE , -1L);

        AppService service = AppService.getInstance(getApplicationContext());

        try {
            service.sendSegnalazione(idSegnalazione);
        } catch (IOException e) {
            return Result.retry();
        }


        return Result.success();
    }
}
