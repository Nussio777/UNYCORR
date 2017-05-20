package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class UnionTest extends TestCase {

	public void testResolveConflictCollectionOfFusableValueOfListOfValueTypeRecordType() {
		Union<String, Movie> crf = new Union<String, Movie>();
		List<FusableValue<List<String>, Movie>> cluster1 = new ArrayList<FusableValue<List<String>, Movie>>();
		cluster1.add(new FusableValue<List<String>, Movie>(
				new ArrayList<String>(), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(
				new ArrayList<String>(), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(
				new ArrayList<String>(), null, null));
		FusedValue<List<String>, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(0, resolvedValue.getValue().size());
	}

	public void testResolveConflictCollectionOfFusableValueOfListOfValueTypeRecordType1() {
		Union<String, Movie> crf = new Union<String, Movie>();
		List<FusableValue<List<String>, Movie>> cluster1 = new ArrayList<FusableValue<List<String>, Movie>>();
		cluster1.add(new FusableValue<List<String>, Movie>(Arrays
				.asList("h0", "h1"), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(Arrays
				.asList("h0", "h1"), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(Arrays
				.asList("h0", "h1"), null, null));
		FusedValue<List<String>, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(2, resolvedValue.getValue().size());

	}

	public void testResolveConflictCollectionOfFusableValueOfListOfValueTypeRecordType2() {
		Union<String, Movie> crf = new Union<String, Movie>();
		List<FusableValue<List<String>, Movie>> cluster1 = new ArrayList<FusableValue<List<String>, Movie>>();
		cluster1.add(new FusableValue<List<String>, Movie>(new ArrayList<String>(), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(Arrays
				.asList("h0", "h1"), null, null));
		cluster1.add(new FusableValue<List<String>, Movie>(Arrays
				.asList("h2", "h1"), null, null));
		FusedValue<List<String>, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(3, resolvedValue.getValue().size());

	}
}
