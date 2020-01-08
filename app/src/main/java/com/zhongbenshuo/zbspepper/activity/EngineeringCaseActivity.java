package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.constant.BannerResources;
import com.zhongbenshuo.zbspepper.glide.loader.GlideImageLoader;
import com.zhongbenshuo.zbspepper.utils.ActivityController;

/**
 * 工程案例页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class EngineeringCaseActivity extends BaseActivity {

    private Context mContext;
    private ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        mContext = this;

        ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(onClickListener);

        Banner banner = findViewById(R.id.banner);
        banner.setImages(BannerResources.getEngineeringCaseResources())
                .setBannerStyle(BannerConfig.NUM_INDICATOR)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(8000)
                .start();

        banner.setOnBannerListener(onBannerListener);

        QiSDK.register(this, robotLifecycleCallbacks);
    }

    private OnBannerListener onBannerListener = new OnBannerListener() {
        @Override
        public void OnBannerClick(int position) {
            if (ivClose.getVisibility() == View.VISIBLE) {
                ivClose.setVisibility(View.GONE);
            } else {
                ivClose.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.ivClose:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            Say say = SayBuilder.with(qiContext).withText("").build();
            say.run();
        }

        @Override
        public void onRobotFocusLost() {
            // 失去焦点

        }

        @Override
        public void onRobotFocusRefused(String reason) {
            // 获得焦点被拒绝

        }
    };

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robotLifecycleCallbacks);
        super.onDestroy();
    }

}
