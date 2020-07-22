package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.utils.ActivityController;
import com.zhongbenshuo.zbspepper.utils.LogUtils;
import com.zhongbenshuo.zbspepper.utils.PermissionUtil;
import com.zhongbenshuo.zbspepper.widget.SelectDialog;

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
     * 检查权限
     */
    private void checkPermission() {
        if (PermissionUtil.isNeedRequestPermission(this)) {
            showRequestPermissionDialog();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            showToWriteSettings();
        } else {
            openActivity(MainActivity.class);
            ActivityController.finishActivity(LogoActivity.this);
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            showToWriteSettings();
        } else {
            openActivity(MainActivity.class);
            ActivityController.finishActivity(LogoActivity.this);
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
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(intent, 500);
            }

            @Override
            public void onCancelClick() {
                ActivityController.finishActivity(LogoActivity.this);
            }
        });
        selectDialog.show();
    }

    /**
     * 提示到设置页面授予修改系统设置权限
     */
    private void showToWriteSettings() {
        SelectDialog selectDialog = new SelectDialog(mContext, getString(R.string.warning_to_write_setting_permission));
        selectDialog.setButtonText(getString(R.string.Cancel), getString(R.string.Continue));
        selectDialog.setCancelable(false);
        selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 600);
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
            } else {
                openActivity(MainActivity.class);
                ActivityController.finishActivity(LogoActivity.this);
            }
        } else if (requestCode == 600) {
            //Settings.System.canWrite方法检测授权结果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
                showRequestPermissionDialog();
            } else {
                openActivity(MainActivity.class);
                ActivityController.finishActivity(LogoActivity.this);
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
