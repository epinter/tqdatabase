/*
 * Copyright (C) 2020 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

public class Teleport extends BaseType {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Teleport{" +
                "description='" + description + '\'' +
                "} " + super.toString();
    }
}
