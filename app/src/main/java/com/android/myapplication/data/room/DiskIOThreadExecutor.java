package com.android.myapplication.data.room;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.annotations.NonNull;

/**
 * on 2020/4/20
 * 只开启一个核心线程，依次执行，不存在并发问题
 */
public class DiskIOThreadExecutor implements Executor {
    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        this.mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}
