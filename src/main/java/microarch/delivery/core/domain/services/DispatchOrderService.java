package microarch.delivery.core.domain.services;

import libs.errs.Error;

import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

public interface DispatchOrderService {
    Result<Courier, Error> dispatch(Order order, Courier[] couriers);
}
