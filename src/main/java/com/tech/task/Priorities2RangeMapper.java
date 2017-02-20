package com.tech.task;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tech.task.Utils.zip;
import static com.tech.task.Utils.zipTriple;

class Priorities2RangeMapper<T> implements Function<Map<BlockingQueue<T>, Triplet<Double, Long, TimeUnit>>, Map<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>>> {

    @Override
    public Map<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>> apply(Map<BlockingQueue<T>, Triplet<Double, Long, TimeUnit>> queuePriorityTimeoutTimeUnit) {
        final Double total = queuePriorityTimeoutTimeUnit.values().stream().map(Triplet::getValue0).reduce(0.0, (a, b) -> a + b);
        List<Pair<BlockingQueue<T>, Triplet<Double, Long, TimeUnit>>> queuesPrioritiesSorted = queuePriorityTimeoutTimeUnit.entrySet()
                .stream().sorted(Comparator.comparing(Map.Entry::getValue))
                .map(e -> new Pair<>(e.getKey(), new Triplet<>(e.getValue().getValue0() / total, e.getValue().getValue1(), e.getValue().getValue2())))
                .collect(Collectors.toList());

        Collection<Triplet<Range, Long, TimeUnit>> sortedRanges = extractRanges(queuesPrioritiesSorted);
        Collection<BlockingQueue<T>> sortedQueues = queuesPrioritiesSorted.stream().map(Pair::getValue0).collect(Collectors.toList());
        Collection<Pair<BlockingQueue<T>, Triplet<Range, Long, TimeUnit>>> zipped = zip(sortedQueues, sortedRanges);
        return zipped.stream().collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));
    }

    private Collection<Triplet<Range, Long, TimeUnit>> extractRanges(List<Pair<BlockingQueue<T>, Triplet<Double, Long, TimeUnit>>> queuesPrioritiesSorted) {
        List<Double> priorities = queuesPrioritiesSorted.stream().map(Pair::getValue1).map(Triplet::getValue0).collect(Collectors.toList());
        List<Long> timeouts = queuesPrioritiesSorted.stream().map(Pair::getValue1).map(Triplet::getValue1).collect(Collectors.toList());
        List<TimeUnit> timeUnits = queuesPrioritiesSorted.stream().map(Pair::getValue1).map(Triplet::getValue2).collect(Collectors.toList());

        List<Double> accumulatedPriorities = accumulatePriorities(priorities);
        List<Range> ranges = mapToRange(accumulatedPriorities);
        return zipTriple(ranges, timeouts, timeUnits);
    }

    private List<Double> accumulatePriorities(List<Double> sortedPriorities) {
        List<Double> scanned = new ArrayList<>();
        scanned.add(0.0);
        ListIterator<Double> doubleListIterator = sortedPriorities.listIterator();
        while (doubleListIterator.hasNext()) {
            if (!doubleListIterator.hasPrevious()) scanned.add(doubleListIterator.next());
            else scanned.add(scanned.get(scanned.size() - 1) + doubleListIterator.next());
        }
        return scanned;
    }

    private List<Range> mapToRange(List<Double> elements) {
        List<Range> result = new ArrayList<>();
        ListIterator<Double> fromIterator = elements.listIterator();
        ListIterator<Double> toIterator = elements.listIterator();
        toIterator.next();
        while (fromIterator.hasNext() && toIterator.hasNext()) {
            result.add(new Range(fromIterator.next(), toIterator.next()));
        }
        return result;
    }
}