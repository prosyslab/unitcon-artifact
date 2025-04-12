import java.util.concurrent.ScheduledExecutorService;
import org.apache.karaf.features.internal.download.impl.MavenDownloadTask;
import org.ops4j.pax.url.mvn.MavenResolver;
import org.junit.Test;

/* Duration of synthesis: 26.8603830338 */
public class UnitconTest1 {
@Test
public void test() {
try {
String url3 = null;
ScheduledExecutorService executor1 = null;
MavenResolver resolver2 = null;
MavenDownloadTask con_recv0 = new MavenDownloadTask(executor1, resolver2, url3);
con_recv0.getUrl();
}
catch(Exception e) {
e.printStackTrace();
}
}
}
