package put.ci.cevo.util.sequence.transforms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

public class TransformMap<K, V> implements Map<K, V> {

	protected final Map<K, V> base;
	protected final Function<K, V> transform;

	public TransformMap(Map<K, V> base, Function<K, V> transform) {
		this.base = base;
		this.transform = transform;
	}

	public TransformMap(Function<K, V> transform) {
		this(new HashMap<K, V>(), transform);
	}

	public TransformMap(Map<K, V> base) {
		this.base = base;
		this.transform = new Function<K, V>() {
			@Override
			public V apply(K k) {
				return TransformMap.this.transform(k);
			}
		};
	}

	public TransformMap() {
		this(new HashMap<K, V>());
	}

	@Override
	public void clear() {
		base.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return base.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return base.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return base.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return base.equals(o);
	}

	@Override
	public V get(Object key) {
		if (base.containsKey(key)) {
			return base.get(key);
		}
		@SuppressWarnings("unchecked")
		K k = (K) key;
		V v = transform.apply(k);
		base.put(k, v);
		return v;
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return base.keySet();
	}

	@Override
	public V put(K key, V value) {
		return base.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		base.putAll(m);
	}

	@Override
	public V remove(Object key) {
		return base.remove(key);
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public Collection<V> values() {
		return base.values();
	}

	protected V transform(K k) {
		throw new RuntimeException("Must either implement transform or pass a Transform");
	}

	@Override
	public String toString() {
		return base.toString();
	}

	protected Map<K, V> getBase() {
		return base;
	}

}
