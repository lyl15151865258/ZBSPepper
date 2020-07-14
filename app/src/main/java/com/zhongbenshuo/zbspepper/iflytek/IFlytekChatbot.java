package com.zhongbenshuo.zbspepper.iflytek;

import android.content.Context;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseChatbot;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.ReplyPriority;
import com.aldebaran.qi.sdk.object.conversation.StandardReplyReaction;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 人机对话过程中，讯飞语义理解对pepper听到的文本进行处理，执行语音合成。
 */

public class IFlytekChatbot extends BaseChatbot {

    private static final String TAG = "IFlytekChatbot";
    private QiContext qiContent;
    private Context mContext;
    private Future<Void> sayFuture;

    public IFlytekChatbot(QiContext qiContent, Context context) {
        super(qiContent);
        this.qiContent = qiContent;
        mContext = context;

        Touch touch = qiContent.getTouch();
        // 头部传感器
        TouchSensor touchSensor = touch.getSensor("Head/Touch");
        touchSensor.addOnStateChangedListener(touchState -> {
            LogUtils.d(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
            if (touchState.getTouched()) {
                LogUtils.d(TAG, "触摸了头部传感器");
                if (sayFuture != null) {
                    LogUtils.d(TAG, "停止讲话");
                    sayFuture.requestCancellation();
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
            }
        };
        bumperSensorBack.addOnStateChangedListener(onStateChangedListenerBumper);
        bumperSensorLeft.addOnStateChangedListener(onStateChangedListenerBumper);
        bumperSensorRight.addOnStateChangedListener(onStateChangedListenerBumper);

    }

    @Override
    public StandardReplyReaction replyTo(Phrase phrase, Locale locale) {
        if (phrase != null) {
            String text = phrase.getText();
            if (!text.isEmpty()) {
                Log.d(TAG, "nuance cloud asr string is :" + text);
                // 通过EventBus发送给UI界面更新对话列表
                EventMsg msg = new EventMsg();
                msg.setTag(Constants.LISTEN);
                msg.setMsg(text);
                EventBus.getDefault().post(msg);
                // 讯飞的nlp与tts处理nuance得到的文本。
                IFlytekNlpReaction iFlytekNlpReaction = new IFlytekNlpReaction(qiContent, text);
                sayFuture = iFlytekNlpReaction.getSayFuture();
                // tts执行后，pepper发起听。
                return new StandardReplyReaction(iFlytekNlpReaction, ReplyPriority.NORMAL);
            }
        }
        return null;
    }

    @Override
    public void acknowledgeHeard(Phrase phrase, Locale locale) {
        Log.i(TAG, "Last phrase heard by the robot and whose chosen answer is not mine: " + phrase.getText());
    }

    @Override
    public void acknowledgeSaid(Phrase phrase, Locale locale) {
        Log.i(TAG, "Another chatbot answered: " + phrase.getText());
    }

}