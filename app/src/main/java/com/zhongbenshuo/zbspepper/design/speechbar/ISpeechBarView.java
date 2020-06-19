package com.zhongbenshuo.zbspepper.design.speechbar;

import com.aldebaran.qi.sdk.object.conversation.Phrase;

public interface ISpeechBarView {
    /**
     * Update the view to show if the robot is listening
     *
     * @param listening boolean representing if the robot is currently listening
     */
    void showListening(boolean listening);

    /**
     * Update the view to show if the robot is hearing human speech
     *
     * @param hearing boolean representing if the robot is currently hearing a human voice
     */
    void showHearing(boolean hearing);

    /**
     * Update the view to show the text heard by the robot
     *
     * @param text the heard text
     */
    void showHeardText(String text);

    /**
     * Update the view to show that the robot does not know what to answer but has a fallback answer
     *
     * @param phrase the heard phrase
     */
    void showOnFallbackReplyFoundFor(Phrase phrase);

    /**
     * Update the view to show that the robot don't know what to answer
     *
     * @param phrase the heard phrase
     */
    void onNoReplyFoundFor(Phrase phrase);

    /**
     * Update the view to show that the robot did not understood
     */
    void showOnNoPhraseRecognized();

    /**
     * Update the view to hide text of the SpeechBarView
     */
    void clearHeardText();

    /**
     * Update the view to show that the robot is processing the speech input
     *
     * @param processing boolean representing if the robot is currently processing
     */
    void showProcessing(boolean processing);
}
