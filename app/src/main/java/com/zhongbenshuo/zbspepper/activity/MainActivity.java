package com.zhongbenshuo.zbspepper.activity;

import android.os.Bundle;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.zhongbenshuo.zbspepper.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, robotLifecycleCallbacks);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, robotLifecycleCallbacks);
        super.onDestroy();
    }

    private RobotLifecycleCallbacks robotLifecycleCallbacks = new RobotLifecycleCallbacks() {
        // 该onRobotFocusGained和onRobotFocusLost方法在后台线程执行，所以当我们将同步使用QiSDK UI线程不会被阻塞。
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            // 获得焦点
            Say say = SayBuilder.with(qiContext).withText("你好").build();
            say.run();
//            // Create the topic
//            Topic topic = TopicBuilder.with(qiContext)
//                    .withResource(R.raw.hello)
//                    .build();
//            //Create the chatbot
//            QiChatbot qiChatbot = QiChatbotBuilder
//                    .with(qiContext)
//                    .withTopic(topic).build();
//            //Create the Chat
//            Chat chat = ChatBuilder
//                    .with(qiContext)
//                    .withChatbot(qiChatbot).build();
//            chat.run();
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
