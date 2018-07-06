package top.jplayer.commonmodule;

import android.app.Application;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import top.jplayer.baseprolibrary.BaseInitApplication;
import top.jplayer.baseprolibrary.ui.SuperBaseActivity;

/**
 * Created by Obl on 2018/6/29.
 * top.jplayer.commonmodule
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 * 自主扩展 BaseInitApplication 以外的功能，如初始化融云，地图的等等
 */

public class CommonInit {
    public static WeakReference<Application> mWeakReference;
    private static CommonInit mInit;

    private CommonInit(Application application) {
        mWeakReference = new WeakReference<>(application);
    }

    public static List<SuperBaseActivity> mActivityList;


    public synchronized static CommonInit create(Application application) {
        if (mInit == null) {

            synchronized (CommonInit.class) {
                if (mInit == null) {
                    mActivityList = new ArrayList<>();
                    mInit = new CommonInit(application);
                }
            }
        }
        return mInit;
    }

    public void init() {
        BaseInitApplication.with(mWeakReference.get())
                .addUrl("test_json", "http://www.wanandroid.com/")
                .retrofit()
                .zxing()
                .swipeBack();
    }
}
