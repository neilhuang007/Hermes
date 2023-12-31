package dev.hermes.utils.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void printlog(String message){
        LOGGER.info(message);
    }
}
