/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.models.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.IReflectivePropertySetter;
import org.eclipse.epsilon.eol.models.java.JavaModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.introspection.java.JavaFieldPropertyGetter;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.introspection.java.JavaFieldPropertySetter;

/**
 * This implementation improves on {@link JavaModel} capabilities, using reflection to simplify loading the model and
 * adding support to set properties using private field access instead of relying on explicit setter methods.
 *
 * It also adds support for inner (nested) classes, which should be specified as Enclosing#Nested.
 *
 * @author Flavio Costa - Initial contribution
 */
public class ReflectiveJavaModel extends JavaModel {

    /**
     * Flattens a Collection, taking all elements of nested (sub-)collections and adding it to a single collection.
     *
     * @param <T> Element type.
     */
    private static final class FlatSetCollector<T> implements Collector<T, Set<T>, Set<T>> {

        @Override
        public Supplier<Set<T>> supplier() {
            return () -> new HashSet<>();
        }

        @SuppressWarnings("unchecked")
        // element type checking can only be done at runtime
        @Override
        public BiConsumer<Set<T>, T> accumulator() {
            return (s, o) -> {
                if (o instanceof Collection) {
                    ((Collection<T>) o).forEach(sub -> accumulator().accept(s, sub));
                } else {
                    s.add(o);
                }
            };
        }

        @Override
        public BinaryOperator<Set<T>> combiner() {
            return (s1, s2) -> {
                s1.addAll(s2);
                return s1;
            };
        }

        @Override
        public Function<Set<T>, Set<T>> finisher() {
            return (s) -> s;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.UNORDERED);
        }
    }

    /**
     * Predicate used to determine whether a certain class would be considered as belonging to the object model.
     *
     * @return Boolean value according to the predicate implementation.
     */
    public static Predicate<Class<?>> isClassInModel() {
        // predicate implementation
        return c -> !(c.isPrimitive() || c.isArray() || c.getPackage() == null
                || c.getPackage().getName().startsWith("java.") || c.getPackage().getName().startsWith("javax."));
    }

    /**
     * Predicate used to determine whether a certain object would be considered as belonging to the object model.
     *
     * @return Boolean value according to the predicate implementation.
     */
    public static Predicate<Object> isObjectInModel() {
        // predicate implementation
        return o -> o == null ? false : isClassInModel().test(o.getClass());
    }

    /**
     * Creates a collector that flattens the input collection.
     *
     * @return Collector instance.
     */
    private static <T> Collector<T, ?, Set<T>> toFlatSet() {
        return new FlatSetCollector<T>();
    }

    /**
     * Predicate used to determine whether a certain method would be a property getter that does not take any
     * parameters.
     *
     * @return Boolean value according to the predicate implementation.
     */
    public static Predicate<Method> isSimpleGetter() {
        // predicate implementation
        return m -> m.getParameterCount() == 0 && (m.getName().startsWith("get") || m.getName().startsWith("is"));
    }

    /**
     * Returns the value for a field. This utility method may be used in lambda expressions, without throwing any
     * checked exceptions.
     *
     * @param o Object instance.
     * @param f Field reference.
     * @return Returned value.
     */
    public static Object getFieldValue(Object o, Field f) {
        try {
            return f.get(o);
        } catch (IllegalAccessException e) {
            // else the function cannot be used in a lambda expression
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes a method to obtain its return value. This utility method may be used in lambda expressions, without
     * throwing any checked
     * exceptions.
     *
     * @param o Object instance.
     * @param m Method reference.
     * @return Returned value, or null if the target method is not accessible.
     */
    public static Object getMethodReturnValue(Object o, Method m) {
        if (Modifier.isPublic(m.getModifiers()) && !m.isAccessible()) {
            // it is public, so even if noit accessible (e.g. method in a private inner class)
            // let's check its return value
            m.setAccessible(true);
        }
        try {
            return m.invoke(o);
        } catch (InvocationTargetException | IllegalAccessException e) {
            // else the function cannot be used in a lambda expression
            throw new RuntimeException(e);
        }
    }

    /**
     * Recursively obtains all objects referenced by a given object instance.
     *
     * @param current Current object to be inspected.
     * @param resultSet List of collected objects (passed recursively to method self-invocations).
     * @return Collection of all referenced objects.
     */
    private static Collection<Object> listObjectTree(Object current, Set<Object> resultSet) {

        if (current instanceof Iterable) {
            // if the parameter is Iterable (e.g. a Colection),
            // then process the elements recursively instead
            ((Iterable<?>) current).forEach(o -> listObjectTree(o, resultSet));
        } else {
            // not Iterable, then process the object fields and methods recursively
            resultSet.add(current);

            Class<?> c = current.getClass();

            Stream<Object> fieldObjects = Arrays.stream(c.getFields()).map(f -> getFieldValue(current, f));
            Stream<Object> returnObjects = Arrays.stream(c.getMethods()).filter(isSimpleGetter())
                    .map(m -> getMethodReturnValue(current, m));

            // concatenate all member objects and produce one single Set,
            // processing recursively as needed with filters to ignore
            // standard Java classes and objects already in the result set
            Set<Object> members = Stream.of(fieldObjects, returnObjects).flatMap(o -> o).collect(toFlatSet());
            Set<Object> filteredMembers = members.stream().filter(isObjectInModel()).filter(o -> !resultSet.contains(o))
                    .collect(Collectors.toSet());
            if (!filteredMembers.isEmpty()) {
                filteredMembers.forEach(o -> listObjectTree(o, resultSet));
            }
        }
        return resultSet;
    }

    /**
     * Recursively obtains all classes referenced by a given Collection of class instances.
     *
     * @param classes All classes to be inspected.
     * @param resultSet List of collected classes (passed recursively to method self-invocations).
     * @return Collection of all referenced classes.
     */
    private static Collection<? extends Class<?>> listClassTree(Collection<? extends Class<?>> classes,
            Set<Class<?>> resultSet) {

        for (Class<?> c : classes) {
            resultSet.add(c);

            Stream<Class<?>> fieldTypes = Arrays.stream(c.getFields()).map(Field::getType);
            Stream<Class<?>> returnTypes = Arrays.stream(c.getMethods()).map(Method::getReturnType);
            Stream<Class<?>> nestedClassTypes = Arrays.stream(c.getClasses());
            Stream<Class<?>> interfaceTypes = Arrays.stream(c.getInterfaces());

            // concatenate all member types and produce one single Set, skipping any duplicates
            Set<Class<?>> memberTypes = Stream.of(fieldTypes, returnTypes, nestedClassTypes, interfaceTypes)
                    .flatMap(t -> t).filter(isClassInModel()).filter(mc -> !resultSet.contains(mc))
                    .collect(Collectors.toSet());

            // process recursively if any relevant members were found
            if (!memberTypes.isEmpty()) {
                listClassTree(memberTypes, resultSet);
            }
        }

        return resultSet;
    }

    /**
     * Retrieves a Collection of Class references for the given object, or the objects contained in it if the argument
     * is a {@link Collection}.
     *
     * @param obj Object to get the class(es) for.
     * @return Resulting classes.
     */
    private static Collection<? extends Class<?>> getClassesForObject(Object obj) {

        if (obj instanceof Collection) {
            // for collections, return the classes for each contained object
            return ((Collection<?>) obj).stream().map(ReflectiveJavaModel::getClassesForObject)
                    .flatMap(Collection::stream).collect(Collectors.toSet());
        } else {
            // class of the object itself as it is not a Collection
            return Collections.singleton(obj.getClass());
        }
    }

    /**
     * Constructor that accepts an object instance. This is useful to create source model instances to be used on
     * transformations.
     *
     * @param name Model name.
     * @param rootObject Instance of the root object of the model. The objects it contains will be obtained through Java
     *            reflection.
     */
    public ReflectiveJavaModel(String name, Object rootObject) {
        super(name, listObjectTree(rootObject, new HashSet<Object>()),
                listClassTree(getClassesForObject(rootObject), new HashSet<Class<?>>()));
        // makes sure the classes for all returned objects are in the model
        objects.stream().map(Object::getClass).filter(o -> !classes.contains(o)).forEach(classes::add);
    }

    /**
     * Constructor that accepts a collection of classes. This is useful to create target model instances to be used on
     * transformations.
     *
     * @param name Model name.
     * @param classes Classes to be added to the model. It may be just the class of the root element of the model, or
     *            more class references if they are not all discoverable from the root element's class.
     */
    public ReflectiveJavaModel(String name, Collection<? extends Class<?>> classes) {
        super(name, new HashSet<Object>(), listClassTree(classes, new HashSet<Class<?>>()));
    }

    /**
     * Constructor that accepts a collection of objects and classes. This is useful for source models, where objects are
     * loaded but there may be other classes besides the ones that match the collection of provided objects.
     *
     * @param name Model name.
     * @param objects Collection of objects in the model.
     * @param classes Classes to be added to the model. It may be just the class of the provided objects, or
     *            more class references if they are not all discoverable from the objects' class.
     */
    public ReflectiveJavaModel(String name, Collection<? extends Object> objects,
            Collection<? extends Class<?>> classes) {
        super(name, objects, classes);
    }

    /**
     * Returns the Java classes included in this model.
     *
     * @return Unmodifiable collection with all model classes.
     */
    public Collection<Class<?>> allClasses() {
        return Collections.unmodifiableCollection(this.classes);
    }

    @Override
    public IReflectivePropertySetter getPropertySetter() {
        return new JavaFieldPropertySetter();
    }

    @Override
    public IPropertyGetter getPropertyGetter() {
        return new JavaFieldPropertyGetter();
    }

    @Override
    public Class<?> classForName(String name) {
        for (Class<?> c : classes) {
            // the first condition gets "Enclosing#Nested" from "my.package.Enclosing$Nested"
            if (c.getName().substring(c.getName().lastIndexOf('.') + 1).replace('$', '#').equals(name)
                    || c.getCanonicalName().replaceAll("::", ".").equals(name)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public Object getEnumerationValue(String enumeration, String label) throws EolEnumerationValueNotFoundException {
        try {
            return classForName(enumeration).getMethod("valueOf", String.class).invoke(null, label);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new EolEnumerationValueNotFoundException(enumeration, label, this.name);
        }
    }

    @Override
    public String toString() {
        return String.format("ReflectiveJavaModel [name=%s]", getName());
    }
}
