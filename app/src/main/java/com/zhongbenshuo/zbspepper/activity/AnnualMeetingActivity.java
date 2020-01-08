package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;
import com.zhongbenshuo.zbspepper.utils.ActivityController;

/**
 * 年会专栏页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class AnnualMeetingActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_meeting);
        mContext = this;
        findViewById(R.id.ivBack).setOnClickListener(onClickListener);

    }

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
