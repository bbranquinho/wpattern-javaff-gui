package javaff.search.data;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;


public class PriorityQueueHashTable<E> extends PriorityQueue<E> {

	private static final long serialVersionUID = -1981048240607915705L;

	private final static int INITIAL_CAPACITY = 11;

	private Hashtable<Integer, E> hashTable;

	private Integer maxCapacity;

	public PriorityQueueHashTable(Integer maxCapacity) {
		super();
		this.hashTable = new Hashtable<Integer, E>();
		this.maxCapacity = maxCapacity;
	}

	public PriorityQueueHashTable(Integer maxCapacity, Comparator<? super E> comparator) {
		super(INITIAL_CAPACITY, comparator);
		this.hashTable = new Hashtable<Integer, E>();
		this.maxCapacity = maxCapacity;
	}

	public boolean insert(E e) {
		if (e != null) {
			if(this.containsElement(e))
				this.removeElement(e);

			if(super.offer(e)) {
				hashTable.put(e.hashCode(), e);
				return true;
			}
		}

		return false;
	}

	public E removeHead() {
		E e = super.poll();

		hashTable.remove(e.hashCode());

		return e ;
	}

	public boolean removeElement(E e) {
		if(super.remove(e)){
			hashTable.remove(e.hashCode());
			return true;
		}

		return false ;
	}

	public boolean containsElement(E e) {
		Integer eHashCode = new Integer(e.hashCode());

		E eInHashTable = this.hashTable.get(eHashCode);

		return (hashTable.containsKey(eHashCode) && eInHashTable.equals(e));
	}

	public E getElement(E e){
		return hashTable.get(e.hashCode());
	}

	public void restoreCapacity(){
		while(this.capacity() > this.maxCapacity()){
			removeHead();
		}
	}

	public Integer capacity(){
		return hashTable.size();
	}

	public Integer maxCapacity(){
		return this.maxCapacity;
	}

}
