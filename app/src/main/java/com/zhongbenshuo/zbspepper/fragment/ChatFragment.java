package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.adapter.ChatAdapter;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

/**
 * 聊天问答
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatFragment extends BaseFragment {

    private static final String TAG = "ChatActivity";
    private Context mContext;
    private MainActivity mainActivity;
    private ChatAdapter chatAdapter;

    @Override
    public void onFirstUserVisible() {

    }

    @Override
    public void onUserVisible() {

    }

    @Override
    public void onFirstUserInvisible() {

    }

    @Override
    public void onUserInvisible() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();

        RecyclerView rvChat = view.findViewById(R.id.rvChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(rvChat);
        rvChat.setAdapter(chatAdapter);

        EventBus.getDefault().register(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    int finalI = i;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.LISTEN, "测试数据" + finalI));
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();

        return view;
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        switch (msg.getTag()) {
            case Constants.LISTEN:
                // 听到的内容
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.LISTEN, msg.getMsg()));
                break;
            case Constants.REPLY:
                // 听清的回复
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.REPLY_CLEAR, msg.getMsg()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}