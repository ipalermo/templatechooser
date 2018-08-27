
package com.android.example.speedrun.ui.common;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.example.speedrun.R;
import com.android.example.speedrun.databinding.GameItemBinding;
import com.android.example.speedrun.util.Objects;
import com.android.example.speedrun.vo.Game;

/**
 * A RecyclerView adapter for {@link Game} class.
 */
public class GameListAdapter extends DataBoundListAdapter<Game, GameItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final GameClickCallback gameClickCallback;

    public GameListAdapter(DataBindingComponent dataBindingComponent,
                           GameClickCallback gameClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.gameClickCallback = gameClickCallback;
    }

    @Override
    protected GameItemBinding createBinding(ViewGroup parent) {
        GameItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.game_item,
                        parent, false, dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Game game = binding.getGame();
            if (game != null && gameClickCallback != null) {
                gameClickCallback.onClick(game);
            }
        });
        return binding;
    }

    @Override
    protected void bind(GameItemBinding binding, Game item) {
        binding.setGame(item);
    }

    @Override
    protected boolean areItemsTheSame(Game oldItem, Game newItem) {
        return Objects.equals(oldItem.id, newItem.id);
    }

    @Override
    protected boolean areContentsTheSame(Game oldItem, Game newItem) {
        return Objects.equals(oldItem.names.international, newItem.names.international) &&
                oldItem.releaseDate == newItem.releaseDate;
    }

    public interface GameClickCallback {
        void onClick(Game game);
    }
}
