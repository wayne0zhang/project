//package com.paxing.test.kaoqin.utils;
//
//import com.alibaba.fastjson.JSON;
//import okhttp3.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.AsyncResult;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//
///**
// * Created by xingruibo on 2016/11/7.
// */
//public class OkHttpUtils {
//
//    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);
//
//    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
//            .readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();
//
//    /**
//     * post请求发送json数据
//     *
//     * @param postUrl
//     * @param content
//     *
//     * @return
//     */
//    public static Future<Boolean> postNewCallByJson(String postUrl, String content) {
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, content);
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                logger.error("Http Post Json请求失败,[url={}, param={}]", postUrl, content);
//                throw new RuntimeException("Http Post Json请求失败,url:" + postUrl);
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//
//                logger.info("postNewCallByJson; [postUrl={}, requestContent={}, responseCode={}, responseBody={}]",
//                        postUrl, content, response.code(), response.body().string());
//                if (response.code() == 200) {
//                } else {
//                    logger.error("Http Post Json请求失败,[url={}, param={}]", postUrl, content);
//                    throw new RuntimeException("Http Post Json请求失败,url:" + postUrl);
//                }
//            }
//        });
//        return new AsyncResult(true);
//
//    }
//
//    /**
//     * post请求发送key=valule&数据
//     *
//     * @param postUrl
//     * @param content
//     *
//     * @return
//     */
//    public static Future<Boolean> postNewCallByForm(String postUrl, String content){
//        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, content);
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response.code() == 200) {
//                logger.info("postNewCallByForm; [postUrl={}, requestContent={}, responseCode={}, responseBody={}]",
//                        postUrl, content, response.code(), response.body().string());
//            }else {
//                logger.error("Http Post Form请求失败,[url={}, param={}]", postUrl, content);
//                throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//            }
//        } catch (IOException e) {
//            logger.error("Http Post Form请求失败,[url={}, param={}]", postUrl, content);
//            throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//        }
//        return new AsyncResult(true);
//    }
//
//    /**
//     * post请求发送key=valule&数据
//     *
//     * @param postUrl
//     * @param content
//     *
//     * @return string
//     */
//    public static String postDataByForm(String postUrl, String content){
//        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, content);
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response.code() == 200) {
//                logger.info("postNewCallByForm; [postUrl={}, requestContent={}, responseCode={}]", postUrl, content, response.code());
//                return response.body().string();
//            }else {
//                logger.error("Http Post Form请求失败,[url={}, param={}]", postUrl, content);
//                throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//            }
//        } catch (IOException e) {
//            logger.error("Http Post Form请求失败,[url={}, param={}]", postUrl, content);
//            throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//        }
//    }
//
//    /**
//     * post请求发送key=valule&数据
//     * @param postUrl
//     * @param headers
//     * @param params
//     * @return
//     */
//    public static HttpResponse<String> postDataByForm(String postUrl, Map<String, String> headers, Map<String, String[]> params){
//        FormBody.Builder bodyBuilder = new FormBody.Builder();
//        for (Map.Entry<String, String[]> entry: params.entrySet()) {
//            for (String value: entry.getValue()) {
//                bodyBuilder.add(entry.getKey(), value);
//            }
//        }
//        FormBody formBody = bodyBuilder.build();
//        Request.Builder requestBuilder = new Request.Builder().url(postUrl);
//        for (Map.Entry<String, String> entry: headers.entrySet()) {
//            requestBuilder.addHeader(entry.getKey(), entry.getValue());
//        }
//        Request request = requestBuilder.post(formBody).build();
//        Response response = null;
//        long cost, start = System.currentTimeMillis();
//        try {
//            HttpResponse<String> result;
//            response = okHttpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                cost = System.currentTimeMillis() - start;
//                String body = response.body().string();
//                result = new HttpResponse<>(response.code(), body);
//                logger.info("Http Post Form successed; cost={}ms, [postUrl={}, headers={}, params:{}], result:{}", cost, postUrl, JSON.toJSONString(headers), JSON.toJSONString(params), body);
//            }else {
//                cost = System.currentTimeMillis() - start;
//                String body = response.body().string();
//                result = new HttpResponse<>(response.code(), body);
//                logger.error("Http Post Form请求失败,cost={}ms, [url={}, headers={}, params:{}], response:{}", cost, postUrl, JSON.toJSONString(headers), JSON.toJSONString(params), body);
//            }
//            return result;
//        } catch (IOException e) {
//            cost = System.currentTimeMillis() - start;
//            logger.error("Http Post Form请求异常,cost={}ms, [url={}, headers={}, params:{}]", cost, postUrl, JSON.toJSONString(headers), JSON.toJSONString(params), e);
//            throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//        }
//    }
//
//    public static String getNewCall(String getUrl) {
//        Request request = new Request.Builder()
//                .url(getUrl)
//                .build();
//        String bodyContent = "";
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            ResponseBody body = response.body();
//            bodyContent = body.string();
//        } catch (IOException e) {
//            throw new RuntimeException("Http Get请求失败,url:" + getUrl);
//        }
//        return bodyContent;
//    }
//
//
//    public static Response syncCall(Request request) throws IOException {
//        return okHttpClient.newCall(request).execute();
//    }
//
//    public static void asyncCall(Request request, Consumer<Exception> failure, Consumer<Response> respConsumer) {
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                failure.accept(e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                respConsumer.accept(response);
//            }
//        });
//    }
//
//    /**
//     * 同步post请求发送key=valule&数据
//     *
//     * @param postUrl
//     * @param content
//     * @return string
//     */
//    public static String postDataByForm(String postUrl, String content, Headers headers) {
//        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, content);
//
//        Request.Builder builder = new Request.Builder();
//        if (null != headers) {
//            builder.headers(headers);
//        }
//        Request request = builder
//                .url(postUrl)
//                .post(body)
//                .build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response.code() == 200) {
//                logger.info("postNewCallByForm; [postUrl={}, requestContent={}, responseCode={}]", postUrl, content, response.code());
//                return response.body().string();
//            } else {
//                logger.error("Http Post Form请求失败,[url={}, param={},responseCode={}]", postUrl, content, response.code());
//                throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//            }
//        } catch (IOException e) {
//            logger.error("Http Post Form请求失败,[url={}, param={}]", postUrl, content, e);
//            throw new RuntimeException("Http Post Form请求失败,url:" + postUrl);
//        }
//    }
//
//    /**
//     * 同步post请求发送json数据
//     *
//     * @param postUrl
//     * @param content
//     * @return string
//     */
//    public static String postDataByJson(String postUrl, String content, Headers headers) {
//        MediaType JSON = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(JSON, content);
//
//        Request.Builder builder = new Request.Builder();
//        if (null != headers) {
//            builder.headers(headers);
//        }
//        Request request = builder
//                .url(postUrl)
//                .post(body)
//                .build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response.code() == 200) {
//                logger.info("postDataByJson; [postUrl={}, requestContent={}, responseCode={}]", postUrl, content, response.code());
//                return response.body().string();
//            } else {
//                logger.error("Http Post Json请求失败,[url={}, param={},responseCode={}]", postUrl, content,response.code());
//                throw new RuntimeException("Http Post Json请求失败,url:" + postUrl);
//            }
//        } catch (IOException e) {
//            logger.error("Http Post Json请求失败,[url={}, param={}]", postUrl, content, e);
//            throw new RuntimeException("Http Post Json请求失败,url:" + postUrl);
//        }
//    }
//
//    public static class HttpResponse<T> {
//        private int code;
//        private T body;
//
//        public HttpResponse(int code, T body) {
//            this.code = code;
//            this.body = body;
//        }
//
//        public int getCode() {
//            return code;
//        }
//
//        public void setCode(int code) {
//            this.code = code;
//        }
//
//        public T getBody() {
//            return body;
//        }
//
//        public void setBody(T body) {
//            this.body = body;
//        }
//
//        public boolean isSuccessful() {
//            return this.code >= 200 && this.code < 300;
//        }
//
//    }
//}
