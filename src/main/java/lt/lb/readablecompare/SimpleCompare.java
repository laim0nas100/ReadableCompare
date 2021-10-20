package lt.lb.readablecompare;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class SimpleCompare<T> implements Comparator<T> {

    public static final SimpleCompare<Comparable<?>> SIMPLE_COMPARE_NULL_THROW = new SimpleCompare(CompareNull.NULL_THROW, Comparator.naturalOrder());

    public static final SimpleCompare<Comparable<?>> SIMPLE_COMPARE_NULL_LOWER = new SimpleCompare(CompareNull.NULL_LOWER, Comparator.naturalOrder());

    public static final SimpleCompare<Comparable<?>> SIMPLE_COMPARE_NULL_HIGHER = new SimpleCompare(CompareNull.NULL_HIGHER, Comparator.naturalOrder());

    public static final SimpleCompare<Comparable<?>> SIMPLE_COMPARE_NULL_EQUAL = new SimpleCompare(CompareNull.NULL_EQUAL, Comparator.naturalOrder());

    public final CompareNull nullCmp;
    public final Comparator<T> cmp;

    protected SimpleCompare(CompareNull nullCmp, Comparator<T> cmp) {
        this.nullCmp = Objects.requireNonNull(nullCmp, "CompareNull is null");
        this.cmp = Objects.requireNonNull(cmp, "Comparator is null");
    }

    /**
     * Apply compare operation with {@link CompareOperator} to given elements.
     *
     * @param elem1
     * @param op
     * @param elem2
     * @return
     */
    public boolean compare(T elem1, CompareOperator op, T elem2) {
        return Compare.compare(elem1, op, elem2, cmp, nullCmp);
    }

    @Override
    public int compare(T o1, T o2) {
        return Compare.cmpAny(o1, o2, cmp, nullCmp);
    }

    /**
     *
     * @param o1
     * @param o2
     * @return higher value by this comparator
     */
    public T max(T o1, T o2) {
        return compare(o1, CompareOperator.GREATER_EQ, o2) ? o1 : o2;
    }

    /**
     *
     * @param o1
     * @param o2
     * @return lower value by this comparator
     */
    public T min(T o1, T o2) {
        return compare(o1, CompareOperator.LESS_EQ, o2) ? o1 : o2;
    }

    /**
     *
     * @param min
     * @param max
     * @param val
     * @return either the value if it is in provided range, otherwise return the
     * bound which was exceeded.
     */
    public T clamp(T min, T val, T max) {
        return max(min(max, val), min);
    }

    /**
     *
     * @param bound
     * @param min
     * @param val
     * @param max
     * @return true if value is inside given bounds
     */
    public boolean inside(Bound bound, T min, T val, T max) {
        Objects.requireNonNull(bound, "Bound is null");
        switch (bound) {
            case INC_INC:
                return compare(min, CompareOperator.LESS_EQ, val) && compare(val, CompareOperator.LESS_EQ, max);
            case INC_EXC:
                return compare(min, CompareOperator.LESS_EQ, val) && compare(val, CompareOperator.LESS, max);
            case EXC_INC:
                return compare(min, CompareOperator.LESS, val) && compare(val, CompareOperator.LESS_EQ, max);
            case EXC_EXC:
                return compare(min, CompareOperator.LESS, val) && compare(val, CompareOperator.LESS, max);
            default: {
                throw new IllegalArgumentException("Unsupported Bound:" + bound);
            }
        }
    }

    /**
     *
     * @param bound
     * @param min
     * @param val
     * @param max
     * @return true if value is outside given bounds
     */
    public boolean outside(Bound bound, T min, T val, T max) {
        Objects.requireNonNull(bound, "Bound is null");
        switch (bound) {
            case INC_INC:
                return compare(min, CompareOperator.GREATER, val) || compare(val, CompareOperator.GREATER, max);
            case INC_EXC:
                return compare(min, CompareOperator.GREATER, val) || compare(val, CompareOperator.GREATER_EQ, max);
            case EXC_INC:
                return compare(min, CompareOperator.GREATER_EQ, val) || compare(val, CompareOperator.GREATER, max);
            case EXC_EXC:
                return compare(min, CompareOperator.GREATER_EQ, val) || compare(val, CompareOperator.GREATER_EQ, max);
            default: {
                throw new IllegalArgumentException("Unsupported Bound:" + bound);
            }
        }
    }

    @Override
    public Comparator<T> reversed() {
        return new SimpleCompare<>(CompareNull.reverseNullOrder(nullCmp), cmp.reversed());
    }

    @Override
    public Comparator<T> thenComparing(Comparator<? super T> other) {
        return new SimpleCompare<>(nullCmp, cmp.thenComparing(other));
    }

    public SimpleCompare<T> thenComparing(CompareNull nullCmp, Comparator<? super T> other) {
        return new SimpleCompare<>(nullCmp, cmp.thenComparing(other));
    }

}
