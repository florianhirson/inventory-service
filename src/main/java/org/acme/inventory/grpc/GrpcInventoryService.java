package org.acme.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.Car;
import org.acme.inventory.model.CarResponse;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class GrpcInventoryService implements InventoryService {

    private final CarInventory inventory;

    @Override
    public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {
        return requests.map(request -> {
            Car car = new Car();
            car.setLicensePlateNumber(request.getLicensePlateNumber());
            car.setManufacturer(request.getManufacturer());
            car.setModel(request.getModel());
            car.setId(CarInventory.carIdCounter.incrementAndGet());
            return car;
        }).onItem().invoke(car -> {
            Log.info("Persisting " + car);
            inventory.getCars().add(car);
        }).map(car -> CarResponse.newBuilder()
                .setLicensePlateNumber(car.getLicensePlateNumber())
                .setManufacturer(car.getManufacturer())
                .setModel(car.getModel())
                .setId(car.getId())
                .build());
    }

    @Override
    public Uni<CarResponse> remove(RemoveCarRequest request) {
        Optional<Car> optionalCar = inventory.getCars().stream()
                .filter(car -> request.getLicensePlateNumber()
                        .equals(car.getLicensePlateNumber()))
                .findFirst();

        if (optionalCar.isPresent()) {
            Car removedCar = optionalCar.get();
            inventory.getCars().remove(removedCar);
            return Uni.createFrom().item(CarResponse.newBuilder()
                    .setLicensePlateNumber(removedCar.getLicensePlateNumber())
                    .setManufacturer(removedCar.getManufacturer())
                    .setModel(removedCar.getModel())
                    .setId(removedCar.getId())
                    .build());
        }
        return Uni.createFrom().nullItem();
    }
}
