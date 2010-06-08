package com.omgrentbbq.server;

import com.google.appengine.api.datastore.*;
import com.omgrentbbq.shared.model.KeyProperty;
import com.omgrentbbq.shared.model.Memento;
import com.omgrentbbq.shared.model.Pair;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static <T extends Memento> void update(final T t) {

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        final Entity entity = $(t);
        Key key = ds.put(entity);
        t.$$(key.getName() == null ? key.getId() : key.getName());
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
        } finally {
        }
        return hashMap;
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
            key = KeyFactory.createKey(m.getType(), (long) aLong);
        } else {
            key = KeyFactory.createKey(m.getType(), (String) serializable);
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
        Entity entity;
        if (null != t.$$())

            entity = new Entity($k(t));
        else
            entity = new Entity(t.getClass().getName());
        for (String nkey : t.$.keySet()) {
            Serializable serializable = t.$(nkey);
            if (serializable instanceof Object[]) {
                entity.setProperty(nkey, Arrays.asList(serializable));
            } else if (serializable instanceof Enum) {
                Enum anEnum = (Enum) serializable;
                entity.setProperty(nkey, anEnum.name());

            } else if (serializable instanceof Memento) {
                Memento memento = (Memento) serializable;
                entity.setProperty(nkey, $k(memento));

            } else {
                entity.setProperty(nkey, serializable);
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
        Map<String, PropertyDescriptor> descriptors = new LinkedHashMap<String, PropertyDescriptor>();
        try {
            Class<T> aClass = null;
            if (optionalClass.length > 0) {
                aClass = optionalClass[0];
                PropertyDescriptor[] de = PropertyUtils.getPropertyDescriptors(aClass);
                for (PropertyDescriptor descriptor : de) {
                    descriptors.put(descriptor.getName(), descriptor);
                }
            } else {
                try {
                    aClass = (Class<T>) Class.forName(Memento.class.getPackage().getName() + '.' + entity.getKey().getKind());
                } catch (Exception e) {
                    aClass = (Class<T>) Memento.class;
                }
            }
            t = aClass.getConstructor().newInstance();


            KeyProperty keyProperty = aClass.getAnnotation(KeyProperty.class);

            Map<String, Object> map = entity.getProperties();
            for (String k : map.keySet()) {
                Object val = map.get(k);
                if (!(val instanceof Class)) {
                    if (k.equals(keyProperty)) {
                        t.$$((Serializable) val);
                    } else {
                        if (val instanceof Memento) {
                            Memento memento = (Memento) val;
                            t.$(k, $k(memento));
                        } else if (val instanceof Key) {
                            Key key = (Key) val;

                            try {
                                Entity e = DatastoreServiceFactory.getDatastoreService().get(key);
                                t.$(k, $(e, (Class<? extends Memento>) Class.forName(key.getKind())));
                            } catch (Exception e) {
                                t.$(k, key);
                            }
                        } else if (val instanceof List && descriptors.containsKey(k) && descriptors.get(k).getPropertyType().isArray()) {
                            List list = (List) val;
                            final PropertyDescriptor propertyDescriptor = descriptors.get(k);
                            final Class<?> c = propertyDescriptor.getPropertyType();
                            final Class<? extends Serializable> type = (Class<? extends Serializable>) c.getComponentType();
                            t.$(k, listToArray(list, type));
                        } else {
                            t.$(k, (Serializable) val);
                        }
                    }

                }
            }
            if (null != keyProperty) {
                t.$.remove(keyProperty.value());
            }
        } catch (Exception ignored) {
            final Throwable throwable = ignored.fillInStackTrace();
        }
        Key key = entity.getKey();
        if (null == t.$$()) {
            t.$$(key.getName() == null ? key.getId() : key.getName());
        }
        return t;
    }

     public static <T extends Memento, X extends Serializable> Object[] listToArray(List list, Class<X> type) {
        X[] theArray = (X[]) Array.newInstance(type, list.size());

        if (!Number.class.isAssignableFrom(type)) {
            for (int i = 0; i < list.toArray().length; i++) {
                theArray[i] = (X) list.get(i);
            }
        } else {
            for (int i = 0; i < list.toArray().length; i++) {

                final Number n = (Number) list.get(i);
                Array.set(theArray, i,
                        Long.class == type ? n.longValue()
                                : Integer.class == type ? n.intValue()
                                : Short.class == type ? n.shortValue()
                                : Byte.class == type ? n.byteValue()
                                : Double.class == type ? n.doubleValue()
                                : Float.class == type ? n.floatValue()
                                : n);
            }

        }
        return theArray;
    }

    public static void embed(Pair<String, ? extends Memento> src, Memento into) {
        final String as = src.getFirst();
        final Memento from = src.getSecond();
        for (String k : from.$.keySet()) {
            into.$(as + "." + k, from.$(k));
        }
        from.$$(new Pair(as, into));
    }

}
