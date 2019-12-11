package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.iflytek.IFlytekChatbot;
import com.zhongbenshuo.zbspepper.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);
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

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robotLifecycleCallbacks);
        super.onDestroy();
    }

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            mSay = SayBuilder.with(qiContext)
                    .withText(getResources().getString(R.string.greeting))
                    .build();
            mSay.async().run().andThenConsume(consume -> {
                // 自定义讯飞Chatbot。
                IFlytekChatbot iflytekChatbot = new IFlytekChatbot(qiContext, mContext);
                // 创建chat。
                mChat = ChatBuilder.with(qiContext)
                        .withChatbot(iflytekChatbot)
                        .build();

                Future<Void> chatFuture = mChat.async().run();
                LogUtils.d(TAG, "走了这里1");
                chatFuture.thenConsume(future -> {
                    LogUtils.d(TAG, "走了这里2");
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
