package com.zhongbenshuo.zbspepper.design.activity.conversationstatus;

import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.ConversationStatus;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.design.speechbar.SpeechBarView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zhongbenshuo.zbspepper.design.activity.conversationstatus.SpeechBarDisplayStrategy.OVERLAY;

public class ConversationStatusBinder implements ConversationStatus.OnNoReplyFoundForListener, ConversationStatus.OnHearingChangedListener, ConversationStatus.OnListeningChangedListener, ConversationStatus.OnNoPhraseRecognizedListener, ConversationStatus.OnNormalReplyFoundForListener, ConversationStatus.OnFallbackReplyFoundForListener {

    private static final long AUTO_HIDE_DELAY = 8_000;
    private static final long ANIMATION_DURATION = 300;
    private final Object conversationStatusLock = new Object();
    private ConversationStatus conversationStatus;
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private Timer autoHideTimer;
    private Future<Void> conversationFuture;
    private Future<Void> releaseFuture;
    private SpeechBarView speechBarView;
    private ConstraintLayout constraintLayout;
    private ConstraintSet defaultConstraintSet;
    private ConstraintSet alwaysVisibleBarConstraintSet;
    private SpeechBarDisplayStrategy displayStrategy = SpeechBarDisplayStrategy.ALWAYS;
    private ConstraintSet overlayVisibleBarConstraintSet;

    public void init(ConstraintLayout constraintLayout, SpeechBarView speechBarView) {
        this.speechBarView = speechBarView;
        this.constraintLayout = constraintLayout;

        if (defaultConstraintSet == null) {
            defaultConstraintSet = new ConstraintSet();
            defaultConstraintSet.clone(constraintLayout);
        }

        if (alwaysVisibleBarConstraintSet == null) {
            alwaysVisibleBarConstraintSet = new ConstraintSet();
            alwaysVisibleBarConstraintSet.clone(speechBarView.getContext(), R.layout.activity_robot_speech_bar_open);
        }

        if (overlayVisibleBarConstraintSet == null) {
            overlayVisibleBarConstraintSet = new ConstraintSet();
            overlayVisibleBarConstraintSet.clone(speechBarView.getContext(), R.layout.activity_robot_speech_bar_overlay);
        }

        resetBarViewState();
    }

    public void bind(QiContext qiContext, SpeechBarView speechBarView) {
        this.speechBarView = speechBarView;

        resetBarViewState();

        releaseConversationSignals();
        watchConversationSignals(qiContext);
    }

    public void unbind(boolean screenIsClosing) {
        isListening.set(false);

        resetBarViewState();
        releaseConversationSignals();

        if (screenIsClosing) {
            cancelAutoHideTimer();
            speechBarView = null;
            constraintLayout = null;
        }
    }

    @UiThread
    private void runOnBarContext(Runnable runnable) {
        try {
            if (speechBarView != null) {
                speechBarView.post(() -> {
                    if (speechBarView != null) {
                        runnable.run();
                    }
                });
            }
        } catch (Exception ignored) {
            // Prevent Android animation crash as is it not thread safe
        }
    }

    @UiThread
    private void runOnLayoutContext(Runnable runnable) {
        try {
            if (constraintLayout != null) {
                constraintLayout.post(() -> {
                    if (constraintLayout != null) {
                        runnable.run();
                    }
                });
            }
        } catch (Exception ignored) {
            // Prevent Android animation crash as is it not thread safe
        }
    }

    private void resetBarViewState() {
        runOnBarContext(() -> speechBarView.showListening(false));
        runOnBarContext(this::applySpeechBarViewDisplayStrategy);
    }

    private void hideSpeechBar() {
        runOnLayoutContext(() -> {
            Transition transition = new AutoTransition();
            transition.setDuration(ANIMATION_DURATION);
            TransitionManager.beginDelayedTransition(constraintLayout, transition);
            defaultConstraintSet.applyTo(constraintLayout);
        });
    }

    private void showSpeechBar() {
        runOnLayoutContext(() -> {
            Transition transition = new AutoTransition();
            if (displayStrategy == SpeechBarDisplayStrategy.ALWAYS) {
                transition.setDuration(0);
                TransitionManager.beginDelayedTransition(constraintLayout, transition);
                alwaysVisibleBarConstraintSet.applyTo(constraintLayout);
            } else if (displayStrategy == OVERLAY) {
                transition.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(constraintLayout, transition);
                overlayVisibleBarConstraintSet.applyTo(constraintLayout);
            }
        });
    }

    private void applySpeechBarViewDisplayStrategy() {
        switch (displayStrategy) {
            case ALWAYS:
                showSpeechBar();
                break;
            case OVERLAY:
                if (isListening.get()) {
                    showSpeechBar();
                } else {
                    hideSpeechBar();
                }
                break;

        }
    }

    private void releaseConversationSignals() {
        synchronized (conversationStatusLock) {
            if (conversationStatus != null) {
                releaseFuture = Future.waitAll(
                        conversationStatus.async().removeOnNormalReplyFoundForListener(this),
                        conversationStatus.async().removeOnHearingChangedListener(this),
                        conversationStatus.async().removeOnListeningChangedListener(this),
                        conversationStatus.async().removeOnFallbackReplyFoundForListener(this),
                        conversationStatus.async().removeOnNoPhraseRecognizedListener(this),
                        conversationStatus.async().removeOnNoReplyFoundForListener(this)
                );
            }

            conversationStatus = null;
        }
    }

    private void watchConversationSignals(QiContext qiContext) {
        conversationFuture = qiContext.getConversation().async().status(qiContext.getRobotContext())
                .andThenConsume(status -> {
                    synchronized (conversationStatusLock) {
                        conversationStatus = status;

                        if (status == null) {
                            return;
                        }
                        status.addOnNormalReplyFoundForListener(this);
                        status.addOnHearingChangedListener(this);
                        status.addOnListeningChangedListener(this);
                        status.addOnFallbackReplyFoundForListener(this);
                        status.addOnNoPhraseRecognizedListener(this);
                        status.addOnNoReplyFoundForListener(this);
                    }
                });
    }

    public void onNormalReplyFoundFor(Phrase heard) {
        runOnBarContext(() -> speechBarView.showHeardText(heard.getText()));
    }

    public void onHearingChanged(Boolean hearing) {
        runOnBarContext(() -> speechBarView.showHearing(hearing));
    }

    public void onListeningChanged(Boolean listening) {
        isListening.set(listening);
        cancelAutoHideTimer();

        if (listening) {
            showSpeechBar();
        }

        if (!listening && displayStrategy == OVERLAY) {
            autoHideTimer = new Timer();
            autoHideTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    hideSpeechBar();
                }
            }, AUTO_HIDE_DELAY);
        }

        runOnBarContext(() -> speechBarView.showListening(listening));
    }

    private void cancelAutoHideTimer() {
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
            autoHideTimer = null;
        }
    }

    public void onFallbackReplyFoundFor(Phrase phrase) {
        runOnBarContext(() -> speechBarView.showOnFallbackReplyFoundFor(phrase));
    }

    public void onNoPhraseRecognized() {
        runOnBarContext(speechBarView::showOnNoPhraseRecognized);
    }

    public void onNoReplyFoundFor(Phrase phrase) {
        runOnBarContext(() -> speechBarView.onNoReplyFoundFor(phrase));
    }

    public void setStrategy(SpeechBarDisplayStrategy speechBarDisplayStrategy) {
        this.displayStrategy = speechBarDisplayStrategy;

        runOnBarContext(this::applySpeechBarViewDisplayStrategy);
    }
}
