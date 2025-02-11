package com.culinaryapi.Order_Service.specifications;

import com.culinaryapi.Order_Service.models.OrderModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThan;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "orderStatus", spec = Equal.class),
            @Spec(path = "orderDate", spec = Equal.class),
            @Spec(path = "totalAmount", spec = GreaterThan.class)
    })
    public interface CourseSpec extends Specification<OrderModel> {}

    public static Specification<OrderModel> byUserId(UUID userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("userId"), userId);
    }
}