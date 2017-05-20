package de.uni_mannheim.informatik.wdi.identityresolution;

import java.io.File;

import junit.framework.TestCase;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.StandardBlocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.StaticBlockingKeyGenerator;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution.MovieDateComparator10Years;
import de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution.MovieDirectorComparatorLevenshtein;
import de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution.MovieTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.MovieFactory;

public class MatchingEngineTest extends TestCase {

	public void testRunMatching() throws Exception {
		DataSet<Movie> ds = new DataSet<>();
		File sourceFile1 = new File("usecase/movie/input/actors.xml");
		ds.loadFromXML(sourceFile1, new MovieFactory(), "/movies/movie");

		DataSet<Movie> ds2 = new DataSet<>();
		File sourceFile2 = new File("usecase/movie/input/academy_awards.xml");
		ds2.loadFromXML(sourceFile2, new MovieFactory(), "/movies/movie");

		LinearCombinationMatchingRule<Movie> rule = new LinearCombinationMatchingRule<>(
				0, 0);
		rule.addComparator(new MovieTitleComparatorLevenshtein(), 0.5);
		rule.addComparator(new MovieDirectorComparatorLevenshtein(), 0.25);
		rule.addComparator(new MovieDateComparator10Years(), 0.25);

		Blocker<Movie> blocker = new StandardBlocker<Movie>(
				new StaticBlockingKeyGenerator<Movie>());
		MatchingEngine<Movie> engine = new MatchingEngine<>(rule, blocker);

		engine.runMatching(ds, ds2);
	}

	public void testRunDeduplication() throws Exception {
		DataSet<Movie> ds = new DataSet<>();
		File sourceFile1 = new File("usecase/movie/input/actors.xml");
		ds.loadFromXML(sourceFile1, new MovieFactory(), "/movies/movie");

		LinearCombinationMatchingRule<Movie> rule = new LinearCombinationMatchingRule<>(
				0, 0);
		rule.addComparator(new MovieTitleComparatorLevenshtein(), 0.5);
		rule.addComparator(new MovieDirectorComparatorLevenshtein(), 0.25);
		rule.addComparator(new MovieDateComparator10Years(), 0.25);

		Blocker<Movie> blocker = new StandardBlocker<Movie>(
				new StaticBlockingKeyGenerator<Movie>());
		MatchingEngine<Movie> engine = new MatchingEngine<>(rule, blocker);

		engine.runDuplicateDetection(ds, true);
	}

	public void testGenerateFeaturesForOptimisation()
			throws Exception {
		DataSet<Movie> ds = new DataSet<>();
		File sourceFile1 = new File("usecase/movie/input/actors.xml");
		ds.loadFromXML(sourceFile1, new MovieFactory(), "/movies/movie");

		DataSet<Movie> ds2 = new DataSet<>();
		File sourceFile2 = new File("usecase/movie/input/academy_awards.xml");
		ds2.loadFromXML(sourceFile2, new MovieFactory(), "/movies/movie");

		LinearCombinationMatchingRule<Movie> rule = new LinearCombinationMatchingRule<>(
				0, 0);
		rule.addComparator(new MovieTitleComparatorLevenshtein(), 0.5);
		rule.addComparator(new MovieDirectorComparatorLevenshtein(), 0.25);
		rule.addComparator(new MovieDateComparator10Years(), 0.25);

		Blocker<Movie> blocker = new StandardBlocker<Movie>(
				new StaticBlockingKeyGenerator<Movie>());
		MatchingEngine<Movie> engine = new MatchingEngine<>(rule, blocker);

		MatchingGoldStandard gs = new MatchingGoldStandard();
		gs.loadFromCSVFile(new File(
				"usecase/movie/goldstandard/gs_academy_awards_2_actors.csv"));

		engine.generateTrainingDataForLearning(ds, ds2, gs);
	}

}
