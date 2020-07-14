package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.transformer.ScaleInTransformer;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.adapter.ImageAdapter;
import com.zhongbenshuo.zbspepper.bean.DataBean;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.indicator.NumIndicator;

import org.jetbrains.annotations.NotNull;

/**
 * 公司简介页面
 * Created at 2019/12/10 0010 9:20
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:20
 */

public class CompanyProfileFragment extends BaseFragment {

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
        banner.setAdapter(new ImageAdapter(DataBean.getCompanyIntroductionResources()))
                .addPageTransformer(new ScaleInTransformer())
                .setIndicator(new NumIndicator(mContext))
                .setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
                .isAutoLoop(false)
                .setDelayTime(Constants.PPT_SHOW_TIME);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.isAutoLoop(true);
        banner.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stop();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        banner.destroy();
    }

}
