package com.zhongbenshuo.zbspepper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面的文本列表适配器
 * Created at 2020/1/7 0007 23:30
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:30
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private List<ChatText> list;
    private RecyclerView rvChat;

    public ChatAdapter(RecyclerView rv) {
        rvChat = rv;
        list = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        chatViewHolder.tvTime = view.findViewById(R.id.tvTime);
        chatViewHolder.txtLeft = view.findViewById(R.id.txtLeft);
        chatViewHolder.txtRight = view.findViewById(R.id.txtRight);
        chatViewHolder.txtLeftDown = view.findViewById(R.id.txtLeft_down);
        chatViewHolder.linearLeft = view.findViewById(R.id.linear_left);
        chatViewHolder.linearRight = view.findViewById(R.id.linear_right);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatViewHolder chatViewHolder = (ChatViewHolder) viewHolder;
        ChatText chatText = list.get(position);
        // 显示时间
        if (position == 0) {
            // 第一条显示时间
            chatViewHolder.tvTime.setText(TimeUtils.handleDate(chatText.getTime()));
            chatViewHolder.tvTime.setVisibility(View.VISIBLE);
        } else {
            // 不是第一条就和上一条比较，如果时间间隔大于5分钟，则显示新的时间
            if (chatText.getTime() - list.get(position - 1).getTime() > 1000 * 60 * 5) {
                chatViewHolder.tvTime.setText(TimeUtils.handleDate(chatText.getTime()));
                chatViewHolder.tvTime.setVisibility(View.VISIBLE);
            } else {
                chatViewHolder.tvTime.setVisibility(View.GONE);
            }
        }
        // 内容显示
        String content = chatText.getChatContent();
        switch (chatText.getChatType()) {
            case REPLY_CLEAR:
                // 清晰的回复
                chatViewHolder.linearLeft.setVisibility(View.VISIBLE);
                chatViewHolder.txtLeftDown.setVisibility(View.GONE);
                chatViewHolder.linearRight.setVisibility(View.GONE);
                chatViewHolder.txtLeft.setText(content);
                break;
            case REPLY_BLURRY:
                // 模糊的回复
                chatViewHolder.linearLeft.setVisibility(View.VISIBLE);
                chatViewHolder.txtLeftDown.setVisibility(View.VISIBLE);
                chatViewHolder.linearRight.setVisibility(View.GONE);
                chatViewHolder.txtLeft.setText(content);
                chatViewHolder.txtLeftDown.setText("尝试提高音量，语速保持适中");
                break;
            case LISTEN:
                // 听到的内容
                chatViewHolder.linearLeft.setVisibility(View.GONE);
                chatViewHolder.linearRight.setVisibility(View.VISIBLE);
                chatViewHolder.txtRight.setText(content);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime, txtLeft, txtRight, txtLeftDown;
        private LinearLayout linearLeft, linearRight;

        private ChatViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void insertData(ChatText chatText) {
        list.add(chatText);
        notifyItemInserted(getItemCount() - 1);
        rvChat.scrollToPosition(getItemCount() - 1);
    }

}
