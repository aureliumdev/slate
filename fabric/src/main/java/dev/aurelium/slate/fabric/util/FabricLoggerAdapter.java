package dev.aurelium.slate.fabric.util;

import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FabricLoggerAdapter extends Logger {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("slate");

    public FabricLoggerAdapter() {
        super(LOGGER.getName(), null);
    }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.SEVERE) {
            LOGGER.error(msg);
        } else if (level == Level.WARNING) {
            LOGGER.warn(msg);
        } else if (level == Level.INFO) {
            LOGGER.info(msg);
        } else {
            LOGGER.debug(msg);
        }
    }
}
