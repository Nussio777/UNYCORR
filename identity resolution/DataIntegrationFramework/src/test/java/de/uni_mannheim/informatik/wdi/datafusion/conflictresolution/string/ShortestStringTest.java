package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.string;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class ShortestStringTest extends TestCase {

	public void testResolveConflictCollectionOfFusableValueOfStringRecordType() {
		ShortestString<Movie> crf = new ShortestString<Movie>();
		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello moto", null,
				null));
		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals("hello", resolvedValue.getValue());
	}

	public void testResolveConflictCollectionOfFusableValueOfStringRecordType2() {
		ShortestString<Movie> crf = new ShortestString<Movie>();
		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello moto", null,
				null));
		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals("", resolvedValue.getValue());
	}

	public void testResolveConflictCollectionOfFusableValueOfStringRecordType3() {
		ShortestString<Movie> crf = new ShortestString<Movie>();
		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();
		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(null, resolvedValue.getValue());
	}

}
