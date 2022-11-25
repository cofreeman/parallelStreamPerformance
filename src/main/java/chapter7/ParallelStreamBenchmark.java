package chapter7;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2,jvmArgs = {"-Xms4G","-Xms4G"})
@State(value = Scope.Thread)
public class ParallelStreamBenchmark {
    private static final long N = 10_000_000L;

    public long sequentialSum(){
        return Stream.iterate(1L,i -> i + 1).limit(N)
                .reduce(0L,Long::sum);
    }

    public long iterativeSum(){
        long result = 0;
        for (int i = 0; i <= N; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long parallelRangedSum(){
        return LongStream.rangeClosed(0,N).parallel()
                .reduce(0L,Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown(){
        System.gc();
    }
    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ParallelStreamBenchmark.class.getSimpleName())
                .forks(2)
                .build();

        new Runner(opt).run();
    }
}
