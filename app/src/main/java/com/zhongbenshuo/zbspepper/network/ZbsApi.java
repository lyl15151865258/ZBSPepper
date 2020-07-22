package com.zhongbenshuo.zbspepper.network;

import com.google.gson.JsonObject;
import com.zhongbenshuo.zbspepper.bean.OpenAndCloseDoorRecord;
import com.zhongbenshuo.zbspepper.bean.Result;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Retrofit网络请求构建接口
 * Created at 2018/11/28 13:48
 *
 * @author LiYuliang
 * @version 1.0
 */

public interface ZbsApi {

    /**
     * 查询公司简介图片
     *
     * @return 返回值
     */
    @GET("pepper/queryPepperCompany")
    Observable<Result> queryPepperCompany();

    /**
     * 查询经营范围图片
     *
     * @return 返回值
     */
    @GET("pepper/queryPepperBusiness")
    Observable<Result> queryPepperBusiness();

    /**
     * 查询工程案例图片
     *
     * @return 返回值
     */
    @GET("pepper/queryPepperProject")
    Observable<Result> queryPepperProject();

    /**
     * 查询自我介绍文本
     *
     * @return 返回值
     */
    @GET("pepper/querySelfIntroduction")
    Observable<Result> querySelfIntroduction();

    /**
     * 查询询问语句
     *
     * @return 返回值
     */
    @GET("pepper/queryAskSentence")
    Observable<Result> queryAskSentence();

    /**
     * 远程开关门
     *
     * @return 返回值
     */
    @POST("user/openAndCloseDoorRecord.do")
    Observable<Result> openAndCloseDoorRecord(@Body OpenAndCloseDoorRecord params);

    /**
     * 查询最新的版本信息
     *
     * @return 返回值
     */
    @POST("user/searchNewVersion.do")
    Observable<Result> searchNewVersion(@Body JsonObject params);

    /**
     * 下载软件
     *
     * @return 文件
     */
    @Streaming
    @GET
    Observable<ResponseBody> executeDownload(@Header("Range") String range, @Url() String url);

}
