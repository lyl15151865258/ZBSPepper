package com.zhongbenshuo.zbspepper.iflytek;

import android.content.Context;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseChatbot;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.ReplyPriority;
import com.aldebaran.qi.sdk.object.conversation.StandardReplyReaction;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.common.DirectSayReaction;

/**
 * 人机对话过程中，讯飞语义理解对pepper听到的文本进行处理，执行语音合成。
 */
public class IFlytekChatbot extends BaseChatbot {

    private static final String TAG = "IFlytekChatbot";
    private QiContext qiContent;
    private Context mContext;

    public IFlytekChatbot(QiContext qiContent, Context context) {
        super(qiContent);
        this.qiContent = qiContent;
        mContext = context;
    }

    @Override
    public StandardReplyReaction replyTo(Phrase phrase, Locale locale) {
        if (phrase != null) {
            String text = phrase.getText();
            if (!text.isEmpty()) {
                Log.d(TAG, "nuance cloud asr string is :" + text);
                // 讯飞的nlp与tts处理nuance得到的文本。
                IFlytekNlpReaction iFlytekNlpReaction = new IFlytekNlpReaction(qiContent, text);
                // tts执行后，pepper发起听。
                return new StandardReplyReaction(iFlytekNlpReaction, ReplyPriority.NORMAL);
            }
        }
        Log.d(TAG, "answer empty: ");
        // pepper 没有听到时兜底语音。
        DirectSayReaction directSayReac = new DirectSayReaction(qiContent, this.qiContent.getResources().getString(R.string.fallback_answer));
        return new StandardReplyReaction(directSayReac, ReplyPriority.FALLBACK);
    }

    @Override
    public void acknowledgeHeard(Phrase phrase, Locale locale) {
        Log.i(TAG, "Last phrase heard by the robot and whose chosen answer is not mine: " + phrase.getText());
    }

    @Override
    public void acknowledgeSaid(Phrase phrase, Locale locale) {
        Log.i(TAG, "Another chatbot answered: " + phrase.getText());
    }

}