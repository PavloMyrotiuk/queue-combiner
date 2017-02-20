package com.tech.task;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

class Utils {

    static <A, B> Collection<Pair<A, B>> zip(Collection<A> la, Collection<B> lb) {
        Collection<Pair<A, B>> result = new ArrayList<>();

        Iterator<A> laIterator = la.iterator();
        Iterator<B> lbIterator = lb.iterator();

        while (laIterator.hasNext() && lbIterator.hasNext()) {
            result.add(new Pair<>(laIterator.next(), lbIterator.next()));
        }
        return result;
    }

    static <A, B, C> Collection<Triplet<A, B, C>> zipTriple(Collection<A> la, Collection<B> lb, Collection<C> lc) {
        Collection<Triplet<A, B, C>> result = new ArrayList<>();

        Iterator<A> laIterator = la.iterator();
        Iterator<B> lbIterator = lb.iterator();
        Iterator<C> lcIterator = lc.iterator();

        while (laIterator.hasNext() && lbIterator.hasNext() && lcIterator.hasNext()) {
            result.add(new Triplet(laIterator.next(), lbIterator.next(), lcIterator.next()));
        }
        return result;
    }
}
