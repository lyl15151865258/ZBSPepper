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
import com.aldebaran.qi.sdk.object.conversation.AutonomousReaction;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionImportance;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionValidity;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.BookmarkStatus;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.EditablePhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.ReplyReaction;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.conversation.TopicStatus;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.ChatAdapter;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.SpeechAnimUtils;
import com.zhongbenshuo.zbspepper.widget.WaveView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天对话页面
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatActivity extends BaseActivity {

    private Context mContext;
    private ChatAdapter chatAdapter;
    private List<ChatText> chatTextList;
    private WaveView wave;
    private ImageView input;
    private Chat mChat;
    private Say mSay;
    private boolean isChat = false;
    private String wakeupContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);

        findViewById(R.id.ivBack).setOnClickListener(onClickListener);

        wave = findViewById(R.id.wave);
        input = findViewById(R.id.input);
        RecyclerView rvChat = findViewById(R.id.rvChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChat.setLayoutManager(linearLayoutManager);

        chatTextList = new ArrayList<>();
        chatAdapter = new ChatAdapter(rvChat, chatTextList);
        rvChat.setAdapter(chatAdapter);

        input.setOnClickListener(onClickListener);

        if (getIntent() != null) {
            wakeupContent = getIntent().getStringExtra("wakeupContent");
        }

        QiSDK.register(this, robotLifecycleCallbacks);
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.ivBack:
                ActivityController.finishActivity(this);
                break;
            case R.id.input:
                if (isChat) {
                    isChat = false;
                    SpeechAnimUtils.stopImmediately(wave);
                    input.setImageResource(R.drawable.im_speech_voice);
                } else {
                    isChat = true;
                    SpeechAnimUtils.StartAnim(wave);
                    input.setImageResource(R.drawable.anim_speech_button);
                    AnimationDrawable ani = (AnimationDrawable) input.getDrawable();
                    ani.start();
                    if (chatTextList.size() % 3 == 0) {
                        chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.LISTEN, "明天天气怎么样"));
                    } else if (chatTextList.size() % 3 == 1) {
                        chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_BLURRY, "抱歉，我没有听清你说什么"));
                    } else {
                        chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_CLEAR, "苏州明天多云转小雨，2-12℃，西北风3-4级"));
                    }
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
            mSay = SayBuilder.with(qiContext)
                    .withText(getResources().getString(R.string.greeting))
                    .build();

            if (wakeupContent != null) {
                chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.LISTEN, wakeupContent));
            }
            chatAdapter.insertData(new ChatText(ChatText.CHATTYPE.REPLY_CLEAR, getResources().getString(R.string.greeting)));

            // 讯飞AIUI平台APPID和APPKEY
            Map<String, String> myAsrParams = new HashMap<>(2);
            String myJson;
            myJson = "{\"appid\":\"5e15570f\",\"headid\": \"AP990396A08Y76100013\",\"key\": \"c90a30ea5266fe3918bf6492115363de\"}";
            myAsrParams.put("iflytek", myJson);

            mSay.async().run().andThenConsume(consume -> {
                // 自定义讯飞Chatbot。
                IFlytekChatbot iflytekChatbot = new IFlytekChatbot(qiContext, mContext);

                QiChatbot qiChatbot = new QiChatbot() {
                    @Override
                    public Async async() {
                        return null;
                    }

                    @Override
                    public PhraseSet concept(String conceptName) {
                        return null;
                    }

                    @Override
                    public EditablePhraseSet dynamicConcept(String conceptName) {
                        return null;
                    }

                    @Override
                    public void goToBookmark(Bookmark bookmark, AutonomousReactionImportance importance, AutonomousReactionValidity validity) {

                    }

                    @Override
                    public BookmarkStatus bookmarkStatus(Bookmark bookmark) {
                        return null;
                    }

                    @Override
                    public TopicStatus topicStatus(Topic topic) {
                        return null;
                    }

                    @Override
                    public QiChatVariable variable(String varName) {
                        return null;
                    }

                    @Override
                    public List<Phrase> globalRecommendations(Integer maxRecommendations) {
                        return null;
                    }

                    @Override
                    public List<Phrase> scopeRecommendations(Integer maxRecommendations) {
                        return null;
                    }

                    @Override
                    public List<Phrase> focusedTopicRecommendations(Integer maxRecommendations) {
                        return null;
                    }

                    @Override
                    public void setOnBookmarkReachedListener(OnBookmarkReachedListener onBookmarkReachedListener) {

                    }

                    @Override
                    public void addOnBookmarkReachedListener(OnBookmarkReachedListener onBookmarkReachedListener) {

                    }

                    @Override
                    public void removeOnBookmarkReachedListener(OnBookmarkReachedListener onBookmarkReachedListener) {

                    }

                    @Override
                    public void removeAllOnBookmarkReachedListeners() {

                    }

                    @Override
                    public void setOnEndedListener(OnEndedListener onEndedListener) {

                    }

                    @Override
                    public void addOnEndedListener(OnEndedListener onEndedListener) {

                    }

                    @Override
                    public void removeOnEndedListener(OnEndedListener onEndedListener) {

                    }

                    @Override
                    public void removeAllOnEndedListeners() {

                    }

                    @Override
                    public List<Topic> getTopics() {
                        return null;
                    }

                    @Override
                    public Topic getFocusedTopic() {
                        return null;
                    }

                    @Override
                    public void setOnFocusedTopicChangedListener(OnFocusedTopicChangedListener onFocusedTopicChangedListener) {

                    }

                    @Override
                    public void addOnFocusedTopicChangedListener(OnFocusedTopicChangedListener onFocusedTopicChangedListener) {

                    }

                    @Override
                    public void removeOnFocusedTopicChangedListener(OnFocusedTopicChangedListener onFocusedTopicChangedListener) {

                    }

                    @Override
                    public void removeAllOnFocusedTopicChangedListeners() {

                    }

                    @Override
                    public BodyLanguageOption getSpeakingBodyLanguage() {
                        return null;
                    }

                    @Override
                    public void setSpeakingBodyLanguage(BodyLanguageOption bodyLanguageOption) {

                    }

                    @Override
                    public Map<String, QiChatExecutor> getExecutors() {
                        return null;
                    }

                    @Override
                    public void setExecutors(Map<String, QiChatExecutor> executors) {

                    }

                    @Override
                    public ReplyReaction replyTo(Phrase phrase, Locale locale) {
                        return null;
                    }

                    @Override
                    public void acknowledgeHeard(Phrase phrase, Locale locale) {

                    }

                    @Override
                    public void acknowledgeSaid(Phrase phrase, Locale locale) {

                    }

                    @Override
                    public AutonomousReaction getAutonomousReaction() {
                        return null;
                    }

                    @Override
                    public void setOnAutonomousReactionChangedListener(OnAutonomousReactionChangedListener onAutonomousReactionChangedListener) {

                    }

                    @Override
                    public void addOnAutonomousReactionChangedListener(OnAutonomousReactionChangedListener onAutonomousReactionChangedListener) {

                    }

                    @Override
                    public void removeOnAutonomousReactionChangedListener(OnAutonomousReactionChangedListener onAutonomousReactionChangedListener) {

                    }

                    @Override
                    public void removeAllOnAutonomousReactionChangedListeners() {

                    }

                    @Override
                    public Integer getMaxHypothesesPerUtterance() {
                        return null;
                    }

                    @Override
                    public void setMaxHypothesesPerUtterance(Integer maxHypothesesPerUtterance) {

                    }

                    @Override
                    public void setOnMaxHypothesesPerUtteranceChangedListener(OnMaxHypothesesPerUtteranceChangedListener onMaxHypothesesPerUtteranceChangedListener) {

                    }

                    @Override
                    public void addOnMaxHypothesesPerUtteranceChangedListener(OnMaxHypothesesPerUtteranceChangedListener onMaxHypothesesPerUtteranceChangedListener) {

                    }

                    @Override
                    public void removeOnMaxHypothesesPerUtteranceChangedListener(OnMaxHypothesesPerUtteranceChangedListener onMaxHypothesesPerUtteranceChangedListener) {

                    }

                    @Override
                    public void removeAllOnMaxHypothesesPerUtteranceChangedListeners() {

                    }
                };

                // 创建chat。
                mChat = ChatBuilder.with(qiContext)
                        .withChatbot(qiChatbot)
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
