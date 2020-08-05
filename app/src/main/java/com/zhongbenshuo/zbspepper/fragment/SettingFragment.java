package com.zhongbenshuo.zbspepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.object.actuation.EnforceTabletReachability;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;
import com.aldebaran.qi.sdk.object.holder.Holder;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.activity.MainActivity;
import com.zhongbenshuo.zbspepper.contentprovider.SPHelper;

import org.jetbrains.annotations.NotNull;

/**
 * 设置页面
 * Created at 2020/6/22 0022 16:21
 *
 * @author : LiYuliang
 * @version : 2020/6/22 0022 16:21
 */

public class SettingFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    // 自主能力是否关闭
    private boolean abilitiesHeld;
    private Holder holder;
    private EnforceTabletReachability enforceTabletReachability;
    private ToggleButton toggleAutonomousAbilities, toggleEnforceTabletReachability, toggleShowBatteryView, toggleTouchStopSay;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toggleAutonomousAbilities:
                    // 自主能力
                    toggleAutonomousAbilities.setEnabled(false);
                    if (abilitiesHeld) {
                        // 释放自主活动
                        releaseAbilities();
                    } else {
                        // 停止自主活动
                        holdAbilities();
                    }
                    break;
                case R.id.toggleEnforceTabletReachability:
                    break;
                case R.id.toggleShowBatteryView:
                    // 显示电池图标
                    mainActivity.showBatteryView(toggleShowBatteryView.isChecked());
                    SPHelper.save("toggleShowBatteryView", toggleShowBatteryView.isChecked());
                    showToast("已" + (toggleShowBatteryView.isChecked() ? "显示" : "隐藏") + "电池图标");
                    break;
                case R.id.toggleTouchStopSay:
                    // 触摸传感器停止讲话
                    SPHelper.save("toggleTouchStopSay", toggleTouchStopSay.isChecked());
                    showToast("已" + (toggleShowBatteryView.isChecked() ? "开启" : "关闭") + "触摸传感器停止讲话");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        toggleAutonomousAbilities = view.findViewById(R.id.toggleAutonomousAbilities);
        toggleEnforceTabletReachability = view.findViewById(R.id.toggleEnforceTabletReachability);
        toggleShowBatteryView = view.findViewById(R.id.toggleShowBatteryView);
        toggleTouchStopSay = view.findViewById(R.id.toggleTouchStopSay);

        // 设置ToggleButton默认显示
        abilitiesHeld = SPHelper.getBoolean("toggleAutonomousAbilities", false);
        toggleAutonomousAbilities.setChecked(!abilitiesHeld);
        toggleEnforceTabletReachability.setChecked(SPHelper.getBoolean("toggleEnforceTabletReachability", false));
        toggleShowBatteryView.setChecked(SPHelper.getBoolean("toggleShowBatteryView", false));
        toggleTouchStopSay.setChecked(SPHelper.getBoolean("toggleTouchStopSay", false));

        // 添加监听
        toggleAutonomousAbilities.setOnClickListener(onClickListener);
        toggleEnforceTabletReachability.setOnClickListener(onClickListener);
        toggleShowBatteryView.setOnClickListener(onClickListener);
        toggleTouchStopSay.setOnClickListener(onClickListener);
        return view;
    }

    /**
     * 停止自主活动
     */
    public void holdAbilities() {
        if (mainActivity.mQiContext != null) {
            holder = HolderBuilder.with(mainActivity.mQiContext)
                    .withAutonomousAbilities(
                            AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                            AutonomousAbilitiesType.BASIC_AWARENESS,
                            AutonomousAbilitiesType.AUTONOMOUS_BLINKING
                    )
                    .build();
            Future<Void> holdFuture = holder.async().hold();
            holdFuture.andThenConsume(Qi.onUiThread((Consumer<Void>) ignore -> {
                abilitiesHeld = true;
                SPHelper.save("toggleAutonomousAbilities", true);
                toggleAutonomousAbilities.setChecked(false);
                toggleAutonomousAbilities.setEnabled(true);
                showToast("已关闭自主活动");
            }));
        }
    }

    /**
     * 释放自主活动
     */
    private void releaseAbilities() {
        if (abilitiesHeld && mainActivity.mQiContext != null) {
            if (holder == null) {
                holder = HolderBuilder.with(mainActivity.mQiContext)
                        .withAutonomousAbilities(
                                AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                                AutonomousAbilitiesType.BASIC_AWARENESS,
                                AutonomousAbilitiesType.AUTONOMOUS_BLINKING
                        )
                        .build();
            }
            Future<Void> releaseFuture = holder.async().release();
            releaseFuture.andThenConsume(Qi.onUiThread((Consumer<Void>) ignore -> {
                abilitiesHeld = false;
                SPHelper.save("toggleAutonomousAbilities", false);
                toggleAutonomousAbilities.setChecked(true);
                toggleAutonomousAbilities.setEnabled(true);
                showToast("已打开自主活动");
            }));
        }
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
