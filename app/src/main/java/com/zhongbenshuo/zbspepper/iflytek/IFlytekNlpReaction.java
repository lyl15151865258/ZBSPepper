package com.zhongbenshuo.zbspepper.iflytek;

import android.content.Context;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.BaseChatbotReaction;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.SpeechEngine;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.aldebaran.qi.sdk.util.IOUtils;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class IFlytekNlpReaction extends BaseChatbotReaction {

    private static String TAG = IFlytekNlpReaction.class.getSimpleName();
    private Context mContext;
    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private String question;
    private String answer;
    // TTS是否开启，需要在讯飞AIUI云端同步配置语音合成下发。
    private boolean useIFlytekTTS = false;
    private SpeechEngine mSpeechEngine;
    private Future<Void> fSay;

    public IFlytekNlpReaction(QiContext context, String question) {
        super(context);
        this.mContext = context;
        this.question = question;
        initSensorListener(context);
    }

    public AIUIAgent createAgent(MyAIUIListener mAIUIListener) {
        LogUtils.d(TAG, "create aiui agent");
        try {
            String params = IOUtils.fromAsset(mContext, "cfg/aiui_phone.cfg");
            params = params.replace("\n", "").replace("\t", "").replace(" ", "");
            return AIUIAgent.createAgent(mContext, params, mAIUIListener);
        } catch (Exception e) {
            LogUtils.d(TAG, "Error " + e.getMessage());
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
                LogUtils.d(TAG, "Event during countDownLatch.await() e: " + e);
            }
            String answers = SPHelper.getString("NlpAnswer", "");
            if (!useIFlytekTTS) {
                if (answers != null) {
                    Say say = SayBuilder.with(speechEngine)
                            .withText(answers)
                            .build();
                    fSay = say.async().run();
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
            LogUtils.d(TAG, "aiuiAgent destroy");
            answer = myAIUIListener.answer;
        }
    }

    /**
     * 初始化传感器监听
     */
    private void initSensorListener(QiContext qiContent) {
        Touch touch = qiContent.getTouch();
        // 头部传感器
        TouchSensor touchSensor = touch.getSensor("Head/Touch");
        touchSensor.addOnStateChangedListener(touchState -> {
            LogUtils.d(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
            if (touchState.getTouched()) {
                LogUtils.d(TAG, "触摸了头部传感器");
                if (fSay != null) {
                    LogUtils.d(TAG, "停止讲话");
                    fSay.requestCancellation();
                } else {
                    LogUtils.d(TAG, "sayFuture为null");
                }
            }
        });
        // 手部传感器
        TouchSensor leftHandSensor = touch.getSensor("LHand/Touch");
        TouchSensor rightHandSensor = touch.getSensor("RHand/Touch");
        TouchSensor.OnStateChangedListener onStateChangedListenerHand = touchState -> {
            LogUtils.d(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
            if (touchState.getTouched()) {
                LogUtils.d(TAG, "触摸了手部传感器");
                if (fSay != null) {
                    LogUtils.d(TAG, "停止讲话");
                    fSay.requestCancellation();
                } else {
                    LogUtils.d(TAG, "sayFuture为null");
                }
            }
        };
        leftHandSensor.addOnStateChangedListener(onStateChangedListenerHand);
        rightHandSensor.addOnStateChangedListener(onStateChangedListenerHand);
        // 底部传感器
        TouchSensor bumperSensorBack = touch.getSensor("Bumper/Back");
        TouchSensor bumperSensorLeft = touch.getSensor("Bumper/FrontLeft");
        TouchSensor bumperSensorRight = touch.getSensor("Bumper/FrontRight");
        TouchSensor.OnStateChangedListener onStateChangedListenerBumper = touchState -> {
            LogUtils.d(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
            if (touchState.getTouched()) {
                LogUtils.d(TAG, "触摸了底部传感器");
                if (fSay != null) {
                    LogUtils.d(TAG, "停止讲话");
                    fSay.requestCancellation();
                } else {
                    LogUtils.d(TAG, "sayFuture为null");
                }
            }
        };
        bumperSensorBack.addOnStateChangedListener(onStateChangedListenerBumper);
        bumperSensorLeft.addOnStateChangedListener(onStateChangedListenerBumper);
        bumperSensorRight.addOnStateChangedListener(onStateChangedListenerBumper);
    }

    private void sendNlpMessage(AIUIAgent mAIUIAgent) {
        if (AIUIConstant.STATE_WORKING != mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }
        LogUtils.d(TAG, "start text nlp");
        String params = "data_type=text,tag=text-tag";
        byte[] textData = question.getBytes(StandardCharsets.UTF_8);

        AIUIMessage write = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
        mAIUIAgent.sendMessage(write);
    }

    @Override
    public void stop() {
    }

    private void doFallback() {
//        if (mSpeechEngine != null) {
//            Say say = SayBuilder.with(mSpeechEngine)
//                    .withText(mContext.getResources().getString(R.string.fallback_answer))
//                    .build();
//            Future fSay = say.async().run();
//            try {
//                fSay.get();
//            } catch (ExecutionException e) {
//                LogUtils.d(TAG, e);
//            } catch (CancellationException e) {
//                LogUtils.d(TAG, "Interruption during Say");
//            }
//        }
    }

    public String getAnswer() {
        return answer;
    }

    private class MyAIUIListener implements AIUIListener {
        private String answer;
        private CountDownLatch countDownLatch;
        private JSONObject resultEvent;

        private void setCountDownLatch(CountDownLatch countDownLatch) {
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
            LogUtils.d(TAG, "on event: " + event.eventType);
            LogUtils.d(TAG, "讯飞AIUI回复：" + event.info);
            switch (event.eventType) {
                case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                    // 连接到服务器
                    LogUtils.d(TAG, "连接到服务器");
                    break;
                case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                    // 与服务器断开连接
                    LogUtils.d(TAG, "与服务器断开连接");
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
                    try {
                        JSONObject data = new JSONObject(event.info).getJSONArray("data").getJSONObject(0);
                        String sub = data.getJSONObject("params").optString("sub");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if ("nlp".equals(sub)) {
                            if (content.has("cnt_id")) {
                                String cnt_id = content.getString("cnt_id");
                                JSONObject result = new JSONObject(new String(event.data.getByteArray(cnt_id), StandardCharsets.UTF_8));
                                resultEvent = result;
                                int rc = result.getJSONObject("intent").getInt("rc");
                                if (rc != 0) {
                                    LogUtils.d(TAG, "nlp rc: " + rc);
                                    countDownLatch.countDown();
                                }
                                // 服务分类
                                String service = result.getJSONObject("intent").getString("service");
                                EventMsg msg = new EventMsg();
                                switch (service) {
                                    case "app":
                                        // 操作APP
                                        String intent = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getString("intent");
                                        String appName = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getJSONArray("slots").getJSONObject(0).getString("value");
                                        msg.setAction(Constants.APP);
                                        msg.setIntent(intent);
                                        msg.setText(appName);
                                        msg.setShow(true);
                                        EventBus.getDefault().post(msg);
                                        break;
                                    case "OS3993444234.action":
                                        // 执行机器人动作
                                        String action = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getJSONArray("slots").getJSONObject(0).getString("normValue");
                                        msg.setAction(Constants.ACTION);
                                        msg.setText(action);
                                        msg.setShow(true);
                                        EventBus.getDefault().post(msg);
                                        LogUtils.d(TAG, "讯飞AIUI回复动作：" + action);
                                        break;
                                    case "OS3993444234.move":
                                        // 执行机器人移动、旋转
                                        String intentMove = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getString("intent");
                                        String direction = null, number = null;
                                        switch (intentMove) {
                                            case "move":
                                                direction = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getJSONArray("slots").getJSONObject(0).getString("normValue");
                                                number = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getJSONArray("slots").getJSONObject(1).getString("normValue");
                                                break;
                                            case "rotate":
                                                direction = result.getJSONObject("intent").getJSONArray("semantic").getJSONObject(0).getJSONArray("slots").getJSONObject(0).getString("normValue");
                                                break;
                                            default:
                                                break;
                                        }
                                        msg.setAction(Constants.MOVE);
                                        msg.setIntent(intentMove);
                                        HashMap<String, String> params = new HashMap<>(2);
                                        params.put("direction", direction);
                                        params.put("number", number);
                                        msg.setParams(params);
                                        msg.setShow(true);
                                        EventBus.getDefault().post(msg);
                                        break;
                                    case "openQA":
                                        // 自定义问答
                                        String topicId = result.getJSONObject("intent").getJSONObject("answer").getString("topicID");
                                        answer = result.getJSONObject("intent").getJSONObject("answer").getString("text");
                                        SPHelper.save("NlpAnswer", answer);
                                        msg.setAction(Constants.QA);
                                        HashMap<String, String> paramsQA = new HashMap<>(2);
                                        paramsQA.put("topicId", topicId);
                                        msg.setParams(paramsQA);
                                        msg.setText(answer);
                                        msg.setShow(true);
                                        EventBus.getDefault().post(msg);
                                        LogUtils.d(TAG, "讯飞AIUI回复自定义问答：" + answer);
                                        break;
                                    default:
                                        answer = result.getJSONObject("intent").getJSONObject("answer").getString("text");
                                        LogUtils.d(TAG, "讯飞AIUI回复：" + answer);
                                        // 古诗词有[k3]开头和[k0]结尾
                                        answer = answer.replaceAll("\\[k0]", "")
                                                .replaceAll("\\[k1]", "")
                                                .replaceAll("\\[k2]", "")
                                                .replaceAll("\\[k3]", "")
                                                .replaceAll("\\[]", "");
                                        SPHelper.save("NlpAnswer", answer);
                                        // 通过EventBus发送给UI界面更新对话列表
                                        msg.setAction(Constants.REPLY);
                                        msg.setText(answer);
                                        msg.setShow(true);
                                        EventBus.getDefault().post(msg);
                                        break;
                                }
                            }
                        }
                    } catch (Throwable e) {
                        LogUtils.d(TAG, "Event during EVENT_RESULT e: " + e);
                    } finally {
                        if (!useIFlytekTTS) {
                            countDownLatch.countDown();
                        }
                    }
                    break;
                case AIUIConstant.EVENT_ERROR:
                    //错误事件
                    LogUtils.d(TAG, "错误: " + event.arg1 + "，" + event.info);
                    LogUtils.d(TAG, "Error EVENT_ERROR: " + event.info);
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
                    LogUtils.d(TAG, "EVENT_TTS: " + "EVENT_CMD_RETURN");
                    break;
                case AIUIConstant.EVENT_TTS:
                    switch (event.arg1) {
                        case AIUIConstant.TTS_SPEAK_BEGIN:
                            LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_BEGIN");
                            break;
                        case AIUIConstant.TTS_SPEAK_PROGRESS:
                            LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_PROGRESS");
                            break;
                        case AIUIConstant.TTS_SPEAK_PAUSED:
                            LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_PAUSED");
                            break;
                        case AIUIConstant.TTS_SPEAK_RESUMED:
                            LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_RESUMED");
                            break;
                        case AIUIConstant.TTS_SPEAK_COMPLETED:
                            LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_COMPLETED");
                            if (useIFlytekTTS) {
                                LogUtils.d(TAG, "EVENT_TTS: " + "TTS_SPEAK_COMPLETED in");
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
}