package com.otaTest.Service;

import android.app.Activity;

import androidx.annotation.Nullable;


import com.otaTest.MainActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {
    @Nullable
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return MainActivity.class;
    }
}
