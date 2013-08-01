package com.example.shoppinglist.model;

import java.util.List; 

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ORMLiteLoader<T, ID> extends AsyncTaskLoader<List<T>> {

	private RuntimeExceptionDao<T, ID> dao;

	private List<T> results;

	public ORMLiteLoader(Context context) {
		super(context);
	}

	public ORMLiteLoader(Context context, RuntimeExceptionDao<T, ID> dao) {
		super(context);
		this.dao = dao;
	}

	@Override
	public List<T> loadInBackground() {
		return dao.queryForAll();
	}

	@Override
	public void deliverResult(List<T> data) {
		results = data;
		if (isStarted()) {
			super.deliverResult(data);
		}
	}

	@Override
	protected void onStartLoading() {
		if (results != null) {
			deliverResult(results);
		}
		if (takeContentChanged() || results == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		results = null;
	}

	public void setDao(RuntimeExceptionDao<T, ID> dao) {
		this.dao = dao;
	}
}
