package put.ci.cevo.framework.serializers;

import java.io.IOException;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;

public class EvaluatedIndividualSerializer<T> implements ObjectSerializer<EvaluatedIndividual<T>> {

	@Override
	public void save(EvaluatedIndividual<T> evaluated, SerializationOutput output) throws IOException,
			SerializationException {
		output.writeDouble(evaluated.getFitness());
		output.writeInt(evaluated.getGeneration());
		output.writeInt(evaluated.getEffort());
		SerializationManager.get().serialize(evaluated.getIndividual(), output);
	}

	@Override
	public EvaluatedIndividual<T> load(SerializationInput input) throws IOException, SerializationException {
		double fitness = input.readDouble();
		int generation = input.readInt();
		int effort = input.readInt();
		T individual = SerializationManager.get().deserialize(input);
		return new EvaluatedIndividual<T>(individual, fitness, generation, effort);
	}

	@Override
	public int getUniqueSerializerId() {
		return 12341134;
	}
}
