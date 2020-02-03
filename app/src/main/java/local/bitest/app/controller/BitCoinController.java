package local.bitest.app.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import local.bitest.app.service.BitcoinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BitCoinController {

    @Value("${app.name}")
    private String appName;

    @Autowired
    private BitcoinClient bitcoinClient;

    @GetMapping("/rates")
    public ResponseEntity getConsumers() {

        JsonArray ja = new JsonArray();
        for (Float rate : bitcoinClient.rates) {
            ja.add(rate);
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("appName", appName);
        jo.addProperty("avgRatesFor10Min", bitcoinClient.avg10Min);
        jo.add("rates", ja);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jo.toString());
    }
}
