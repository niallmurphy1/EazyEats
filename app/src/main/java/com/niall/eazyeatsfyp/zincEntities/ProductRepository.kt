package com.niall.eazyeatsfyp.zincEntities

import javax.security.auth.callback.Callback

interface ProductRepository {
    //fun getAmazonProduct(name: String, callback: Callback<ProductObject>)
    //fun getAmazonProducts(names: List<String>, callback: Callback<AmazonProductResults>)
}

data class AmazonProductResults(val successItems: Map<String, ProductObject>, val errorItems: Map<String, Throwable>)