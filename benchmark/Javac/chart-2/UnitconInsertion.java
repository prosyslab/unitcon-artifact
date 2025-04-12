import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;

/* Unitcon added class with error function to clarify error point */
public class UnitconInsertion {
    public UnitconInsertion() {

    }

    public void errorEntryMethod(XYDataset dataset) {
        Range r = DatasetUtilities.iterateDomainBounds(dataset);
        r.getLowerBound();
    }
}
