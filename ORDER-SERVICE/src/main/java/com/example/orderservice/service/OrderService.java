package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtos().stream().map(this::mapToOrderLineItems).collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItems);

        //get list of skuCode
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(orderLineItems1 -> orderLineItems1.getSkuCode()).collect(Collectors.toList());

        //we need to call inventory service to check if product in stock or not!
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://INVENTORY-SERVICE/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean isInStock  = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if(isInStock){
            orderRepository.save(order);
            return "order placed successfully";
        }
        else {
            throw new IllegalArgumentException("Product is  not in the stock please try again!");
        }
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
