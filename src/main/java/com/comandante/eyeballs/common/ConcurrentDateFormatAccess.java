package com.comandante.eyeballs.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentDateFormatAccess {

    private ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-M-yyyy");
        }

        @Override
        public DateFormat get() {
            return super.get();
        }

        @Override
        public void set(DateFormat value) {
            super.set(value);
        }

        @Override
        public void remove() {
            super.remove();
        }
    };

    public Date convertStringToDate(String dateString) throws ParseException {
        return df.get().parse(dateString);
    }

    public String convertDateToString(Date date) throws ParseException {
        return df.get().format(date);
    }
}
