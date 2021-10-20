package lt.lb.readablecompare;

/**
 *
 * @author laim0nas100
 */
public enum Bound {
    /**
     * Including min and max
     */
    INC_INC,
    /**
     * Including min excluding max
     */
    INC_EXC,
    /**
     * Excluding min including max
     */
    EXC_INC,
    /**
     * Excluding min and max
     */
    EXC_EXC;

}
