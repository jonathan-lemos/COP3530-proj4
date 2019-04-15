import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CuckooHashMap<K, V> {
	private Entry<K, V>[] arr1;
	private Entry<K, V>[] arr2;
	private int cap;
	private int prime;
	private int size = 0;

	public CuckooHashMap() {
		this(4, 10007);
	}

	public CuckooHashMap(int cap) {
		this(cap, 10007);
	}

	public CuckooHashMap(int cap, int prime) {
		this.prime = prime;
		resize(cap);
	}

	public float loadFactor() {
		return (float)size / (float)cap;
	}

	public int size() {
		return this.size;
	}

	public int capacity() {
		return this.cap;
	}

	public V get(K key) {
		int h1h = h1(key);
		int h2h = h2(key);

		if (arr1[h1h] != null && arr1[h1h].getKey().equals(key)) {
			return arr1[h1h].getVal();
		}
		if (arr2[h2h]  != null && arr2[h2h].getKey().equals(key)) {
			return arr2[h2h].getVal();
		}
		return null;
	}

	public V remove(K key) {
		int h1h = h1(key);
		int h2h = h2(key);

		if (arr1[h1h] != null && arr1[h1h].getKey().equals(key)) {
			V ret = arr1[h1h].getVal();
			arr1[h1h] = null;
			size--;
			if (loadFactor() < 0.25 && capacity() >= 8) {
				resize(cap / 2);
			}
			return ret;
		}
		if (arr2[h2h] != null && arr2[h2h].getKey().equals(key)) {
			V ret = arr2[h2h].getVal();
			arr2[h2h] = null;
			size--;
			if (loadFactor() < 0.25 && capacity() >= 8) {
				resize(cap / 2);
			}
			return ret;
		}

		return null;
	}

	public V put(K key, V value) {
		HashSet<K> keysOld = new HashSet<>();
		Entry<K, V> n = new Entry<>(key, value);
		Entry<K, V> old = null;
		int h;
		boolean a1 = true;

		{
			int h1h = h1(key);
			int h2h = h2(key);

			if (arr1[h1h] != null && arr1[h1h].getKey().equals(key)) {
				V tmp = arr1[h1h].getVal();
				arr1[h1h].setVal(value);
				return tmp;
			}
			if (arr2[h2h] != null && arr2[h2h].getKey().equals(key)) {
				V tmp = arr2[h2h].getVal();
				arr2[h2h].setVal(value);
				return tmp;
			}
		}

		if ((float)(size + 1) / (float)cap >= 0.5) {
			resize(this.cap * 2);
			return put(key, value);
		}

		while (true) {
			if (keysOld.contains(n.getKey())) {
				rehash();
				return put(key, value);
			}
			keysOld.add(n.getKey());
			if (a1) {
				h = h1(n.getKey());
				if (arr1[h] == null) {
					arr1[h] = n;
					size++;
					return old == null ? null : old.getVal();
				}
				Entry<K, V> tmp = n;
				old = n;
				n = arr1[h];
				arr1[h] = tmp;
				a1 = false;
			} else {
				h = h2(n.getKey());
				if (arr2[h] == null) {
					arr2[h] = n;
					size++;
					return old.getVal();
				}
				Entry<K, V> tmp = n;
				old = n;
				n = arr2[h];
				arr2[h] = tmp;
				a1 = true;
			}
		}
	}

	public Iterable<Entry<K, V>> entrySet() {
		ArrayList<Entry<K, V>> tmp = new ArrayList<>();
		if (arr1 == null || arr2 == null) {
			return tmp;
		}
		for (int i = 0; i < arr1.length; ++i) {
			if (arr1[i] != null)
				tmp.add(arr1[i]);
			if (arr2[i] != null)
				tmp.add(arr2[i]);
		}
		return tmp;
	}

	public Iterable<K> keySet() {
		ArrayList<K> tmp = new ArrayList<>();
		if (arr1 == null || arr2 == null) {
			return tmp;
		}
		for (int i = 0; i < arr1.length; ++i) {
			if (arr1[i] != null)
				tmp.add(arr1[i].getKey());
			if (arr2[i] != null)
				tmp.add(arr2[i].getKey());
		}
		return tmp;
	}

	public Iterable<V> valueSet() {
		ArrayList<V> tmp = new ArrayList<>();
		if (arr1 == null || arr2 == null) {
			return tmp;
		}
		for (int i = 0; i < arr1.length; ++i) {
			if (arr1[i] != null)
				tmp.add(arr1[i].getVal());
			if (arr2[i] != null)
				tmp.add(arr2[i].getVal());
		}
		return tmp;
	}

	private void rehash() {
		while (true) {
			prime++;
			int i;
			for (i = 2; i < prime; ++i)
				if (prime % i == 0)
					break;
			if (i == prime)
				break;
		}
		Iterable<Entry<K, V>> old = entrySet();
		for (Entry<K, V> x : old) {
			put(x.getKey(), x.getVal());
		}
	}

	private void resize(int newCap) {
		Iterable<Entry<K, V>> old = entrySet();

		size = 0;
		cap = newCap;
		arr1 = (Entry<K, V>[])(new Entry[cap / 2]);
		arr2 = (Entry<K, V>[])(new Entry[cap / 2]);

		for (Entry<K, V> x : old) {
			put(x.getKey(), x.getVal());
		}
	}

	private int h1(K key) {
		return (Math.abs(key.hashCode()) % prime) % (cap / 2);
	}

	private int h2(K key) {
		return ((Math.abs(key.hashCode()) / prime) % prime) % (cap / 2);
	}
}
