package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;

public class Session {
    private HashMap<Class<? extends SessionData>, SessionData> data = new HashMap<>();
    public @Nullable Instant deleteAt;

    /**
     * Session default constructor
     * @param deleteAt Instant when delete this session, null for never
     */
    public Session(@Nullable Instant deleteAt) {
        this.deleteAt = deleteAt;
    }

    /**
     * Gets data from this session
     * @param type Class who extends {@link SessionData} with default zero params constructor
     * @return instance of type
     */
    public <T extends SessionData> T get(Class<T> type) {
        if(data.containsKey(type)) {
            return type.cast(data.get(type));
        } else {
            try {
                Constructor<?>[] constructors = type.getDeclaredConstructors();

                // Проходим по всем конструкторам и ищем конструктор без аргументов
                Constructor<?> defaultConstructor = null;
                for (Constructor<?> constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        defaultConstructor = constructor;
                        break;
                    }
                }
                defaultConstructor.setAccessible(true);
                T t = (T) defaultConstructor.newInstance();
                data.put(type, t);
                return t;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Deletes SessionData from session storage
     * @param type Class who extends {@link SessionData} with default zero params constructor
     */
    public <T extends SessionData> void delete(Class<T> type) {
        data.remove(type);
    }
}
