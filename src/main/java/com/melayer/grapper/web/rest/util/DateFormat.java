package com.melayer.grapper.web.rest.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pooja on 11/1/18.
 */
public class DateFormat {
    public static String getFormatedDate(Long dateInMillies){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(new Date(dateInMillies));
    }
}
