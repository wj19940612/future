package com.jnhyxx.html5.domain.local;

import com.jnhyxx.html5.domain.market.MarketBrief;
import com.jnhyxx.html5.domain.order.PositionBrief;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.utils.adapter.GroupAdapter;

import java.util.List;

public class ProductPkg implements GroupAdapter.Groupable  {

    private Product mProduct;
    private MarketBrief mMarketBrief;
    private PositionBrief mPositionBrief;

    public ProductPkg(Product product) {
        mProduct = product;
    }

    public void setPositionBrief(PositionBrief positionBrief) {
        mPositionBrief = positionBrief;
    }

    public void setMarketBrief(MarketBrief marketBrief) {
        mMarketBrief = marketBrief;
    }

    public Product getProduct() {
        return mProduct;
    }

    public MarketBrief getMarketBrief() {
        return mMarketBrief;
    }

    public PositionBrief getPositionBrief() {
        return mPositionBrief;
    }

    public static void updateProductPkgList(List<ProductPkg> productPkgList, List<Product> productList,
                                            List<PositionBrief> positionBriefList,
                                            List<MarketBrief> marketBriefList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        productPkgList.clear();

        for (int i = 0; productList != null && i < productList.size(); i++) {
            Product product = productList.get(i);
            ProductPkg pkg = new ProductPkg(product);

            if (positionBriefList != null ) {
                for (int j = 0; j < positionBriefList.size(); j++) {
                    PositionBrief brief = positionBriefList.get(j);
                    if (product.getVarietyType().equalsIgnoreCase(brief.getInstrumentCode())) {
                        pkg.setPositionBrief(brief);
                        break;
                    }
                }
            }

            if (marketBriefList != null) {
                for (int k = 0; k < marketBriefList.size(); k++) {
                    MarketBrief marketBrief = marketBriefList.get(k);
                    if (product.getVarietyType().equalsIgnoreCase(marketBrief.getCode())) {
                        pkg.setMarketBrief(marketBrief);
                        break;
                    }
                }
            }

            productPkgList.add(pkg);
        }

    }

    public static boolean updatePositionInProductPkg(List<ProductPkg> productPkgList,
                                                     List<PositionBrief> positionBriefList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        int count = 0;
        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            Product product = pkg.getProduct();
            for (int j = 0; positionBriefList != null && j < positionBriefList.size(); j++) {
                PositionBrief positionBrief= positionBriefList.get(j);
                if (product.getVarietyType().equalsIgnoreCase(positionBrief.getInstrumentCode())) {
                    pkg.setPositionBrief(positionBrief);
                    count++; // when each product has its position brief, count++.
                    break;
                }
            }
        }

        boolean haveSameSize = true;
        boolean haveSameProducts = true;
        if (positionBriefList != null) {
            haveSameSize = (productPkgList.size() == positionBriefList.size());
            haveSameProducts = (count == productPkgList.size());
        }

        boolean updateProductList = !(haveSameProducts && haveSameSize);

        return updateProductList;
    }

    public static boolean updateMarketInProductPkgList(List<ProductPkg> productPkgList,
                                                       List<MarketBrief> marketBriefList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        int count = 0;
        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            Product product = pkg.getProduct();
            for (int j = 0; marketBriefList != null && j < marketBriefList.size(); j++) {
                MarketBrief marketBrief = marketBriefList.get(j);
                if (product.getVarietyType().equalsIgnoreCase(marketBrief.getCode())) {
                    pkg.setMarketBrief(marketBrief);
                    count++; // when each product has its position brief, count++.
                    break;
                }
            }
        }

        boolean haveSameSize = true;
        boolean haveSameProducts = true;
        if (marketBriefList != null) {
            haveSameSize = (productPkgList.size() == marketBriefList.size());
            haveSameProducts = (count == productPkgList.size());
        }

        boolean updateProductList = !(haveSameProducts && haveSameSize);

        return updateProductList;
    }

    public static void clearPositionBriefs(List<ProductPkg> productPkgList) {
        if (productPkgList == null) {
            throw new NullPointerException("productPkgList is null");
        }

        for (int i = 0; i < productPkgList.size(); i++) {
            ProductPkg pkg = productPkgList.get(i);
            pkg.setPositionBrief(null);
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
