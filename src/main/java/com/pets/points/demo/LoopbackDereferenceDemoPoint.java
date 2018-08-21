package com.pets.points.demo;

import com.pets.Orders;
import com.pets.points.Http;
import com.septima.application.AsyncEndPoint;
import com.septima.application.endpoint.Answer;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/demo/loopback/dereference", asyncSupported = true)
public class LoopbackDereferenceDemoPoint extends AsyncEndPoint {

    @Override
    public void get(Answer answer) {
        Orders orders = new Orders(entities);
        orders.getOwners().query(Map.of()).thenCombine(
                Http.fetchJsonArray("http://localhost:8080/septima-demo/data/com/pets/pet/")
                        .thenApply(pets -> Http.reviveDates("birthdate", pets))
                        .thenApply(orders.getPets()::toDomain),
                (owners, pets) -> pets.values().stream()
                        .map(Orders.Pet::getOwner)
                        .map(Orders.Owner::asMap)
                        .collect(Collectors.toList()))
                .thenAccept(answer::withJsonArray)
                .exceptionally(answer::exceptionally);
    }
}
