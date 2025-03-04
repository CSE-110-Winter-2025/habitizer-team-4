package edu.ucsd.cse110.habitizer.app.ui.editlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogEditTaskNameBinding;


public class EditTaskNameDialogFragment extends DialogFragment {
    private FragmentDialogEditTaskNameBinding view;
    private MainViewModel activityModel;
    private int taskId;
    private String taskTitle;

    EditTaskNameDialogFragment () {
        // Required empty public constructor
    }

    public static EditTaskNameDialogFragment newInstance(int taskId, String taskTitle) {
        var fragment = new EditTaskNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt("taskId", taskId);
        args.putString("taskTitle", taskTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        if (getArguments() != null) {
            this.taskId = getArguments().getInt("taskId");
            this.taskTitle = getArguments().getString("taskTitle");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogEditTaskNameBinding.inflate(getLayoutInflater());

        var dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Set Task Name")
                .setMessage("Please provide a task name.")
                .setView(view.getRoot())
                .setPositiveButton("Set", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> onPositiveButtonClick(dialog,0));
        });

        return dialog;
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var newTitle = view.editTaskName.getText().toString();
        if (newTitle.isEmpty()) {
            view.editTaskName.setError("Task name cannot be empty");
        } else {
            activityModel.updateTaskName(taskId, newTitle);
            dialog.dismiss();
        }
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

}
