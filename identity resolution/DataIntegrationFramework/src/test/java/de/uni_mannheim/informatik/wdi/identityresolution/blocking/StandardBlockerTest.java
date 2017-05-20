package de.uni_mannheim.informatik.wdi.identityresolution.blocking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import de.uni_mannheim.informatik.wdi.identityresolution.Correspondence;
import de.uni_mannheim.informatik.wdi.identityresolution.MatchingEvaluator;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.Performance;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.MovieFactory;

public class StandardBlockerTest extends TestCase {

	public void testGeneratePairs() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		DataSet<Movie> ds = new DataSet<>();
		File sourceFile1 = new File("usecase/movie/input/actors.xml");
		ds.loadFromXML(sourceFile1, new MovieFactory(), "/movies/movie");

		DataSet<Movie> ds2 = new DataSet<>();
		File sourceFile2 = new File("usecase/movie/input/academy_awards.xml");
		ds2.loadFromXML(sourceFile2, new MovieFactory(), "/movies/movie");

		Blocker<Movie> blocker = new StandardBlocker<Movie>(
				new StaticBlockingKeyGenerator<Movie>());

		MatchingGoldStandard gs = new MatchingGoldStandard();
		gs.loadFromCSVFile(new File(
				"usecase/movie/goldstandard/gs_academy_awards_2_actors.csv"));

		List<Pair<Movie, Movie>> pairs = blocker.generatePairs(ds, ds2);
		List<Correspondence<Movie>> correspondences = new ArrayList<>(
				pairs.size());

		// transform pairs into correspondences
		for (Pair<Movie, Movie> p : pairs) {
			correspondences.add(new Correspondence<Movie>(p.getFirst(), p
					.getSecond(), 1.0));
		}

		// check if all examples from the gold standard were in the pairs
		MatchingEvaluator<Movie> eval = new MatchingEvaluator<>(true);

		Performance perf = eval.evaluateMatching(correspondences, gs);

		assertEquals(1.0, perf.getRecall());
	}

}
