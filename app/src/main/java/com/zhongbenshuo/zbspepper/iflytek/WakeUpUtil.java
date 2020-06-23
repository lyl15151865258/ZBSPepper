package com.zhongbenshuo.zbspepper.iflytek;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.zhongbenshuo.zbspepper.activity.BaseActivity;
import com.zhongbenshuo.zbspepper.fragment.ChatFragment;
import com.zhongbenshuo.zbspepper.constant.Iflytek;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 语音唤醒工具类
 * Created at 2020/1/8 0008 18:05
 *
 * @author : LiYuliang
 * @version : 2020/1/8 0008 18:05
 */

public class WakeUpUtil {

    private static final String TAG = "WakeUpUtil";
    private BaseActivity baseActivity;
    // 语音唤醒对象
    private VoiceWakeuper voiceWakeuper;
    // 设置门限值 ： 门限值越低越容易被唤醒
    private static final int curThresh = 1600;
    private static final String keep_alive = "1";
    private static final String ivwNetMode = "1";

    private WakeUpUtil(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        // 初始化唤醒对象
        voiceWakeuper = VoiceWakeuper.createWakeuper(baseActivity, initListener);
        LogUtils.d(TAG, "初始化唤醒对象");
        //非空判断，防止因空指针使程序崩溃
        voiceWakeuper = VoiceWakeuper.getWakeuper();
        if (voiceWakeuper != null) {
            LogUtils.d(TAG, "voiceWakeuper不为空");
            // 清空参数
            voiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            voiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh + ";");
            // 设置唤醒模式
            voiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            voiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            voiceWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径
            voiceWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            voiceWakeuper.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
            voiceWakeuper.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //voiceWakeuper.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
            // 启动唤醒
            /*	voiceWakeuper.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

            voiceWakeuper.startListening(mWakeuperListener);
				/*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/msc/ivw1.wav");
				byte[] byetsFromFile = getByetsFromFile(file);
				voiceWakeuper.writeAudio(byetsFromFile,0,byetsFromFile.length);*/
            //	voiceWakeuper.stopListening();
        } else {
            baseActivity.showToast("唤醒未初始化");
            LogUtils.d(TAG, "唤醒未初始化");
        }
    }

    private InitListener initListener = i -> {
        if (i == ErrorCode.SUCCESS) {
            LogUtils.d(TAG, "语音唤醒初始化成功");
        } else {
            LogUtils.d(TAG, "语音唤醒初始化失败");
        }
    };

    public static WakeUpUtil getInstance(BaseActivity baseActivity) {
        return new WakeUpUtil(baseActivity);
    }

    public void onPause() {
        voiceWakeuper.stopListening();
    }

    public void onDestroy() {
        // 销毁合成对象
        voiceWakeuper = VoiceWakeuper.getWakeuper();
        if (voiceWakeuper != null) {
            voiceWakeuper.destroy();
        }
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(baseActivity, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + Iflytek.APP_ID + ".jet");
        LogUtils.d(TAG, "resPath: " + resPath);
        return resPath;
    }

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
                        wakeupContent = "齐天大圣";
                        break;
                    default:
                        break;
                }
                if (wakeupContent != null) {
                    Intent intent = new Intent(baseActivity, ChatFragment.class);
                    intent.putExtra("wakeupContent", wakeupContent);
                    baseActivity.startActivity(intent);
                }
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            LogUtils.d(TAG, "唤醒结果：" + resultString);
        }

        @Override
        public void onError(SpeechError error) {
            baseActivity.showToast(error.getPlainDescription(true));
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

}
