package local.bitest.app.task;

import local.bitest.app.service.BitcoinClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FetchRateTask {
    private Logger logger = LoggerFactory.getLogger(FetchRateTask.class);
    @Autowired
    private BitcoinClient bitCoinClient;

    @Scheduled(fixedRate = 1000 * 60 )
    public void fetchBitcoinRate() {
        bitCoinClient.storeRate(bitCoinClient.fetchBitcoinRate());
        logger.info(bitCoinClient.rates.toString());

    }
}
