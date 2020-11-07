package thetadev.constructionwand.basics.pool;

public interface IPool<T>
{
	void add(T element);
	T draw();
	void reset();
}
