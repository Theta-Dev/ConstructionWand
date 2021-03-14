package thetadev.constructionwand.basics.pool;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class RandomPool<T> implements IPool<T>
{
    private final Random rng;
    private final HashMap<T, Integer> elements;
    private HashSet<T> pool;

    public RandomPool(Random rng) {
        this.rng = rng;
        elements = new HashMap<>();
        reset();
    }

    @Override
    public void add(T element) {
        addWithWeight(element, 1);
    }

    @Override
    public void remove(T element) {
        elements.remove(element);
        pool.remove(element);
    }

    public void addWithWeight(T element, int weight) {
        if(weight < 1) return;
        elements.merge(element, weight, Integer::sum);
        pool.add(element);
    }

    @Nullable
    @Override
    public T draw() {
        int allWeights = pool.stream().reduce(0, (partialRes, e) -> partialRes + elements.get(e), Integer::sum);
        if(allWeights < 1) return null;

        int random = rng.nextInt(allWeights);
        int accWeight = 0;

        for(T e : pool) {
            accWeight += elements.get(e);
            if(random < accWeight) {
                pool.remove(e);
                return e;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        pool = new HashSet<>(elements.keySet());
    }
}
