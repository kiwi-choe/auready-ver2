package com.kiwi.auready_ver2.taskheads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.tasks.TasksActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsFragment extends Fragment implements TaskHeadsContract.View {

    public static final String TAG_TASKHEADSFRAGMENT = "TAG_TasksFragment";

    private TaskHeadsContract.Presenter mPresenter;

    // interface
    private TaskHeadsFragmentListener mListener;
    private TaskHeadsAdapter mTaskHeadsAdapter;

    private TextView mNoTaskHeadTxt;
    private ListView mTaskHeadsView;

    public TaskHeadsFragment() {
        // Required empty public constructor
    }

    public static TaskHeadsFragment newInstance() {

        return new TaskHeadsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Destroy all menu and recall onCreateOptionsMenu
//        getActivity().supportInvalidateOptionsMenu();
        mPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskHeadsFragmentListener) {
            mListener = (TaskHeadsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskHeadsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void setPresenter(TaskHeadsContract.Presenter tasksPresenter) {
        mPresenter = checkNotNull(tasksPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_taskheads, container, false);

        mNoTaskHeadTxt = (TextView) root.findViewById(R.id.no_taskhead_txt);
        mTaskHeadsView = (ListView) root.findViewById(R.id.taskheads);
        mTaskHeadsAdapter = new TaskHeadsAdapter(new ArrayList<TaskHead>(0), mItemListener);
        mTaskHeadsView.setAdapter(mTaskHeadsAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_taskhead);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTaskHead();
            }
        });
    }

    @Override
    public void setLoginSuccessUI() {

        if (mListener != null) {
            mListener.onLoginSuccess();
        }
    }

    @Override
    public void showTaskHeads(List<TaskHead> taskHeads) {
        mTaskHeadsAdapter.replaceData(taskHeads);

        mNoTaskHeadTxt.setVisibility(View.GONE);
        mTaskHeadsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoTaskHeads() {
        mTaskHeadsView.setVisibility(View.GONE);
        mNoTaskHeadTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTaskHeadDetail(int cntOfTaskHeads) {

        Intent intent = new Intent(getContext(), TaskHeadDetailActivity.class);
        intent.putExtra(TaskHeadDetailActivity.ARG_CNT_OF_TASKHEADS, cntOfTaskHeads);
        startActivityForResult(intent, TaskHeadsActivity.REQ_ADD_TASKHEAD);
    }

    @Override
    public void showTasksView(String taskHeadId) {
        Intent intent = new Intent(getContext(), TasksActivity.class);
        intent.putExtra(TasksActivity.ARG_TASKHEAD_ID, taskHeadId);

        startActivity(intent);
    }

    @Override
    public void setLogoutSuccessUI() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Snackbar.make(mTaskHeadsView, "CANCELED", Snackbar.LENGTH_LONG).show();
        mPresenter.result(requestCode, resultCode, data);
    }

    // Interface with TaskHeadsActivity
    public interface TaskHeadsFragmentListener {
        void onLoginSuccess();
    }

    /*
    * Listener for clicks on taskHeads in the ListView
    * */
    TaskHeadItemListener
            mItemListener = new TaskHeadItemListener() {
        @Override
        public void onTaskHeadItemClick(String taskHeadId) {
            showTasksView(taskHeadId);
        }

        @Override
        public void onDeleteClick(TaskHead clickedTaskHead) {
            mPresenter.deleteTaskHead(clickedTaskHead.getId());
        }
    };

    public interface TaskHeadItemListener {
        void onTaskHeadItemClick(String taskHeadId);
        void onDeleteClick(TaskHead clickedTaskHead);
    }
}
