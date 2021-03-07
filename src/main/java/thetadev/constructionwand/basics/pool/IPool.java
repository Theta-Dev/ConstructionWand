package thetadev.constructionwand.basics.pool;

import javax.annotation.Nullable;

public interface IPool<T>
{
    void add(T element);

    void remove(T element);

    @Nullable
    T draw();

    void reset();
}
