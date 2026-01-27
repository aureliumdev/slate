package dev.aurelium.slate.ref;

/**
 * A wrapper class for platform-dependent item instances
 */
public interface ItemRef {

    Object get();

    ItemRef clone();

}
