package com.zhongbenshuo.zbspepper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.Menu;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 菜单按钮适配器
 * Created at 2020/7/6 0006 11:16
 *
 * @author : LiYuliang
 * @version : 2020/7/6 0006 11:16
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<Menu> list;
    private OnItemClickListener mListener;

    public MenuAdapter(Context context, List<Menu> lv) {
        this.context = context;
        list = lv;
    }

    @NotNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu, viewGroup, false);
        MenuViewHolder menuViewHolder = new MenuViewHolder(view);
        menuViewHolder.menu = view.findViewById(R.id.menu);
        menuViewHolder.tvMenu = view.findViewById(R.id.tvMenu);
        menuViewHolder.ivMenu = view.findViewById(R.id.ivMenu);
        return menuViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull MenuViewHolder viewHolder, int position) {
        Menu menu = list.get(position);
        viewHolder.tvMenu.setText(menu.getMenuText());
        RequestOptions options = new RequestOptions().error(R.drawable.pepper).placeholder(R.drawable.pepper).dontAnimate();
        Glide.with(context).load(menu.getMenuImg()).apply(options).into(viewHolder.ivMenu);
        if (menu.isSelected()) {
            viewHolder.itemView.setBackgroundResource(R.color.color_blue);
        } else {
            viewHolder.itemView.setBackgroundResource(R.color.transparent);
        }
        viewHolder.itemView.setOnClickListener((v) -> {
            if (mListener != null) {
                mListener.onItemClick(viewHolder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout menu;
        private TextView tvMenu;
        private ImageView ivMenu;

        private MenuViewHolder(View itemView) {
            super(itemView);
        }

        public void clickItem() {
            menu.performClick();
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
