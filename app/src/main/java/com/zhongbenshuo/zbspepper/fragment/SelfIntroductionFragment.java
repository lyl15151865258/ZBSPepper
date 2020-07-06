package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;

import org.jetbrains.annotations.NotNull;

/**
 * 自我介绍页面
 * Created at 2020/6/22 0022 16:21
 *
 * @author : LiYuliang
 * @version : 2020/6/22 0022 16:21
 */

public class SelfIntroductionFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_self_introduction, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();

        return view;
    }

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

}
