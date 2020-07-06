package com.zhongbenshuo.zbspepper.utils;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.zhongbenshuo.zbspepper.widget.WaveView;

/**
 * Created by xl on 2018/1/31.
 */

public class SpeechAnimUtils {
	/**
	 * 开始动画
	 */
	public static void StartAnim(WaveView waveView) {
		waveView.setDuration(4000);//一个波纹从创建到消失的时间
		waveView.setInitialRadius(45);//设置半径
		waveView.setStyle(Paint.Style.STROKE);//设置扩散风格（STROKE：线   fill:面）
		waveView.setColor(Color.parseColor("#0BB6A8"));//设置颜色
		waveView.setSpeed(500);//波纹创建速度（500ms）
		waveView.setInterpolator(new LinearOutSlowInInterpolator());
		waveView.start();
	}

	/**
	 * 缓慢停止
	 */
	public static void stop(WaveView waveView) {
		waveView.stop();
	}

	/**
	 * 立即停止
	 */
	public static void stopImmediately(WaveView waveView) {
		waveView.stopImmediately();
	}
}
