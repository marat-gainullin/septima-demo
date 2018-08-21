package com.pets.points.demo;

import com.pets.Orders;
import com.septima.application.AsyncEndPoint;
import com.septima.application.endpoint.Answer;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/demo/dereference", asyncSupported = true)
public class DereferenceDemoPoint extends AsyncEndPoint {

    @Override
    public void get(Answer answer) {
        Orders orders = new Orders(entities);
        orders.getOwners().query(Map.of()).thenCombine(
                orders.getPets().query(Map.of()),
                (owners, pets) -> pets.values().stream()
                        .map(Orders.Pet::getOwner)
                        .map(Orders.Owner::asMap)
                        .collect(Collectors.toList()))
                .thenAccept(answer::withJsonArray)
                .exceptionally(answer::exceptionally);
    }
}
