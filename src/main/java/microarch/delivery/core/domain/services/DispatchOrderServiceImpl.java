package microarch.delivery.core.domain.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.springframework.stereotype.Service;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

@Service
public final class DispatchOrderServiceImpl implements DispatchOrderService {

    @Override
    public Result<Courier, Error> dispatch(Order order, Courier[] couriers) {

        if (order == null || order.getStatus() != OrderStatus.Created) {
            return Result.failure(Error.of("not_created", "Заказ не в статусе Создан"));
        }

        var nearestCourier = chooseNearestCourier(order, couriers);
        if (nearestCourier.isFailure()) {
            return Result.failure(nearestCourier.getError());
        }
        Courier chosedCourier = nearestCourier.getValue();
        var takeOrderResult = chosedCourier.assignOrder(order);
        if (takeOrderResult.isFailure()) {
            return Result.failure(takeOrderResult.getError());
        }
        var assignResult = order.assign();

        if (assignResult.isFailure()) {
            return Result.failure(assignResult.getError());
        }

        return Result.success(chosedCourier);
    }

    private Result<Courier, Error> chooseNearestCourier(Order order, Courier[] couriers) {
        var orderVolume = order.getVolume();
        Optional<Courier> nearest = Arrays.stream(couriers)
                .filter(courier -> courier.canTakeOrder(orderVolume).isSuccess())
                .min(Comparator.comparingInt(courier -> courier.getLocation().distanceTo(order.getLocation())));

        if (nearest.isEmpty()) {
            return Result.failure(Error.of("courier_not_found", "Подходящий курьер не найден"));
        }
        return Result.success(nearest.get());
    }
}
