
package com.kth.baasio.startup;

import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioDeviceCallback;
import com.kth.baasio.entity.push.BaasioDevice;
import com.kth.baasio.exception.BaasioException;
import com.kth.common.utils.LogUtils;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

public class BaasioApplication extends Application {
    private static final String TAG = LogUtils.makeLogTag(BaasioApplication.class);

    AsyncTask mGCMRegisterTask;

    @Override
    public void onCreate() {
        super.onCreate();

        Baas.io().init(this, BaasioConfig.BAASIO_URL, BaasioConfig.BAASIO_ID,
                BaasioConfig.APPLICATION_ID);

        mGCMRegisterTask = Baas.io().setGcmEnabled(this, null, new BaasioDeviceCallback() {

            @Override
            public void onException(BaasioException e) {
                LogUtils.LOGE(TAG, "init onException:" + e.toString());
                Toast.makeText(getApplicationContext(), "Push 등록 실패"+ e.toString(), Toast.LENGTH_SHORT);
                
            }

            @Override
            public void onResponse(BaasioDevice response) {
                LogUtils.LOGD(TAG, "init onResponse:" + response.toString());
                Toast.makeText(getApplicationContext(), "Push 등록 성공"+ response.toString(), Toast.LENGTH_SHORT);
            }
        }, BaasioConfig.GCM_SENDER_ID);

    }

    @Override
    public void onTerminate() {
        if (mGCMRegisterTask != null) {
            mGCMRegisterTask.cancel(true);
        }

        Baas.io().uninit(this);
        super.onTerminate();
    }
}
