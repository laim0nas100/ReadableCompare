package lt.lb.readablecompare;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public abstract class Compare {

    /**
     * Compare 2 optionally-null {@link Comparable} elements using given
     * {@link CompareNull} operator treating {@code null} as lower of the two.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> boolean compareNullLower(T elem1, CompareOperator cmpOp, T elem2) {
        return compare(elem1, cmpOp, elem2, Comparator.naturalOrder(), CompareNull.NULL_LOWER);
    }

    /**
     * Compare 2 optionally-null {@link Comparable} elements using given
     * {@link CompareNull} operator treating {@code null} as higher of the two.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> boolean compareNullHigher(T elem1, CompareOperator cmpOp, T elem2) {
        return compare(elem1, cmpOp, elem2, Comparator.naturalOrder(), CompareNull.NULL_HIGHER);
    }

    /**
     * Compare 2 optionally-null {@link Comparable} elements using given
     * {@link CompareNull} operator treating {@code null} as equal to the other.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> boolean compareNullEqual(T elem1, CompareOperator cmpOp, T elem2) {
        return compare(elem1, cmpOp, elem2, Comparator.naturalOrder(), CompareNull.NULL_EQUAL);
    }

    /**
     * Traditional (-1,0,1) compare 2 optionally-null {@link Comparable}
     * elements. Treats {@code null} as lower of the two. Useful for quick
     * method reference as lambda.
     *
     * @param <T>
     * @param elem1
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> int cmpNullLower(T elem1, T elem2) {
        return cmpAny(elem1, elem2, Comparator.naturalOrder(), CompareNull.NULL_LOWER);
    }

    /**
     * Traditional (-1,0,1) compare 2 optionally-null {@link Comparable}
     * elements. Treats {@code null} as higher of the two. Useful for quick
     * method reference as lambda.
     *
     * @param <T>
     * @param elem1
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> int cmpNullHigher(T elem1, T elem2) {
        return cmpAny(elem1, elem2, Comparator.naturalOrder(), CompareNull.NULL_HIGHER);
    }

    /**
     * Traditional (-1,0,1) compare 2 optionally-null {@link Comparable}
     * elements. Treats {@code null} as equal to the other. Useful for quick
     * method reference as lambda.
     *
     * @param <T>
     * @param elem1
     * @param elem2
     * @return
     */
    public static <T extends Comparable<T>> int cmpNullEqual(T elem1, T elem2) {
        return cmpAny(elem1, elem2, Comparator.naturalOrder(), CompareNull.NULL_EQUAL);
    }

    /**
     * Compare 2 optionally-null operators based on given {@link CompareNull}
     * and {@link CompareNull} then map result according to
     * {@link CompareOperator}.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @param cmp
     * @param nullOp
     * @return
     */
    public static <T> boolean compare(T elem1, CompareOperator cmpOp, T elem2, Comparator<T> cmp, CompareNull nullOp) {
        Objects.requireNonNull(cmpOp);
        Objects.requireNonNull(nullOp);
        Objects.requireNonNull(cmp);
        int cmpRes = cmpAny(elem1, elem2, cmp, nullOp);
        return cmpResultSwitch(cmpRes, cmpOp);

    }

    /**
     * Map traditional (-1,0,1) result to given {@link CompareOperator}.
     *
     * @param cmpRes
     * @param cmpOp
     * @return
     */
    public static boolean cmpResultSwitch(int cmpRes, CompareOperator cmpOp) {
        Objects.requireNonNull(cmpOp);
        switch (cmpOp) {
            case LESS:
                return cmpRes < 0;
            case LESS_EQ:
                return cmpRes <= 0;
            case GREATER:
                return cmpRes > 0;
            case GREATER_EQ:
                return cmpRes >= 0;
            case EQ:
                return cmpRes == 0;
            case NOT_EQ:
                return cmpRes != 0;
            default:
                throw new IllegalArgumentException("Unrecognized CompareOperator:" + cmpOp);
        }
    }

    /**
     * Compare 2 optionally-null operators based on given {@link CompareNull}
     * and map result according to {@link CompareOperator}.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @param nullOp
     * @return
     */
    public static <T> boolean compareNulls(T elem1, CompareOperator cmpOp, T elem2, CompareNull nullOp) {
        Objects.requireNonNull(cmpOp);
        return cmpResultSwitch(cmpNulls(elem1, elem2, nullOp), cmpOp);

    }

    /**
     * Traditional (-1,0,1) compare 2 optionally-null operators based on given
     * {@link Comparator} and {@link CompareNull}.
     *
     * @param <T>
     * @param elem1
     * @param elem2
     * @param cmp
     * @param cmpNull
     * @return
     */
    public static <T> int cmpAny(T elem1, T elem2, Comparator<T> cmp, CompareNull cmpNull) {
        Objects.requireNonNull(cmpNull);
        Objects.requireNonNull(cmp);

        if (elem1 == null || elem2 == null) {
            return cmpNulls(elem1, elem2, cmpNull);
        } else {
            return cmp.compare(elem1, elem2);
        }
    }

    /**
     * Traditional (-1,0,1) compare 2 optionally-null elements based on
     * {@link CompareNull} operator. At least one of them must be null.
     *
     * @param <T>
     * @param elem1
     * @param elem2
     * @param nullOp
     * @return
     */
    public static <T> int cmpNulls(T elem1, T elem2, CompareNull nullOp) {
        Objects.requireNonNull(nullOp);
        boolean firstNull = elem1 == null;
        boolean secondNull = elem2 == null;

        if (!firstNull && !secondNull) {
            throw new IllegalArgumentException("One of the elements must be null");
        }

        switch (nullOp) {
            case NULL_THROW: {
                Objects.requireNonNull(elem1, "Recieved null first element");
                Objects.requireNonNull(elem2, "Recieved null second element");
                break;
            }
            case NULL_LOWER: {
                if (firstNull && secondNull) {
                    return 0;
                }
                return firstNull ? -1 : 1;
            }
            case NULL_HIGHER: {
                if (firstNull && secondNull) {
                    return 0;
                }
                return secondNull ? -1 : 1;
            }
            case NULL_EQUAL: {
                return 0;
            }
            default:
                throw new IllegalArgumentException("Unsupported ComapareNull:" + nullOp);
        }
        return 0;//dead code
    }

    /**
     * Traditional (-1,0,1) compare 2 non-null elements using
     * {@link CompareOperator} and {@link Comparator}.
     *
     * @param <T>
     * @param elem1
     * @param cmpOp
     * @param elem2
     * @param cmp
     * @return
     */
    public static <T> boolean compareNotNulls(T elem1, CompareOperator cmpOp, T elem2, Comparator<T> cmp) {
        Objects.requireNonNull(cmp, "Comparator is null");
        Objects.requireNonNull(cmpOp, "CompareOperator is null");
        Objects.requireNonNull(elem1, "Element 1 is null");
        Objects.requireNonNull(elem2, "Element 2 is null");
        int compare = cmp.compare(elem1, elem2);
        return cmpResultSwitch(compare, cmpOp);
    }

    public static <T> SimpleCompare<T> of(CompareNull nullCmp, Comparator<T> cmp) {
        return new SimpleCompare<>(nullCmp, cmp);
    }

    /**
     * Create instance of {@link SimpleCompare} using provided
     * {@link Comparator} and {@link CompareNull#NULL_THROW} rule.
     *
     * @param <T>
     * @param cmp
     * @return
     */
    public static <T> SimpleCompare<T> of(Comparator<T> cmp) {
        return new SimpleCompare<>(CompareNull.NULL_THROW, cmp);
    }

    /**
     * Create instance of {@link SimpleCompare} using natural order and provided
     * {@link  CompareNull} rule.
     *
     * @param <T>
     * @param nullCmp
     * @return
     */
    public static <T extends Comparable<? super T>> SimpleCompare<T> of(CompareNull nullCmp) {
        Objects.requireNonNull(nullCmp, "CompareNull is null");
        switch (nullCmp) {
            case NULL_THROW:
                return (SimpleCompare<T>) SimpleCompare.SIMPLE_COMPARE_NULL_THROW;
            case NULL_LOWER:
                return (SimpleCompare<T>) SimpleCompare.SIMPLE_COMPARE_NULL_LOWER;
            case NULL_HIGHER:
                return (SimpleCompare<T>) SimpleCompare.SIMPLE_COMPARE_NULL_HIGHER;
            case NULL_EQUAL:
                return (SimpleCompare<T>) SimpleCompare.SIMPLE_COMPARE_NULL_EQUAL;
            default: {
                throw new IllegalArgumentException("Unsupported CompareNull type:" + nullCmp);
            }
        }
    }
}
