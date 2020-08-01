package us.kosdt.arl.util.activator;

/**
 * Interface that allows classes to have a universal ID. Note that if a class, A, implements this interface, then all of
 * its subclasses should have ids that do not clash with those given to any class which is also a subclass of A. Because
 * of this behavior, other interfaces should not extend this one.
 */
public interface Identifiable {

    /**
     * Gets the unique ID given to this object. Should be unique among the set of all objects extending the highest
     * common superclass that implements Identifiable.
     *
     * @return This object's unique ID.
     */
    long getID();
}
