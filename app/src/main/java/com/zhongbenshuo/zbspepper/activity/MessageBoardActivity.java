package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.widget.LinedEditText;

import java.lang.reflect.Method;

/**
 * 留言板页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class MessageBoardActivity extends BaseActivity {

    private Context mContext;
    private LinedEditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mContext = this;
        findViewById(R.id.ivBack).setOnClickListener(onClickListener);
        etMessage = findViewById(R.id.etMessage);
        disableShowInput();
        etMessage.addTextChangedListener(textWatcher);
        etMessage.requestFocus();
    }

    // 禁止显示软键盘
    public void disableShowInput() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            etMessage.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(etMessage, false);
            } catch (Exception e) {//TODO: handle exception
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(etMessage, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            etMessage.setSelection(s.length());
        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.ivBack:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化唤醒对象
        WakeUpUtil.getInstance(this);
    }

}
