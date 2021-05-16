package com.zj.libcommon.callback;

import com.kingja.loadsir.callback.Callback;
import com.zj.libcommon.R;

public class EmptyCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.layout_empty;
    }

}
