package local.bitest.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class BitcoinClient {
    public int insertBit = 1;
    public float bitSum = 0;
    public float avg10Min;
    public Queue<Float> rates = new LinkedList<>();
    private static String apiURL = "https://blockchain.info/ticker";
    private OkHttpClient client = new OkHttpClient();


    public void storeRate(Float rate) {

        if (insertBit == 10) {
            avg10Min = bitSum / 10;
            insertBit = 1;
            bitSum = 0;
        }

        if (rates.size() == 20) {
            rates.poll();
        }

        rates.add(rate);
        insertBit++;
        bitSum += rate;
    }


    public float fetchBitcoinRate() {
        Request request = new Request.Builder()
                .url(this.apiURL)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String json = response.body().string();
            JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);
            return convertedObject.getAsJsonObject("USD").get("last").getAsFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
