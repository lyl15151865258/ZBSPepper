package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.widget.LoadingDialog;

import org.jetbrains.annotations.NotNull;

/**
 * fragment基类
 * Created by Li Yuliang on 2017/2/13 0013.
 *
 * @author LiYuliang
 * @version 2017/10/30
 */

public abstract class BaseFragment extends Fragment {

    public String TAG = getClass().getName();

    protected float mDensity;
    protected int mDensityDpi;
    protected int mWidth;
    protected int mHeight;
    protected float mRatio;
    protected int mAvatarSize;
    protected ViewGroup viewGroup;
    protected LayoutInflater mInflater;

    private boolean isPrepared;
    // 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
    private boolean isFirstResume = true;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    private Context mContext;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        loadingDialog = new LoadingDialog(mContext, R.style.loading_dialog);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onCreate() ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public abstract void onFirstUserVisible();

    /**
     * fragment可见（切换回来或者onResume）
     */
    public abstract void onUserVisible();

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public abstract void onFirstUserInvisible();

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public abstract void onUserInvisible();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onCreateView() ");
        this.mInflater = inflater;
        this.viewGroup = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onViewCreated() ");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
        //取消显示dialog
        cancelDialog();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onPause() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onDestroy() ");
    }

    /**
     * 显示加载的dialogs
     *
     * @param context    Context对象
     * @param msg        显示的信息
     * @param cancelable 是否可取消
     */
    public void showLoadingDialog(Context context, String msg, boolean cancelable) {
        if (!loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
            loadingDialog.setCancelable(cancelable);

            if (!((AppCompatActivity) context).isFinishing()) {
                //显示dialog
                loadingDialog.show();
                loadingDialog.setMessage(msg);
            }
        }
    }

    /**
     * 显示加载的dialogs
     *
     * @param context    Context对象
     * @param cancelable 是否可取消
     */
    public void showLoadingDialog(Context context, boolean cancelable) {
        if (!loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
            loadingDialog.setCancelable(cancelable);

            if (!((AppCompatActivity) context).isFinishing()) {
                //显示dialog
                loadingDialog.show();
            }
        }
    }

    /**
     * 取消dialog显示
     */
    public void cancelDialog() {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }


    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
