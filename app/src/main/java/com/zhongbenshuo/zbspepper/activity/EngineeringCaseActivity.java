package com.zhongbenshuo.zbspepper.activity;

import android.os.Bundle;
import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.constant.BannerResources;
import com.zhongbenshuo.zbspepper.glide.loader.GlideImageLoader;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;
import com.zhongbenshuo.zbspepper.utils.ActivityController;

/**
 * 工程案例页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class EngineeringCaseActivity extends BaseActivity {

    private WakeUpUtil wakeUpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        findViewById(R.id.ivBack).setOnClickListener(onClickListener);

        Banner banner = findViewById(R.id.banner);
        banner.setImages(BannerResources.getEngineeringCaseResources())
                .setBannerStyle(BannerConfig.NUM_INDICATOR)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(8000)
                .start();

    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.ivBack:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化唤醒对象
        wakeUpUtil = WakeUpUtil.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeUpUtil.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeUpUtil.onDestroy();
    }

}