package com.pets.points.demo;

import com.pets.Orders;
import com.septima.application.AsyncEndPoint;
import com.septima.application.endpoint.Answer;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/demo/rename-owners", asyncSupported = true)
public class UpdateDemoPoint extends AsyncEndPoint {

    @Override
    public void get(Answer answer) {
        Orders orders = new Orders(entities);
        orders.getOwners().query(Map.of()).thenAcceptBoth(
                orders.getPets().query(Map.of()),
                (owners, pets) -> pets.values().stream()
                        .map(Orders.Pet::getOwner)
                        .forEach(owner -> owner.setFirstName(owner.getFirstName() + "_1")))
                .thenApply(v -> orders.save())
                .thenCompose(Function.identity())
                .thenAccept(answer::withJsonValue)
                .exceptionally(answer::exceptionally);
    }
}
