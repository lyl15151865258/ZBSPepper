package com.zhongbenshuo.zbspepper.iflytek;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.BaseChatbotReaction;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.SpeechEngine;
import com.aldebaran.qi.sdk.util.IOUtils;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;


public class IFlytekNlpReaction extends BaseChatbotReaction {
    private static String TAG = IFlytekNlpReaction.class.getSimpleName();
    private Context mContext;
    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private String question;
    private String answer;
    // TTS是否开启需要在讯飞AIUI云端同步配置。
    private boolean useIFlytekTTS = true;
    private SpeechEngine mSpeechEngine;
    private Handler mHandler;

    public IFlytekNlpReaction(QiContext context, String question) {
        super(context);
        this.mContext = context;
        this.question = question;
    }

    public AIUIAgent createAgent(MyAIUIListener mAIUIListener) {
        Log.i(TAG, "create aiui agent");
        try {
            String params = IOUtils.fromAsset(mContext, "cfg/aiui_phone.cfg");
            params = params.replace("\n", "").replace("\t", "").replace(" ", "");
            AIUIAgent aIUIAgent = AIUIAgent.createAgent(mContext, params, mAIUIListener);
            return aIUIAgent;
        } catch (Exception e) {
            Log.e(TAG, "Error " + e.getMessage());
            return null;
        }

    }

    @Override
    public void runWith(SpeechEngine speechEngine) {
        mSpeechEngine = speechEngine;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MyAIUIListener myAIUIListener = new MyAIUIListener();
        myAIUIListener.setCountDownLatch(countDownLatch);
        AIUIAgent aiuiAgent = createAgent(myAIUIListener);

        if (aiuiAgent == null) {
            doFallback();
        } else {

            sendNlpMessage(aiuiAgent);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Log.e(TAG, "Event during countDownLatch.await() e: " + e);
            }
            String answers = SPHelper.getString("NlpAnswer", "");
            if (!useIFlytekTTS) {
                if (answers != null) {
                    Say say = SayBuilder.with(speechEngine)
                            .withText(answers)
                            .build();

                    Future fSay = say.async().run();

                    try {
                        fSay.get();
                    } catch (ExecutionException e) {
                        Log.e(TAG, "Error during Say", e);
                    } catch (CancellationException e) {
                        Log.i(TAG, "Interruption during Say" + e);
                    }
                } else {
                    doFallback();
                }
            }
            aiuiAgent.destroy();
            SPHelper.save("NlpAnswer", "");
            Log.i(TAG, "aiuiAgent destroy");
            answer = myAIUIListener.answer;
        }
    }

    private void sendNlpMessage(AIUIAgent mAIUIAgent) {
        if (AIUIConstant.STATE_WORKING != mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }

        Log.i(TAG, "start text nlp");
        try {
            String params = "data_type=text,tag=text-tag";
            byte[] textData = question.getBytes("utf-8");

            AIUIMessage write = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
            mAIUIAgent.sendMessage(write);


        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error during send nlp message exception " + e.getCause());
        }
    }

    @Override
    public void stop() {
    }

    private class MyAIUIListener implements AIUIListener {
        private String answer;
        private CountDownLatch countDownLatch;
        private JSONObject resultEvent;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public String getAnswer() {
            return answer;
        }

        public JSONObject getResultEvent() {
            return resultEvent;
        }

        @Override
        public void onEvent(AIUIEvent event) {
            Log.i(TAG, "on event: " + event.eventType);
            switch (event.eventType) {
                case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                    break;
                case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                    if (countDownLatch.getCount() == 1) {
                        countDownLatch.countDown();
                    }
                    break;
                case AIUIConstant.EVENT_WAKEUP:
                    // 唤醒事件
                    LogUtils.d(TAG, "唤醒事件");
                    break;
                case AIUIConstant.EVENT_RESULT:
                    // 结果解析事件
                    LogUtils.d(TAG, "结果解析事件");
                    try {
                        JSONObject data = new JSONObject(event.info).getJSONArray("data").getJSONObject(0);
                        String sub = data.getJSONObject("params").optString("sub");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if ("nlp".equals(sub)) {
                            if (content.has("cnt_id")) {
                                String cnt_id = content.getString("cnt_id");
                                JSONObject result = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                                resultEvent = result;
                                int rc = result.getJSONObject("intent").getInt("rc");
                                if (rc != 0) {
                                    Log.w(TAG, "nlp rc: " + rc);
                                    countDownLatch.countDown();
                                }
                                answer = result.getJSONObject("intent").getJSONObject("answer").getString("text");
                                SPHelper.save("NlpAnswer", answer);
                            }
                        }

                    } catch (Throwable e) {
                        Log.e(TAG, "Event during EVENT_RESULT e: " + e);
                    } finally {
                        if (!useIFlytekTTS) {
                            countDownLatch.countDown();
                        }
                    }
                    break;
                case AIUIConstant.EVENT_ERROR:
                    //错误事件
                    Log.e(TAG, "错误: " + event.arg1 + "，" + event.info);
                    Log.e(TAG, "Error EVENT_ERROR: " + event.info);
                    countDownLatch.countDown();
                    break;
                case AIUIConstant.EVENT_VAD:
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        //语音前端点
                        LogUtils.d(TAG, "语音前端点");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //语音后端点
                        LogUtils.d(TAG, "语音后端点");
                    }
                    break;
                case AIUIConstant.EVENT_START_RECORD:
                    //开始录音
                    LogUtils.d(TAG, "开始录音");
                    break;
                case AIUIConstant.EVENT_STOP_RECORD:
                    //停止录音
                    LogUtils.d(TAG, "停止录音");
                    break;
                case AIUIConstant.EVENT_STATE:
                    //状态事件
                    LogUtils.d(TAG, "状态事件");
                    mAIUIState = event.arg1;
                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                        LogUtils.d(TAG, "闲置状态，AIUI未开启");
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                        LogUtils.d(TAG, "AIUI已就绪，等待唤醒");
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                        LogUtils.d(TAG, "AIUI工作中，可进行交互");
                    }
                    break;
                case AIUIConstant.EVENT_CMD_RETURN:
                    break;
                case AIUIConstant.EVENT_TTS:
                    switch (event.arg1) {
                        case AIUIConstant.TTS_SPEAK_BEGIN:
                            Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_BEGIN");
                            break;
                        case AIUIConstant.TTS_SPEAK_PROGRESS:
                            Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_PROGRESS");
                            break;
                        case AIUIConstant.TTS_SPEAK_PAUSED:
                            Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_PAUSED");
                            break;
                        case AIUIConstant.TTS_SPEAK_RESUMED:
                            Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_RESUMED");
                            break;
                        case AIUIConstant.TTS_SPEAK_COMPLETED:
                            Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_COMPLETED");
                            if (useIFlytekTTS) {
                                Log.i(TAG, "EVENT_TTS: " + "TTS_SPEAK_COMPLETED in");
                                countDownLatch.countDown();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public String getAnswer() {
        return answer;
    }

    private void doFallback() {
        if (mSpeechEngine != null) {
            Say say = SayBuilder.with(mSpeechEngine)
                    .withText(mContext.getResources().getString(R.string.fallback_answer))
                    .build();
            Future fSay = say.async().run();
            try {
                fSay.get();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error during Say", e);
            } catch (CancellationException e) {
                Log.i(TAG, "Interruption during Say");
            }
        }
    }
}