package com.ncgroup.marketplaceserver.order.controller;

import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncgroup.marketplaceserver.model.dto.UserDto;
import com.ncgroup.marketplaceserver.order.model.OrderStatus;
import com.ncgroup.marketplaceserver.order.model.dto.OrderPostDto;
import com.ncgroup.marketplaceserver.order.model.dto.OrderReadDto;
import com.ncgroup.marketplaceserver.order.service.OrderService;
import com.ncgroup.marketplaceserver.shopping.cart.model.dto.ShoppingCartItemCreateDto;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/orders")
@RestController
@Slf4j
public class OrderController {
	
	private OrderService orderService;
	
	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@GetMapping()
	public ResponseEntity<List<OrderReadDto>> getOrdersForCourier(
			@RequestHeader("Authorization") String token,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		List<OrderReadDto> orders = orderService.getCourierOrders(token, page);
		if(orders.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(orderService.getCourierOrders(token, page), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<OrderReadDto> getOrder(@PathVariable long id) {
		OrderReadDto order = orderService.getOrder(id);
		if(order == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(orderService.getOrder(id), HttpStatus.OK);
	}
	
	@PostMapping("/{id}/status")
	public ResponseEntity<OrderReadDto> modifyStatus(@PathVariable long id) {
		return new ResponseEntity<>(orderService.modifyStatus(id), HttpStatus.OK);
	}
	
	@GetMapping("/validate")
	public ResponseEntity<Void> validateCart(@Valid List<ShoppingCartItemCreateDto> shoppingCart) {
		return ResponseEntity.noContent().build(); 
	}
	
	@GetMapping("/freeslots")
	public ResponseEntity<List<LocalDateTime>> getFreeSlots() {
		return new ResponseEntity<>(orderService.getFreeSlots(), HttpStatus.OK);
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderReadDto> saveOrder(
			@RequestHeader(value = "Authorization", required = false) String token,
			@RequestBody @Valid OrderPostDto order) {
		log.info("HERE " + order.toString());
		return new ResponseEntity<>(orderService.addOrder(order, token), HttpStatus.CREATED);
	}
	
}
