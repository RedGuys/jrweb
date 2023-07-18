package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;

public class Session {
    private HashMap<Class<? extends SessionData>, SessionData> data = new HashMap<>();
    public @Nullable Instant deleteAt;

    public Session(@Nullable Instant deleteAt) {
        this.deleteAt = deleteAt;
    }

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
}
