package com.unimib.unimibike.Util;

import com.unimib.unimibike.Model.Bike;

public interface FragmentCallback {
    void callbackMethod(boolean rental_start, Bike bike_used);
}
