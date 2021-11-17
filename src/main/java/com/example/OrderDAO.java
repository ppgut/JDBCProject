package com.example;

import com.example.util.DataAccessObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DataAccessObject<Order> {

    private static final String GET_ID = "SELECT " +
            "c.first_name, c.last_name, c.email, " +
            "o.order_id, o.creation_date, o.total_due, o.status, " +
            "s.first_name, s.last_name, s.email, " +
            "ol.quantity, p.code, p.name, p.size, p.variety, p.price " +
            "FROM orders o " +
            "join customer c on o.customer_id=c.customer_id " +
            "join salesperson s on o.salesperson_id=s.salesperson_id " +
            "join order_item ol on ol.order_id=o.order_id " +
            "join product p on ol.product_id=p.product_id " +
            "WHERE o.order_id=?";

    private static final String GET_FOR_CUST = "SELECT * FROM get_orders_by_customer(?)";

    public OrderDAO(Connection connection) {
        super(connection);
    }

    @Override
    public Order findById(long id) {
        Order order = new Order();
        try (PreparedStatement statement = connection.prepareStatement(GET_ID)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                order.setCustomerFirstName(rs.getString(1));
                order.setCustomerLastName(rs.getString(2));
                order.setCustomerEmail(rs.getString(3));
                order.setId(rs.getLong(4));
                order.setCreationDate(new Date(rs.getDate(5).getTime()));
                order.setTotalDue(rs.getBigDecimal(6));
                order.setStatus(rs.getString(7));
                order.setSalespersonFirstName(rs.getString(8));
                order.setSalespersonLastName(rs.getString(9));
                order.setSalespersonEmail(rs.getString(10));
                do {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setQuantity(rs.getInt(11));
                    orderItem.setProductCode(rs.getString(12));
                    orderItem.setProductName(rs.getString(13));
                    orderItem.setProductSize(rs.getInt(14));
                    orderItem.setProductVariety(rs.getString(15));
                    orderItem.setProductPrice(rs.getBigDecimal(16));
                    order.addOrderItem(orderItem);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return order;
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public Order update(Order dto) {
        return null;
    }

    @Override
    public Order create(Order dto) {
        return null;
    }

    @Override
    public void delete(long id) {
    }

    public List<Order> getOrdersForCustomer(long customerId) {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement statement = this.connection.prepareStatement(GET_FOR_CUST)) {
            statement.setLong(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            long orderId = 0;
            Order order = null;
            while (resultSet.next()) {
                long localOrderId = resultSet.getLong(4);
                if (orderId!=localOrderId) {
                    order = new Order();
                    orders.add(order);
                    order.setId(localOrderId);
                    orderId = localOrderId;
                    order.setCustomerFirstName(resultSet.getString(1));
                    order.setCustomerLastName(resultSet.getString(2));
                    order.setCustomerEmail(resultSet.getString(3));
                    order.setCreationDate(new Date(resultSet.getDate(5).getTime()));
                    order.setTotalDue(resultSet.getBigDecimal(6));
                    order.setStatus(resultSet.getString(7));
                    order.setSalespersonFirstName(resultSet.getString(8));
                    order.setSalespersonLastName(resultSet.getString(9));
                    order.setSalespersonEmail(resultSet.getString(10));
                    List<OrderItem> orderItems = new ArrayList<>();
                    order.setOrderItems(orderItems);
                }
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(resultSet.getInt(11));
                orderItem.setProductCode(resultSet.getString(12));
                orderItem.setProductName(resultSet.getString(13));
                orderItem.setProductSize(resultSet.getInt(14));
                orderItem.setProductVariety(resultSet.getString(15));
                orderItem.setProductPrice(resultSet.getBigDecimal(16));
                order.getOrderItems().add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return orders;
    }
}
