package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;
import com.aldebaran.qi.sdk.object.holder.Holder;
import com.youth.banner.Banner;
import com.youth.banner.transformer.AlphaPageTransformer;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.FragmentAdapter;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.adapter.TextAdapter;
import com.zhongbenshuo.zbspepper.bean.DataBean;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.constant.Iflytek;
import com.zhongbenshuo.zbspepper.design.activity.conversationstatus.ConversationStatusBinder;
import com.zhongbenshuo.zbspepper.design.activity.utils.KeyboardVisibilityWatcher;
import com.zhongbenshuo.zbspepper.design.activity.utils.ScreenFlagsChecker;
import com.zhongbenshuo.zbspepper.design.speechbar.SpeechBarView;
import com.zhongbenshuo.zbspepper.fragment.ApplicationFragment;
import com.zhongbenshuo.zbspepper.fragment.BusinessScopeFragment;
import com.zhongbenshuo.zbspepper.fragment.ChatFragment;
import com.zhongbenshuo.zbspepper.fragment.CompanyProfileFragment;
import com.zhongbenshuo.zbspepper.fragment.EngineeringCaseFragment;
import com.zhongbenshuo.zbspepper.fragment.SelfIntroductionFragment;
import com.zhongbenshuo.zbspepper.fragment.SettingFragment;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.GsonUtils;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.widget.InputPasswordDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页面
 * Created at 2019/12/10 0010 9:06
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:06
 */

public class MainActivity extends BaseActivity {

    private Context mContext;
    private QiContext mQiContext;
    private boolean abilitiesHeld = false;
    private Holder holder;
    private final ConversationStatusBinder conversationStatusBinder = new ConversationStatusBinder();
    private KeyboardVisibilityWatcher keyboardVisibilityWatcher = new KeyboardVisibilityWatcher();
    private ConstraintLayout constraintLayout;
    private List<Menu> menuList;
    private MenuAdapter menuAdapter;
    private ViewPager2 viewPager;
    private SpeechBarView speechBarView;
    private Banner banner;
    private Chat mChat;
    private Future<Void> chatFuture;
    private Say mSay;

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
        if (position == menuList.size() - 1) {
            InputPasswordDialog inputDialog = new InputPasswordDialog(mContext);
            inputDialog.setOnDialogClickListener(new InputPasswordDialog.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    if (TextUtils.isEmpty(inputDialog.getInputContent())) {
                        showToast("密码不能为空");
                    } else {
                        if (inputDialog.getInputContent().equals("66261935")) {
                            inputDialog.dismiss();
                            for (Menu menu : menuList) {
                                menu.setSelected(false);
                            }
                            //设置选择效果
                            menuList.get(position).setSelected(true);
                            menuAdapter.notifyDataSetChanged();
                            //参数false代表瞬间切换，true表示平滑过渡
                            viewPager.setCurrentItem(position, false);
                        } else {
                            inputDialog.clearInputContent();
                            showToast("密码错误");
                        }
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            inputDialog.setCancelable(false);
            inputDialog.show();
        } else {
            for (Menu menu : menuList) {
                menu.setSelected(false);
            }
            //设置选择效果
            menuList.get(position).setSelected(true);
            menuAdapter.notifyDataSetChanged();
            //参数false代表瞬间切换，true表示平滑过渡
            viewPager.setCurrentItem(position, false);
        }
    };

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            LogUtils.d(TAG, "获取到焦点");

            mQiContext = qiContext;

            mSay = SayBuilder.with(qiContext)
                    .withText("你好")
                    .build();
            Future<Void> fSay = mSay.async().run();
            fSay.requestCancellation();

            // 停止自主活动
            holdAbilities();

            conversationStatusBinder.bind(qiContext, speechBarView);

            // 讯飞AIUI平台APPID和APPKEY
            Map<String, String> myAsrParams = new HashMap<>(2);
            Map<String, String> params = new HashMap<>(2);
            params.put("appid", Iflytek.APP_ID);
            params.put("key", Iflytek.APP_KEY);
//            params.put("headid", Iflytek.HEAD_ID);
            myAsrParams.put("iflytek", GsonUtils.convertJSON(params));

//            mSay = SayBuilder.with(qiContext)
//                    .withText(getResources().getString(R.string.greeting))
//                    .build();
//
//            mSay.async().run().andThenConsume(consume -> {
            // 自定义讯飞Chatbot。
            IFlytekChatbot iflytekChatbot = new IFlytekChatbot(qiContext, mContext);

            // 创建chat。
            mChat = ChatBuilder.with(qiContext)
                    .withChatbot(iflytekChatbot)
                    .withAsrDriverParameters(myAsrParams)
                    .build();

            chatFuture = mChat.async().run();

            chatFuture.thenConsume(future -> {
                if (future.hasError()) {
                    String message = "finished with error.";
                    LogUtils.d(TAG, message + future.getError());
                } else if (future.isSuccess()) {
                    LogUtils.d(TAG, "run iflytekChatbot successful");
                } else if (future.isDone()) {
                    LogUtils.d(TAG, "run iflytekChatbot isDone");
                }
            });
//            });

        }

        @Override
        public void onRobotFocusLost() {
            // 失去焦点
            LogUtils.d(TAG, "失去焦点");
            mQiContext = null;
            conversationStatusBinder.unbind(false);
            if (chatFuture != null) {
                chatFuture.requestCancellation();
            }
        }

        @Override
        public void onRobotFocusRefused(String reason) {
            // 获得焦点被拒绝
            LogUtils.d(TAG, "获得焦点被拒绝");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        constraintLayout = findViewById(R.id.layout);

        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMenu.setLayoutManager(linearLayoutManager);

        menuList = new ArrayList<>();
        menuList.add(new Menu(R.drawable.company, getString(R.string.CompanyProfile), true));
        menuList.add(new Menu(R.drawable.scope, getString(R.string.BusinessScope), false));
        menuList.add(new Menu(R.drawable.cases, getString(R.string.EngineeringCase), false));
        menuList.add(new Menu(R.drawable.self, getString(R.string.SelfIntroduction), false));
        menuList.add(new Menu(R.drawable.conversation, getString(R.string.ChatPage), false));
        menuList.add(new Menu(R.drawable.application, getString(R.string.MyApplication), false));
        menuList.add(new Menu(R.drawable.setting, getString(R.string.Setting), false));
        menuAdapter = new MenuAdapter(this, menuList);
        menuAdapter.setOnItemClickListener(onItemClickListener);
        rvMenu.setAdapter(menuAdapter);

        viewPager = findViewById(R.id.viewpager);
        // 不允许滑动ViewPager
        viewPager.setUserInputEnabled(false);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CompanyProfileFragment());
        fragments.add(new BusinessScopeFragment());
        fragments.add(new EngineeringCaseFragment());
        fragments.add(new SelfIntroductionFragment());
        fragments.add(new ChatFragment());
        fragments.add(new ApplicationFragment());
        fragments.add(new SettingFragment());
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this, fragments);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(menuList.size());

        speechBarView = findViewById(R.id.speechBar);

        banner = findViewById(R.id.banner);
        banner.setAdapter(new TextAdapter(DataBean.getAskContent()))
                .addPageTransformer(new AlphaPageTransformer())
                .setOrientation(Banner.VERTICAL)
                .setUserInputEnabled(false)
                .isAutoLoop(true)
                .setDelayTime(5000);

        QiSDK.register(this, robotLifecycleCallbacks);
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        switch (msg.getTag()) {

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        banner.start();
        conversationStatusBinder.init(constraintLayout, speechBarView);
        keyboardVisibilityWatcher.subscribe(this::hideSystemBars, this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        banner.stop();
        keyboardVisibilityWatcher.release();
        conversationStatusBinder.unbind(true);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        banner.destroy();
        QiSDK.unregister(this, robotLifecycleCallbacks);
        conversationStatusBinder.unbind(true);
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        hideSystemBars();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        hideSystemBars();
    }

    private void hideSystemBars() {
        if (new ScreenFlagsChecker().hasFlags(getWindow().getDecorView().getSystemUiVisibility(),
                View.SYSTEM_UI_FLAG_FULLSCREEN,
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)) {
            return;
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void holdAbilities() {
        // Build and store the holder for the abilities.
        if (mQiContext != null) {
            holder = HolderBuilder.with(mQiContext)
                    .withAutonomousAbilities(
                            AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                            AutonomousAbilitiesType.BASIC_AWARENESS,
                            AutonomousAbilitiesType.AUTONOMOUS_BLINKING
                    )
                    .build();
            // Hold the abilities asynchronously.
            Future<Void> holdFuture = holder.async().hold();
            // Chain the hold with a lambda on the UI thread.
            holdFuture.andThenConsume(Qi.onUiThread((Consumer<Void>) ignore -> {
                // Store the abilities status.
                abilitiesHeld = true;
            }));
        }
    }

    private void releaseAbilities() {
        // Release the holder asynchronously.
        if (mQiContext != null) {
            holder = HolderBuilder.with(mQiContext)
                    .withAutonomousAbilities(
                            AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                            AutonomousAbilitiesType.BASIC_AWARENESS,
                            AutonomousAbilitiesType.AUTONOMOUS_BLINKING
                    )
                    .build();
            Future<Void> releaseFuture = holder.async().release();
            // Chain the release with a lambda on the UI thread.
            releaseFuture.andThenConsume(Qi.onUiThread((Consumer<Void>) ignore -> {
                // Store the abilities status.
                abilitiesHeld = false;
            }));
        }
    }

}
