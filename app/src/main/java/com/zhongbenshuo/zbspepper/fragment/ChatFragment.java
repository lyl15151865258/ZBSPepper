package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Map;

/**
 * 聊天问答
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatFragment extends BaseFragment {

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(rvChat);
        rvChat.setAdapter(chatAdapter);

        EventBus.getDefault().register(this);

        return view;
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        switch (msg.getAction()) {
            case Constants.LISTEN:
                // 听到的内容
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.LISTEN, msg.getText()));
                break;
            case Constants.REPLY:
                // 听清的回复
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.REPLY_CLEAR, msg.getText()));
                break;
            case Constants.QA:
                // 自定义问答
                Map<String, String> params = msg.getParams();
                String topicId = params.get("topicId");
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.CHATTYPE.REPLY_CLEAR, msg.getText()));
                if (!TextUtils.isEmpty(topicId)) {
                    switch (topicId) {
                        case "79993617082946208":
                            // 公司简介
                            mainActivity.moveToPosition(0);
                            break;
                        case "79994247423223502":
                            // 经营范围
                            mainActivity.moveToPosition(1);
                            break;
                        case "79994402879962156":
                            // 工程案例
                            mainActivity.moveToPosition(2);
                            break;
                        default:
                            break;
                    }
                }
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