// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.util;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

public final class ReflectionUtil {

  private ReflectionUtil() { }

  @NotNull
  public static Field findAssignableField(@NotNull Class<?> clazz, @Nullable("null means any type") final Class<?> fieldType, @NotNull @NonNls String fieldName) throws NoSuchFieldException {
    Field result = findFieldInHierarchy(clazz, field -> fieldName.equals(field.getName()) && (fieldType == null || fieldType.isAssignableFrom(field.getType())));
    if (result != null) {
      return result;
    }
    throw new NoSuchFieldException("Class: " + clazz + " fieldName: " + fieldName + " fieldType: " + fieldType);
  }

  public static @Nullable Field findFieldInHierarchy(@NotNull Class<?> rootClass,
                                                     @NotNull Predicate<? super Field> checker) {
    for (Class<?> aClass = rootClass; aClass != null; aClass = aClass.getSuperclass()) {
      for (Field field : aClass.getDeclaredFields()) {
        if (checker.test(field)) {
          field.setAccessible(true);
          return field;
        }
      }
    }
    return processInterfaces(rootClass.getInterfaces(), new HashSet<>(), checker);
  }

  @Nullable
  private static Field processInterfaces(Class<?> @NotNull [] interfaces,
                                         @NotNull Set<? super Class<?>> visited,
                                         @NotNull Predicate<? super Field> checker) {
    for (Class<?> anInterface : interfaces) {
      if (!visited.add(anInterface)) {
        continue;
      }

      for (Field field : anInterface.getDeclaredFields()) {
        if (checker.test(field)) {
          field.setAccessible(true);
          return field;
        }
      }

      Field field = processInterfaces(anInterface.getInterfaces(), visited, checker);
      if (field != null) {
        return field;
      }
    }
    return null;
  }

  public static <T> T getField(@NotNull Class<?> objectClass, @Nullable Object object, @Nullable("null means any type") Class<T> fieldType, @NotNull @NonNls String fieldName) {
    try {
      Field field = findAssignableField(objectClass, fieldType, fieldName);
      return getFieldValue(field, object);
    }
    catch (NoSuchFieldException e) {
      return null;
    }
  }


  public static <T> @Nullable T getFieldValue(@NotNull Field field, @Nullable Object object) {
    try {
      return (T)field.get(object);
    }
    catch (IllegalAccessException e) {
      return null;
    }
  }
}