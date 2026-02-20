package org.acme.inventory.service;

import lombok.RequiredArgsConstructor;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

import static org.acme.inventory.database.CarInventory.carIdCounter;

@GraphQLApi
@RequiredArgsConstructor
public class GraphQLInventoryService {

    private final CarInventory carInventory;

    @Query
    public List<Car> getCars() {
        return carInventory.getCars();
    }

    @Mutation
    public Car register(Car car) {
        car.setId(carIdCounter.incrementAndGet());
        carInventory.getCars().add(car);
        return car;
    }

    @Mutation
    public boolean remove(String licensePlateNumber) {
        List<Car> cars = carInventory.getCars();
        Optional<Car> carToRemove = cars.stream()
                .filter(c -> c.getLicensePlateNumber().equals(licensePlateNumber))
                .findAny();
        return carToRemove.isPresent() && cars.remove(carToRemove.get());
    }
}
