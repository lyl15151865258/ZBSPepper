package com.zhongbenshuo.zbspepper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.AppInfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 菜单网格的适配器
 * Created by LiYuliang on 2017/09/07 0007.
 *
 * @author LiYuliang
 * @version 2017/11/17
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private Context context;
    private List<AppInfo> list;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;

    public AppAdapter(Context context, List<AppInfo> lv) {
        this.context = context;
        list = lv;
    }

    @NotNull
    @Override
    public AppAdapter.AppViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app, viewGroup, false);
        AppViewHolder appViewHolder = new AppViewHolder(view);
        appViewHolder.tvName = view.findViewById(R.id.tvName);
        appViewHolder.ivIcon = view.findViewById(R.id.ivIcon);
        return appViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull AppAdapter.AppViewHolder viewHolder, int position) {
        AppInfo appInfo = list.get(position);
        viewHolder.tvName.setText(appInfo.getAppName());
        viewHolder.ivIcon.setImageDrawable(appInfo.getAppIcon());
        // 点击事件
        viewHolder.itemView.setOnClickListener((v) -> {
            if (mListener != null) {
                mListener.onItemClick(viewHolder.itemView, position);
            }
        });
        // 长按事件
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (mLongListener != null) {
                mLongListener.onItemLongClick(viewHolder.itemView, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivIcon;

        private AppViewHolder(View itemView) {
            super(itemView);
        }
    }
}
