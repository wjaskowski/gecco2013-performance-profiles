package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationOutput;

public class DoubleArraySerializer implements ObjectSerializer<double[]> {
	@Override
	public void save(double[] arr, SerializationOutput output) throws IOException {
		output.writeInt(arr.length);
		for (double element : arr)
			output.writeDouble(element);
	}

	@Override
	public double[] load(SerializationInput input) throws IOException, SerializationException {
		int len = input.readInt();
		double[] arr = new double[len];
		for (int i = 0; i < len; ++i)
			arr[i] = input.readDouble();
		return arr;
	}

	@Override
	public int getUniqueSerializerId() {
		return 1234;
	}
}