public class Entry<K,V> {
	private K key;
	private V val;

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getVal() {
		return val;
	}

	public void setVal(V val) {
		this.val = val;
	}

	public Entry(K key, V val) {
		this.key = key;
		this.val = val;
	}

	@Override
	public String toString() {
		return "(" + this.key + ", " + this.val + ")";
	}
}
