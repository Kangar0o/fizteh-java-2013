package ru.fizteh.fivt.students.vishnevskiy.binder;

import ru.fizteh.fivt.binder.*;

import java.util.Stack;
import java.lang.reflect.Field;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Handler extends DefaultHandler {
    int depth;
    private Class<?> clazz;
    private Stack<Object> objects;
    private Stack<Field> fields;
    private Object object;

    public Handler(Class<?> clazz) {
        depth = 0;
        this.clazz = clazz;
        objects = new Stack<Object>();
        fields = new Stack<Field>();
    }

    public Object getObject() {
        return this.object;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        depth++;

        Object currObject = objects.peek();
        Class<?> currClass = currObject.getClass();
        if (depth % 2 != 0) {  // if class

            try {
                if (depth == 1) {   // if top class
                    objects.push(this.clazz.newInstance());
                } else {
                    objects.push(fields.peek().getType().newInstance());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid object");
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Invalid object");
            }

        } else {            // if field

            Field currField = null;
            for (Field field : currClass.getDeclaredFields()) {
                if (field.getAnnotation(DoNotBind.class) != null) {
                    continue;
                }
                String currFieldName = null;
                Name name = field.getAnnotation(Name.class);
                if (name != null) {
                    currFieldName = name.value();
                } else {
                    currFieldName = field.getName();
                }
                if (qName.equals(currFieldName)) {
                    currField = field;
                }
            }
            if (currField == null) {
                throw new IllegalArgumentException("Field not found");
            }

            currField.setAccessible(true);
            fields.push(currField);
            try {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    if (attributes.getQName(i).equals("value")) {
                        if (attributes.getValue(i).equals("empty")) {
                            currField.set(currObject, "");
                        } else if (attributes.getValue(i).equals("null")) {
                            currField.set(currObject, null);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid object");
            }

        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        depth--;
        if (depth % 2 == 0) {     // if class
            if (depth == 0) {     // if top class
                this.object = objects.pop();
            } else {
                Field currField = fields.peek();
                currField.setAccessible(true);
                try {
                    currField.set(objects.peek(), object);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid object");
                }
            }
        } else {                 // if field
            fields.pop();
        }
    }

    public void characters(char chars[], int start, int length) throws SAXException {
        try {

            Class<?> currentType = fields.peek().getType();
            Field currField = fields.peek();
            Object currObject = objects.peek();
            currField.setAccessible(true);
            String value = new String(chars, start, length);

            if (currentType.equals(char.class)) {
                currField.set(currObject, chars[start]);
            } else if (currentType.equals(String.class)) {
                currField.set(currObject, value);
            } else if (currentType.equals(int.class)) {
                currField.set(currObject, Integer.parseInt(value));
            } else if (currentType.equals(long.class)) {
                currField.set(currObject, Long.parseLong(value));
            } else if (currentType.equals(byte.class)) {
                currField.set(currObject, Short.parseShort(value));
            } else if (currentType.equals(float.class)) {
                currField.set(currObject, Float.parseFloat(value));
            } else if (currentType.equals(double.class)) {
                currField.set(currObject, Double.parseDouble(value));
            } else if (currentType.equals(boolean.class)) {
                currField.set(currObject, Boolean.parseBoolean(value));
            } else if (currentType.isEnum()) {
                Object enumConstant = null;
                for (Object object : currentType.getEnumConstants()) {
                    if (value.equals(object.toString())) {
                        enumConstant = object;
                    }
                }
                if (enumConstant == null) {
                    throw new IllegalArgumentException("Enum not found");
                }
                currField.set(currObject, enumConstant);
            }

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid object");
        }
    }
}