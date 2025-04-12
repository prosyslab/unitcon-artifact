import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.junit.Test;

/* Duration of synthesis: 297.651261091 */
public class UnitconTest5 {
@Test
public void test() {
try {
DecodeOptions options4 = org.apache.pdfbox.filter.DecodeOptions.DEFAULT;
RandomAccessRead randomAccessRead92 = null;
InputStream encoded1 = new RandomAccessInputStream(randomAccessRead92);
List filterList2 = new ArrayList();
COSDictionary parameters3 = new COSDictionary();
Filter.decode(encoded1, filterList2, parameters3, options4, filterList2);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
