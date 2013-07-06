package put.ci.cevo.profiles;

import java.io.IOException;
import java.util.ArrayList;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

public class PerfProfileDatabaseSerializer<T> implements ObjectSerializer<PerfProfileDatabase<T>> {

	@Override
	public void save(PerfProfileDatabase<T> db, SerializationOutput output) throws IOException, SerializationException {
		output.writeInt(db.getNumBuckets());
		for (int i = 0; i < db.getNumBuckets(); ++i) {
			ArrayList<EvaluatedIndividual<T>> bucket = new ArrayList<EvaluatedIndividual<T>>(db.getBucket(i));
			SerializationManager.get().serialize(bucket, output);
		}
	}

	@Override
	public PerfProfileDatabase<T> load(SerializationInput input) throws IOException, SerializationException {
		int numBuckets = input.readInt();
		PerfProfileDatabase<T> db = new PerfProfileDatabase<T>(numBuckets);
		for (int i = 0; i < db.getNumBuckets(); ++i) {
			db.setBucket(i, SerializationManager.get().<ArrayList<EvaluatedIndividual<T>>> deserialize(input));
		}
		return db;
	}

	@Override
	public int getUniqueSerializerId() {
		return 551234530;
	}
}
