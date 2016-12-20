package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.friend.FriendsActivity;
import com.kiwi.auready_ver2.friend.FriendsFragment;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHead;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */

public class TaskHeadDetailPresenter implements TaskHeadDetailContract.Presenter {

    @NonNull
    private final UseCaseHandler mUseCaseHandler;
    @Nullable
    private final String mTaskHeadId;
    private final TaskHeadDetailContract.View mView;
    @NonNull
    private final SaveTaskHead mSaveTaskHead;
    @NonNull
    private final GetTaskHead mGetTaskHead;

    public TaskHeadDetailPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String taskHeadId,
                                   @NonNull TaskHeadDetailContract.View view, @NonNull SaveTaskHead saveTaskHead,
                                   @NonNull GetTaskHead getTaskHead) {
        mUseCaseHandler = useCaseHandler;
        mTaskHeadId = taskHeadId;
        mView = view;
        mSaveTaskHead = saveTaskHead;
        mGetTaskHead = getTaskHead;

        mView.setPresenter(this);
    }


    @Override
    public void start() {
        if(mTaskHeadId != null) {
            populateTaskHead();
        }
    }

    @Override
    public void saveTaskHead(String title, List<Friend> members) {
        if (isNewTaskHead()) {
            createTaskHead(title, members);
        } else {
            updateTaskHead(title, members);
        }
    }

    @Override
    public void populateTaskHead() {
        if (mTaskHeadId == null) {
            throw new RuntimeException("populateTaskHead() was called but taskhead is new.");
        }
        mUseCaseHandler.execute(mGetTaskHead, new GetTaskHead.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHead.ResponseValue response) {
                        showTaskHead(response.getTaskHead());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (FriendsActivity.REQ_FRIENDS == requestCode
                && Activity.RESULT_OK == resultCode) {
            if (data.hasExtra(FriendsFragment.EXTRA_KEY_SELECTED_FRIENDS)) {
                ArrayList<Friend> friends =
                        data.getParcelableArrayListExtra(FriendsFragment.EXTRA_KEY_SELECTED_FRIENDS);
                mView.setMembers(friends);
            }
        }
    }

    private void showTaskHead(TaskHead taskHead) {
        mView.setTitle(taskHead.getTitle());
        mView.setMembers(taskHead.getMembers());
    }

    private void createTaskHead(String title, List<Friend> members) {
        final TaskHead newTaskHead = new TaskHead(title, members);
        if (newTaskHead.isEmpty()) {
            mView.showEmptyTaskHeadError();
        } else {
            mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(newTaskHead),
                    new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {

                        @Override
                        public void onSuccess(SaveTaskHead.ResponseValue response) {
                            mView.setResultToTaskHeadsView(newTaskHead.getId());
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    private void updateTaskHead(String title, List<Friend> members) {
        if (mTaskHeadId == null) {
            throw new RuntimeException("updateTaskHead() was called but taskHead is new.");
        }
        final TaskHead taskHead = new TaskHead(mTaskHeadId, title, members);
        mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(taskHead),
                new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTaskHead.ResponseValue response) {
                        mView.setResultToTaskHeadsView(taskHead.getId());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private boolean isNewTaskHead() {
        return mTaskHeadId == null;
    }
}
