package com.zhongbenshuo.zbspepper.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.adapter.MenuAdapter;
import com.zhongbenshuo.zbspepper.bean.Menu;
import com.zhongbenshuo.zbspepper.iflytek.WakeUpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 * Created at 2019/12/10 0010 9:06
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:06
 */

public class MainActivity extends BaseActivity {

    private Context mContext;
    private WakeUpUtil wakeUpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //菜单网格
        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvMenu.setLayoutManager(gridLayoutManager);
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(0, R.drawable.self, getString(R.string.SelfIntroduction), true));
        menuList.add(new Menu(0, R.drawable.company, getString(R.string.CompanyProfile), true));
        menuList.add(new Menu(0, R.drawable.scope, getString(R.string.BusinessScope), true));
        menuList.add(new Menu(0, R.drawable.cases, getString(R.string.EngineeringCase), true));
        menuList.add(new Menu(0, R.drawable.message, getString(R.string.MessageBoard), true));
        menuList.add(new Menu(0, R.drawable.meeting, getString(R.string.AnnualMeeting), true));
        MenuAdapter menuAdapter = new MenuAdapter(mContext, menuList);
        menuAdapter.setOnItemClickListener(onItemClickListener);
        rvMenu.setAdapter(menuAdapter);
    }

    private MenuAdapter.OnItemClickListener onItemClickListener = (view, position) -> {
        switch (position) {
            case 0:
                // 自我介绍
                openActivity(SelfIntroductionActivity.class);
                break;
            case 1:
                // 公司简介
                openActivity(CompanyProfileActivity.class);
                break;
            case 2:
                // 经营范围
                openActivity(BusinessScopeActivity.class);
                break;
            case 3:
                // 工程案例
                openActivity(EngineeringCaseActivity.class);
                break;
            case 4:
                // 留言板
                openActivity(MessageBoardActivity.class);
                break;
            case 5:
                // 年会专栏
                openActivity(AnnualMeetingActivity.class);
                break;
            default:
                break;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化唤醒对象
        wakeUpUtil = WakeUpUtil.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeUpUtil.onPause();
    }

    @Override
    protected void onDestroy() {
        wakeUpUtil.onDestroy();
        super.onDestroy();
    }

}
