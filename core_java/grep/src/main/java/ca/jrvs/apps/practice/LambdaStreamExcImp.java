package ca.jrvs.apps.practice;

//import sun.lwawt.macosx.CInputMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamExcImp implements LambdaStreamExc {


    /**
     * Create a String stream from array
     *
     * note: arbitrary number of value will be stored in an array
     *
     * @param strings
     * @return
     */
    @Override
    // String... means you can pass any number of string arguments
    public Stream<String> createStrStream(String... strings) {
        Stream<String> stream;
        stream = Arrays.stream(strings);
        return stream;
    }

    /**
     * Convert all strings to uppercase
     * please use createStrStream
     *
     * @param strings
     * @return
     */
    @Override
    public Stream<String> toUpperCase(String... strings) {
        // creates a stream with the list of arrays
        Stream<String> strs =  createStrStream(strings);

        // returns a stream by applying a reference
        return strs.map(String::toUpperCase);
    }

    /**
     * filter strings that contains the pattern
     * e.g.
     * filter(stringStream, "a") will return another stream which no element contains a
     *
     *
     * @param stringStream
     * @param pattern
     * @return
     */

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        return stringStream.filter(str -> !str.contains(pattern));
    }

    /**
     * Create a intStream from a arr[]
     * @param arr
     * @return
     */
    @Override
    public IntStream createIntStream(int[] arr) {
        IntStream stream = Arrays.stream(arr);
        return stream;
        // return Arrays.stream(arr) will give us an IntStream automatically
    }

    /**
     * Convert a stream to list
     *
     * @param stream
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> toList(Stream<E> stream) {
        // this takes a stream, the collect method triggers the processing
        // The collect method in Java's Stream API is a terminal operation that transforms the elements of a stream into
        // a different form, typically into a collection like a List, Set, or Map, or into a single object by applying
        // a reduction operation
        return stream.collect(Collectors.toList());
    }

    /**
     * Convert a intStream to list
     * @param intStream
     * @return
     */

    @Override
    public List<Integer> toList(IntStream intStream) {
        // The boxed() method converts each primitive int in the IntStream to an instance of Integer. This is necessary
        // because List<Integer> cannot hold primitive int types, but only objects. The boxed() method returns a Stream<Integer>.
        // in Lists, we put in primitive ints, and List automatically converts it into Integar objects
        return intStream.boxed().collect(Collectors.toList());
    }

    /**
     * Create a IntStream range from start to end inclusive
     * @param start
     * @param end
     * @return
     */
    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.range(start, end);
    }

    /**
     * Convert a intStream to a doubleStream
     * and compute square root of each element
     * @param intStream
     * @return
     */
    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        // converts intStream that holds primitive ints rather than Integar objects like Stream<Integar>
        // into a DoubleStream which is basically a stream that holds primitive doubles
        return intStream.asDoubleStream().map(Math::sqrt);
    }

    /**
     * filter all even number and return odd numbers from a intStream
     * @param intStream
     * @return
     */
    @Override
    public IntStream getOdd(IntStream intStream) {
        return intStream.filter(n -> n % 2 != 0);
    }

    /**
     * Return a lambda function that print a message with a prefix and suffix
     * This lambda can be useful to format logs
     *
     * You will learn:
     *   - functional interface http://bit.ly/2pTXRwM & http://bit.ly/33onFig
     *   - lambda syntax
     *
     * e.g.
     * LambdaStreamExc lse = new LambdaStreamImp();
     * Consumer<String> printer = lse.getLambdaPrinter("start>", "<end");
     * printer.accept("Message body");
     *
     * sout:
     * start>Message body<end
     *
     * Consumer is a functional interface, it only has one method that we can use
     * Here we are returning a Consumer object that prints
     * @param prefix prefix str
     * @param suffix suffix str
     * @return
     */
    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {

        return str -> System.out.println(prefix + str + suffix);
    }

    /**
     * Print each message with a given printer
     * Please use `getLambdaPrinter` method
     *
     * e.g.
     * String[] messages = {"a","b", "c"};
     * lse.printMessages(messages, lse.getLambdaPrinter("msg:", "!") );
     *
     * sout:
     * msg:a!
     * msg:b!
     * msg:c!
     *
     * @param messages
     * @param printer
     */
    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
        for (String message : messages) {
            printer.accept(message);
        }
    }

    /**
     * Print all odd number from a intStream.
     * Please use `createIntStream` and `getLambdaPrinter` methods
     *
     * e.g.
     * lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
     *
     * sout:
     * odd number:1!
     * odd number:3!
     * odd number:5!
     *
     * @param intStream
     * @param printer
     */

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        // i need to loop through intStream, if odd accept it, and then put the results into intStream
        // what I ended up doing, got all odd in intStream format, then forEached into the stream, where I used a
        // lambda function that said for every element apply this lambda function
        // what was this lambda? it was to do printer.accept(int converted into str = {what happens here: int automatically boxed into Integar object and from there into a string})
        IntStream stream = getOdd(intStream);
        stream.forEach(str -> printer.accept(Integer.toString(str) ) );
    }

    /**
     * Square each number from the input.
     * Please write two solutions and compare difference
     *   - using flatMap
     *
     * @param ints
     * @return
     */
    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
//        List<Integer> intsConverted = new ArrayList<>();
//        ints.forEach(ints2 -> ints2.forEach(input -> intsConverted.add(input*input)));
//        return intsConverted.stream();

        // Alternative solution

        // flattens the Stream of lists into a Stream of ints (inner stuff)
        Stream<Integer> streamOfIntsFlat = ints.flatMap(list -> list.stream());
        // then converts each item in the stream to its squared counter part
        return streamOfIntsFlat.map(input -> input*input);

    }
}
