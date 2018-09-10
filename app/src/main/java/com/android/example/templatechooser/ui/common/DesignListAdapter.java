
package com.android.example.templatechooser.ui.common;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.example.templatechooser.R;
import com.android.example.templatechooser.databinding.ListItemBinding;
import com.android.example.templatechooser.util.Objects;
import com.android.example.templatechooser.vo.Design;

/**
 * A RecyclerView adapter for {@link Design} class.
 */
public class DesignListAdapter extends DataBoundListAdapter<Design, ListItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final DesignClickCallback designClickCallback;

    public DesignListAdapter(DataBindingComponent dataBindingComponent,
                             DesignClickCallback designClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.designClickCallback = designClickCallback;
    }

    @Override
    protected ListItemBinding createBinding(ViewGroup parent) {
        ListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item,
                        parent, false, dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Design design = binding.getDesign();
            if (design != null && designClickCallback != null) {
                designClickCallback.onClick(design);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ListItemBinding binding, Design item) {
        binding.setDesign(item);
    }

    @Override
    protected boolean areItemsTheSame(Design oldItem, Design newItem) {
        return Objects.equals(oldItem.id, newItem.id);
    }

    @Override
    protected boolean areContentsTheSame(Design oldItem, Design newItem) {
        return Objects.equals(oldItem.name, newItem.name) &&
                oldItem.screenshots.medium == newItem.screenshots.medium;
    }

    public interface DesignClickCallback {
        void onClick(Design design);
    }
}
