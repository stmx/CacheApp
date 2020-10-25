package com.stmlab.android.cacheapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.stmlab.android.cacheapp.database.TrefleSchema;

public class DeleteDataFromTableWorker extends Worker {

    public DeleteDataFromTableWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        getApplicationContext().getContentResolver().delete(TrefleSchema.TrefleEntry.CONTENT_URI, null, null);
        return Result.success();
    }
}
