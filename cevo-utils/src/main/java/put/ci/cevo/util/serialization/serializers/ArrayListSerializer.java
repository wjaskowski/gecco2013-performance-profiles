package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;
import java.util.ArrayList;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

public class ArrayListSerializer<T> implements ObjectSerializer<ArrayList<T>> {
	@Override
	public void save(ArrayList<T> arr, SerializationOutput output) throws IOException, SerializationException {
		output.writeInt(arr.size());
		for (T element : arr)
			SerializationManager.get().serialize(element, output);
	}

	@Override
	public ArrayList<T> load(SerializationInput input) throws IOException, SerializationException {
		int len = input.readInt();
		ArrayList<T> arr = new ArrayList<T>(len);
		for (int i = 0; i < len; ++i)
			arr.add(SerializationManager.get().<T> deserialize(input));
		return arr;
	}

	@Override
	public int getUniqueSerializerId() {
		return 123499;
	}
}