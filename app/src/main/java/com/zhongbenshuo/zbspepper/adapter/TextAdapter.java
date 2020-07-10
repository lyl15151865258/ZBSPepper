package com.zhongbenshuo.zbspepper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.bean.DataBean;

import java.util.List;

/**
 * 自定义布局，文本
 */

public class TextAdapter extends BannerAdapter<DataBean, TextAdapter.TextHolder> {

    public TextAdapter(List<DataBean> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
    }

    //更新数据
    public void updateData(List<DataBean> data) {
        //这里的代码自己发挥，比如如下的写法等等
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public TextHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ask, parent, false);
        TextAdapter.TextHolder textHolder = new TextAdapter.TextHolder(view);
        textHolder.tvAskContent = view.findViewById(R.id.tvAskContent);
        return textHolder;
    }

    @Override
    public void onBindView(TextHolder holder, DataBean data, int position, int size) {
        holder.tvAskContent.setText(data.title);
    }

    static class TextHolder extends RecyclerView.ViewHolder {
        public TextView tvAskContent;

        public TextHolder(@NonNull View view) {
            super(view);
        }
    }

}
