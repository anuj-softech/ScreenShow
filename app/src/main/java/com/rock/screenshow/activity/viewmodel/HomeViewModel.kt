package com.rock.screenshow.activity.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rock.screenshow.model.VideoRow
import com.rock.screenshow.repository.MediaRepository
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val repository = MediaRepository(context)

    private val _rows = MutableLiveData<List<VideoRow>>()
    val rows: LiveData<List<VideoRow>> get() = _rows

    fun loadHomeRows() {
        viewModelScope.launch {
            try {
                val result = repository.getHomeRows()
                _rows.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}