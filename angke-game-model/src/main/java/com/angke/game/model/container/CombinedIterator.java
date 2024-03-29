package com.angke.game.model.container;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E> implements Iterator<E> {

	private final Iterator<E> i1;
//    private final Iterator<E> i2;
    private Iterator<E> currentIterator;

    CombinedIterator(Iterator<E> i1) {
        if (i1 == null) {
            throw new NullPointerException("i1");
        }
//        if (i2 == null) {
//            throw new NullPointerException("i2");
//        }
        this.i1 = i1;
//        this.i2 = i2;
        currentIterator = i1;
    }

    @Override
    public boolean hasNext() {
        for (;;) {
            if (currentIterator.hasNext()) {
                return true;
            }

            if (currentIterator == i1) {
//                currentIterator = i2;
            } else {
                return false;
            }
        }
    }

    @Override
    public E next() {
        for (;;) {
            try {
                return currentIterator.next();
            } catch (NoSuchElementException e) {
//                if (currentIterator == i1) {
//                    currentIterator = i2;
//                } else {
//                    throw e;
//                }
				throw e;
            }
        }
    }

    @Override
    public void remove() {
        currentIterator.remove();
    }
}
