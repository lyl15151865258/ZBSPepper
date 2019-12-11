package com.zhongbenshuo.zbspepper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.Menu;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 菜单网格的适配器
 * Created by LiYuliang on 2017/09/07 0007.
 *
 * @author LiYuliang
 * @version 2017/11/17
 */

public class MenuAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Menu> list;
    private OnItemClickListener mListener;

    public MenuAdapter(Context context, List<Menu> lv) {
        this.context = context;
        list = lv;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu, viewGroup, false);
        MenuViewHolder menuViewHolder = new MenuViewHolder(view);
        menuViewHolder.tvMenu = view.findViewById(R.id.tvMenu);
        menuViewHolder.ivMenu = view.findViewById(R.id.ivMenu);
        return menuViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        Menu menu = list.get(position);
        holder.tvMenu.setText(menu.getMenuText());
        RequestOptions options = new RequestOptions().error(R.drawable.pepper).placeholder(R.drawable.pepper).dontAnimate().circleCrop();
        Glide.with(context).load(menu.getMenuImg()).apply(options).into(holder.ivMenu);
        if (mListener != null && menu.isEnable()) {
            holder.ivMenu.setOnClickListener((v) -> mListener.onItemClick(v, holder.getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMenu;
        private ImageView ivMenu;

        private MenuViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        /**
         * item点击事件
         *
         * @param view     被点击的item控件
         * @param position 被点击的位置
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
