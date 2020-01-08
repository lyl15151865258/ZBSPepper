package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.widget.MyToolbar;

/**
 * 留言板页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class MessageBoardActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(R.string.MessageBoard, R.drawable.back_white, -1, -1, -1, onClickListener);

    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.toolbarLeft:
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
