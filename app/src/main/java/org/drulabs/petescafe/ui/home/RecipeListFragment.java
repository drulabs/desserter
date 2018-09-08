package org.drulabs.petescafe.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.di.DaggerViewComponent;
import org.drulabs.petescafe.di.ViewComponent;
import org.drulabs.petescafe.utils.Constants;
import org.drulabs.petescafe.widget.RecipeWidgetProvider;

import java.util.Collections;

public class RecipeListFragment extends Fragment implements RecipeListAdapter.RecipeListListener {

    private static final String ARG_SORT_ORDER = "sort_order";
    public static final int SORT_ALPHABETICALLY = 0;
    public static final int SORT_STEP_COUNT = 1;

    private int sortOrder = SORT_ALPHABETICALLY;

    private OnFragmentInteractionListener mListener;

    private RecipeListAdapter adapter;
    private int columns = Constants.DEFAULT_SPAN_COUNT;

    HomeVM homeVM;

    public RecipeListFragment() {
        // Required empty public constructor
    }


    public static RecipeListFragment newInstance(int sortOrder) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SORT_ORDER, sortOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortOrder = getArguments().getInt(ARG_SORT_ORDER);
        }

        columns = getResources().getInteger(R.integer.span_count);

        adapter = new RecipeListAdapter(this, columns);

        ViewComponent viewComponent = DaggerViewComponent.builder()
                .appComponent(((CafeApp) getActivity().getApplicationContext()).getAppComponent())
                .build();
        HomeVMFactory homeVMFactory = viewComponent.getHomeVMFactory();
        homeVM = ViewModelProviders.of(this, homeVMFactory)
                .get(HomeVM.class);
        homeVM.getRecipes().observe(this, recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                Collections.sort(recipes, (r1, r2) -> {
                    switch (sortOrder) {
                        case SORT_STEP_COUNT:
                            return r1.getSteps().size() - r2.getSteps().size();
                        default:
                            return r1.getName().compareTo(r2.getName());
                    }
                });
                adapter.setRecipes(recipes);
                mListener.onRecipesFetched();
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getActivity(), columns);
        RecyclerView recyclerView = view.findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTapped(Recipe recipe) {
        homeVM.saveAsCurrentRecipe(recipe);
        RecipeWidgetProvider.updateWidgets(getActivity());
        mListener.onRecipeSelected(recipe);
    }

    public interface OnFragmentInteractionListener {
        void onRecipeSelected(Recipe recipe);

        void onRecipesFetched();
    }
}
