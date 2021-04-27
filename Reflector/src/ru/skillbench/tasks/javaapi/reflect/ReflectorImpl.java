package ru.skillbench.tasks.javaapi.reflect;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectorImpl implements Reflector {

    private Class<?> clazz;

    /**
     * Задает класс, у которого нужно брать метаданные в других методах.
     *
     * @param clazz
     */
    @Override
    public void setClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Метод возвращает имена всех public методов, принимающих параметры заданных типов, -
     * для класса, заданного методом {@link #setClass(Class)}, в т.ч. его суперклассов.<br/>
     * Если метод какого-либо суперкласса переопределен методом класса, и имеет то же возвращаемое значение,
     * метод суперкласса возвращаться не должен.
     *
     * @param paramTypes Типы параметров, которые входят в сигнатуру искомых методов
     * @return Набор имен методов
     * @throws NullPointerException если класс равен null
     */
    @Override
    public Stream<String> getMethodNames(Class<?>... paramTypes) {
        if (clazz == null) {
            throw new NullPointerException();
        }

        List<String> result = new LinkedList<>();
        Method[] methods = clazz.getMethods();
        Class<?>[] methodParamTypes;
        boolean isParamTypesEqual;
        for (Method m : methods) {
            methodParamTypes = m.getParameterTypes();

            isParamTypesEqual = true;
            if (paramTypes.length == methodParamTypes.length) {
                for (int i = 0; i < paramTypes.length; ++i) {
                    if (!Objects.equals(paramTypes[i], methodParamTypes[i])) {
                        isParamTypesEqual = false;
                        break;
                    }
                }

                if (isParamTypesEqual) {
                    result.add(m.getName());
                }
            }
        }
        return result.stream();
    }
    /**
     * Метод возвращает набор всех не-static полей класса, заданного методом {@link #setClass(Class)},
     * в т.ч. полей его суперклассов.<br/>
     * Для выделения не-статических полей рекомендуется использовать метод {@link Stream#filter(Predicate)}
     * с lambda-выражением.<br/>
     * В отличие от {@link Class#getFields()}, возвращаемые поля могут иметь любые модификаторы доступа:
     * private, public, protected или default.
     *
     * @return Набор не-static полей для всей иерархии наследования данного класса.
     * @throws NullPointerException если класс равен null
     */
    @Override
    public Stream<Field> getAllDeclaredFields() {
        if (clazz == null) {
            throw new NullPointerException();
        }

        LinkedList<Field> fields = new LinkedList<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz != null) {
            Collections.addAll(fields, superClazz.getDeclaredFields());
            superClazz = superClazz.getSuperclass();
        }
        Field[] allFields = fields.toArray(new Field[0]);
        return Arrays.stream(allFields).filter(f -> (!Modifier.isStatic(f.getModifiers())));
    }

    /**
     * Возвращает значение поля заданного объекта без расчета на то, что у поля есть getter.<br/>
     * Поле может иметь любой идентификатор доступа.<br/>
     * Поле объявлено в классе, который задан методом {@link #setClass(Class), а если он не задан, -
     * в классе <code>target.getClass()</code>.<br/>
     * Примечание: объект <code>target</code> может быть экземпляром подкласса, и тогда
     * в <code>target.getClass()</code> не объявлено поле с заданным именем.
     *
     * @param target    Объект, где хранится значение поля
     * @param fieldName Имя поля
     * @return Значение поля
     * @throws NoSuchFieldException   если поля с указанным именем не существует
     * @throws IllegalAccessException если к полю нет доступа
     *                                (при правильно реализованном методе такого исключения возникать не должно)
     */
    @Override
    public Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> targetClazz = target.getClass();
        Field field = targetClazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * Метод создает экземпляр класса, заданного методом {@link #setClass(Class)},
     * с помощью public конструктора с параметром <code>constructorParameter</code>,
     * после чего вызвает его метод с <code>methodName</code>, в который передаются <code>methodParams</code>.<br/>
     * Метод может иметь любой модификатор доступа - необязательно public,
     * однако если метод не public, то объявлен именно в этом классе (а не в его суперклассе).
     *
     * @param constructorParam Передаваемый конструктору параметр или null, чтобы использовать
     *                         конструктор без параметров. Тип параметра конструктора равен <code>constructorParam.getClass()</code>
     * @param methodName       Название метода, который нужно вызвать.
     * @param methodParams     Массив параметров для вызова метода, ни один из которых не равен null;
     *                         предполагается, что сигнатура метода содержит типы элементов methodParams (а не их супертипы).
     * @return Результат, который возвращает метод. Может быть Void
     * @throws IllegalAccessException    если к конструктору или к методу нет доступа
     *                                   (при правильно реализованном {@link #getMethodResult(Object, String, Object...)} такого возникать не должно)
     * @throws InstantiationException    если класс не может быть инстанциирован (является абстрактным и т.п.)
     * @throws NoSuchMethodException     если в классе не существует подходящего конструктора или метода
     * @throws InvocationTargetException конструктор или метод при вызове выбросили проверяемое исключение
     * @throws RuntimeException          если конструктор или метод при вызове выбросили непроверяемое исключение;
     *                                   для этого в {@link #getMethodResult(Object, String, Object...)} необходимо обрабатывать InvocationTargetException
     */
    @Override
    public Object getMethodResult(Object constructorParam, String methodName, Object... methodParams) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object result = null;
        try {
            Constructor<?> constructor = (constructorParam != null)
                    ? clazz.getDeclaredConstructor(constructorParam.getClass())
                    : clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object obj = (constructorParam != null)
                    ? constructor.newInstance(constructorParam)
                    : constructor.newInstance();

            Class<?>[] paramTypes = new Class[methodParams.length];
            for (int i = 0; i < paramTypes.length; ++i) {
                paramTypes[i] = methodParams[i].getClass();
            }

            Method[] methods = obj.getClass().getMethods();
            boolean isPublicMethod = false;
            int i;
            for (i = 0; i < methods.length; ++i) {
                if ((Objects.equals(methods[i].getName(), methodName))
                        && (areParametersEqual(paramTypes, methods[i].getParameterTypes()))) {
                    isPublicMethod = true;
                    break;
                }
            }
            if (!isPublicMethod) {
                methods[i] = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            }
            methods[i].setAccessible(true);
            result = methods[i].invoke(obj, methodParams);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            else {
                throw e;
            }
        }
        return result;
    }

    private boolean areParametersEqual(Class<?>[] paramTypes, Class<?>[] methodParamTypes) {
        boolean isParamTypesEqual = false;
        if (paramTypes.length == methodParamTypes.length) {
            if (paramTypes.length == 0) {
                isParamTypesEqual = true;
            } else {
                for (int i = 0; i < paramTypes.length; ++i) {
                    if (Objects.equals(paramTypes[i], methodParamTypes[i])) {
                        isParamTypesEqual = true;
                    } else {
                        isParamTypesEqual = false;
                        break;
                    }
                }
            }
        }
        return isParamTypesEqual;
    }
}
