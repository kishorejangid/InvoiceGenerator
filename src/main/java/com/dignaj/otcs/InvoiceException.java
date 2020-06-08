package com.dignaj.otcs;

public class InvoiceException extends Exception {

    public InvoiceException(Throwable t)
    {
        super(t);
    }

    public InvoiceException(String message, Throwable t)
    {
        super(message, t);
    }

}
