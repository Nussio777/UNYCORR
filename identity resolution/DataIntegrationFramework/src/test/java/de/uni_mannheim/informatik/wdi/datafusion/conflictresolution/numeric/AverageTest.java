package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.numeric;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class AverageTest extends TestCase {

	public void testResolveConflictCollectionOfFusableValueOfDoubleRecordType() {

		Average<Movie> crf = new Average<Movie>();
		List<FusableValue<Double, Movie>> cluster1 = new ArrayList<FusableValue<Double, Movie>>();
		cluster1.add(new FusableValue<Double, Movie>(1.0, null, null));
		cluster1.add(new FusableValue<Double, Movie>(2.0, null, null));
		cluster1.add(new FusableValue<Double, Movie>(3.0, null, null));
		FusedValue<Double, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(2.0, resolvedValue.getValue());
	}

	public void testResolveConflictCollectionOfFusableValueOfDoubleRecordType2() {

		Average<Movie> crf = new Average<Movie>();
		List<FusableValue<Double, Movie>> cluster2 = new ArrayList<FusableValue<Double, Movie>>();
		FusedValue<Double, Movie> resolvedValue = crf
				.resolveConflict(cluster2);
		assertEquals(null, resolvedValue.getValue());
	}

}
