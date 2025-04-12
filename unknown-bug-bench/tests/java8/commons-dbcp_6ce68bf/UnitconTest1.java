import java.sql.DatabaseMetaData;
import org.apache.commons.dbcp2.DelegatingConnection;
import org.apache.commons.dbcp2.DelegatingDatabaseMetaData;
import org.junit.Test;

/* Duration of synthesis: 13.4324839115 */
public class UnitconTest1 {
@Test
public void test() {
try {
String catalog1 = null;
DelegatingConnection connection4 = null;
DatabaseMetaData databaseMetaData9 = null;
DelegatingDatabaseMetaData con_recv5 = new DelegatingDatabaseMetaData(connection4, databaseMetaData9);
DatabaseMetaData databaseMetaData5 = con_recv5.getInnermostDelegate();
DelegatingDatabaseMetaData con_recv1 = new DelegatingDatabaseMetaData(connection4, databaseMetaData5);
con_recv1.getFunctions(catalog1, catalog1, catalog1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
