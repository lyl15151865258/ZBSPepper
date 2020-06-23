package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.constant.BannerResources;
import com.zhongbenshuo.zbspepper.glide.loader.GlideImageLoader;

import org.jetbrains.annotations.NotNull;

/**
 * 经营范围页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class BusinessScopeFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    private Banner banner;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_company_profile, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        banner = view.findViewById(R.id.banner);
        banner.setImages(BannerResources.getBusinessScopeResources())
                .setBannerStyle(BannerConfig.NUM_INDICATOR)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(8000)
                .start();
        banner.stopAutoPlay();
        return view;
    }

    @Override
    public void onFirstUserVisible() {
        banner.startAutoPlay();
    }

    @Override
    public void onUserVisible() {
        banner.startAutoPlay();
    }

    @Override
    public void onFirstUserInvisible() {

    }

    @Override
    public void onUserInvisible() {
        banner.stopAutoPlay();
    }

}
