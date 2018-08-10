package top.jplayer.quick_test.ui.activity;

import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import top.jplayer.baseprolibrary.ui.activity.CommonToolBarActivity;
import top.jplayer.baseprolibrary.utils.ActivityUtils;
import top.jplayer.quick_test.R;

/**
 * Created by Obl on 2018/8/8.
 * top.jplayer.quick_test.ui.activity
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */

public class UIActivity extends CommonToolBarActivity {


    @BindView(R.id.btn01)
    Button mBtn01;
    private Unbinder mUnbinder;

    @Override
    public int initAddLayout() {
        return R.layout.activity_ui;
    }

    @Override
    public void initAddView(FrameLayout rootView) {
        super.initAddView(rootView);
        mUnbinder = ButterKnife.bind(this, rootView);
        mBtn01.setOnClickListener(v -> ActivityUtils.init().start(this, LoginGuideActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

}