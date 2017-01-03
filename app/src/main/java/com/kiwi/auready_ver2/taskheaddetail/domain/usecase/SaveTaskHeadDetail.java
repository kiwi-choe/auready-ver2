package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Save taskHeadDetail ({@link TaskHead} and {@link Member})
 */
public class SaveTaskHeadDetail extends UseCase<SaveTaskHeadDetail.RequestValues, SaveTaskHeadDetail.ResponseValue> {

    private final TaskHeadDetailRepository mRepository;

    public SaveTaskHeadDetail(@NonNull TaskHeadDetailRepository taskHeadDetailRepository) {
        mRepository = checkNotNull(taskHeadDetailRepository, "taskHeadDetailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.saveTaskHeadDetail(requestValues.getTaskHeadDetail(), new TaskHeadDetailDataSource.SaveCallback() {

            @Override
            public void onSaveSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onSaveFailed() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static class RequestValues implements UseCase.RequestValues {
        private final TaskHeadDetail mTaskHeadDetail;

        public RequestValues(@NonNull TaskHeadDetail taskHeadDetail) {
            mTaskHeadDetail = checkNotNull(taskHeadDetail, "taskHeadDetail cannot be null");
        }

        public TaskHeadDetail getTaskHeadDetail() {
            return mTaskHeadDetail;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {
    }

}