package com.zhongbenshuo.zbspepper.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 * Created at 2019/12/10 0010 9:06
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:06
 */

public class MainActivity extends BaseActivity {

    private TextView tvBottom;
    private WakeUpUtil wakeUpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvMenu.setLayoutManager(gridLayoutManager);
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(0, R.drawable.company, getString(R.string.CompanyProfile), true));
        menuList.add(new Menu(0, R.drawable.scope, getString(R.string.BusinessScope), true));
        menuList.add(new Menu(0, R.drawable.cases, getString(R.string.EngineeringCase), true));
        MenuAdapter menuAdapter = new MenuAdapter(this, menuList);
        menuAdapter.setOnItemClickListener(onItemClickListener);
        rvMenu.setAdapter(menuAdapter);

        tvBottom = findViewById(R.id.tvBottom);
        tvBottom.setText("您可以这样问我：明天的天气怎么样？");
        tvBottom.setOnClickListener((onClickListener) -> openActivity(ChatActivity.class));

        QiSDK.register(this, robotLifecycleCallbacks);
    }

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
        switch (position) {
            case 0:
                // 公司简介
                openActivity(CompanyProfileActivity.class);
                break;
            case 1:
                // 经营范围
                openActivity(BusinessScopeActivity.class);
                break;
            case 2:
                // 工程案例
                openActivity(EngineeringCaseActivity.class);
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
            LogUtils.d(TAG, "获取到焦点");
            Touch touch = qiContext.getTouch();

            // 头部传感器
            TouchSensor touchSensor = touch.getSensor("Head/Touch");
            touchSensor.addOnStateChangedListener(touchState -> {
                LogUtils.d(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
                if (touchState.getTouched()) {
                    Say mSay = SayBuilder.with(qiContext)
                            .withText("摸我的头，我会容易睡着哒")
                            .build();
                    mSay.run();
                }
            });

        }

        @Override
        public void onRobotFocusLost() {
            // 失去焦点
            LogUtils.d(TAG, "失去焦点");
        }

        @Override
        public void onRobotFocusRefused(String reason) {
            // 获得焦点被拒绝
            LogUtils.d(TAG, "获得焦点被拒绝");
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
        wakeUpUtil.onDestroy();
        QiSDK.unregister(this, robotLifecycleCallbacks);
        super.onDestroy();
    }

}
