package com.jnhyxx.html5.domain.local;

import com.jnhyxx.html5.domain.market.MarketData;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.utils.adapter.GroupAdapter;

import java.util.List;

public class ProductPkg implements GroupAdapter.Groupable  {

    private Product mProduct;
    private MarketData mMarketData;
    private HomePositions.Position mPosition;

    public ProductPkg(Product product) {
        mProduct = product;
    }

    public void setMarketData(MarketData marketData) {
        mMarketData = marketData;
    }

    public Product getProduct() {
        return mProduct;
    }

    public MarketData getMarketData() {
        return mMarketData;
    }

    public HomePositions.Position getPosition() {
        return mPosition;
    }

    public void setPosition(HomePositions.Position position) {
        mPosition = position;
    }

    public static void updateProductPkgList(List<ProductPkg> productPkgList,
                                            List<Product> productList,
                                            List<? extends HomePositions.Position> positionList,
                                            List<MarketData> marketDataList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        productPkgList.clear();

        for (int i = 0; productList != null && i < productList.size(); i++) {
            Product product = productList.get(i);
            ProductPkg pkg = new ProductPkg(product);

            if (positionList != null ) {
                for (int j = 0; j < positionList.size(); j++) {
                    HomePositions.Position position = positionList.get(j);
                    if (product.getVarietyType().equalsIgnoreCase(position.getVarietyType())) {
                        pkg.setPosition(position);
                        break;
                    }
                }
            }

            if (marketDataList != null) {
                for (int k = 0; k < marketDataList.size(); k++) {
                    MarketData marketData = marketDataList.get(k);
                    if (product.getVarietyType().equalsIgnoreCase(marketData.getVarietyType())) {
                        pkg.setMarketData(marketData);
                        break;
                    }
                }
            }

            productPkgList.add(pkg);
        }

    }

    public static boolean updatePositionInProductPkg(List<ProductPkg> productPkgList,
                                                     List<? extends HomePositions.Position> positionList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        int count = 0;
        boolean holdingPositionWhenMarketClosed = false;
        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            Product product = pkg.getProduct();
            for (int j = 0; positionList != null && j < positionList.size(); j++) {
                HomePositions.Position position = positionList.get(j);
                if (product.getVarietyType().equalsIgnoreCase(position.getVarietyType())) {

                    if (position.getHandsNum() > 0
                            && product.getExchangeStatus() == Product.MARKET_STATUS_CLOSE) {
                        holdingPositionWhenMarketClosed = true;
                        // product market status is closed, but user has positions
                        // means we need to refresh product list
                    }

                    pkg.setPosition(position);
                    count++; // when each product has its position brief, count++.
                    break;
                }
            }
        }

        boolean haveSameSize = true;
        boolean haveSameProducts = true;
        if (positionList != null) {
            haveSameSize = (productPkgList.size() == positionList.size());
            haveSameProducts = (count == productPkgList.size());
        }

        boolean updateProductList = !(haveSameProducts && haveSameSize);

        return updateProductList || holdingPositionWhenMarketClosed;
    }



    public static boolean updateMarketInProductPkgList(List<ProductPkg> productPkgList,
                                                       List<MarketData> marketDataList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        int count = 0;
        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            Product product = pkg.getProduct();
            for (int j = 0; marketDataList != null && j < marketDataList.size(); j++) {
                MarketData marketData = marketDataList.get(j);
                if (product.getVarietyType().equalsIgnoreCase(marketData.getVarietyType())) {
                    pkg.setMarketData(marketData);
                    count++; // when each product has its position brief, count++.
                    break;
                }
            }
        }

        boolean haveSameSize = true;
        boolean haveSameProducts = true;
        if (marketDataList != null) {
            haveSameSize = (productPkgList.size() == marketDataList.size());
            haveSameProducts = (count == productPkgList.size());
        }

        boolean updateProductList = !(haveSameProducts && haveSameSize);

        return updateProductList;
    }

    public static void clearPositions(List<ProductPkg> productPkgList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            pkg.setPosition(null);
        }
    }

    @Override
    public String getGroupName() {
        if (getProduct().getIsDomestic() == Product.IS_DOMESTIC) {
            return "国内期货";
        }
        return "国外期货";
    }
}
