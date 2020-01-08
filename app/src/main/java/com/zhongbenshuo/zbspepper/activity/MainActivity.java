package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.constant.Iflytek;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Chat mChat;
    private Say mSay;

    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 设置门限值 ： 门限值越低越容易被唤醒
    private static final int curThresh = 1450;
    private static final String keep_alive = "1";
    private static final String ivwNetMode = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //菜单网格
        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvMenu.setLayoutManager(gridLayoutManager);
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(0, R.drawable.self, getString(R.string.SelfIntroduction), true));
        menuList.add(new Menu(0, R.drawable.company, getString(R.string.CompanyProfile), true));
        menuList.add(new Menu(0, R.drawable.scope, getString(R.string.BusinessScope), true));
        menuList.add(new Menu(0, R.drawable.cases, getString(R.string.EngineeringCase), true));
        menuList.add(new Menu(0, R.drawable.message, getString(R.string.MessageBoard), true));
        menuList.add(new Menu(0, R.drawable.meeting, getString(R.string.AnnualMeeting), true));
        MenuAdapter menuAdapter = new MenuAdapter(mContext, menuList);
        menuAdapter.setOnItemClickListener(onItemClickListener);
        rvMenu.setAdapter(menuAdapter);

        QiSDK.register(this, robotLifecycleCallbacks);

        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
            mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
            // 启动唤醒
            /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

            mIvw.startListening(mWakeuperListener);
				/*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/msc/ivw1.wav");
				byte[] byetsFromFile = getByetsFromFile(file);
				mIvw.writeAudio(byetsFromFile,0,byetsFromFile.length);*/
            //	mIvw.stopListening();
        } else {
            showToast("唤醒未初始化");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIvw.stopListening();
    }

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
        switch (position) {
            case 0:
                // 自我介绍
                openActivity(SelfIntroductionActivity.class);
                break;
            case 1:
                // 公司简介
                openActivity(CompanyProfileActivity.class);
                break;
            case 2:
                // 经营范围
                openActivity(BusinessScopeActivity.class);
                break;
            case 3:
                // 工程案例
                openActivity(EngineeringCaseActivity.class);
                break;
            case 4:
                // 留言板
                openActivity(MessageBoardActivity.class);
                break;
            case 5:
                // 年会专栏
                openActivity(AnnualMeetingActivity.class);
                break;
            default:
                break;
        }
    };

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            LogUtils.d(TAG, "onResult");
            String resultString;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【唤醒词】" + object.optString("keyword"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();

                String wakeupContent = null;
                switch (object.optString("id")) {
                    case "0":
                        wakeupContent = "小硕小硕";
                        break;
                    case "1":
                        wakeupContent = "你好小硕";
                        break;
                    case "2":
                        wakeupContent = "小硕你好";
                        break;
                    case "3":
                        wakeupContent = "你好硕硕";
                        break;
                    case "4":
                        wakeupContent = "硕硕你好";
                        break;
                    default:
                        break;
                }
                if (wakeupContent != null) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("wakeupContent", wakeupContent);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            LogUtils.d(TAG, "唤醒结果：" + resultString);
        }

        @Override
        public void onError(SpeechError error) {
            showToast(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    LogUtils.d(TAG, "ivw audio length: " + audio.length);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robotLifecycleCallbacks);
        // 销毁合成对象
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
        super.onDestroy();
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + Iflytek.APP_ID + ".jet");
        LogUtils.d(TAG, "resPath: " + resPath);
        return resPath;
    }

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            mSay = SayBuilder.with(qiContext)
                    .withText(getResources().getString(R.string.greeting))
                    .build();

            // 讯飞AIUI平台APPID和APPKEY
            Map<String, String> myAsrParams = new HashMap<>(2);
            String myJson;
            myJson = "{\"appid\":\"5dfb175a\",\"headid\": \"AP990396A08Y76100013\",\"key\": \"5d5d1186d73a4555c3b035987424f30b\"}";
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

}
