import com.bocom.LaterDisposableAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    // trigger flush
    private static Logger flushLog = LoggerFactory.getLogger(LaterDisposableAppender.FLUSH_KEY_WORD);
    // trigger discard
    private static Logger discardLog = LoggerFactory.getLogger(LaterDisposableAppender.DISCARD_KEY_WORD);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            MDC.put("NUM", i+"");
            log.info("hello" + i);
            Thread.sleep(100);
        }
        flushLog.info("flush");
        discardLog.info("discard");

        for (int i = 11; i < 20; i++) {
            MDC.put("NUM", i+"");
            log.info("hello" + i);
            Thread.sleep(100);
        }
        flushLog.info("flush");
    }
}
