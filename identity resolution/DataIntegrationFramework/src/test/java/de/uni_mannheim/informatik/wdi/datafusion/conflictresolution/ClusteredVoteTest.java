package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

public class ClusteredVoteTest extends TestCase {

	public void testResolveConflict() {
		ClusteredVote<String, Movie> crf = new ClusteredVote<String, Movie>(
				new LevenshteinSimilarity(), 0.0);

		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();
		cluster1.add(new FusableValue<String, Movie>("hi", null, null));
		cluster1.add(new FusableValue<String, Movie>("hi1", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello1", null,
				null));
		cluster1.add(new FusableValue<String, Movie>("hello", null, null));
		cluster1.add(new FusableValue<String, Movie>("hello2", null,
				null));

		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals("hello1", resolvedValue.getValue());
	}

	public void testResolveConflict1() {
		ClusteredVote<String, Movie> crf = new ClusteredVote<String, Movie>(
				new LevenshteinSimilarity(), 0.0);

		List<FusableValue<String, Movie>> cluster1 = new ArrayList<FusableValue<String, Movie>>();

		FusedValue<String, Movie> resolvedValue = crf
				.resolveConflict(cluster1);
		assertEquals(null, resolvedValue.getValue());
	}
}
