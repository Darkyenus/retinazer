/*******************************************************************************
 * Copyright (C) 2015 Anton Gustafsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.github.antag99.retinazer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

final class WireCache {
    private final Engine engine;
    private final Field[] fields;
    private final WireResolver[] wireResolvers;

    public WireCache(Engine engine, Class<?> type, WireResolver[] wireResolvers) {
        final List<Field> fields = new ArrayList<>();

        for (Class<?> current = type; current != Object.class; current = current.getSuperclass()){
            for (Field field : ClassReflection.getDeclaredFields(current)) {
                field.setAccessible(true);

                if (field.getDeclaredAnnotation(Wire.class) == null) {
                    continue;
                }


                if (field.isStatic()){
                    throw new RetinazerException("Static fields can not be wired ("+current.getName()+"#"+field.getName()+")");
                }

                if (field.isSynthetic()) {
                    continue;
                }

                fields.add(field);
            }
        }

        this.engine = engine;
        this.fields = fields.toArray(new Field[fields.size()]);
        this.wireResolvers = wireResolvers;
    }

    public void wire(Object object) {
        final WireResolver[] wireResolvers = this.wireResolvers;

        fields:
        for (final Field field : fields) {
            for (WireResolver wireResolver : wireResolvers) {
                try {
                    if (wireResolver.wire(engine, object, field)) {
                        continue fields;
                    }
                } catch (Throwable ex) {
                    throw Internal.sneakyThrow(ex);
                }
            }

            throw new RetinazerException("Failed to wire field " +
                    field.getName() + " of " +
                    field.getDeclaringClass().getName() + " of type "+field.getType().getName()+"; no resolver");
        }
    }

}
