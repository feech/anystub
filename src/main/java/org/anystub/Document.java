package org.anystub;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * Document for keeping requests/response
 * Created by Kirill on 9/2/2016.
 */
public class Document {

    private static Logger logger = Logger.getLogger(Document.class.getName());
    private final List<String> keys = new ArrayList<>();
    private final List<String> exception = new ArrayList<>();
    private final List<String> values = new ArrayList<>();
    private boolean nullValue = false;

    public Document() {
        // just explicit declaration. to be consistent
    }

    public Document(String... keys) {
        stream(keys)
                .forEach(this.keys::add);

    }

    public Document(Throwable ex, String... keys) {
        stream(keys)
                .forEach(this.keys::add);

        this.exception.add(ex.getClass().getCanonicalName());
        this.exception.add(ex.getMessage());
    }

    public Document(Map<String, Object> document) {
        this.assign(document);
    }

    /**
     * for internal use
     *
     * @return description of Exception
     */
    public List<String> getException() {
        return exception;
    }

    /**
     * for internal use
     *
     * @param exception exception's description. the description consist of
     *                  one or two elements, the 1st is type of the exception,
     *                  the 2nd is message of the exception
     */
    public void setException(List<String> exception) {
        this.exception.clear();
        this.exception.addAll(exception);
    }

    public Document setValues(String... values) {
        nullValue = false;
        this.values.clear();
        stream(values)
                .forEach(this.values::add);

        return this;
    }

    public Document setValues(Iterable<String> values) {
        nullValue = false;
        this.values.clear();
        values
                .forEach(this.values::add);

        return this;
    }

    public Document setNull() {
        nullValue = true;
        this.values.clear();
        return this;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public String get() {
        return getVals().next();
    }

    public <E extends Throwable> Iterator<String> getVals() throws E {
        if (exception.isEmpty()) {
            if (nullValue) {
                return null;
            }
            return values.iterator();
        }

        try {
            Class<?> aClass = Class.forName(exception.get(0));

            Constructor<?> constructor = aClass.getConstructor(String.class);
            Object o = constructor.newInstance(exception.get(1));
            throw (E) o;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            String msg = String.format("exception class %s not found, RuntimeException is thrown", exception.get(0));
            logger.warning(() -> msg);
            throw new RuntimeException(exception.get(1), e);
        }

    }

    public boolean keyEqual_to(String... keys) {
        return this.keys.equals(asList(keys));
    }

    /**
     * exact matching the document to given key.
     * the document is not matched to 'keys' if:
     * - it has less values in key then argument array
     * - it has at least one value in its key that's not equal to correspondent non-null value in argument array
     * * null values in argument array aren't used for matching
     *
     * @param keys keys for matching (null values are skipped from matching but length of key is compared)
     * @return true if document is matched
     */
    public boolean match_to(String... keys) {
        if (this.keys.size() < keys.length) {
            return false;
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                continue;
            }
            if (!keys[i].equals(this.keys.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * match documents using keys
     * matching uses regexp
     *
     * @param keys keys for matching
     * @return true id document is matched
     */
    public boolean matchEx_to(String... keys) {
        if (keys == null || keys.length == 0) {
            return true;
        }
        if (this.keys.size() < keys.length) {
            return false;
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                continue;
            }
            if (!Pattern.matches(keys[i], this.keys.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * match documents using keys and values
     * matching uses regexp
     *
     * @param keys   values for matching by keys
     * @param values values for matching by values
     * @return true if document is matched
     */
    public boolean matchEx_to(String[] keys, String[] values) {
        if (this.nullValue) {
            return values == null;
        }

        if (this.values.size() < values.length) {
            return false;
        }
        if (!matchEx_to(keys)) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }
            if (!Pattern.matches(values[i], this.values.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void assert_to(String... keys) {
        if (!match_to(keys)) {
            fail(keys);
        }
    }
    public void assertEx_to(String... keys) {
        if (!matchEx_to(keys)) {
            fail(keys);
        }
    }
    public void assertEx_to(String[] keys, String[] values) {
        if (!matchEx_to(keys,values)) {
            if(!matchEx_to(keys)){
                fail(keys);
            }else {
                fail_value(values);
            }
        }
    }

    private void fail(String... keys) {
        String msg;

        if (this.keys.size() < keys.length) {
            msg = "number values in key expected: "
                    + keys.length + " and more"
                    + " but was: "
                    + this.keys.size();
        } else {
            msg = "expected: "
                    + key_to_string(keys)
                    + " but was: "
                    + key_to_string();
        }
        throw new AssertionError(msg);
    }

    private void fail_value(String... values) {
        String msg;

        if (this.keys.size() < values.length) {
            msg = "number values in value expected: "
                    + values.length + " and more"
                    + " but was: "
                    + this.keys.size();
        } else {
            msg = "expected value: "
                    + key_to_string(values)
                    + " but was: "
                    + key_to_string();
        }
        throw new AssertionError(msg);
    }

    public String key_to_string() {
        if (nullValue) {
            return "null";
        }
        return key_to_string(keys.toArray(new String[0]));
    }

    public static String key_to_string(String... keys) {
        if (keys == null) {
            return "null";
        } else if (keys.length == 0) {
            return "[]";
        } else if (keys.length == 1) {
            return keys[0];
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(stream(keys).collect(Collectors.joining(", ")));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        Document that = (Document) other;

        if (keys.isEmpty() && that.keys.isEmpty()) {
            return true;
        }
        return keys.equals(that.keys);
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    /**
     * return an array of strings
     *
     * @param keys values of array
     * @return array of string
     */
    public static String[] ars(String... keys) {
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        return keys;
    }

    /**
     * return array of string
     *
     * @param keys values for result string, strings are copied into result array, Integers are transformed to
     *             correspondent amount of null values
     * @return array of string
     * @throws UnsupportedDataTypeException occurs if there is an key with type rather then String or Integer
     */
    public static String[] aro(Object... keys) throws UnsupportedDataTypeException {
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<>();
        for (Object o : keys) {
            if (o instanceof String) {
                result.add((String) o);
            } else if (o instanceof Integer) {
                result.addAll(asList(new String[(Integer) o]));
            } else {
                throw new UnsupportedDataTypeException();
            }
        }
        return result.toArray(new String[0]);
    }


    public Map<String, Object> toMap() {
        TreeMap<String, Object> res = new TreeMap<>();
        if (keys.size() == 1) {
            res.put("keys", keys.get(0));
        } else {
            res.put("keys", keys);
        }

        if (nullValue) {
            res.put("values", null);
        } else if (values.size() == 1 && values.get(0) != null) {
            res.put("values", values.get(0));
        } else {
            res.put("values", values);
        }

        res.put("exception", exception);

        return res;
    }


    private void assign(Map<String, Object> document) {
        if (document.get("keys") instanceof Iterable) {
            ((Iterable) document.get("keys"))
                    .forEach(x -> this.keys.add((String) x));
        } else {
            this.keys.add((String) document.get("keys"));
        }

        if (document.get("values") == null) {
            this.setNull();
        } else if (document.get("values") instanceof Iterable) {
            ((Iterable) document.get("values"))
                    .forEach(x -> this.values.add((String) x));
        } else {
            this.values.add((String) document.get("values"));
        }

        if (document.get("exception") instanceof Iterable) {
            ((Iterable) document.get("exception"))
                    .forEach(x -> this.exception.add((String) x));
        } else {
            this.exception.add((String) document.get("exception"));
        }

    }


}
