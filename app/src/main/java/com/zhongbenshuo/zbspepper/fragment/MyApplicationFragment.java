package com.zhongbenshuo.zbspepper.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.adapter.AppAdapter;
import com.zhongbenshuo.zbspepper.bean.AppInfo;
import com.zhongbenshuo.zbspepper.utils.ApkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 我的应用页面
 * Created at 2020/6/22 0022 16:21
 *
 * @author : LiYuliang
 * @version : 2020/6/22 0022 16:21
 */

public class MyApplicationFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    private List<AppInfo> appList;
    private AppAdapter appAdapter;
    private AppAdapter.OnItemClickListener onItemClickListener = (view, position) -> ApkUtils.openAppByPackageName(mContext, appList.get(position).getPackageName());
    private AppAdapter.OnItemLongClickListener onItemLongClickListener = (view, position) -> ApkUtils.unInstall(mContext, appList.get(position).getPackageName());
    private BroadcastReceiver installListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String type = null;
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                // 安装成功
                type = "安装成功";
                appList.clear();
                appList.addAll(ApkUtils.scanApps(mContext));
                appAdapter.notifyDataSetChanged();
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                // 卸载成功
                type = "卸载成功";
                appList.clear();
                appList.addAll(ApkUtils.scanApps(mContext));
                appAdapter.notifyDataSetChanged();
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                // 替换成功
                type = "更新成功";
                appList.clear();
                appList.addAll(ApkUtils.scanApps(mContext));
                appAdapter.notifyDataSetChanged();
            } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                // 应用被更改
                type = "应用被更改";
                appList.clear();
                appList.addAll(ApkUtils.scanApps(mContext));
                appAdapter.notifyDataSetChanged();
            }
            showToast(type);
        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_application, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();

        RecyclerView rvApps = view.findViewById(R.id.rvApps);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 8);
        rvApps.setLayoutManager(gridLayoutManager);

        appList = ApkUtils.scanApps(mContext);
        appAdapter = new AppAdapter(mContext, appList);
        appAdapter.setOnItemClickListener(onItemClickListener);
        appAdapter.setOnItemLongClickListener(onItemLongClickListener);
        rvApps.setAdapter(appAdapter);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(installListener, intentFilter);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(installListener);
    }
}
