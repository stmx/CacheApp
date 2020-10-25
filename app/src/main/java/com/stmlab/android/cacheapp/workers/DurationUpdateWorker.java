package com.stmlab.android.cacheapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.stmlab.android.cacheapp.MainActivity;

public class DurationUpdateWorker extends Worker {
    public DurationUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MainActivity.activityStarted = true;
        return Result.success();
    }

}
