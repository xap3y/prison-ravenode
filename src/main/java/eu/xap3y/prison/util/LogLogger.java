package eu.xap3y.prison.util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

public class LogLogger extends AbstractFilter {


    private static final boolean USE_RAW_STRING = true;

    public LogLogger() {
        super(Filter.Result.DENY, Filter.Result.NEUTRAL);
    }

    private Result doFilter(String message) {
        if (message != null) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
            return onMismatch;
        }
        return onMatch;
    }

    @Override
    public Result filter(LogEvent event) {
        Message msg = event == null ? null : event.getMessage();
        String message = msg == null ? null : (USE_RAW_STRING
                ? msg.getFormat()
                : msg.getFormattedMessage());
        return doFilter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return doFilter(msg == null ? null : msg.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return doFilter(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        String message = msg == null ? null : (USE_RAW_STRING
                ? msg.getFormat()
                : msg.getFormattedMessage());
        return doFilter(message);
    }
}
