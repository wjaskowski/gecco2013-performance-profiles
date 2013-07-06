package put.ci.cevo.profiles.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import put.ci.cevo.experiments.serializers.WPCSerializer;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.experiments.wpc.othello.OthelloWPCInteraction;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.serializers.EvaluatedIndividualSerializer;
import put.ci.cevo.games.encodings.WPC;
import put.ci.cevo.games.othello.OthelloWPCPlayer;
import put.ci.cevo.profiles.PerfProfile;
import put.ci.cevo.profiles.PerfProfileDatabase;
import put.ci.cevo.profiles.PerfProfileDatabaseSerializer;
import put.ci.cevo.util.Statistics;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;

/**
 * Computes the average performance and profile of the standard WPC heuristic for Othello
 */
public class ComputeExpectedUtilityAndProfile {

	private static final int NUM_OPPONENTS = 50000;

	public static void main(String[] args) throws FileNotFoundException, SerializationException, IOException {
		if (args.length != 1) {
			System.out.println("Usage: <program> file-with-wpc");
			System.exit(0);
		}
		SerializationManager.get().register(new EvaluatedIndividualSerializer<Object>());
		SerializationManager.get().register(new PerfProfileDatabaseSerializer<>());
		SerializationManager.get().register(new WPCSerializer());

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		InteractionDomain<WPC, WPC> othello = new OthelloWPCInteraction(true);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(othello, new UniformRandomPopulationFactory<>(
			new WPCIndividualFactory(1.0, -1.0, OthelloWPCPlayer.NUM_WEIGHTS)), NUM_OPPONENTS, random);

		PerfProfileDatabase<WPC> db = SerializationManager.get().deserialize(
			new File("othello-pprofile-db-random-new.dump"));

		WPC wpc = loadFromFile(new File(args[0]));

		StatisticalSummary perf = measure.measure(wpc, random);
		System.out.println(String.format("Expected Utility: %.2f%%; 95%% Confidence Interval: %.2f%%", 100.0 * perf.getMean(),
			100.0 * Statistics.getConfidenceIntervalDelta(perf, 0.05)));

		// Compute performance profile
		PerfProfile profile = PerfProfile.createForPlayerTeam(db, othello, Arrays.asList(wpc));
		profile.saveAsCSV(new File("profile.csv"));
		System.out.println("Generated a profile: profile.csv");
	}

	private static WPC loadFromFile(File file) throws FileNotFoundException {
		try (Scanner scanner = new Scanner(file)) {
			double[] buffer = new double[OthelloWPCPlayer.NUM_WEIGHTS];
			for (int i = 0; i < buffer.length; ++i) {
				buffer[i] = scanner.nextDouble();
			}
			return new WPC(buffer);
		}
	}
}