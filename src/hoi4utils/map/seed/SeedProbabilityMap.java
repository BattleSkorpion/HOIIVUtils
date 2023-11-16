package hoi4utils.map.seed;

import hoi4utils.map.AbstractMapGeneration;
import hoi4utils.map.Heightmap;
import hoi4utils.map.MapPoint;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class SeedProbabilityMap extends AbstractMapGeneration {
	double[][] seedProbabilityMap;      // y, x
	double[] cumulativeProbabilities;   // per map, inclusive of previous maps
	final Heightmap heightmap;
	int pointNumber = 0;

	public SeedProbabilityMap(Heightmap heightmap) {
		this.heightmap = heightmap;

		seedProbabilityMap = new double[heightmap.xyHeight()][heightmap.width()]; // y, x
		cumulativeProbabilities = new double[(heightmap.xyHeight())];
		initializeProbabilityMap();
	}

	private void initializeProbabilityMap() {
		for (int yi = 0; yi < heightmap.xyHeight(); yi++) {
			for (int xi = 0; xi < heightmap.xyHeight(); xi++) {
				double p = 1.0;
				// improved performance over .getRGB()
				byte height = heightmap.xyHeight(xi, yi);
				int type = provinceType(height);

				/* probability */
				p *= type == 0 ? 1.15 : 0.85;

				seedProbabilityMap[yi][xi] = p;
			}
			cumulativeProbabilities[yi] = 0.0;
		}
		normalize();
	}

	public void normalize() {
		int length = seedProbabilityMap.length;
		ForkJoinPool pool = new ForkJoinPool();
		double sum = pool.invoke(new ProbabilitySumPerXTask(0, length));
		if (sum == 0) {
			return;
		}

		pool.invoke(new CumulativeProbabilityPerXTask(0, length, sum));
		for (int i = 1; i < cumulativeProbabilities.length; i++) {
			cumulativeProbabilities[i] += cumulativeProbabilities[i - 1];
		}
	}

	public MapPoint getPoint(Random random) {
		normalize();

		double p = random.nextDouble();
		// Find the first index where cumulative probability >= p
		int y = findCumulativeProbabilityIndex(p);
		if (y == -1) {
			System.err.println("Index -1 in " + this);
			Arrays.stream(cumulativeProbabilities).forEach(System.out::println);
			return null;
		}

		// Find the first MapPoint where cumulative probability >= p
		double cumulative = (y == 0) ? 0.0 : cumulativeProbabilities[y - 1];
		//System.out.println(cumulative);
		System.out.println(++pointNumber);
		MapPoint mp = null;
		for (int x = 0; x < seedProbabilityMap[y].length; x++) {
			cumulative += seedProbabilityMap[y][x];
			if (cumulative >= p) {
				mp = new MapPoint(x, y, provinceType(heightmap.xyHeight(x, y)));
				break;
			}
		}
		if (mp == null) {
			//System.out.println(cumulativeProbabilities[heightmap.getHeight() - 1]);
			System.out.println("bad probability? " + p);    // todo
			return getPoint(random);
		}
		/* adjust probabilities */
		adjustProbabilitiesInRadius(mp, 9);
		return mp;
	}

	private void adjustProbabilitiesInRadius(MapPoint mp, int r) {
		int minY = Math.max(mp.y - r, 0);
		int maxY = Math.min(mp.y + r, seedProbabilityMap.length - 1);
		int minX = Math.max(mp.x - r, 0);
		int maxX = Math.min(mp.x + r, seedProbabilityMap[0].length - 1);
		double[] distanceModifiers = new double[r + 1]; // 0 to r
		distanceModifiers[0] = 0.0;
		for (int i = 1; i <= r; i++) {
			distanceModifiers[i] = (double) (i - 0.2) / r; // Normalize the value to be between 0 and 1
		}

		//int rSquared = r * r;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				double distance = mp.distance(x, y);
				// Check if the distance is within the specified radius
				if (distance <= r) {
					// Adjust the probability at position (y, x)
					seedProbabilityMap[y][x] *= distanceModifiers[(int) distance];
				}
			}
		}
	}

	private int findCumulativeProbabilityIndex(double p) {
		return IntStream.range(0, cumulativeProbabilities.length)
				.filter(i -> Double.compare(cumulativeProbabilities[i], p) >= 0)
				.findFirst()
				.orElse(-1);
	}

	public class CumulativeProbabilityPerXTask extends RecursiveTask<Void> {
		private static final int THRESHOLD = 8; // Adjust based on your data size
		private final int start;        // inclusive
		private final int end;          // exclusive
		private final double pSum;      // probability sum
		private final double recipSum;

		public CumulativeProbabilityPerXTask(int start, int end, double pSum) {
			this.start = start;
			this.end = end;
			this.pSum = pSum;
			this.recipSum = 1.0 / pSum;
		}

		private CumulativeProbabilityPerXTask(int start, int end, double pSum, double recipSum) {
			this.start = start;
			this.end = end;
			this.pSum = pSum;
			this.recipSum = recipSum;
		}

		@Override
		protected Void compute() {
			if (end - start > THRESHOLD) {
				int mid = (start + end) / 2;
				invokeAll(
						new CumulativeProbabilityPerXTask(start, mid, pSum, recipSum),
						new CumulativeProbabilityPerXTask(mid, end, pSum, recipSum)
				);
			} else {
				cumulativeProbability(start, end);
			}
			return null;
		}

		private void cumulativeProbability(int start, int end) {
			for (int i = start; i < end; i++) {
				double cumulativeProbability = 0.0;
				for (int j = 0; j < seedProbabilityMap[i].length; j++) {
					seedProbabilityMap[i][j] *= recipSum;
					cumulativeProbability += seedProbabilityMap[i][j];
				}
				cumulativeProbabilities[i] = cumulativeProbability;
			}
		}
	}

	public class ProbabilitySumPerXTask extends RecursiveTask<Double> {
		private static final int THRESHOLD = 4; // Adjust based on your data size
		private final int start;
		private final int end;

		public ProbabilitySumPerXTask(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		protected Double compute() {
			if (end - start > THRESHOLD) {
				int mid = (start + end) / 2;
				var left = new ProbabilitySumPerXTask(start, mid);
				var right = new ProbabilitySumPerXTask(mid, end);

				invokeAll(left, right);

				return left.join() + right.join();
			} else {
				return computePartialSum();
			}
		}

		private double computePartialSum() {
			double sum = 0.0;
			for (int i = start; i < end; i++) {
				for (double v : seedProbabilityMap[i]) {
					sum += v;
				}
			}
			return sum;
		}
	}

}