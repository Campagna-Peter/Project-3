package ods;

import java.lang.reflect.Array;

/**
 * A linear-probed hash table, using the strategies from the reading. This code
 * has not been tested well. You may find bugs! Report them, please.
 */
public class BubbaHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_LOADFACTOR = 0.5;

    private Entry[] table;
    private int size;
    private double loadFactor;

    public BubbaHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOADFACTOR);
    }

    public BubbaHashMap(int initCap) {
        this(initCap, DEFAULT_LOADFACTOR);
    }

    public BubbaHashMap(double lf) {
        this(DEFAULT_CAPACITY, lf);
    }

    public BubbaHashMap(int initCap, double lf) {
        clear(initCap);
        loadFactor = lf;
    }

    /**
     * TODO: Rewrite to use Bubba's ideas
     */
    public int calcOff(int hashValue, int index) {
        if (index - hashValue < 0) {
            return index - hashValue + table.length;
        } else {
            return (index - hashValue);
        }
    }

    // If the table[idx] != null and the the offset at this location is >= then
    // the offset of the value we are inserting at this location. If it's a
    // duplicate then we replace it and return the value at that location.
    // Otherwise if the spot is null then we just insert the value. Otherwise
    // we found a spot to place our value but there is another value there
    // so if we recursively call put with the value that used to be there.

    public V bubbaPut(K key, V value) {
        int idx = hash(key);
        int hashVal = idx;
        var entry = new Entry(key, value, 0);

        while (table[idx] != null) {
            if (table[idx].offset == calcOff(hashVal, idx) && table[idx].key.equals(key)) {
                V oldVal = table[idx].value;
                table[idx].value = value;
                return oldVal;
            } else if (table[idx].offset < calcOff(hashVal, idx)) {
                var temp = table[idx];
                table[idx] = entry;
                entry.offset = calcOff(hashVal, idx);
                entry = temp;
                hashVal = hash(entry.key);
            }
            idx = increment(idx);
        }
        table[idx] = entry;
        entry.offset = calcOff(hashVal, idx);
        size++;
        if (size >= loadFactor * table.length) {
            rehash(table.length * 2);
        }
        return value;
    }

    /**
     * TODO: Bubba rewrite
     */

    public V bubbaRemove(K key) {
        int idx = hash(key);
        int hashVal = idx;

        // If the table[idx] != null and the the offset at this location is >= then
        // the offset of the value we are inserting at this location. If it's the value
        // we are looking for then we set it to null. Otherwise then we check to see if
        // the next
        // location needs to be bumped, if so then we move it.
        while (table[idx] != null && table[idx].offset >= calcOff(hashVal, idx)) {
            if (table[idx].key.equals(key)) {
                table[idx] = null;
                size--;
                break;
            }
            idx = increment(idx);
        }
        while (table[increment(idx)] != null && table[increment(idx)].offset != 0) {
            table[idx] = table[increment(idx)];
            idx = increment(idx);
        }
        if (size < loadFactor * table.length / 4.0) {
            rehash(table.length / 2);
        }
        if (table[hashVal] != null) {
            return table[hashVal].value;
        }
        // bump over until offset is 0 or null, if we d
        table[idx] = null;
        return null;

    }

    /**
     * TODO: Bubba knows best
     */
    public V bubbaGet(K key) {
        int idx = hash(key);
        int hashVal = idx;

        while (table[idx] != null && table[idx].offset >= calcOff(hashVal, idx)) {
            if (table[idx].key != null && table[idx].equals(key)) {
                return table[idx].value;
            }
            increment(idx);
        }
        return null;
    }

    public int bubbaGetProbe(K key) {
        int idx = hash(key);
        int hashVal = idx;
        int probe = 0;

        while (table[idx] != null && table[idx].offset >= calcOff(hashVal, idx)) {
            if (table[idx].equals(key)) {
                probe++;
                return probe;
            }
            probe++;
            idx = increment(idx);
        }
        return probe;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        clear(DEFAULT_CAPACITY);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void clear(int cap) {
        table = (Entry[]) Array.newInstance(Entry.class, cap);
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % table.length);
    }

    private int increment(int idx) {
        idx++;
        return idx == table.length ? 0 : idx;
    }

    /**
     * TODO: Does this need a rewrite?
     * 
     * @param newCap
     */
    private void rehash(int newCap) {
        System.out.println("ARGHHHH");
        // make a new table of the new size, then iterate over the old
        // table and reinsert each entry.
        var oldTable = table;
        clear(newCap);
        for (var e : oldTable) {
            // skip nulls and tombstones.
            if (e != null) {
                this.bubbaPut(e.key, e.value);
            }
        }
    }

    public void printStats() {
        System.out.println("Size: " + size);
        System.out.println("Capacity: " + table.length);

    }

    public void print() {
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                System.out.print("null" + " ");
            } else {
                System.out.print(table[i].value + " ");
            }
        }
    }

    /**
     * An entry in our table. Note that we'll use an entry with key==null to
     * indicate a deleted entry (what the text called DEL; also called tombstones).
     *
     * TODO: What adjustments do Bubba's ideas suggest here?
     */

    private class Entry {
        K key;
        V value;
        int offset;

        Entry(K k, V v, int offset) {
            this.key = k;
            this.value = v;
            offset = -1;
        }
    }

    @Override
    public V put(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public V remove(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public V get(K key) {
        // TODO Auto-generated method stub
        return null;
    }
}
