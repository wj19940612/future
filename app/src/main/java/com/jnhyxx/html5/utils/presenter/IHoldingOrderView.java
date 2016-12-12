package com.jnhyxx.html5.utils.presenter;

import java.util.List;

public interface IHoldingOrderView<T> {

    void onShowHoldingOrderList(List<T> holdingOrderList);

    void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio);

    void onSubmitAllHoldingOrdersCompleted(String message);

    void onSubmitHoldingOrderCompleted(T holdingOrder);

    void onRiskControlTriggered(String showIds);
}
