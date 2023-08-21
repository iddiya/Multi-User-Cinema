package com.ecinema.app.domain.enums;

public enum TicketType {

    CHILD(8),
    ADULT(12),
    SENIOR(9);

    private final int price;

    TicketType(int price) {
        this.price = price;
    }

    public final int getPrice() {
        return price;
    }

}
