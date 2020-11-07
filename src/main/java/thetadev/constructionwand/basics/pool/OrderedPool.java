package thetadev.constructionwand.basics.pool;

import java.util.ArrayList;

public class OrderedPool<T> implements IPool<T>
{
	private final ArrayList<T> elements;
	private int index;

	public OrderedPool() {
		elements = new ArrayList<>();
		reset();
	}

	@Override
	public void add(T element) {
		elements.add(element);
	}

	@Override
	public T draw() {
		if(index >= elements.size()) return null;
		T e = elements.get(index);
		index++;
		return e;
	}

	@Override
	public void reset() {
		index = 0;
	}
}
