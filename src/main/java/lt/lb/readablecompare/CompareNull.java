package lt.lb.readablecompare;

import java.util.Objects;

/**
 * How to behave when encountering a null element in a comparison operation.
 *
 * @author laim0nas100
 */
public enum CompareNull {

    /**
     * Null element is treated as lower of the two
     */
    NULL_LOWER,
    /**
     * Null element is treated as higher of the two
     */
    NULL_HIGHER,
    /**
     * Null element is treated as equal to the other
     */
    NULL_EQUAL,
    /**
     * Null elements throw {@link NullPointerException}
     */
    NULL_THROW;

    /**
     * If possible, reverses null order. Only works with {@link CompareNull#NULL_LOWER) or {@link CompareNull#NULL_HIGHER}.
     *
     * @param cmp
     * @return
     */
    public static CompareNull reverseNullOrder(CompareNull cmp) {
        Objects.requireNonNull(cmp);
        switch (cmp) {
            case NULL_LOWER:
                return NULL_HIGHER;
            case NULL_HIGHER:
                return NULL_LOWER;
            default:
                return cmp;
        }
    }

}
