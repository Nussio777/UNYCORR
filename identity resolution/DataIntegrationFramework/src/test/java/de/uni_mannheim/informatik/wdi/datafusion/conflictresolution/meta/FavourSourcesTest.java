package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.meta;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class FavourSourcesTest extends TestCase {

	public void testResolveConflict() {

		FavourSources<Double, Movie> crf = new FavourSources<Double, Movie>();
		List<FusableValue<Double, Movie>> cluster1 = new ArrayList<FusableValue<Double, Movie>>();
		FusableDataSet<Movie> ds1 = new FusableDataSet<>();
		ds1.setScore(1.0);
		cluster1.add(new FusableValue<Double, Movie>(1.0, null, ds1));
		FusableDataSet<Movie> ds2 = new FusableDataSet<>();
		ds2.setScore(0.5);
		cluster1.add(new FusableValue<Double, Movie>(2.0, null, ds2));
		FusableDataSet<Movie> ds3 = new FusableDataSet<>();
		ds3.setScore(0.1);
		cluster1.add(new FusableValue<Double, Movie>(3.0, null, ds3));
		FusedValue<Double, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(1.0, resolvedValue.getValue());

	}

	public void testResolveConflict1() {

		FavourSources<Double, Movie> crf = new FavourSources<Double, Movie>();
		List<FusableValue<Double, Movie>> cluster1 = new ArrayList<FusableValue<Double, Movie>>();
		FusedValue<Double, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(null, resolvedValue.getValue());

	}
	
	public void testResolveConflict2() {

		FavourSources<Double, Movie> crf = new FavourSources<Double, Movie>();
		List<FusableValue<Double, Movie>> cluster1 = new ArrayList<FusableValue<Double, Movie>>();
		FusableDataSet<Movie> ds1 = new FusableDataSet<>();
		ds1.setScore(1.0);
		cluster1.add(new FusableValue<Double, Movie>(1.0, null, ds1));
		FusableDataSet<Movie> ds2 = new FusableDataSet<>();
		ds2.setScore(0.5);
		cluster1.add(new FusableValue<Double, Movie>(2.0, null, ds2));
		FusableDataSet<Movie> ds3 = new FusableDataSet<>();
		ds3.setScore(10.1);
		cluster1.add(new FusableValue<Double, Movie>(3.0, null, ds3));
		FusedValue<Double, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(3.0, resolvedValue.getValue());

	}

}
