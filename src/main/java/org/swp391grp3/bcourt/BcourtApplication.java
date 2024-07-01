package org.swp391grp3.bcourt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.swp391grp3.bcourt.services.OrderService;

@EnableScheduling
@SpringBootApplication
public class BcourtApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BcourtApplication.class, args);

		// Get the OrderService bean from the context
		OrderService orderService = context.getBean(OrderService.class);

		// Run autoCancelOrders method after application startup
		orderService.autoCancelOrders();
	}

}
