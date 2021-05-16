package com.zj.libcommon.callback;

import android.content.Context;
import android.view.View;

import com.kingja.loadsir.callback.Callback;
import com.zj.libcommon.R;

public class LoadingCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.layout_loading;
    }


    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }
}
