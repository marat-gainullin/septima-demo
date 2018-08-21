package com.pets.points.demo;

import com.pets.Orders;
import com.pets.points.Http;
import com.septima.application.AsyncEndPoint;
import com.septima.application.endpoint.Answer;
import com.septima.application.exceptions.InvalidRequestException;
import com.septima.application.exceptions.NoInstanceException;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/demo/owners/*", asyncSupported = true)
public class OwnersDemoPoint extends AsyncEndPoint {

    @Override
    public void get(Answer answer) {
        Orders orders = new Orders(entities);
        orders.getOwners().query(Map.of())
                .thenApply(owners -> owners.values().stream()
                        .map(orders.getOwners().getReverseMapper())
                        .collect(Collectors.toList())
                )
                .thenAccept(answer::withJsonArray)
                .exceptionally(answer::exceptionally);
    }

    @Override
    public void post(Answer answer) {
        Orders orders = new Orders(entities);
        Map<Long, Orders.Owner> owners = orders.getOwners().toDomain(Set.of());
        answer.onJsonObject()
                .thenApply(orders.getOwners().getForwardMapper())
                .thenApply(owner -> {
                    owners.put(owner.getId(), owner);
                    return orders.save();
                })
                .thenCompose(Function.identity())
                .thenAccept(answer::withJsonValue)
                .exceptionally(answer::exceptionally);
    }

    @Override
    public void delete(Answer answer) {
        Orders orders = new Orders(entities);
        Http.withTailKey(answer, tailKey -> orders.getOwners().query(Map.of())
                        .thenApply(data -> {
                            Orders.Owner deleted = data.remove(Long.valueOf(tailKey));
                            if (deleted != null) {
                                return orders.save();
                            } else {
                                throw new NoInstanceException("/demo/owners", "id", tailKey);
                            }
                        })
                        .thenCompose(Function.identity())
                        .thenAccept(answer::withJsonValue)
                        .exceptionally(answer::exceptionally),
                () -> {
                    throw new InvalidRequestException("Owner key should be included in the request URL as follows: /demo/owners/{owner-key}");
                });
    }
}
