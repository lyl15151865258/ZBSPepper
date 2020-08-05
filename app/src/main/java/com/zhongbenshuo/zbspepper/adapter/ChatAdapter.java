package com.zhongbenshuo.zbspepper.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.ChatPicture;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.interfaces.MessageType;
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

    private Context mContext;
    private List<MessageType> list;
    private RecyclerView rvChat;

    public ChatAdapter(Context mContext, RecyclerView rv) {
        this.mContext = mContext;
        rvChat = rv;
        list = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {
            case MessageType.TEXT:
                // 文字消息
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_text, viewGroup, false);
                return new TextViewHolder(itemView);
            case MessageType.PICTURE:
                // 图片
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_picture, viewGroup, false);
                return new PictureViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case MessageType.TEXT:
                ((TextViewHolder) viewHolder).bindView(position);
                break;
            case MessageType.PICTURE:
                ((PictureViewHolder) viewHolder).bindView(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    public void insertData(MessageType messageType) {
        list.add(messageType);
        notifyItemInserted(getItemCount() - 1);
        rvChat.scrollToPosition(getItemCount() - 1);
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime, txtLeft, txtRight, txtLeftDown;
        private LinearLayout linearLeft, linearRight;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            txtLeft = itemView.findViewById(R.id.txtLeft);
            txtRight = itemView.findViewById(R.id.txtRight);
            txtLeftDown = itemView.findViewById(R.id.txtLeft_down);
            linearLeft = itemView.findViewById(R.id.linear_left);
            linearRight = itemView.findViewById(R.id.linear_right);
        }

        void bindView(int position) {
            ChatText chatText = (ChatText) list.get(position);
            // 显示时间
            if (position == 0) {
                // 第一条显示时间
                tvTime.setText(TimeUtils.handleDate(chatText.getTime()));
                tvTime.setVisibility(View.VISIBLE);
            } else {
                // 不是第一条就和上一条比较，如果时间间隔大于5分钟，则显示新的时间
                if (chatText.getTime() - list.get(position - 1).getTime() > 1000 * 60 * 5) {
                    tvTime.setText(TimeUtils.handleDate(chatText.getTime()));
                    tvTime.setVisibility(View.VISIBLE);
                } else {
                    tvTime.setVisibility(View.GONE);
                }
            }
            // 内容显示
            String content = chatText.getChatContent();
            switch (chatText.getChatType()) {
                case REPLY_CLEAR:
                    // 清晰的回复
                    linearLeft.setVisibility(View.VISIBLE);
                    txtLeftDown.setVisibility(View.GONE);
                    linearRight.setVisibility(View.GONE);
                    txtLeft.setText(content);
                    break;
                case REPLY_BLURRY:
                    // 模糊的回复
                    linearLeft.setVisibility(View.VISIBLE);
                    txtLeftDown.setVisibility(View.VISIBLE);
                    linearRight.setVisibility(View.GONE);
                    txtLeft.setText(content);
                    txtLeftDown.setText("尝试提高音量，语速保持适中");
                    break;
                case LISTEN:
                    // 听到的内容
                    linearLeft.setVisibility(View.GONE);
                    linearRight.setVisibility(View.VISIBLE);
                    txtRight.setText(content);
                    break;
                default:
                    break;
            }
        }
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime, txtLeft;
        private ImageView picture;

        public PictureViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            txtLeft = itemView.findViewById(R.id.txtLeft);
            picture = itemView.findViewById(R.id.picture);
        }

        void bindView(int position) {
            ChatPicture chatPicture = (ChatPicture) list.get(position);
            // 显示时间
            if (position == 0) {
                // 第一条显示时间
                tvTime.setText(TimeUtils.handleDate(chatPicture.getTime()));
                tvTime.setVisibility(View.VISIBLE);
            } else {
                // 不是第一条就和上一条比较，如果时间间隔大于5分钟，则显示新的时间
                if (chatPicture.getTime() - list.get(position - 1).getTime() > 1000 * 60 * 5) {
                    tvTime.setText(TimeUtils.handleDate(chatPicture.getTime()));
                    tvTime.setVisibility(View.VISIBLE);
                } else {
                    tvTime.setVisibility(View.GONE);
                }
            }
            if (TextUtils.isEmpty(chatPicture.getText())) {
                txtLeft.setVisibility(View.GONE);
            } else {
                txtLeft.setVisibility(View.VISIBLE);
                txtLeft.setText(chatPicture.getText());
            }
            // 内容显示
            switch (chatPicture.getChatType()) {
                case URL:
                    // 图片链接
                    if (TextUtils.isEmpty(chatPicture.getUrl())) {
                        picture.setVisibility(View.GONE);
                    } else {
                        picture.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(chatPicture.getUrl()).into(picture);
                    }
                    break;
                case BITMAP:
                    // bitmap对象
                    if (chatPicture.getBitmap() == null) {
                        picture.setVisibility(View.GONE);
                    } else {
                        picture.setVisibility(View.VISIBLE);
                        picture.setImageBitmap(chatPicture.getBitmap());
                    }
                    break;
                case RESOURCE:
                    // 资源文件
                    if (chatPicture.getResource() <= 0) {
                        picture.setVisibility(View.GONE);
                    } else {
                        picture.setVisibility(View.VISIBLE);
                        picture.setImageResource(chatPicture.getResource());
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
