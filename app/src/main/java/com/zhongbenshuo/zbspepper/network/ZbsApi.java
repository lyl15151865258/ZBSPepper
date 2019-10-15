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
     * 查询服务器RSA公钥
     *
     * @return 返回值
     */
    @GET("user/rsaPublicKey.do")
    Observable<Result> getRSAPublicKey();

    /**
     * 查询员工状态
     *
     * @return 返回值
     */
    @POST("user/getEmployeeStatus.do")
    Observable<Result> getEmployeeStatus(@Body JsonObject params);

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
