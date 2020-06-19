package com.zhongbenshuo.zbspepper.design.activity.conversationstatus;

/**
 * Strategies related to the SpeechBarView display
 * <li>{@link #ALWAYS}</li>
 * <li>{@link #OVERLAY}</li>
 */
public enum SpeechBarDisplayStrategy {
    /*
     * Strategy that always displays the SpeechBar
     */
    ALWAYS,

    /*
     * Strategy that only displays the SpeechBar when running a Conversation action
     * The bar will automatically appear and disappear overlapping the content
     */
    OVERLAY,
}