package com.erbe.fiadeveloper.model;

import java.util.Date;

public class Available {

    private Date from;
    private Date to;

    public Available() {
    }

    public Available(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
