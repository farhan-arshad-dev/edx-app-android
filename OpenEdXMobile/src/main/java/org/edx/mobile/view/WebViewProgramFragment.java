package org.edx.mobile.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import org.edx.mobile.R;
import org.edx.mobile.event.EnrolledInCourseEvent;
import org.edx.mobile.event.NetworkConnectivityChangeEvent;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.util.links.DefaultActionListener;

import de.greenrobot.event.EventBus;
import roboguice.inject.InjectView;

public class WebViewProgramFragment extends AuthenticatedWebViewFragment {
    @InjectView(R.id.loading_indicator)
    private ProgressBar progressWheel;
    private boolean refreshOnResume = false;

    public static Fragment newInstance(@NonNull String url) {
        final Fragment fragment = new WebViewProgramFragment();
        fragment.setArguments(makeArguments(url, null, true));
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authWebView.getWebViewClient().setActionListener(new DefaultActionListener(getActivity(),
                progressWheel, new DefaultActionListener.EnrollCallback() {
            @Override
            public void onResponse(@NonNull EnrolledCoursesResponse course) {

            }

            @Override
            public void onFailure(@NonNull Throwable error) {

            }

            @Override
            public void onUserNotLoggedIn(@NonNull String courseId, boolean emailOptIn) {

            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshOnResume) {
            refreshOnResume = false;
            authWebView.loadUrl(true, authWebView.getWebView().getUrl());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        OfflineSupportUtils.setUserVisibleHint(getActivity(), isVisibleToUser,
                authWebView != null && authWebView.isShowingError());
    }

    @SuppressWarnings("unused")
    public void onEvent(NetworkConnectivityChangeEvent event) {
        OfflineSupportUtils.onNetworkConnectivityChangeEvent(getActivity(), getUserVisibleHint(), authWebView.isShowingError());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EnrolledInCourseEvent event) {
        refreshOnResume = true;
    }

    @Override
    protected void onRevisit() {
        OfflineSupportUtils.onRevisit(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
