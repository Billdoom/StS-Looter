package lootermod.cards.generator;

import com.megacrit.cardcrawl.random.Random;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class WeightedList<T> {

    private ArrayList<WeightedOption> options;
    private int totalWeight = 0;

    private class WeightedOption {
        T option;
        int weight;

        public WeightedOption(T option, int weight) {
            this.option = option;
            this.weight = weight;
        }
    }

    public WeightedList(WeightedOption... options) {
        this.options = new ArrayList<>();
        for (WeightedOption option : options) {
            this.options.add(option);
            totalWeight += option.weight;
        }
    }

    public void addOption(T option, int weight) {
        this.options.add(new WeightedOption(option, weight));
        totalWeight += weight;
    }

    public static <Z> WeightedList<Z> of(Z item1, int weight1, Object... remaining) {
        WeightedList<Z> list = new WeightedList<>();
        list.addOption(item1, weight1);
        for (int i = 0; i < remaining.length; i += 2) {
            list.addOption((Z)remaining[i], (int)remaining[i+1]);
        }
        return list;
    }

    public T get(Random random) {
        if (options.isEmpty()) {
            return null;
        }
        int roll = random.random(totalWeight);
        Iterator<WeightedOption> optionIterator = options.iterator();
        WeightedOption option = optionIterator.next();
        while (roll > option.weight) {
            roll -= option.weight;
            option = optionIterator.next();
        }
        return option.option;
    }

    public T get(Random random, Predicate<T> filter) {
        WeightedList<T> filteredList = new WeightedList<>((WeightedOption[])options.stream().filter(o -> filter.test(o.option)).toArray());
        return filteredList.get(random);
    }
}
