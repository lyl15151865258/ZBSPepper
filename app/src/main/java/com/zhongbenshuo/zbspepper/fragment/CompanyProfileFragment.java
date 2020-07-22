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
import com.zhongbenshuo.zbspepper.bean.Picture;
import com.zhongbenshuo.zbspepper.bean.Result;
import com.zhongbenshuo.zbspepper.constant.Constants;
import com.zhongbenshuo.zbspepper.constant.ErrorCode;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.indicator.NumIndicator;
import com.zhongbenshuo.zbspepper.network.ExceptionHandle;
import com.zhongbenshuo.zbspepper.network.NetClient;
import com.zhongbenshuo.zbspepper.network.NetworkObserver;
import com.zhongbenshuo.zbspepper.utils.GsonUtils;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.NetworkUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        queryPepperCompany();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG,"走了onResume");
        banner.isAutoLoop(true);
        banner.start();
    }

    /**
     * 获取图片
     */
    private void queryPepperCompany() {
        Observable<Result> observable = NetClient.getInstance(NetClient.getBaseUrl(), false).getZbsApi().queryPepperCompany();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(mContext) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    loadLastPictures();
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                loadLastPictures();
            }

            @Override
            public void onNext(Result result) {
                if (result.getCode() == ErrorCode.SUCCESS) {
                    List<Picture> pictureResult = GsonUtils.parseJSONList(GsonUtils.convertJSON(result.getData()), Picture.class);
                    List<DataBean> dataBeans = new ArrayList<>();
                    for (Picture picture : pictureResult) {
                        dataBeans.add(new DataBean((NetClient.getResourceUrl() + picture.getUrl()).replace("\\", "/"), null, 1));
                    }
                    SPHelper.save("company", GsonUtils.convertJSON(dataBeans));
                    LogUtils.d(TAG,"走了setAdapter");
                    banner.setAdapter(new ImageAdapter(mContext, dataBeans))
                            .addPageTransformer(new ScaleInTransformer())
                            .setIndicator(new NumIndicator(mContext))
                            .setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
                            .isAutoLoop(false)
                            .setDelayTime(Constants.PPT_SHOW_TIME);
                }else{
                    loadLastPictures();
                }
            }
        });
    }

    /**
     * 加载上一次保存的图片
     */
    private void loadLastPictures() {
        List<DataBean> dataBeans = GsonUtils.parseJSONList(SPHelper.getString("company", GsonUtils.convertJSON(new ArrayList<>())), DataBean.class);
        banner.setAdapter(new ImageAdapter(mContext, dataBeans))
                .addPageTransformer(new ScaleInTransformer())
                .setIndicator(new NumIndicator(mContext))
                .setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
                .isAutoLoop(false)
                .setDelayTime(Constants.PPT_SHOW_TIME);
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
