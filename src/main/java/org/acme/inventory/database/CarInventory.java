package org.acme.inventory.database;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.acme.inventory.model.Car;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@ApplicationScoped
public class CarInventory {

    private List<Car> cars;
    public static final AtomicLong carIdCounter = new AtomicLong(0);

    @PostConstruct
    void initialize() {
        cars = new CopyOnWriteArrayList<>();
        initialData();
    }

    private void initialData() {
        cars.add(new Car(carIdCounter.incrementAndGet(), "ABC123", "Toyota", "Corolla"));
        cars.add(new Car(carIdCounter.incrementAndGet(), "XYZ789", "Honda", "Civic"));
    }

}
