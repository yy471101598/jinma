package com.shoppay.jinma.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.shoppay.jinma.R;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyResponse {
    RequestQueue rq;
    String TAG = "tag";
    public static int status;
    private static VolleyResponse preManager;
    private static Gson gson;
    String notificationTxt = "尊敬的车主您好，您的账号在其他手机上登录了，您将被强制下线，如需继续使用请重新登录。如非本人授权请修改密码或联系客服处理。";

    private VolleyResponse() {

    }

    public static synchronized VolleyResponse instance() {
        if (preManager == null)
            preManager = new VolleyResponse();
        if (gson == null) {
            gson = new Gson();
        }
        return preManager;
    }

    /**
     * 用户获取JsonObject
     *
     * @param method
     * @param url
     * @param params
     */
    // public void getVollecyJsonObject(final int method, final String url,
    // final Map<String, Object> params,
    // final InterfaceVolleyResponse inter_response, final Context context) {
    // JSONObject jsonObject1 = new JSONObject(params);
    // Log.e("js", jsonObject1.toString());
    // Log.e("url", url);
    //
    // JsonObjectRequest req = new JsonObjectRequest(method, url,
    // new Listener<JSONObject>() {
    // @Override
    // public void onResponse(JSONObject response) {
    // inter_response.onResponse(status, response.toString());
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // error.printStackTrace();
    // if (error!=null &&!TextUtils.isEmpty(error.getMessage()) &&
    // error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")){
    // DialogUtil.getLogoffNotification(context,notificationTxt);
    // return;
    // }
    // if (null != error && null != error.networkResponse) {
    // int code = error.networkResponse.statusCode;
    // byte[] data = error.networkResponse.data;
    // Log.v("tag", "错误msg:" + new String(data) + " code:"
    // + code, error);
    //
    // if(code==401){
    // DialogUtil.getLogoffNotification(context,notificationTxt);
    // }else{
    // ErrorMsg msg = gson.fromJson(new String(data),
    // ErrorMsg.class);
    // inter_response.onErrorResponse(code, msg.respMsg);
    // }
    //
    // } else {
    // inter_response.onErrorResponse(501, "网络无响应");
    // }
    // }
    // }) {
    // @Override
    // protected Response<JSONObject> parseNetworkResponse(
    // NetworkResponse response) {
    // try {
    // status = response.statusCode;
    //
    // Log.v("status", "data--detail -> "
    // + new String(response.data).toString());
    // byte[] data = response.data;
    // JSONObject jsonObject = new JSONObject();
    //
    // if (null != data && data.length > 0) {
    // jsonObject = new JSONObject(new String(response.data,
    // "UTF-8"));
    // }
    // return Response.success(jsonObject,
    // HttpHeaderParser.parseCacheHeaders(response));
    // } catch (UnsupportedEncodingException e) {
    // return Response.error(new ParseError(e));
    // } catch (Exception je) {
    // return Response.error(new ParseError(je));
    // }
    // }
    //
    // @Override
    // public Map<String, String> getHeaders() {
    // HashMap<String, String> headers = new HashMap<String, String>();
    //
    // // headers.put("Content-Type","application/json");
    // headers.put("Accept", "application/json");
    // String tokenId=PreferenceHelper.readString( context, "freight",
    // "userId","");
    // String token=PreferenceHelper.readString( context, "freight",
    // "token"+tokenId,"");
    // if(!TextUtils.isEmpty(token)){
    // headers.put("TOKEN", token);
    // }
    //
    // return headers;
    // }
    // };
    // req.setRetryPolicy(new DefaultRetryPolicy(20000,
    // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    // addRequest(context, req);
    // }

    /**
     * 用于获取JsonArray
     */
    // public void getVollecyJsonArray(final int method, final String url,
    // final Map<String, Object> params,
    // final InterfaceVolleyResponse inter_response, final Context context) {
    // JSONObject jsonObject1 = new JSONObject(params);
    // Log.e("js", jsonObject1.toString());
    // Log.e("url", url);
    // JsonArrayRequest req = new JsonArrayRequest(method, url, jsonObject1,
    // new Listener<JSONArray>() {
    // @Override
    // public void onResponse(JSONArray response) {
    // inter_response.onResponse(status, response.toString());
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // error.printStackTrace();
    //
    // if (null != error &&!TextUtils.isEmpty(error.getMessage()) &&
    // error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")){
    //
    // DialogUtil.getLogoffNotification(context,notificationTxt);
    // return;
    // }
    // if (null != error && null != error.networkResponse) {
    // int code = error.networkResponse.statusCode;
    // byte[] data = error.networkResponse.data;
    // Log.v("tag", "错误msg:" + new String(data) + " code:"
    // + code, error);
    // // Log.e("tag", "返回报错信息" + error.getMessage(),
    // // error);
    // if(code==401){
    // DialogUtil.getLogoffNotification(context,notificationTxt);
    // }else if(code==400){
    // inter_response.onErrorResponse(code, "服务器报错");
    // }else{
    // ErrorMsg msg = gson.fromJson(new String(data),
    // ErrorMsg.class);
    // inter_response.onErrorResponse(code, msg.respMsg);
    // }
    //
    // } else {
    // inter_response.onErrorResponse(501, "网络无响应");
    // }
    // }
    // }) {
    // @Override
    // protected Response<JSONArray> parseNetworkResponse(
    // NetworkResponse response) {
    // try {
    // status = response.statusCode;
    // Log.v("tag", "..data--detail -> "
    // + new String(response.data).toString());
    // byte[] data = response.data;
    // JSONArray jsonArray = new JSONArray();
    // if (null != data && data.length > 0) {
    // jsonArray = new JSONArray(new String(response.data,
    // "UTF-8"));
    // }
    // return Response.success(jsonArray,
    // HttpHeaderParser.parseCacheHeaders(response));
    // } catch (UnsupportedEncodingException e) {
    // return Response.error(new ParseError(e));
    // } catch (Exception je) {
    // return Response.error(new ParseError(je));
    // }
    // }
    //
    // @Override
    // public Map<String, String> getHeaders() throws AuthFailureError {
    // HashMap<String, String> headers = new HashMap<String, String>();
    // // headers.put("Content-Type","application/json");
    // headers.put("Accept", "application/json");
    // String tokenId=PreferenceHelper.readString( context, "freight",
    // "userId","");
    // String token=PreferenceHelper.readString( context, "freight",
    // "token"+tokenId,"");
    // if(!TextUtils.isEmpty(token)){
    // headers.put("TOKEN", token);
    // }
    // return headers;
    // }
    // };
    // req.setRetryPolicy(new DefaultRetryPolicy(20000,
    // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    // addRequest(context, req);
    // }

    // public void getVolleyResponseJson(Context context, String url,
    // JSONArray array, final InterfaceVolleyResponse inter_response) {
    // JSONObject jsonObject1 = null;
    // try {
    // jsonObject1 = new JSONObject(array.toString());
    // } catch (JSONException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // Log.e("js", jsonObject1.toString());
    // Log.e("url", url);
    // // jsonObject1.toString();
    // JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
    // Method.POST, url, jsonObject1,
    // new Response.Listener<JSONObject>() {
    // @Override
    // public void onResponse(JSONObject response) {
    // // Log.v("tag", "返回的信息detail -> " +
    // // response.toString());
    // inter_response.onResponse(status, response.toString());
    // }
    // }, new Response.ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // error.printStackTrace();
    // Log.d("tag", "error");
    // if (null != error && null != error.networkResponse) {
    // int code = error.networkResponse.statusCode;
    // byte[] data = error.networkResponse.data;
    // Log.v("tag", "错误msg:" + new String(data) + " code:"
    // + code, error);
    //
    // // Log.e("tag", "返回报错信息" + error.getMessage(),
    // // error);
    //
    // inter_response.onErrorResponse(code, new String(data));
    // } else {
    // inter_response.onErrorResponse(501, "网络无响应");
    // }
    // }
    // }) {
    // @Override
    // protected Response<JSONObject> parseNetworkResponse(
    // NetworkResponse response) {
    // try {
    // status = response.statusCode;
    // Log.v("status", "statusCode--detail -> " + status);
    // Log.v("status", "data--detail -> "
    // + new String(response.data).toString());
    // byte[] data = response.data;
    // JSONObject jsonObject = new JSONObject();
    // // if(status==ConstantUtils.CODE_REGISTER){
    // // inter_response.onResponse(status,response);
    // // }
    // if (null != data && data.length > 0) {
    // jsonObject = new JSONObject(new String(response.data,
    // "UTF-8"));
    // }
    // return Response.success(jsonObject,
    // HttpHeaderParser.parseCacheHeaders(response));
    // } catch (UnsupportedEncodingException e) {
    // return Response.error(new ParseError(e));
    // } catch (Exception je) {
    // return Response.error(new ParseError(je));
    // }
    // }
    //
    // @Override
    // public Map<String, String> getHeaders() {
    // HashMap<String, String> headers = new HashMap<String, String>();
    //
    // // headers.put("Content-Type","application/json");
    // headers.put("Accept", "application/json");
    //
    // return headers;
    // }
    // };
    // jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
    // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    // addRequest(context, jsonRequest);
    // }

    // public void getVollecyResponse(int method, final String url,
    // final InterfaceVolleyResponse inter) {
    // JsonArrayRequest req = new JsonArrayRequest(method, url,
    // new Listener<JSONArray>() {
    // @Override
    // public void onResponse(JSONArray arg0) {
    // inter.onResponse(status, arg0.toString());
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // if (null != error && null != error.networkResponse) {
    // int code = error.networkResponse.statusCode;
    // byte[] data = error.networkResponse.data;
    // Log.v("tag", "错误msg:" + new String(data) + " code:"
    // + code, error);
    // // Log.e("tag", "返回报错信息" + error.getMessage(),
    // // error);
    // // if(code==401){
    // // DialogUtil.getLogoffNotification(context,notificationTxt);
    // // }else{
    // ErrorMsg msg = gson.fromJson(new String(data),
    // ErrorMsg.class);
    // inter.onErrorResponse(code, msg.respMsg);
    // // }
    //
    // } else {
    // inter.onErrorResponse(501, "网络无响应");
    // }
    // }
    // }) {
    // @Override
    // protected Response<JSONArray> parseNetworkResponse(
    // NetworkResponse response) {
    // try {
    // status = response.statusCode;
    //
    // Log.v("status", "data--detail -> "
    // + new String(response.data).toString());
    // byte[] data = response.data;
    // JSONArray jsonObject = new JSONArray();
    //
    // if (null != data && data.length > 0) {
    // jsonObject = new JSONArray(new String(response.data,
    // "UTF-8"));
    // }
    // return Response.success(jsonObject,
    // HttpHeaderParser.parseCacheHeaders(response));
    // } catch (UnsupportedEncodingException e) {
    // return Response.error(new ParseError(e));
    // } catch (Exception je) {
    // return Response.error(new ParseError(je));
    // }
    // }
    // };
    // }
    public void getVolleyString(final Context context, String Url, final InterfaceVolleyResponse inter_response) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 处理返回的JSON数据
                String[] str1 = response
                        .split("<string xmlns=\"http://tempuri.org/\">");
                String str2 = str1[1].replace("</string>", "");
                System.out.println(str2);
                Log.d("xxkeypost", str2);
                inter_response.onResponse(1, str2);
                // {"Code":"3","Msg":"获取成功","Keys":[{"KeyID":"1123","KeyExpireTime":"2017/11/1 0:00:00"}]}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                inter_response.onErrorResponse(0, "网络异常");
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(context, stringRequest);
    }

    /**
     * 返回的jsonObject 且是用map
     *
     * @param context
     * @param url
     * @param map
     * @param inter_response
     */
    public void getVolleyResponse(final Context context, final String url,
                                  Map<String, Object> map,
                                  final InterfaceVolleyResponse inter_response) {
        JSONObject jsonObject1 = new JSONObject(map);
        Log.e("js", jsonObject1.toString());
        Log.e("url", url);

        // jsonObject1.toString();
        // JSONArray s = new JSONArray();
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
                Method.POST, url, jsonObject1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.v("tag", "返回的信息detail -> " +
                        // response.toString());
                        inter_response.onResponse(status, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (null != error && null != error.networkResponse) {
                    int code = error.networkResponse.statusCode;
                    byte[] data = error.networkResponse.data;
                    Log.d("tag", "error==" + new String(data));
                    inter_response.onErrorResponse(code, "返回为空");
                } else {
                    inter_response.onErrorResponse(501, "网络无响应");
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(
                    NetworkResponse response) {
                try {
                    status = response.statusCode;

                    Log.v("status", "data--detail -> "
                            + new String(response.data).toString());
                    byte[] data = response.data;
                    JSONObject jsonObject = new JSONObject();

                    if (null != data && data.length > 0) {
                        jsonObject = new JSONObject(new String(response.data,
                                "UTF-8"));
                    }
                    return Response.success(jsonObject,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();

                // headers.put("Content-Type","application/json");
                headers.put("Accept", "application/json");
                // if(url.equals(ConstantUtils.LoginUrl)){
                // Log.d("logintoken", "xxx");
                // }else{
                // String tokenId=PreferenceHelper.readString( context,
                // "freight", "userId","");
                // String token=PreferenceHelper.readString( context, "freight",
                // "token"+tokenId,"");
                // if(!TextUtils.isEmpty(token)){
                // headers.put("TOKEN", token);
                // }
                // Log.d("token", token);
                // }
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(context, jsonRequest);
    }

    /**
     * 用get 得到String
     *
     * @param context
     * @param url
     * @param inter_response
     */
    public void getVolleyResponseGet(final Context context, String url,

                                     final InterfaceVolleyResponse inter_response) {
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        inter_response.onResponse(status, response);
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                // // TODO Auto-generated method stub
                // if (error!=null
                // &&!TextUtils.isEmpty(error.getMessage()) &&
                // error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")){
                // DialogUtil.getLogoffNotification(context,notificationTxt);
                // return;
                // }
                // if (null != error && null != error.networkResponse) {
                // int code = error.networkResponse.statusCode;
                // if(code==401){
                // DialogUtil.getLogoffNotification(context,notificationTxt);
                // } else{
                // inter_response.onErrorResponse(1,
                // error.getMessage());
                // }
                inter_response.onErrorResponse(status, error);
                // }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type","application/json");
                headers.put("Accept", "application/json");
                // String tokenId=PreferenceHelper.readString( context,
                // "freight", "userId","");
                // String token=PreferenceHelper.readString( context, "freight",
                // "token"+tokenId,"");
                // if(!TextUtils.isEmpty(token)){
                // headers.put("TOKEN", token);
                // }
                return headers;
            }

            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                // TODO Auto-generated method stub
                StringBuffer sb = new StringBuffer();
                sb.append(response.statusCode);
                String str = null;
                try {
                    str = new String(response.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sb.append(str);
                // Log.v("tag",
                // "data:"+sb.toString()+HttpHeaderParser.parseCacheHeaders(response));
                return Response.success(sb.toString(),
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(context, stringRequest);
    }

    /**
     * 下载图片
     *
     * @param context
     * @param url
     * @param img
     */
    public void getInternetImg(Context context, String url, ImageView img) {
        if (null == rq) {

            rq = Volley.newRequestQueue(context);
        }
        Log.d("xximgurl", url);
        ImageLoader il = new ImageLoader(rq, new BitmapCache(context));
        ImageListener ilis = ImageLoader.getImageListener(img,
                R.mipmap.messge_nourl, R.mipmap.messge_nourl);
        // 限制宽高都是300
        il.get(url, ilis, 300, 300);
        //il.get(url, ilis);
    }

    public void getInternetImgBitmap(Context context, String url,
                                     final InterfaceVolleyResponse interface_response) {

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        interface_response.onResponse(1, response);
                        // imageView.setImageBitmap(response);
                    }
                }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // imageView.setImageResource(R.drawable.default_image);
            }
        });
        // imageRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(context, imageRequest);
    }

    /**
     * 添加到栈列
     *
     * @param context
     * @param jsonRequest
     */
    public void addRequest(Context context, Request jsonRequest) {
        if (null == rq) {
            rq = Volley.newRequestQueue(context);
        }
        jsonRequest.setTag(TAG);
        rq.add(jsonRequest);
    }

    public void cancleAllRequest() {
        if (null != rq && rq.getSequenceNumber() > 0) {

            rq.cancelAll(TAG);
            // rq.stop();
        }

    }

    /**
     * 把图片保存到本地
     */
    public static void saveBitmap2(Bitmap mBitmap, String imageURL, Context cxt) {

        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);

        FileOutputStream fos = null;

        try {
            fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取本地缓存的图片
     */
    public static Bitmap getBitmap2(String fileName, Context cxt) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        FileInputStream fis = null;
        try {
            fis = cxt.openFileInput(bitmapName);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            fis.close();
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
        }
        // 读取产生异常，返回null
        return null;
    }

    /**
     * 判断本地的私有文件夹里面是否存在当前名字的文件
     */
    public static boolean isFileExist(String fileName, Context cxt) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        List<String> nameLst = Arrays.asList(cxt.fileList());
        if (nameLst.contains(bitmapName)) {
            return true;
        } else {
            return false;
        }

    }

    public class BitmapCache implements ImageCache {

        private LruCache<String, Bitmap> mCache;
        private Context context;

        public BitmapCache(Context context) {
            // 我们将缓存图片的大小设置为10M
            this.context = context;
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };

        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
//			saveBitmap2(bitmap, url, context);
        }

    }
}
