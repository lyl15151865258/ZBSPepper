package com.zhongbenshuo.zbspepper;

import com.zhongbenshuo.zbspepper.utils.TimeUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        long time = TimeUtils.getCurrentTimeMillis();
        System.out.println(time);
        System.out.println(TimeUtils.timeMillis2String(time));
    }
}