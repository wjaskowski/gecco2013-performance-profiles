package put.ci.cevo.util.serialization.serializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

public class HashMapSerializer<K, V> implements ObjectSerializer<HashMap<K, V>> {
	@Override
	public void save(HashMap<K, V> arr, SerializationOutput output) throws IOException, SerializationException {
		output.writeInt(arr.entrySet().size());
		for (Entry<K, V> element : arr.entrySet()) {
			SerializationManager.get().serialize(element.getKey(), output);
			SerializationManager.get().serialize(element.getValue(), output);
		}
	}

	@Override
	public HashMap<K, V> load(SerializationInput input) throws IOException, SerializationException {
		int size = input.readInt();
		HashMap<K, V> map = new HashMap<K, V>(size * 2);
		for (int i = 0; i < size; ++i) {
			K key = SerializationManager.get().deserialize(input);
			V value = SerializationManager.get().deserialize(input);
			map.put(key, value);
		}
		return map;
	}

	@Override
	public int getUniqueSerializerId() {
		return 17;
	}
}