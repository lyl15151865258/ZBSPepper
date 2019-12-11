package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.zhongbenshuo.zbspepper.R;
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
        setContentView(R.layout.activity_introduction);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(R.string.MessageBoard, R.drawable.back_white, -1, -1, -1, onClickListener);


        QiSDK.register(this, robotLifecycleCallbacks);
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

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            Say say = SayBuilder.with(qiContext).withText("留言板").build();
            say.run();
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
