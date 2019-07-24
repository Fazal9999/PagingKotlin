package com.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.model.main.PhotoListModel
import com.app.network.main.MainApi
import com.app.util.NetworkState
import javax.inject.Inject

class MainViewModel @Inject constructor(mainApi: MainApi) : ViewModel()
{
    // FOR DATA ---
    var imgsLiveData : LiveData<PagedList<PhotoListModel>>
    val data = MutableLiveData<MainDataSourceClass>()
    private var mainApi: MainApi? = null

    // OBSERVABLES ---
    val networkState: LiveData<NetworkState<String>>? = switchMap(data) { it.getNetworkState() }

    // UTILS ---
    init
    {
        this.mainApi = mainApi

        val config = PagedList.Config.Builder().setPageSize(20)
            .setEnablePlaceholders(true).build()

        imgsLiveData = initializedPagedListBuilder(config).build()
    }

    /**
     * Fetch a list of Photo [id,title] by PhotoListModel
     */

    fun getData() : LiveData<PagedList<PhotoListModel>> = imgsLiveData

    private fun initializedPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Int, PhotoListModel> {

        val dataSourceFactory = object : DataSource.Factory<Int, PhotoListModel>() {
            override fun create(): MainDataSourceClass {
                val source = MainDataSourceClass(mainApi!!)
                data.postValue(source)
                return source
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }
}