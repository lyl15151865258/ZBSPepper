package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.ChatAdapter;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.design.activity.conversationstatus.ConversationStatusBinder;
import com.zhongbenshuo.zbspepper.design.speechbar.SpeechBarView;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天对话页面
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    private Context mContext;
    private ChatAdapter chatAdapter;
    private ImageView input;
    private Chat mChat;
    private Say mSay;
    private boolean isChat = false;
    private String wakeupContent = null;
    private boolean isFirst = true;

    private SpeechBarView speechBarView;
    private final ConversationStatusBinder conversationStatusBinder = new ConversationStatusBinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);

        speechBarView = findViewById(R.id.speech_bar);

        findViewById(R.id.ivBack).setOnClickListener(onClickListener);

        input = findViewById(R.id.input);
        RecyclerView rvChat = findViewById(R.id.rvChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(rvChat);
        rvChat.setAdapter(chatAdapter);

        input.setOnClickListener(onClickListener);

        if (getIntent() != null) {
            wakeupContent = getIntent().getStringExtra("wakeupContent");
        }

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
            case Constants.LISTEN:
                // 听到的内容
                chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.LISTEN, msg.getMsg()));
                break;
            case Constants.REPLY_CLEAR:
                // 听清的回复
                chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_CLEAR, msg.getMsg()));
                break;
            case Constants.REPLY_BLURRY:
                // 听不清的回复
                chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_BLURRY, msg.getMsg()));
                break;
            case Constants.EXIT:
                // 退出对话页面
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.ivBack:
                ActivityController.finishActivity(this);
                break;
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

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            LogUtils.d(TAG, "获取到焦点");

            conversationStatusBinder.bind(qiContext, speechBarView);

            mSay = SayBuilder.with(qiContext)
                    .withText(getResources().getString(R.string.greeting))
                    .build();

            // 在主线程中更新list
            if (isFirst) {
                runOnUiThread(() -> {
                    if (wakeupContent != null) {
                        chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.LISTEN, wakeupContent));
                    }
                    chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_CLEAR, getResources().getString(R.string.greeting)));
                });
                isFirst = false;
            }

            // 讯飞AIUI平台APPID和APPKEY
            Map<String, String> myAsrParams = new HashMap<>(2);
            String myJson;
            myJson = "{\"appid\":\"5ee96c8d\",\"headid\": \"AP990396A08Y76100013\",\"key\": \"1527905efb15923a4a59cbcea1ba3c54\"}";
            myAsrParams.put("iflytek", myJson);

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
    protected void onDestroy() {
        QiSDK.unregister(this, robotLifecycleCallbacks);
        conversationStatusBinder.unbind(false);
        super.onDestroy();
    }

}