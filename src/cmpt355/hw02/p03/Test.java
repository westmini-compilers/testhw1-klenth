/*
 * Tester program for lexer grammar of HW2, problem 3.
 */

package cmpt355.hw02.p03;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Test {

    // If you change the name of the lexer, update it here so that this code can find it.
    public static final String RECOGNIZER_CLASS = "cmpt355.hw02.p03.HW02P03";


    private static StringBuilder buffer = new StringBuilder(100);
    private static Map<Integer, String> tokenNames = new HashMap<>();

    public static final String INPUT_FILE = "input.txt";

    /* Using reflection, grab a list of all public static final int fields declared in the lexer class to get names of
       token types */
    static {
        try {
            var lexerClass = Class.forName(RECOGNIZER_CLASS);
            for (var field : lexerClass.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                // Assume it's a token type field if it's public, static, final, an int, and name starts with uppercase
                if (Modifier.isPublic(modifiers)
                        && Modifier.isStatic(modifiers)
                        && Modifier.isFinal(modifiers)
                        && Integer.TYPE.equals(field.getType())
                        && Character.isUpperCase(field.getName().charAt(0))) {
                    String name = field.getName();
                    int value = field.getInt(null);
                    tokenNames.put(value, name);
                }
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Could not find lexer class!");
        } catch (IllegalAccessException ex) {
            System.err.println("Unable to fetch value of static field");
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws IOException {
        try {
            Class<? extends Lexer> lexerClass = (Class<? extends Lexer>)Class.forName(RECOGNIZER_CLASS);
            Lexer lexer = newRecognizer(lexerClass, CharStreams.fromFileName(INPUT_FILE));
            lexer.getAllTokens().forEach(Test::printToken);
        } catch (ClassNotFoundException ex) {
            System.err.printf("""
                    Unable to find recognizer class %s. Have you...
                      • Generated the recognizer? (IDEA: Right-click .g4 file, Generate ANTLR Recognizer)
                      • Compiled it?\n""", RECOGNIZER_CLASS);
        } catch (InvocationTargetException ex) {
            System.err.printf("Unable to instantiate recognizer class %s because the constructor threw an exception:\n",
                    RECOGNIZER_CLASS);
            ex.getTargetException().printStackTrace();
        } catch (ReflectiveOperationException ex) {
            System.err.println("""
                    Unable to instantiate the recognizer class. Exception:""");
            ex.printStackTrace();
        }
    }

    private static <R extends Recognizer<?, ?>> R newRecognizer(Class<R> recognizerClass, Object... arguments)
                throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var argTypes = new Class<?>[arguments.length];
        var constructor = findConstructorFor(recognizerClass, argTypes);
        return (R)constructor.newInstance(arguments);
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findConstructorFor(Class<T> clazz, Object[] arguments) throws NoSuchMethodException {
        for (var constructor : clazz.getDeclaredConstructors()) {
            if (typesMatch(constructor.getParameterTypes(), arguments))
                return (Constructor<T>)constructor;
        }
        throw new NoSuchMethodException();
    }

    private static boolean typesMatch(Class<?>[] types, Object[] values) {
        if (types.length != values.length)
            return false;
        for (int i = 0; i < types.length; ++i) {
            if (values[i] == null && types[i].isPrimitive())
                return false;
            else if (values[i] != null && !types[i].isAssignableFrom(values[i].getClass()))
                return false;
        }
        return true;
    }

    private static void printToken(Token token) {
        String tokenName = tokenNames.getOrDefault(token.getType(), "" + token.getType());
        System.out.printf("%20s {%s}\n", tokenName, escape(token.getText()));
    }

    private synchronized static String escape(String s) {
        buffer.setLength(0);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            buffer.append(switch (c) {
                case '\n' -> "\\n";
                case '\r' -> "\\r";
                case '\t' -> "\\t";
                case '\\' -> "\\\\";
                default -> "" + c;
            });
        }

        return buffer.toString();
    }
}
