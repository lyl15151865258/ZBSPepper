package com.zhongbenshuo.zbspepper.glide.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.youth.banner.loader.ImageLoader;
import com.zhongbenshuo.zbspepper.R;

/**
 * Glide图片加载工具
 * Created by LiYuliang on 2017/10/20 0020.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，此方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        if (Util.isOnMainThread()) {
            RequestOptions options = new RequestOptions().error(R.drawable.no_banner).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .apply(options)
                    .into(imageView);
        }
    }

//    @Override
//    public ImageView createImageView(Context context) {
//        //圆角
//        return new RoundAngleImageView(context);
//    }
}
