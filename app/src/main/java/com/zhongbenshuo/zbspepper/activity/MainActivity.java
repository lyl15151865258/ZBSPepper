package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.GoToBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.FreeFrame;
import com.aldebaran.qi.sdk.object.actuation.GoTo;
import com.aldebaran.qi.sdk.object.actuation.Mapping;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.youth.banner.Banner;
import com.youth.banner.transformer.AlphaPageTransformer;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.FragmentAdapter;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.adapter.TextAdapter;
import com.zhongbenshuo.zbspepper.bean.DataBean;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.bean.Result;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.constant.ErrorCode;
import com.zhongbenshuo.zbspepper.constant.Iflytek;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
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
import com.zhongbenshuo.zbspepper.network.ExceptionHandle;
import com.zhongbenshuo.zbspepper.network.NetClient;
import com.zhongbenshuo.zbspepper.network.NetworkObserver;
import com.zhongbenshuo.zbspepper.utils.GsonUtils;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.NetworkUtil;
import com.zhongbenshuo.zbspepper.widget.InputPasswordDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 主页面
 * Created at 2019/12/10 0010 9:06
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:06
 */

public class MainActivity extends BaseActivity {

    private Context mContext;
    public QiContext mQiContext;
    private final ConversationStatusBinder conversationStatusBinder = new ConversationStatusBinder();
    private KeyboardVisibilityWatcher keyboardVisibilityWatcher = new KeyboardVisibilityWatcher();
    private ConstraintLayout constraintLayout;
    private List<Menu> menuList;
    private RecyclerView rvMenu;
    private MenuAdapter menuAdapter;
    private ViewPager2 viewPager;
    private SpeechBarView speechBarView;
    private Banner banner;
    private LinearLayout llBatteryView;
    private Chat mChat;
    private Future<Void> chatFuture;
    private Say mSay;
    private Animate animate;
    private static final int WAIT_TIME_SECONDS = 10;
    private static boolean flag = true;
    private static volatile long seconds = 0;
    private InputPasswordDialog inputDialog;
    private SyncTimeTask syncTimeTask;

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
//        if (position == menuList.size() - 1) {
//            inputDialog = new InputPasswordDialog(mContext);
//            inputDialog.setTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    seconds = 0;
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//            inputDialog.setOnDialogClickListener(new InputPasswordDialog.OnDialogClickListener() {
//                @Override
//                public void onOKClick() {
//                    if (TextUtils.isEmpty(inputDialog.getInputContent())) {
//                        showToast("密码不能为空");
//                    } else {
//                        if (inputDialog.getInputContent().equals("66261935")) {
//                            inputDialog.dismiss();
//                            for (Menu menu : menuList) {
//                                menu.setSelected(false);
//                            }
//                            //设置选择效果
//                            menuList.get(position).setSelected(true);
//                            menuAdapter.notifyDataSetChanged();
//                            //参数false代表瞬间切换，true表示平滑过渡
//                            viewPager.setCurrentItem(position, false);
//                        } else {
//                            inputDialog.clearInputContent();
//                            showToast("密码错误");
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelClick() {
//
//                }
//            });
//            seconds = 0;
//            inputDialog.setCancelable(false);
//            inputDialog.show();
//        } else {
        for (Menu menu : menuList) {
            menu.setSelected(false);
        }
        //设置选择效果
        menuList.get(position).setSelected(true);
        menuAdapter.notifyDataSetChanged();
        //参数false代表瞬间切换，true表示平滑过渡
        viewPager.setCurrentItem(position, false);
//        }
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
            try {
                fSay.get();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error during Say", e);
            } catch (CancellationException e) {
                Log.i(TAG, "Interruption during Say" + e);
            }

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
            if (animate != null) {
                animate.removeAllOnStartedListeners();
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

        rvMenu = findViewById(R.id.rvMenu);
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
        llBatteryView = findViewById(R.id.llBattery);
        llBatteryView.setVisibility(SPHelper.getBoolean("toggleShowBatteryView", false) ? View.VISIBLE : View.GONE);

        QiSDK.register(this, robotLifecycleCallbacks);

        syncTimeTask = new SyncTimeTask(this);
        syncTimeTask.execute();

        queryAskSentence();
    }

    /**
     * 获取询问语句
     */
    private void queryAskSentence() {
        Observable<Result> observable = NetClient.getInstance(NetClient.getBaseUrl(), false).getZbsApi().queryAskSentence();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(mContext) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    loadLastAskSentence();
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                loadLastAskSentence();
            }

            @Override
            public void onNext(Result result) {
                if (result.getCode() == ErrorCode.SUCCESS) {
                    List<String> stringList = GsonUtils.parseJSONList(GsonUtils.convertJSON(result.getData()), String.class);
                    List<DataBean> dataBeans = new ArrayList<>();
                    for (String content : stringList) {
                        dataBeans.add(new DataBean(0, content, 1));
                    }
                    SPHelper.save("askSentence", GsonUtils.convertJSON(dataBeans));
                    banner.setAdapter(new TextAdapter(dataBeans))
                            .addPageTransformer(new AlphaPageTransformer())
                            .setOrientation(Banner.VERTICAL)
                            .setUserInputEnabled(false)
                            .isAutoLoop(true)
                            .setDelayTime(5000);
                } else {
                    loadLastAskSentence();
                }
            }
        });
    }

    private void loadLastAskSentence() {
        List<DataBean> dataBeans = GsonUtils.parseJSONList(SPHelper.getString("askSentence", GsonUtils.convertJSON(new ArrayList<>())), DataBean.class);
        banner.setAdapter(new TextAdapter(dataBeans))
                .addPageTransformer(new AlphaPageTransformer())
                .setOrientation(Banner.VERTICAL)
                .setUserInputEnabled(false)
                .isAutoLoop(true)
                .setDelayTime(5000);
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        switch (msg.getAction()) {
            case Constants.ACTION:
                // 执行动作
                new Thread(() -> {
                    Future<Animation> myAnimation = null;
                    MediaPlayer mediaPlayer = null;
                    LogUtils.d(TAG, "当前需要执行的动作为：" + msg.getAction());
                    switch (msg.getText()) {
                        case "举手":
                            // 举手
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            mediaPlayer = MediaPlayer.create(mQiContext, R.raw.elephant_sound);
                            break;
                        case "鞠躬":
                            // 鞠躬
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "向前看":
                            // 向前看
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "向右看":
                            // 向右看
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "向左看":
                            // 向左看
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "低头":
                            // 低头
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "抬头":
                            // 抬头
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.elephant_a001).buildAsync();
                            break;
                        case "点头":
                            // 点头
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.dance_b001).buildAsync();
                            break;
                        case "摇头":
                            // 摇头
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.disco_a001).buildAsync();
                            break;
                        case "敬礼":
                            // 敬礼
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.dog_a001).buildAsync();
                            break;
                        case "挥手":
                            // 挥手
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.dizzy_a001).buildAsync();
                            break;
                        case "握手":
                            // 握手
                            myAnimation = AnimationBuilder.with(mQiContext).withResources(R.raw.dizzy_a002).buildAsync();
                            break;
                        default:
                            break;
                    }
                    if (myAnimation != null) {
                        try {
                            animate = AnimateBuilder.with(mQiContext).withAnimation(myAnimation.get()).build();
                            MediaPlayer finalMediaPlayer = mediaPlayer;
                            animate.addOnStartedListener(() -> {
                                LogUtils.d(TAG, "OnStartedListener：动作开始");
                                if (finalMediaPlayer != null) {
                                    finalMediaPlayer.start();
                                }
                            });

                            Future<Void> animateFuture = animate.async().run();
                            animateFuture.thenConsume(future -> {
                                if (future.isSuccess()) {
                                    LogUtils.d(TAG, "animateFuture：动作执行成功");
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行动作成功");
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                } else if (future.isCancelled()) {
                                    LogUtils.d(TAG, "animateFuture：动作取消");
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行动作取消");
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                } else if (future.hasError()) {
                                    LogUtils.d(TAG, "animateFuture：动作出错");
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行动作出错");
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                }
                            });
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
                break;
            case Constants.MOVE:
                // 移动
                new Thread(() -> {
                    LogUtils.d(TAG, "当前需要执行的移动为：" + msg.getText());
                    Actuation actuation = mQiContext.getActuation();
                    Frame robotFrame = actuation.robotFrame();
                    // 定义一个沿 X 轴方向的 1 米的偏移量。
                    Transform transform = null;
                    Map<String, String> params = msg.getParams();
                    String direction = params.get("direction");
                    String number = params.get("number");
                    if (!TextUtils.isEmpty(direction)) {
                        switch (direction) {
                            case "前":
                                transform = TransformBuilder.create().fromXTranslation(TextUtils.isEmpty(number) ? 0 : Integer.parseInt(number));
                                break;
                            case "后":
                                transform = TransformBuilder.create().fromXTranslation(TextUtils.isEmpty(number) ? 0 : -Integer.parseInt(number));
                                break;
                            case "左":
                                transform = TransformBuilder.create().from2DTranslation(0, TextUtils.isEmpty(number) ? 0 : Integer.parseInt(number));
                                break;
                            case "右":
                                transform = TransformBuilder.create().from2DTranslation(0, TextUtils.isEmpty(number) ? 0 : -Integer.parseInt(number));
                                break;
                            default:
                                break;
                        }
                        if (transform != null) {
                            Mapping mapping = mQiContext.getMapping();
                            FreeFrame targetFrame = mapping.makeFreeFrame();
                            // 0L 是指时间戳，可以指定时间来更新 FreeFrame。
                            targetFrame.update(robotFrame, transform, 0L);
                            GoTo goTo = GoToBuilder.with(mQiContext)
                                    .withFrame(targetFrame.frame())
                                    .build();
                            goTo.addOnStartedListener(() -> LogUtils.d(TAG, "GoTo：机器人开始移动"));

                            Future<Void> goToFuture = goTo.async().run();
                            goToFuture.thenConsume(future -> {
                                if (future.isSuccess()) {
                                    LogUtils.d(TAG, "GoTo：机器人移动成功");
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行移动成功");
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                } else if (future.isCancelled()) {
                                    LogUtils.d(TAG, "GoTo：机器人移动被取消");
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行移动取消");
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                } else if (future.hasError()) {
                                    LogUtils.d(TAG, "GoTo：机器人移动出错:" + future.getError().toString());
                                    msg.setAction(Constants.REPLY);
                                    msg.setText("执行移动出错:" + future.getError().getMessage());
                                    msg.setShow(true);
                                    EventBus.getDefault().post(msg);
                                }
                            });
                        }
                    }
                }).start();
                break;
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
        flag = false;
        if (syncTimeTask != null) {
            syncTimeTask.cancel(true);
            syncTimeTask = null;
        }
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

    // 无限循环的定时任务
    private static class SyncTimeTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<MainActivity> mainActivityWeakReference;

        private SyncTimeTask(MainActivity activity) {
            mainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (flag) {
                if (isCancelled()) {
                    break;
                }
                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seconds++;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
            if (isCancelled()) {
                return;
            }
            MainActivity mainActivity = mainActivityWeakReference.get();
            if (seconds > WAIT_TIME_SECONDS && mainActivity.inputDialog != null) {
                mainActivity.inputDialog.dismiss();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    // 移动到指定的Fragment
    public void moveToPosition(int position) {
        if (position <= rvMenu.getChildCount()) {
            View view = rvMenu.getChildAt(position);
            MenuAdapter.MenuViewHolder viewHolder = (MenuAdapter.MenuViewHolder) rvMenu.getChildViewHolder(view);
            viewHolder.clickItem();
        }
    }

    // 显示或隐藏电池图标
    public void showBatteryView(boolean show) {
        llBatteryView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
