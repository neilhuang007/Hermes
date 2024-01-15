package dev.hermes.utils.client.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void printLog(String message){
        LOGGER.info(message);
    }
}
