/*
 * Plumbing.java
 *
 * Copyright (c) 2006 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the library it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" plus the "Classpath Exception" (you may link to this code as a
 * library into other programs provided you don't make a derivation of it).
 * See the LICENCE file for the terms governing usage and redistribution.
 */
package org.freedesktop.bindings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parent of all classes in the translation layer of a bindings library. This
 * class provides the infrastructure (or, "plumbing") that the generated code
 * can access the native values held within Proxy, Constant, etc.
 * 
 * <p>
 * <i><b>No one using developing applications which happen to use bindings
 * based on this package should ever need to see, use, or subclass, this.</b>
 * People hacking on the bindings themselves will end up call generated or
 * crafted methods in the translation layer, but they too will never have to
 * use these mechanisms directly. Note that individual library families will
 * likely subclass this to extend its instance lookup behaviour in a manner
 * appropriate to the type systems in use within those libraries.</i>
 * 
 * @author Andrew Cowie
 * @since 4.0.0
 */
public abstract class Plumbing
{
    protected Plumbing() {}

    /**
     * Every Proxy that gets created gets added to this Map so that if a call
     * down to the native layer returns an object that has already been
     * created Java side that instance is returned rather than creating a new
     * one.
     */
    static final HashMap knownProxies;

    /**
     * When Enums get created, we add them to this Map so we can find an
     * appropriate instance for a given ordinal. The table is two tier:
     * 
     * Class => ArrayList[Constant+]
     */
    /*
     * Neither keys nor values are to be weak references here; we are not
     * unloading Constant classes so the Class keys will stay strongly
     * reachable; the Constant instances themselves will always be present by
     * virtue of their being in this two tier table.
     */
    static final HashMap knownConstants;

    static {
        /*
         * TODO: any particular reason to pick a starting array size?
         * 
         * FIXME! Early on we used WeakHashMap, but that is weak on _keys_
         * only, and in fact we definitely do _not_ want that. We need to
         * switch to weak _values_; we're going to need to wrap and unwrap
         * WeakReference around the Proxies we put as values to achieve that.
         */
        knownProxies = new HashMap();
        knownConstants = new HashMap();
    }

    /*
     * Proxy handling ------------------------------------
     */

    /**
     * When a Proxy is created, it must call this method so that other methods
     * that need that to return that particular Proxy (but, coming from the
     * native side, only know the pointer address) can do so by doing a
     * lookup.
     */
    static final void registerProxy(Proxy obj) {
        knownProxies.put(new Long(obj.pointer), obj);
    }

    /**
     * Called by the release() function of a Proxy object, this method cleans
     * up any entries of the Proxy in the Plumbings internals.
     */
    static final void unregisterProxy(Proxy obj) {
        knownProxies.remove(new Long(obj.pointer));
    }

    /**
     * Get the memory address which is the location of the Object or Structure
     * that a given Proxy represents. That doesn't mean anything on the Java
     * side so don't try to interpret it - it's for use by the translation
     * layer as they marshall objects through to the native layer.
     * 
     * @return opaque data to be passed to native methods only.
     */
    /*
     * We go to considerable effort to keep this method out of the visibility
     * of plublic users which is why translation layer code subclass this
     * org.freedesktop.bindings.Pluming which has package visibility of Proxy
     * and Constant. Even more, there's nothing we can do about this being
     * protected, so we choose a method name other than getPointer() to keep
     * it totally of out of view from get<COMPLETE>.
     */
    protected static final long pointerOf(Proxy reference) {
        return reference.pointer;
    }

    /**
     * Given a pointer, find out if we already have a Proxy for it Java side.
     * 
     * <p>
     * This will return the Proxy instance already created by a concrete
     * constructor if one was created Java side. This is sufficient if you are
     * only using this for Proxys that were created as a result of objects
     * being constructed in Java. If, on the other hand, you are calling this
     * from a native to Java code path, then you need to account for the fact
     * that it is likely that a returned pointer will not yet have a Proxy
     * here. Override this method with your own <code>instanceFor()</code>
     * implementation; call this method to find out if there is a Proxy
     * already; then if not take appropriate action to create (and in so
     * doing, register) a new Proxy object.
     * 
     * <p>
     * Note that under this architecture, denaturation should <b>not</b>
     * occur because if we created the type, then we will already and always
     * have a reference to it. Regardless if our type is a much derived
     * subclass of whatever the native library's equivalent is, any look up of
     * that pointer will be routed to our Proxy subtype.
     * 
     * <p>
     * <i><b>This must be overridden by any library using these bindings, or
     * you will only be able to get instances for objects created Java side.</b></i>.
     * 
     * @param pointer
     *            opaque memory reference as passed from the C side.
     * @return the instance corresponding to a given poiner, or null if that
     *         pointer isn't registered.
     */
    /*
     * This is a skeleton implementation with the necessary functionality of
     * looking up existing Proxys but nevertheless this is, in effect, an
     * "abstract" method; if you are using java-gnome to wrap a non GLib based
     * library, you will need to implement an instanceFor() that knows how to
     * create appropriate Proxy instances based on something that can be
     * looked up.
     */
    protected static Proxy instanceFor(long pointer) {
        return (Proxy) knownProxies.get(new Long(pointer));
    }

    /*
     * Constant handling ----------------------------------
     */

    /**
     * When a Constant (ie, our Enum wrapper) is created, this must be called
     * to ensure the appropriate constant object can be retrieved on request
     * when all that is known is type and ordinal.
     * 
     * <p>
     * Note that there is no need for an <code>unregisterConstant()</code>;
     * once loaded Constant objects are expected to be around for the lifetime
     * of the VM.
     */
    /*
     * TODO It would be cool if we had a way of sizing the array perfectly on
     * allocation. Better yet, if we knew the correct number of instances, we
     * could use an Java array [object] instead of an ArrayList.
     */
    static final void registerConstant(Constant obj) {
        final Class type;
        ArrayList list;

        type = obj.getClass();

        list = (ArrayList) knownConstants.get(type);

        if (list == null) {
            list = new ArrayList(4);
            knownConstants.put(type, list);
        }

        // for some reason I don't understand, set() doesn't work
        list.add(obj.ordinal, obj);
    }

    /**
     * Get the ordinal number corresponding to the enum that a given Constant
     * represents. That value is deliberately obscured on the Java side
     * because by itself it doesn't mean anything without an enclosing
     * Constant class and the machinery to handle it. This method is for use
     * by the translation layer only as it marshalls objects through to the
     * native layer.
     * 
     * @return opaque data to be passed to native methods only
     */
    /*
     * Like pointerOf(), this is carefully shielded from hackers writing the
     * public API layer of the bindings.
     */
    protected static final int numOf(Constant reference) {
        return reference.ordinal;
    }

    /**
     * Given a Class and an ordinal number, lookup the Constant object that
     * corresponds to that native enum.
     * 
     * @throws IllegalArgumentException
     *             if it can't find a Constant object corresponding to the
     *             Class, ordinal combination you've requested.
     */
    protected static Constant constantFor(Class type, int ordinal) {
        final ArrayList list;
        final Constant obj;

        list = (ArrayList) knownConstants.get(type);
        if (list == null) {
            throw new IllegalArgumentException("No Constants of type " + type.getName()
                    + " are registered");
        }

        obj = (Constant) list.get(ordinal);
        if (obj == null) {
            throw new IllegalArgumentException("You asked for ordinal " + ordinal
                    + " which is not known for the requested Constant type " + type.getName());
        }

        return obj;
    }
}
