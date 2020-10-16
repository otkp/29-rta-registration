package org.epragati.vahan;

public enum ResponseType {

    SUCCESS(1, "success"),
    ERROR(2, "error"),
    NOT_FOUND(3, "not found");

    private int value;
    private String label;

    private ResponseType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

}
