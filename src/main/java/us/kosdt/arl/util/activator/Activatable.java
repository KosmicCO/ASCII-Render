package us.kosdt.arl.util.activator;

/**
 * Interface for classes which have an activated and deactivated state.
 */
public interface Activatable {

    /**
     * Puts object into active mode. Repeated uses of this method in a row should not do anything after the first call.
     */
    void activate();

    /**
     * Puts object into deactive mode. Repeated uses of this method in a row should not do anything after the first
     * call.
     */
    void deactivate();
}
