/*------------------------------------------------------------------------------
 - Copyright (c) 2018. This code was created by Younes Charfaoui in the process of Graduation Project for the year of  2018 , which is about creating a platform  for students and professors to help them in the communication and the get known of the university information and so on.
 -----------------------------------------------------------------------------*/

package com.ibnkhaldoun.studentside.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ibnkhaldoun.studentside.R;
import com.ibnkhaldoun.studentside.Utilities.PreferencesManager;
import com.ibnkhaldoun.studentside.adapters.SavedAdapter;
import com.ibnkhaldoun.studentside.asyncTask.UnSaveAsyncTask;
import com.ibnkhaldoun.studentside.database.DatabaseContract;
import com.ibnkhaldoun.studentside.interfaces.UnsaveListener;
import com.ibnkhaldoun.studentside.models.Saved;
import com.ibnkhaldoun.studentside.networking.models.RequestPackage;
import com.ibnkhaldoun.studentside.networking.models.Response;
import com.ibnkhaldoun.studentside.networking.utilities.NetworkUtilities;
import com.ibnkhaldoun.studentside.providers.EndPointsProvider;
import com.ibnkhaldoun.studentside.services.LoadDataService;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ibnkhaldoun.studentside.Utilities.PreferencesManager.STUDENT;
import static com.ibnkhaldoun.studentside.networking.models.RequestPackage.POST;
import static com.ibnkhaldoun.studentside.networking.models.Response.IO_EXCEPTION;
import static com.ibnkhaldoun.studentside.networking.models.Response.JSON_EXCEPTION;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_POST_ID2;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_SAVE_ACTION;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_ID;
import static com.ibnkhaldoun.studentside.services.LoadDataService.ACTION_ERROR;
import static com.ibnkhaldoun.studentside.services.LoadDataService.KEY_ACTION;
import static com.ibnkhaldoun.studentside.services.LoadDataService.KEY_ERROR;
import static com.ibnkhaldoun.studentside.services.LoadDataService.KEY_REQUEST;
import static com.ibnkhaldoun.studentside.services.LoadDataService.SAVED_ACTION;
import static com.ibnkhaldoun.studentside.services.LoadDataService.SAVED_TYPE;

public class SavedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
        , UnsaveListener {

    public static final String KEY_SAVED = "keySaved";
    private static final int ID_SAVED_LOADER = 727;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private SavedAdapter mAdapter;
    private ProgressBar mLoadingProgressBar;
    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mSavedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(KEY_SAVED)) {
                ArrayList<Saved> savedList = intent.getParcelableArrayListExtra(KEY_SAVED);
                mLoadingProgressBar.setVisibility(GONE);
                if (savedList != null) {
                    if (savedList.size() != 0) {
                        mRecyclerView.setVisibility(VISIBLE);
                        mAdapter.swapList(savedList);
                    } else {
                        getSupportLoaderManager()
                                .restartLoader(ID_SAVED_LOADER, null, SavedActivity.this)
                                .forceLoad();
                    }
                } else {
                    getSupportLoaderManager()
                            .restartLoader(ID_SAVED_LOADER, null, SavedActivity.this)
                            .forceLoad();
                }
            }
        }
    };

    private BroadcastReceiver mFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(KEY_ERROR, -1);
            getSupportLoaderManager().restartLoader(ID_SAVED_LOADER, null, SavedActivity.this).forceLoad();
            switch (type) {
                case JSON_EXCEPTION:
                    Snackbar JsonSnackBar = Snackbar.make(findViewById(R.id.saved_main_view)
                            , R.string.error_json,
                            Snackbar.LENGTH_SHORT);
                    JsonSnackBar.setAction(R.string.retry_string, v -> {
                        if (JsonSnackBar.isShownOrQueued()) {
                            JsonSnackBar.dismiss();
                        }
                        getSavedFromTheInternet();
                    });
                    JsonSnackBar.show();
                    break;
                case IO_EXCEPTION:
                    Snackbar IOSnackBar = Snackbar.make(findViewById(R.id.saved_main_view)
                            , R.string.error_json,
                            Snackbar.LENGTH_SHORT);
                    IOSnackBar.setAction(R.string.retry_string, v -> {
                        if (IOSnackBar.isShownOrQueued()) {
                            IOSnackBar.dismiss();
                        }
                        getSavedFromTheInternet();
                    });
                    IOSnackBar.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmptyView = findViewById(R.id.saved_empty_view);
        mRecyclerView = findViewById(R.id.saved_recycler_view);
        mLoadingProgressBar = findViewById(R.id.saved_progressbar);

        setupRecyclerView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(mSavedReceiver,
                new IntentFilter(SAVED_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mFailedReceiver,
                new IntentFilter(ACTION_ERROR));

        if (NetworkUtilities.isConnected(this)) {
            getSavedFromTheInternet();
        } else {
            getSupportLoaderManager().initLoader(ID_SAVED_LOADER, null, SavedActivity.this);
        }
    }

    private void setupRecyclerView() {
        mAdapter = new SavedAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {"*"};
        switch (id) {
            case ID_SAVED_LOADER:
                return new CursorLoader(this,
                        DatabaseContract.SavedEntry.CONTENT_SAVED_URI,
                        projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingProgressBar.setVisibility(GONE);
        if (data.getCount() == 0) {
            mEmptyView.setVisibility(VISIBLE);
        } else {
            mRecyclerView.setVisibility(VISIBLE);
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mRecyclerView.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
        mLoadingProgressBar.setVisibility(GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_saved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_refresh:
                getSavedFromTheInternet();
                break;
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return true;
    }

    public void getSavedFromTheInternet() {
        if (NetworkUtilities.isConnected(this)) {
            mLoadingProgressBar.setVisibility(VISIBLE);
            mEmptyView.setVisibility(GONE);
            mRecyclerView.setVisibility(GONE);

            RequestPackage request = new RequestPackage.Builder()
                    .addEndPoint(EndPointsProvider.getSavedEndPoint())
                    .addMethod(POST)
                    .addParams(JSON_STUDENT_ID, new PreferencesManager(this, STUDENT).getIdUser())
                    .create();

            Intent serviceIntent = new Intent(this, LoadDataService.class);
            serviceIntent.putExtra(KEY_REQUEST, request);
            serviceIntent.putExtra(KEY_ACTION, SAVED_TYPE);
            startService(serviceIntent);
        } else {
            Snackbar networkSnackBar = Snackbar.make(findViewById(R.id.saved_main_view)
                    , R.string.no_internet_connection_string,
                    Snackbar.LENGTH_SHORT);
            networkSnackBar.setAction(R.string.retry_string, v -> {
                if (networkSnackBar.isShownOrQueued()) {
                    networkSnackBar.dismiss();
                }
                getSavedFromTheInternet();
            });
            networkSnackBar.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSavedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFailedReceiver);
    }

    @Override
    public void OnUnsaveFinished(Response response, int position) {
        mProgressDialog.dismiss();
        Snackbar.make(findViewById(R.id.saved_main_view)
                , R.string.unsaved_succ,
                Snackbar.LENGTH_SHORT).show();
        Uri pathToSave = DatabaseContract.SavedEntry.
                CONTENT_SAVED_URI.buildUpon()
                .appendEncodedPath(String.valueOf(mAdapter.getIdAt(position)))
                .build();
        getContentResolver().delete(pathToSave, null, null);
        mAdapter.removeSaveAt(position);

    }

    @Override
    public void OnUnsaveStarted(long id, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.unsave_post_title)
                .setMessage(R.string.unsave_post_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (NetworkUtilities.isConnected(this)) {
                        RequestPackage request = new RequestPackage.Builder()
                                .addEndPoint(EndPointsProvider.getUnsaveEndpoint())
                                .addMethod(POST)
                                .addParams(JSON_STUDENT_ID,
                                        new PreferencesManager(this, PreferencesManager.STUDENT).getIdUser())
                                .addParams(JSON_POST_ID2, String.valueOf(id))
                                .addParams(JSON_SAVE_ACTION, String.valueOf(0))
                                .create();

                        UnSaveAsyncTask task = new UnSaveAsyncTask(this, position);
                        task.execute(request);
                        dialog.cancel();
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setTitle(getString(R.string.wait_please));
                        mProgressDialog.setMessage(getString(R.string.unsaving_current));
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                    } else {
                        Toast.makeText(this,
                                R.string.no_internet_connection_string,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void OnUnsavedEmpty() {
        mRecyclerView.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
        mLoadingProgressBar.setVisibility(GONE);
    }
}