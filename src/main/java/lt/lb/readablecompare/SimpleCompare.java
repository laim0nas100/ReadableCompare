package lt.lb.readablecompare;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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
    public <U extends T> U max(U o1, U o2) {
        return compare(o1, CompareOperator.GREATER_EQ, o2) ? o1 : o2;
    }

    /**
     *
     * @param o1
     * @param o2
     * @return lower value by this comparator
     */
    public <U extends T> U min(U o1, U o2) {
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
    public <U extends T> U clamp(U min, U val, U max) {
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
    public <U extends T> boolean inside(Bound bound, U min, U val, U max) {
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
    public <U extends T> boolean outside(Bound bound, U min, U val, U max) {
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
    public SimpleCompare<T> reversed() {
        return new SimpleCompare<>(CompareNull.reverseNullOrder(nullCmp), cmp.reversed());
    }

    @Override
    public SimpleCompare<T> thenComparing(Comparator<? super T> other) {
        return new SimpleCompare<>(nullCmp, new SimpleCompare<>(nullCmp, cmp.thenComparing(other)));
    }

    public SimpleCompare<T> thenComparing(CompareNull nullCmp, Comparator<? super T> other) {
        return new SimpleCompare<>(this.nullCmp, new SimpleCompare<>(nullCmp, cmp.thenComparing(other)));
    }

    @Override
    public <U> SimpleCompare<T> thenComparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return thenComparing(nullCmp, keyExtractor, keyComparator);
    }

    public <U> SimpleCompare<T> thenComparing(CompareNull nullCmp, Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        Objects.requireNonNull(nullCmp);
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(keyComparator);
        return thenComparing((a, b) -> {
            return Compare.cmpAny(keyExtractor.apply(a), keyExtractor.apply(b), keyComparator, nullCmp);
        });
    }

    @Override
    public <U extends Comparable<? super U>> SimpleCompare<T> thenComparing(Function<? super T, ? extends U> keyExtractor) {
        return thenComparing(nullCmp, keyExtractor, Comparator.naturalOrder());
    }

    public <U extends Comparable<? super U>> SimpleCompare<T> thenComparing(CompareNull nullCmp, Function<? super T, ? extends U> keyExtractor) {
        return thenComparing(nullCmp, keyExtractor, Comparator.naturalOrder());
    }

    public <U> SimpleCompare<T> thenComparingOptional(CompareNull nullCmp, Function<? super T, Optional<? extends U>> keyExtractor, Comparator<? super U> keyComparator) {
        Objects.requireNonNull(nullCmp);
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(keyComparator);
        return thenComparing(nullCmp,(a, b) -> {
            return Compare.cmpAny(
                    keyExtractor.apply(a).orElse(null),
                    keyExtractor.apply(b).orElse(null),
                    keyComparator,
                    nullCmp
            );
        });

    }

    public <U extends Comparable<? super U>> SimpleCompare<T> thenComparingOptional(CompareNull nullCmp, Function<? super T, Optional<? extends U>> keyExtractor) {
        return thenComparingOptional(nullCmp, keyExtractor, Comparator.naturalOrder());
    }

    public <U extends Comparable<? super U>> SimpleCompare<T> thenComparingOptional(Function<? super T, Optional<? extends U>> keyExtractor) {
        return thenComparingOptional(nullCmp, keyExtractor, Comparator.naturalOrder());
    }

    @Override
    public SimpleCompare<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return thenComparing((a, b) -> {
            return Integer.compare(keyExtractor.applyAsInt(a), keyExtractor.applyAsInt(b));
        });
    }

    @Override
    public SimpleCompare<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return thenComparing((a, b) -> {
            return Long.compare(keyExtractor.applyAsLong(a), keyExtractor.applyAsLong(b));
        });
    }

    @Override
    public SimpleCompare<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return thenComparing((a, b) -> {
            return Double.compare(keyExtractor.applyAsDouble(a), keyExtractor.applyAsDouble(b));
        });
    }

}
