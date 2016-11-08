package com.prestonparris.paymo.graph;

public class UserVertex {

    private int id;

    public UserVertex(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public UserVertex setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserVertex) {
            return this.getId() == ((UserVertex) obj).getId();
        } else {
            return false;
        }
    }
}
