package name.abhijitsarkar.java.rx;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import name.abhijitsarkar.java.repository.YahooApiClient;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Random;

import static java.util.Collections.singleton;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
public class YahooFinanceService {
    private final YahooApiClient client;

    public double netAsset1(SimpleImmutableEntry<String, Long> stock1, SimpleImmutableEntry<String, Long> stock2) {
        Consumer consumer = new Consumer(stock1, stock2, "netAsset1");

        Observable.just(stock1.getKey(), stock2.getKey())
                .flatMap(this::buildObservable)
                /* onNext is called as soon as any price comes back */
                .toBlocking()
                .forEach(consumer);

        return consumer.netAsset;
    }

    @RequiredArgsConstructor
    private static final class Consumer implements Action1<Map.Entry<String, Double>> {
        private double netAsset;

        private final SimpleImmutableEntry<String, Long> stock1;
        private final SimpleImmutableEntry<String, Long> stock2;
        private final String caller;

        @Override
        public void call(Map.Entry<String, Double> e) {
            log.info("[{}] Calculating net asset on thread: {}.", caller, Thread.currentThread().getName());

            String key = e.getKey();
            double price = e.getValue();

            double asset = stock1.getKey().equals(key) ? stock1.getValue() * price : stock2.getValue() * price;

            netAsset += asset;
        }
    }

    public double netAsset2(SimpleImmutableEntry<String, Long> stock1, SimpleImmutableEntry<String, Long> stock2) {
        return Observable.combineLatest(buildObservable(stock1.getKey()), buildObservable(stock2.getKey()),
                /* Executes only when both prices come back */
                (e1, e2) -> {
                    log.info("[netAsset2] Calculating net asset on thread: {}.", Thread.currentThread().getName());

                    return e1.getValue() * stock1.getValue() + e2.getValue() * stock2.getValue();
                })
                .toBlocking()
                .single();
    }

    public double netAsset3(SimpleImmutableEntry<String, Long> stock1, SimpleImmutableEntry<String, Long> stock2) {
        return Observable.zip(buildObservable(stock1.getKey()), buildObservable(stock2.getKey()),
                /* Executes only when both prices come back */
                (e1, e2) -> {
                    log.info("[netAsset3] Calculating net asset on thread: {}.", Thread.currentThread().getName());

                    return e1.getValue() * stock1.getValue() + e2.getValue() * stock2.getValue();
                })
                .toBlocking()
                .single();
    }

    public double netAsset4(SimpleImmutableEntry<String, Long> stock1, SimpleImmutableEntry<String, Long> stock2) {
        return Observable.merge(buildObservable(stock1.getKey()), buildObservable(stock2.getKey()))
                /* reduce is called as soon as any price comes back */
                .<Double>reduce(0.0d, (acc, e) -> {
                    log.info("[netAsset4] Calculating net asset on thread: {}.", Thread.currentThread().getName());

                    String key = e.getKey();
                    double price = e.getValue();

                    double asset = stock1.getKey().equals(key) ? stock1.getValue() * price : stock2.getValue() * price;

                    return acc + asset;
                })
                .toBlocking()
                .single();
    }

    public double netAsset5(SimpleImmutableEntry<String, Long> stock1, SimpleImmutableEntry<String, Long> stock2) {
        Consumer consumer = new Consumer(stock1, stock2, "netAsset5");

        Observable.just(stock1.getKey(), stock2.getKey())
                .flatMap(this::buildObservable2)
                .toBlocking()
                .forEach(consumer);

        return consumer.netAsset;
    }

    private Observable<Map.Entry<String, Double>> buildObservable(String stock) {
        return Observable.just(stock)
                .subscribeOn(Schedulers.computation())
                .map(s -> {
                    sleepRandom();
                    return client.getPrice(singleton(s)).entrySet().iterator().next();
                });
    }

    private Observable<Map.Entry<String, Double>> buildObservable2(String stock) {
        return Observable.create((Subscriber<? super Map.Entry<String, Double>> s) -> {
            /* Simulate latency */
            sleepRandom();
            Map.Entry<String, Double> next = client.getPrice(singleton(stock)).entrySet().iterator().next();
            s.onNext(next);

            s.onCompleted();
        }).subscribeOn(Schedulers.computation());
    }

    @SneakyThrows
    private void sleepRandom() {
        int sleepTime = new Random().nextInt(10);

        String threadName = Thread.currentThread().getName();
        log.debug("Thread {} going to sleep for {} sec.", threadName, sleepTime);

        Thread.sleep(1000 * sleepTime);
    }
}
