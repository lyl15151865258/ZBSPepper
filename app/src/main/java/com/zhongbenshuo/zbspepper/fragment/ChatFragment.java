package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.builder.TakePictureBuilder;
import com.aldebaran.qi.sdk.object.camera.TakePicture;
import com.aldebaran.qi.sdk.object.image.EncodedImage;
import com.aldebaran.qi.sdk.object.image.EncodedImageHandle;
import com.aldebaran.qi.sdk.object.image.TimestampedImageHandle;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.adapter.ChatAdapter;
import com.zhongbenshuo.zbspepper.bean.ChatPicture;
import com.zhongbenshuo.zbspepper.bean.ChatText;
import com.zhongbenshuo.zbspepper.bean.EventMsg;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
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

        chatAdapter = new ChatAdapter(mContext, rvChat);
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
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.TEXTTYPE.LISTEN, msg.getText()));
                break;
            case Constants.REPLY:
                // 听清的回复
            case Constants.ANIMAL_CRIES:
                // 动物叫声
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.TEXTTYPE.REPLY_CLEAR, msg.getText()));
                break;
            case Constants.QA:
                // 自定义问答
                Map<String, String> params = msg.getParams();
                String topicId = String.valueOf(params.get("topicId"));
                chatAdapter.insertData(new ChatText(TimeUtils.getCurrentTimeMillis(), ChatText.TEXTTYPE.REPLY_CLEAR, msg.getText()));
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
            case Constants.APP:
                // 打开应用（打开相机不处理，由聊天页面处理）
                if (msg.getIntent().equals("LAUNCH") && msg.getText().equals("相机")) {
                    takePicture();
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

    /**
     * 拍照片
     */
    private void takePicture() {
        if (mainActivity.mQiContext == null) {
            return;
        }
        // 先移动到对话页面
        mainActivity.moveToPosition(4);
        Future<TakePicture> takePictureFuture = TakePictureBuilder.with(mainActivity.mQiContext).buildAsync();
        Future<TimestampedImageHandle> timestampedImageHandleFuture = takePictureFuture.andThenCompose(takePicture -> {
            LogUtils.d(TAG, "take picture launched!");
            return takePicture.async().run();
        });
        timestampedImageHandleFuture.andThenConsume(timestampedImageHandle -> {
            // Consume take picture action when it's ready
            LogUtils.d(TAG, "Picture taken");
            // get picture
            EncodedImageHandle encodedImageHandle = timestampedImageHandle.getImage();

            EncodedImage encodedImage = encodedImageHandle.getValue();
            LogUtils.d(TAG, "PICTURE RECEIVED!");

            // get the byte buffer and cast it to byte array
            ByteBuffer buffer = encodedImage.getData();
            buffer.rewind();
            final int pictureBufferSize = buffer.remaining();
            final byte[] pictureArray = new byte[pictureBufferSize];
            buffer.get(pictureArray);

            Log.i(TAG, "PICTURE RECEIVED! (" + pictureBufferSize + " Bytes)");
            // display picture
            Bitmap pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureBufferSize);

            mainActivity.runOnUiThread(() ->
                    chatAdapter.insertData(new ChatPicture(TimeUtils.getCurrentTimeMillis(), ChatPicture.PICTURETYPE.BITMAP, null, null, 0, pictureBitmap)));
        });
    }

}