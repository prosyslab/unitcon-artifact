import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.PatternSubtreeConfigurationWrapper;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.junit.Test;

/* Duration of synthesis: 46.72970891 */
public class UnitconTest17 {
@Test
public void test() {
try {
String path2 = "root";
Configuration conf6 = null;
ExpressionEngine engine7 = null;
HierarchicalConfiguration config1 = ConfigurationUtils.convertToHierarchical(conf6, engine7);
PatternSubtreeConfigurationWrapper con_recv0 = new PatternSubtreeConfigurationWrapper(config1, path2);
con_recv0.interpolatedConfiguration();
}
catch(Exception e) {
e.printStackTrace();
}
}
}
