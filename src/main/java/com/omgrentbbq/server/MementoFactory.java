package com.omgrentbbq.server;

import com.google.appengine.api.datastore.*;
import com.omgrentbbq.shared.model.KeyProperty;
import com.omgrentbbq.shared.model.Memento;
import org.apache.commons.beanutils.BeanMap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 10:17:53 PM
 */
public class MementoFactory {
    private static final String[] ACCESSORPREFIXES = new String[]{
            "get", "is"
    };
//          TransformerPPP n;

    public static <T extends Memento> void update(final T t) {

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Key key = ds.put($(t));
        assert (key.getName() == null ? key.getId() : key.getName()).equals(t.$$());

    }

    /**
     * creates a BeanMap and then dumbs it down for a GWT copmatible map
     *
     * @param proto any java object
     * @param <T>   the object's type
     * @return
     */
    public static <T> Map<String, Serializable> createMap(T proto) {

        LinkedHashMap<String, Serializable> hashMap = new LinkedHashMap<String, Serializable>();
        try {
            Map map = new BeanMap(proto);

            for (Object k : map.keySet()) {
                try {
                    Object o1 = map.get(k);
                    if (o1 instanceof Serializable && !(o1 instanceof Class)) {
                        hashMap.put(k.toString(), (Serializable) o1);
                    }
                } catch (Exception ignored) {

                }
            }
        } catch (Throwable e) {
            for (Method method : proto.getClass().getMethods()) {
                final String k = method.getName();
                for (String accessor : ACCESSORPREFIXES) {
                    if (k.startsWith(accessor)) {
                        try {
                            inner(proto, hashMap, method, k, accessor);
                        } catch (Throwable ignored) {
                            final String message = ignored.getMessage();
                        }
                        finally {
                            break;
                        }
                    }
                }


            }
//            return hashMap;
        }
        return hashMap;
    }

    private static <T> void inner(Object proto, LinkedHashMap<String, Serializable> hashMap, Method method, String k, String accessor) throws IllegalAccessException, InvocationTargetException {
        if (Serializable.class.isAssignableFrom(method.getReturnType())) {

            Object o = method.invoke(proto);
            if (o instanceof Serializable) {
                Serializable serializable = (Serializable) o;

                final String key = MessageFormat.format("{0}{1}", Character.toLowerCase(k.charAt(accessor.length())), k.substring(1 + accessor.length()));
                hashMap.put(key, serializable);
            }
        }
    }

    /**
     * this creates a new Entity in the ds and returns a memento of it overloaded to use newInstance
     *
     * @param proto
     * @param aClass
     * @param <T>
     * @param <P>
     * @return
     */
    public static <T extends Memento, P> T writeMemento(P proto, Class<T> aClass) {

        Map<String, Serializable> map = createMap(proto);
        Entity entity;

        String keyname = null;
        Key key = null;
        if (aClass.isAnnotationPresent(KeyProperty.class)) {
            keyname = aClass.getAnnotation(KeyProperty.class).value();
            Serializable serializable = map.get(keyname);
            key = $k(aClass, serializable);

            entity = new Entity(key);
        } else {
            entity = new Entity(aClass.getName());
        }

        for (String k : map.keySet()) {
            try {
                if (!k.equals(keyname)) {
                    entity.setProperty(k, map.get(k));
                }
            } catch (Exception ignored) {
            }
        }

        Memento memento = $(entity, aClass);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key key1 = ds.put(entity);
        if (!key1.equals(key)) {
            memento.$$(key1.getName() == null ? key1.getId() : key1.getName());
        }
        return (T) memento;
    }

    public static <T extends Memento> Key $k(T m) {
        Serializable serializable = m.$$();
        Key key;
        if (serializable instanceof Long) {
            long aLong = (Long) serializable;
            key = KeyFactory.createKey(m.getClass().getName(), (long) aLong);
        } else {
            key = KeyFactory.createKey(m.getClass().getName(), String.valueOf(serializable));
        }
        return key;
    }

    public static <T extends Memento> Key $k(Class<T> aClass, Serializable serializable) {
        Key key = null;
        try {
            if (serializable instanceof Long) {
                long aLong = (Long) serializable;
                key = KeyFactory.createKey(aClass.getName(), (long) aLong);
            } else {
                key = KeyFactory.createKey(aClass.getName(), String.valueOf(serializable));
            }
        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return key;
    }

    private static <T extends Memento> Entity $(T t) {
        Entity entity = new Entity($k(t));
        for (String s : t.$.keySet()) {
            Serializable serializable = t.$.get(s);
            if (serializable instanceof Memento) {
                Memento memento = (Memento) serializable;
                entity.setProperty(s, $k(memento));

            } else {
                entity.setProperty(s, serializable);
            }

        }
        return entity;
    }

    /**
     * this creates a memento object from an existing entity instance.
     *
     * @param entity
     * @param optionalClass
     * @param <T>           the expected object type
     * @return
     */
    public static <T extends Memento> T $(Entity entity, Class<T>... optionalClass) {
        T t = null;
        try {
            Class<T> aClass = null;
            if (optionalClass.length > 0) {
                aClass = optionalClass[0];
            } else {
                try {
                    aClass = (Class<T>) Class.forName(entity.getKey().getKind());
                } catch (Exception e) {
                    aClass = (Class<T>) Memento.class;
                }
            }
            t = aClass.getConstructor().newInstance();
            KeyProperty keyProperty = aClass.getAnnotation(KeyProperty.class);

            Map<String, Object> map = entity.getProperties();
            for (String k : map.keySet()) {
                Object o = map.get(k);
                if (!(o instanceof Class)) {
                    if (k.equals(keyProperty)) {
                        t.$$((Serializable) o);
                    } else {
                        if (o instanceof Memento) {
                            Memento memento = (Memento) o;
                            t.$.put(k, $k(memento));

                        } else if (o instanceof Key) {
                            Key key = (Key) o;

                            try {
                                Entity e = DatastoreServiceFactory.getDatastoreService().get(key);
                                t.$.put(k, $(e, (Class<? extends Memento>) Class.forName(key.getKind())));
                            } catch (Exception e) {
                                t.$.put(k, key);
                            }
                        } else {
                            t.$.put(k, (Serializable) o);
                        }
                    }

                }
            }
            if (null != keyProperty) {
                t.$.remove(keyProperty.value());
            }
        } catch (Exception ignored) {
        }
        Key key = entity.getKey();
        if (null == t.$$()) {
            t.$$(key.getName() == null ? key.getId() : key.getName());
        }
        return t;
    }
}
