package com.video.test.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.video.test.utils.AESUtils;
import com.video.test.utils.EncryptUtils;
import com.video.test.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Enoch Created on 2019-04-24.
 */
public class JsonConvertFactory extends Converter.Factory {
    private static final String TAG = "JsonConvertFactory";
    private final Gson gson;

    public static JsonConvertFactory create() {
        return create(new Gson());
    }

    public static JsonConvertFactory create(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        return new JsonConvertFactory(gson);
    }

    private JsonConvertFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonResponseBodyConverter<>(gson, adapter);
    }


    class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");
        private final Gson gson;
        private final TypeAdapter<T> adapter;


        JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }


    class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;


        JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        /**
         * 转换
         *
         * @return
         * @throws IOException
         */
        @Override
        public T convert(ResponseBody responseBody) throws IOException {
            String oriString = responseBody.string();
            JsonObject oriJsonObject = new JsonParser().parse(oriString).getAsJsonObject();
            if (oriJsonObject.has("data") && oriJsonObject.get("data").isJsonObject()) {
                if (oriJsonObject.get("data").getAsJsonObject().has("response_key")) {
                    LogUtils.i(TAG, "加密");
                    String aesKey = EncryptUtils.keyFromJNI();
                    String aesVi = EncryptUtils.viFromJNI();
                    String responseInfo = oriJsonObject.get("data").getAsJsonObject().get("response_key").getAsString();
                    LogUtils.i(TAG, "Encrypt Info : " + responseInfo);
                    String decryptString = AESUtils.decrypt(responseInfo, aesKey, aesVi);
                    LogUtils.i(TAG, "Decrypt Info : " + decryptString);
                    int code = oriJsonObject.get("code").getAsInt();
                    String msg = oriJsonObject.get("msg").getAsString();
                    //转换成新的json对象
                    String jsonString = createJsonString(code, msg, decryptString);
                    LogUtils.i(TAG, "jsonResult : " + jsonString);
                    Reader stringReader = new StringReader(jsonString);
                    JsonReader jsonReader = gson.newJsonReader(stringReader);

                    try {
                        T result = adapter.read(jsonReader);
                        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                            throw new JsonIOException("JSON document was not fully consumed.");
                        }
                        return result;
                    } finally {
                        responseBody.close();
                    }
                }
            }

            LogUtils.i(TAG, "未加密");
            // 未加密方式，按照原来的方式来返回参数
            JsonReader jsonReader = gson.newJsonReader(new StringReader(oriString));
            try {
                T result = adapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
                return result;
            } finally {
                responseBody.close();
            }
        }

        private String createJsonString(int code, String msg, String decryptString) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", code);
            jsonObject.addProperty("msg", msg);
            boolean isJsonObj = new JsonParser().parse(decryptString).isJsonObject();
            if (isJsonObj) {
                JsonObject jo = new JsonParser().parse(decryptString).getAsJsonObject();
                jsonObject.add("data", jo);
            } else {
                JsonArray ja = new JsonParser().parse(decryptString).getAsJsonArray();
                jsonObject.add("data", ja);
            }
            return gson.toJson(jsonObject);
        }

    }
}
