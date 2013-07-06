package put.ci.cevo.games.encodings;

import java.io.Serializable;
import java.util.Arrays;

public class WPC implements Comparable<WPC>, Serializable {

	private static final long serialVersionUID = 8899559906200648797L;

	final public double buffer[];

	public WPC(int size) {
		buffer = new double[size];
	}

	public WPC(double arr[]) {
		buffer = arr;
	}

	public double get(int idx) {
		return buffer[idx];
	}

	public double set(int idx, double value) {
		return buffer[idx] = value;
	}

	public int getSize() {
		return buffer.length;
	}

	@Override
	public int compareTo(WPC other) {
		return Arrays.toString(buffer).compareTo(Arrays.toString(other.buffer));
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof WPC) {
			WPC that = (WPC) other;
			result = Arrays.equals(this.buffer, that.buffer);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(buffer);
	}

	@Override
	public String toString() {
		String s = "[" + buffer.length + "]";
		for (int i = 0; i < buffer.length; ++i)
			s += String.format(" %.2f,", buffer[i]);
		return s;
	}
}
