package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.string;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class LongestStringTest extends TestCase {

	public void testResolveConflictCollectionOfFusableValueOfStringRecordType() {
		LongestString<Movie> crf = new LongestString<Movie>();
		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello moto", null,
				null));
		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals("hello moto", resolvedValue.getValue());

	}

	public void testResolveConflictCollectionOfFusableValueOfStringRecordType2() {
		LongestString<Movie> crf = new LongestString<Movie>();
		List<FusableValue<String, Movie>> cluster2 = new ArrayList<FusableValue<String, Movie>>();
		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster2);
		assertEquals(null, resolvedValue.getValue());

	}

}
