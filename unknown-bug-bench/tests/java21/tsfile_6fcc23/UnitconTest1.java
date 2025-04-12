import org.apache.tsfile.read.controller.IChunkLoader;
import org.apache.tsfile.read.controller.IMetadataQuerier;
import org.apache.tsfile.read.expression.QueryExpression;
import org.apache.tsfile.read.query.executor.TsFileExecutor;
import org.junit.Test;

/* Duration of synthesis: 16.8014128208 */
public class UnitconTest1 {
@Test
public void test() {
try {
QueryExpression queryExpression1 = null;
IChunkLoader chunkLoader3 = null;
IMetadataQuerier metadataQuerier2 = null;
TsFileExecutor con_recv0 = new TsFileExecutor(metadataQuerier2, chunkLoader3);
con_recv0.execute(queryExpression1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
