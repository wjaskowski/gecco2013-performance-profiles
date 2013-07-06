package put.ci.cevo.util.serialization;

import java.io.IOException;

public interface ObjectSerializer<T> {
	public void save(T object, SerializationOutput output) throws IOException, SerializationException;

	public T load(SerializationInput input) throws IOException, SerializationException;

	public int getUniqueSerializerId();
}
