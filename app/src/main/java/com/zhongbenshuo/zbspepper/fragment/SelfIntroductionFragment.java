package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.bean.Result;
import com.zhongbenshuo.zbspepper.constant.ErrorCode;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.network.ExceptionHandle;
import com.zhongbenshuo.zbspepper.network.NetClient;
import com.zhongbenshuo.zbspepper.network.NetworkObserver;
import com.zhongbenshuo.zbspepper.utils.NetworkUtil;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private AppCompatTextView tvSelf;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_self_introduction, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        tvSelf = view.findViewById(R.id.tvSelf);
        tvSelf.setText(getString(R.string.self));
        querySelfIntroduction();
        return view;
    }

    /**
     * 获取自我介绍文本
     */
    private void querySelfIntroduction() {
        Observable<Result> observable = NetClient.getInstance(NetClient.getBaseUrl(), false).getZbsApi().querySelfIntroduction();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(mContext) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    loadLastSelfIntroduction();
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                loadLastSelfIntroduction();
            }

            @Override
            public void onNext(Result result) {
                if (result.getCode() == ErrorCode.SUCCESS) {
                    String self = "\t\t\t\t\t" + String.valueOf(result.getData()).replace("\n", "\n\t\t\t\t\t");
                    SPHelper.save("self", self);
                    tvSelf.setText(self);
                } else {
                    loadLastSelfIntroduction();
                }
            }
        });
    }

    private void loadLastSelfIntroduction() {
        tvSelf.setText(SPHelper.getString("self", ""));
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
