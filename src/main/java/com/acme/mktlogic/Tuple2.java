package com.acme.mktlogic;

import java.util.StringJoiner;

/*
 * Scala tuple2 implementation
 **/
public class Tuple2<A, B> {

    public final A _1;
    public final B _2;

    public Tuple2(A a, B b) {
        this._1 = a;
        this._2 = b;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("_1=" + _1).add("_2=" + _2)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple2<?, ?> tuple = (Tuple2<?, ?>) o;
        if (!_1.equals(tuple._1)) {
            return false;
        }
        return _2.equals(tuple._2);
    }

    @Override
    public int hashCode() {
        return 31 * _1.hashCode() + _2.hashCode();
    }

}
