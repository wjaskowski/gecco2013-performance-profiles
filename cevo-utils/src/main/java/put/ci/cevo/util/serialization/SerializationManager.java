package put.ci.cevo.util.serialization;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jodah.typetools.TypeResolver;

import put.ci.cevo.util.serialization.serializers.ArrayListSerializer;
import put.ci.cevo.util.serialization.serializers.DoubleArraySerializer;
import put.ci.cevo.util.serialization.serializers.HashMapSerializer;
import put.ci.cevo.util.serialization.serializers.IntArraySerializer;
import put.ci.cevo.util.serialization.serializers.IntegerSerializer;

import com.google.common.base.Preconditions;

public final class SerializationManager {
	private static final SerializationManager instance = new SerializationManager();
	private static final Logger logger = Logger.getLogger(SerializationManager.class);

	private final Map<Type, ObjectSerializer<? extends Object>> defaultSerializers = new HashMap<>();
	private final Map<Integer, ObjectSerializer<? extends Object>> deserializers = new HashMap<>();

	/**
	 * Get SerializationManager singleton instance
	 */
	public static SerializationManager get() {
		return instance;
	}

	private SerializationManager() {
		if (instance != null)
			throw new IllegalStateException("Already instantiated");
		registerBuiltInSerializers();
	}

	private void registerBuiltInSerializers() {
		try {
			register(new IntegerSerializer());
			register(new DoubleArraySerializer());
			register(new IntArraySerializer());
			register(new ArrayListSerializer<>());
			register(new HashMapSerializer<>());
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Or maybe registerDefault(Class<T extends ObjectSerializer> clazz)? Then clazz.newInstance()?
	/**
	 * Registers serializer and deserializer. A warning is logged if serializer for the same type already existed
	 */
	public <T> void register(ObjectSerializer<T> serializer) throws SerializationException {
		registerSerializer(serializer);
		registerDeserializer(serializer);
	}

	/**
	 * Used to register old serializers. They will be used only for deserialization, never for serialization.
	 */
	public <T> void registerAdditional(ObjectSerializer<T> serializer) throws SerializationException {
		registerDeserializer(serializer);
	}

	/**
	 * Unregister a serializer (and a deserializer). Unregistration is based on Serializer's id and generic param type.
	 */
	public <T> void unregister(ObjectSerializer<T> serializer) {
		defaultSerializers.remove(getSerializerObjectType(serializer));
		deserializers.remove(serializer.getUniqueSerializerId());
	}

	/**
	 * Serialize object to output
	 * 
	 * @param object
	 *            Object to be serialized
	 * @param output
	 */
	public <T> void serialize(T object, SerializationOutput output) throws SerializationException {
		Preconditions.checkNotNull(object);

		ObjectSerializer<T> serializer = ((ObjectSerializer<T>) defaultSerializers.get(object.getClass()));
		if (serializer == null)
			throw new SerializationException("No serializer has been registered for type "
				+ object.getClass().toString());
		serialize(object, serializer, output);
	}

	/**
	 * Serialize object to file with BinarySerializator.
	 * 
	 * @param object
	 *            Object to be serialized
	 * @param file
	 *            Destination file
	 */
	public <T> void serialize(T object, File file) throws SerializationException, FileNotFoundException {
		Preconditions.checkNotNull(object);
		try (BinarySerializationOutput output = new BinarySerializationOutput(openOutputStream(file))) {
			serialize(object, output);
		} catch (IOException e) {
			throw new SerializationException("Cound to close file: " + file.toString(), e);
		}
	}

	/**
	 * Deserialize object from input and return it
	 */
	public <T> T deserialize(SerializationInput input) throws SerializationException {
		try {
			int id = input.readInt();
			ObjectSerializer<?> deserializer = deserializers.get(id);
			if (deserializer == null)
				throw new SerializationException("Deserializator with id " + id + " was not registered");
			return (T) deserializer.load(input);
		} catch (IOException e) {
			throw new SerializationException("Failed to deserialize object", e);
		}
	}

	/**
	 * Deserialize object from file with BinarySerializator
	 */
	public <T> T deserialize(File file) throws SerializationException, FileNotFoundException {
		try (BinarySerializationInput input = new BinarySerializationInput(openInputStream(file))) {
			return deserialize(input);
		} catch (IOException e) {
			throw new SerializationException("Could not deserialize from file: " + file.toString(), e);
		}
	}

	/**
	 * Serialize with a explicitly given serializer. Just for testing.
	 * 
	 * @throws SerializationException
	 */
	<T> void serialize(T object, ObjectSerializer<T> serializer, SerializationOutput output)
			throws SerializationException {
		try {
			output.writeInt(serializer.getUniqueSerializerId());
			serializer.save(object, output);
		} catch (IOException e) {
			throw new SerializationException("Could not serialize object", e);
		}
	}

	private <T> void registerSerializer(ObjectSerializer<T> serializer) {
		Type type = getSerializerObjectType(serializer);

		if (defaultSerializers.containsKey(defaultSerializers.containsKey(type))) {
			logger.warn("Restering serializer " + serializer.getClass().getName()
				+ " which overrides previously registered serializer for class " + type);
		}
		defaultSerializers.put(type, serializer);
	}

	private <T> void registerDeserializer(ObjectSerializer<T> serializer) throws SerializationException {
		if (deserializers.containsKey(serializer.getUniqueSerializerId()))
			throw new SerializationException("Cannot register " + serializer.getClass().getName()
				+ " because other serializer with this Id has been already registered for deserialization");
		deserializers.put(serializer.getUniqueSerializerId(), serializer);
	}

	/**
	 * Returns type of T in ObjectSerializer<T>
	 */
	private <T> Class<?> getSerializerObjectType(ObjectSerializer<T> serializer) {
		return TypeResolver.resolveRawArgument(ObjectSerializer.class, serializer.getClass());
	}
}
