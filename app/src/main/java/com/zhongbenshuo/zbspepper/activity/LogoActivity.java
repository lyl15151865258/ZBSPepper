package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.RSAResult;
import com.zhongbenshuo.zbspepper.bean.Result;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;
import com.zhongbenshuo.zbspepper.network.ExceptionHandle;
import com.zhongbenshuo.zbspepper.network.NetClient;
import com.zhongbenshuo.zbspepper.network.NetworkObserver;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.utils.GsonUtils;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.NetworkUtil;
import com.zhongbenshuo.zbspepper.utils.PermissionUtil;
import com.zhongbenshuo.zbspepper.widget.SelectDialog;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Logo页面
 * Created at 2019/10/15 0015 15:52
 *
 * @author : LiYuliang
 * @version : 2019/10/15 0015 15:52
 */

public class LogoActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        checkPermission();
    }

    /**
     * 获取服务器的RSA公钥（不需要加解密）
     */
    private void getRSAPublicKey() {
        Observable<Result> observable = NetClient.getInstance(NetClient.getBaseUrlProject(), false, false).getZbsApi().getRSAPublicKey();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("网络不可用");
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                openActivity(MainActivity.class);
                ActivityController.finishActivity(LogoActivity.this);
            }

            @Override
            public void onNext(Result result) {
                RSAResult rsaResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), RSAResult.class);
                LogUtils.d(TAG, "服务器的RSA公钥是：" + rsaResult.getRsaPublicKey());
                SPHelper.save("serverPublicKey", rsaResult.getRsaPublicKey());
                openActivity(MainActivity.class);
                ActivityController.finishActivity(LogoActivity.this);
            }
        });
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (PermissionUtil.isNeedRequestPermission(this)) {
            showRequestPermissionDialog();
        } else {
            getRSAPublicKey();
        }
    }

    /**
     * 显示权限申请说明
     */
    private void showRequestPermissionDialog() {
        SelectDialog selectDialog = new SelectDialog(mContext, getString(R.string.warning_request_permission));
        selectDialog.setButtonText(getString(R.string.Cancel), getString(R.string.Continue));
        selectDialog.setCancelable(false);
        selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                // 开始权限检查
                PermissionUtil.requestPermission(LogoActivity.this);
            }

            @Override
            public void onCancelClick() {
                ActivityController.finishActivity(LogoActivity.this);
            }
        });
        selectDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.d(TAG, "走了onRequestPermissionsResult");
        if (PermissionUtil.isNeedRequestPermission(this)) {
            // 权限被拒绝，提示用户到设置页面授予权限（防止用户点击了“不再提示”后，无法通过弹窗申请权限）
            showToSettings();
        } else {
            getRSAPublicKey();
        }
    }

    /**
     * 提示到设置页面授予权限
     */
    private void showToSettings() {
        SelectDialog selectDialog = new SelectDialog(mContext, getString(R.string.warning_to_setting_permission));
        selectDialog.setButtonText(getString(R.string.Cancel), getString(R.string.Continue));
        selectDialog.setCancelable(false);
        selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 500);
            }

            @Override
            public void onCancelClick() {
                ActivityController.finishActivity(LogoActivity.this);
            }
        });
        selectDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 500) {
            // 从设置页面返回来
            if (PermissionUtil.isNeedRequestPermission(this)) {
                // 权限被拒绝，提示用户到设置页面授予权限（防止用户点击了“不再提示”后，无法通过弹窗申请权限）
                showToSettings();
            }
        }
    }

    /**
     * Logo页面不允许退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
