package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.design.activity.conversationstatus.ConversationStatusBinder;
import com.zhongbenshuo.zbspepper.design.speechbar.SpeechBarView;
import com.zhongbenshuo.zbspepper.fragment.BusinessScopeFragment;
import com.zhongbenshuo.zbspepper.fragment.ChatFragment;
import com.zhongbenshuo.zbspepper.fragment.CompanyProfileFragment;
import com.zhongbenshuo.zbspepper.fragment.EngineeringCaseFragment;
import com.zhongbenshuo.zbspepper.fragment.MyApplicationFragment;
import com.zhongbenshuo.zbspepper.fragment.SelfIntroductionFragment;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.widget.NoScrollViewPager;

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
    private final ConversationStatusBinder conversationStatusBinder = new ConversationStatusBinder();
    private List<Menu> menuList;
    private RecyclerView rvMenu;
    private MenuAdapter menuAdapter;
    private NoScrollViewPager viewPager;
    private ImageView input;
    private SpeechBarView speechBarView;
    private boolean isChat = false;
    private Chat mChat;
    private Say mSay;
    private FragmentStatePagerAdapter viewPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                //自我介绍
                case 0:
                    return new SelfIntroductionFragment();
                //公司简介
                case 1:
                    return new CompanyProfileFragment();
                //经营范围
                case 2:
                    return new BusinessScopeFragment();
                //工程案例
                case 3:
                    return new EngineeringCaseFragment();
                //对话页面
                case 4:
                    return new ChatFragment();
                //应用页面
                case 5:
                    return new MyApplicationFragment();
                default:
                    break;
            }
            return null;
        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.input:
                if (isChat) {
                    isChat = false;
                    input.setImageResource(R.drawable.im_speech_voice);
                } else {
                    isChat = true;
                    input.setImageResource(R.drawable.anim_speech_button);
                    AnimationDrawable ani = (AnimationDrawable) input.getDrawable();
                    ani.start();
                }
                break;
            default:
                break;
        }
    };

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
        LogUtils.d(TAG, "点击了菜单列表");
        for (Menu menu : menuList) {
            menu.setSelected(false);
        }
        //设置选择效果
        menuList.get(position).setSelected(true);
        menuAdapter.notifyDataSetChanged();
        //参数false代表瞬间切换，true表示平滑过渡
        viewPager.setCurrentItem(position, false);
    };

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            LogUtils.d(TAG, "获取到焦点");

            conversationStatusBinder.bind(qiContext, speechBarView);

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

            // 讯飞AIUI平台APPID和APPKEY
            Map<String, String> myAsrParams = new HashMap<>(2);
            String myJson;
            myJson = "{\"appid\":\"5ee96c8d\",\"headid\": \"AP990396A08Y76100013\",\"key\": \"1527905efb15923a4a59cbcea1ba3c54\"}";
            myAsrParams.put("iflytek", myJson);


            mSay = SayBuilder.with(qiContext)
                    .withText(getResources().getString(R.string.greeting))
                    .build();

            mSay.async().run().andThenConsume(consume -> {
                // 自定义讯飞Chatbot。
                IFlytekChatbot iflytekChatbot = new IFlytekChatbot(qiContext, mContext);

                // 创建chat。
                mChat = ChatBuilder.with(qiContext)
                        .withChatbot(iflytekChatbot)
                        .withAsrDriverParameters(myAsrParams)
                        .build();

                Future<Void> chatFuture = mChat.async().run();

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
            });

        }

        @Override
        public void onRobotFocusLost() {
            // 失去焦点
            LogUtils.d(TAG, "失去焦点");
            conversationStatusBinder.unbind(false);
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
        rvMenu = findViewById(R.id.rvMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMenu.setLayoutManager(linearLayoutManager);

        menuList = new ArrayList<>();
        menuList.add(new Menu(R.drawable.self, getString(R.string.SelfIntroduction), true));
        menuList.add(new Menu(R.drawable.company, getString(R.string.CompanyProfile), false));
        menuList.add(new Menu(R.drawable.scope, getString(R.string.BusinessScope), false));
        menuList.add(new Menu(R.drawable.cases, getString(R.string.EngineeringCase), false));
        menuList.add(new Menu(R.drawable.cases, getString(R.string.ChatPage), false));
        menuList.add(new Menu(R.drawable.message, getString(R.string.MyApplication), false));
        menuAdapter = new MenuAdapter(this, menuList);
        menuAdapter.setOnItemClickListener(onItemClickListener);
        rvMenu.setAdapter(menuAdapter);

        viewPager = findViewById(R.id.viewpager);
        // 不允许滑动ViewPager
        viewPager.setNoScroll(true);
        viewPager.setAdapter(viewPagerAdapter);
        //设置Fragment预加载，非常重要,可以保存每个页面fragment已有的信息,防止切换后原页面信息丢失
        viewPager.setOffscreenPageLimit(6);

        speechBarView = findViewById(R.id.speech_bar);
        input = findViewById(R.id.input);
        input.setOnClickListener(onClickListener);

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
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robotLifecycleCallbacks);
        conversationStatusBinder.unbind(false);
        super.onDestroy();
    }

}
