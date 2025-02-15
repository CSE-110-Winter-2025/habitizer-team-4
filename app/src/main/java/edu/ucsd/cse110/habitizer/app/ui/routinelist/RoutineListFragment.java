package edu.ucsd.cse110.habitizer.app.ui.routinelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentRoutineListBinding;

public class RoutineListFragment extends Fragment {
    private MainViewModel activityModel;
    private RoutineListAdapter adapter; // adapter for ListView
    private FragmentRoutineListBinding view;

    public RoutineListFragment() {}

    public static RoutineListFragment newInstance() {
        RoutineListFragment fragment = new RoutineListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.adapter = new RoutineListAdapter(requireContext(), List.of(), activityModel, modelOwner);

        activityModel.loadRoutineList().observe(routines -> {
            // when a change is detected by observer
            // this will clear all contents in the adapter
            // and then get repopulate with new data
            if (routines == null) return;

            adapter.clear();
            adapter.addAll(new ArrayList<>(routines));
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRoutineListBinding.inflate(inflater, container, false);
        view.routineList.setAdapter(adapter);
        return view.getRoot();

    }
}
