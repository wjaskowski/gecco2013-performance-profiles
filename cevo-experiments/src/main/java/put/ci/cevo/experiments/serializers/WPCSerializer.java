package put.ci.cevo.experiments.serializers;

import java.io.IOException;

import put.ci.cevo.games.encodings.WPC;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

public class WPCSerializer implements ObjectSerializer<WPC> {

	@Override
	public void save(WPC object, SerializationOutput output) throws IOException, SerializationException {
		SerializationManager.get().serialize(object.buffer, output);
	}

	@Override
	public WPC load(SerializationInput input) throws IOException, SerializationException {
		double[] buffer = SerializationManager.get().deserialize(input);
		return new WPC(buffer);
	}

	@Override
	public int getUniqueSerializerId() {
		return 34534;
	}
}
