package put.ci.cevo.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.carrotsearch.hppc.IntIndexedContainer;

public class ArrayUtils {
	/**
	 * Creates a list of size {@code length}, which elements are created by a factory
	 */
	public static <T> List<T> createRepeated(int length, Factory<T> factory) {
		List<T> list = new ArrayList<T>(length);
		for (int i = 0; i < length; ++i)
			list.add(factory.create());
		return list;
	}

	public static <T> T getRandomElement(List<T> arr, RandomDataGenerator random) {
		T e = arr.get(0);
		if (arr.size() > 1) {
			int rand = random.nextInt(0, arr.size() - 1);
			e = arr.get(rand);
		}
		return e;
	}

	public static int getRandomElement(int[] arr, RandomDataGenerator random) {
		int e = arr[0];
		if (arr.length > 1) {
			int rand = random.nextInt(0, arr.length - 1);
			e = arr[rand];
		}
		return e;
	}

	public static int getRandomElement(IntIndexedContainer arr, RandomDataGenerator random) {
		int e = arr.get(0);
		if (arr.size() > 1) {
			int rand = random.nextInt(0, arr.size() - 1);
			e = arr.get(rand);
		}
		return e;
	}

	/**
	 * Returns a string representation of 2D array such that it can be used as Java code
	 */
	public static String toJavaArray(int[][] arr) {
		StringBuilder str = new StringBuilder();
		str.append("new int[][] {\n");
		for (int i = 0; i < arr.length; i++) {
			str.append("{");
			for (int j = 0; j < arr.length; j++) {
				str.append(arr[i][j] + ",");
			}
			str.append("},\n");
		}
		str.append("}");
		return str.toString();
	}

	/**
	 * Returns a string representation of array such that it can be used as Java code
	 */
	public static String toJavaArray(double[] arr) {
		StringBuilder str = new StringBuilder();
		str.append("new double[] { ");
		for (int i = 0; i < arr.length; i++) {
			str.append(arr[i] + ",");
		}
		str.append("}");
		return str.toString();
	}

	/** Computes the number of elements in 2d array */
	private static int numElements(double[][] arr) {
		int count = 0;
		for (int i = 0; i < arr.length; ++i)
			count += arr[i].length;
		return count;
	}

	/** Converts 2d array to 1d array */
	public static double[] flatten(double[][] arr) {
		// TODO: Performance can be improved
		double[] result = new double[numElements(arr)];
		int index = 0;
		for (int i = 0; i < arr.length; ++i)
			for (int j = 0; j < arr[i].length; ++j)
				result[index++] = arr[i][j];
		return result;
	}
}
