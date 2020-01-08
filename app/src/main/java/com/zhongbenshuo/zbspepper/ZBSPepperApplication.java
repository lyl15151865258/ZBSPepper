package com.zhongbenshuo.zbspepper;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.aldebaran.qi.sdk.util.IOUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zhongbenshuo.zbspepper.constant.Iflytek;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.utils.CrashHandler;
import com.zhongbenshuo.zbspepper.utils.encrypt.RSAUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Application类
 * Created by Li Yuliang on 2019/03/26.
 *
 * @author LiYuliang
 * @version 2019/03/26
 */

public class ZBSPepperApplication extends Application {

    private static final String TAG = "ZBSPepperApplication";
    private static ZBSPepperApplication instance;
    public static String publicKeyString, privateKeyString;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SPHelper.init(this);
        // 捕捉异常
        CrashHandler.getInstance().init(this);
        // 检查在aiui_phone是否配置appid。
        checkAppIdAndKey();
        // 初始化讯飞语音唤醒
        initWakeUp();
        // 初始化加密秘钥
        initKey();
    }

    private void initKey() {
        new Thread(() -> {
            // 生成RSA密钥对
            SparseArray<String> keyMap = null;
            try {
                keyMap = RSAUtils.genKeyPair(1024);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (keyMap != null) {
                publicKeyString = keyMap.get(0);
                privateKeyString = keyMap.get(1);
            }
        }).start();
    }

    // 检查是否配置在讯飞开放平台创建应用所对应的appid。
    private void checkAppIdAndKey() {
        String params = IOUtils.fromAsset(this, "cfg/aiui_phone.cfg");
        params = params.replace("\n", "").replace("\t", "").replace(" ", "");
        Log.i(TAG, "NlpApplication  params " + params);
        try {
            JSONObject paramsJSonObject = new JSONObject(params);
            String appId = paramsJSonObject.getJSONObject("login").getString("appid");
            if (TextUtils.isEmpty(appId)) {
                Toast.makeText(this, getResources().getString(R.string.check_appid_key), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error during check appid " + e);
        }
    }

    /**
     * 初始化讯飞语音唤醒
     */
    private void initWakeUp() {
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        StringBuilder param = new StringBuilder();
        param.append("appid=" + Iflytek.APP_ID);
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(instance, param.toString());
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return application实例
     */
    public static ZBSPepperApplication getInstance() {
        if (instance == null) {
            instance = new ZBSPepperApplication();
        }
        return instance;
    }

}
