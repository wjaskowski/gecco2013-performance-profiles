package put.ci.cevo.framework;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import org.apache.log4j.Logger;

import put.ci.cevo.util.sequence.transforms.TransformMap;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class ConcurrentRandomSupplier {
	
	private static final Logger logger = Logger.getLogger(ConcurrentRandomSupplier.class);

//	private static final ConfigurationKey EXPERIMENT_SEED = new ConfKey("experiment.seed");

	private static final Supplier<ConcurrentRandomSupplier> INSTANCE = Suppliers
		.memoize(new Supplier<ConcurrentRandomSupplier>() {
			@Override
			public ConcurrentRandomSupplier get() {
//				int seed = Configuration.getConfiguration().getSeed(EXPERIMENT_SEED, 1337);
				logger.info("Initializing random pool with seed: " + 1337);
				return new ConcurrentRandomSupplier(new MersenneTwister(1337));
			}
		});

	private final Map<String, RandomDataGenerator> pool;
	private final RandomGenerator baseRandom;

	private ConcurrentRandomSupplier(RandomGenerator random) {
		this.baseRandom = new SynchronizedRandomGenerator(random);
		this.pool = new TransformMap<String, RandomDataGenerator>(
			new Object2ObjectOpenHashMap<String, RandomDataGenerator>()) {
				@Override
				protected RandomDataGenerator transform(String threadName) {
					int seed = baseRandom.nextInt();
					logger.debug("Creating new random data generator for thread: " + threadName + " with seed: " + seed);
					return new RandomDataGenerator(new MersenneTwister(seed));
				}
			};
	}

	public static RandomDataGenerator forThread(Thread thread, Class<?> clazz) {
		return forName(thread.getName() + " @ " + clazz.getSimpleName());
	}

	public static RandomDataGenerator forThread(Thread thread) {
		return forName(thread.getName());
	}

	public static RandomDataGenerator forName(String name) {
		try {
			return getPool().get(name);
		} catch (NoSuchElementException e) {
			throw new RuntimeException("The pool is exhausted!", e);
		} catch (IllegalStateException e) {
			throw new RuntimeException("The pool has already been closed!", e);
		} catch (Exception e) {
			throw new RuntimeException("Internal error while trying to acquire the pooled object", e);
		}
	}

	protected static Map<String, RandomDataGenerator> getPool() {
		return INSTANCE.get().pool;
	}

}
