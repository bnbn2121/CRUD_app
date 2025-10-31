package com.aston.homework.dto;

public class EventDto {
    EventName event;
    String email;

    public EventDto() {
    }

    public EventDto(EventName event, String email) {
        this.event = event;
        this.email = email;
    }

    public EventName getEvent() {
        return event;
    }

    public void setEvent(EventName event) {
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
