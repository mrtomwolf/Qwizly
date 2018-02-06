package com.tomasforsman.qwizly;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.tomasforsman.qwizly.db.QDB;
import com.tomasforsman.qwizly.db.entity.CommentEntity;
import com.tomasforsman.qwizly.db.entity.ProductEntity;

import java.util.List;

/**
 * Repository handling the work with products and comments.
 */
public class QwizlyRepository {

    private static QwizlyRepository sInstance;

    private final QDB mDatabase;
    private MediatorLiveData<List<ProductEntity>> mObservableProducts;

    private QwizlyRepository(final QDB database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        mObservableProducts.addSource(mDatabase.productDao().loadAllProducts(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });
    }

    public static QwizlyRepository getInstance(final QDB database) {
        if (sInstance == null) {
            synchronized (QwizlyRepository.class) {
                if (sInstance == null) {
                    sInstance = new QwizlyRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mObservableProducts;
    }

    public LiveData<ProductEntity> loadProduct(final int productId) {
        return mDatabase.productDao().loadProduct(productId);
    }

    public LiveData<List<CommentEntity>> loadComments(final int productId) {
        return mDatabase.commentDao().loadComments(productId);
    }
}
