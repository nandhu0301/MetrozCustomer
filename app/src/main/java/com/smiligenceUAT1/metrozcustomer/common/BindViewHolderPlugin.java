package com.smiligenceUAT1.metrozcustomer.common;

import android.view.View;

public interface BindViewHolderPlugin {
    void hookToOnBindViewHolder(View rootView);

    void hookToShimmerLayout(View shimmerLayout);
}
