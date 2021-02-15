

package com.skripsi.berbincang.presenters;

import android.content.Context;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import autodagger.AutoInjector;
import com.skripsi.berbincang.adapters.items.MentionAutocompleteItem;
import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.mention.Mention;
import com.skripsi.berbincang.models.json.mention.MentionOverall;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@AutoInjector(NextcloudTalkApplication.class)
public class MentionAutocompletePresenter extends RecyclerViewPresenter<Mention> implements FlexibleAdapter.OnItemClickListener {
    @Inject
    NcApi ncApi;
    @Inject
    UserUtils userUtils;
    private UserEntity currentUser;
    private FlexibleAdapter<AbstractFlexibleItem> adapter;
    private Context context;

    private String roomToken;

    private List<AbstractFlexibleItem> abstractFlexibleItemList = new ArrayList<>();

    public MentionAutocompletePresenter(Context context) {
        super(context);
        this.context = context;
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
        currentUser = userUtils.getCurrentUser();
    }

    public MentionAutocompletePresenter(Context context, String roomToken) {
        super(context);
        this.roomToken = roomToken;
        this.context = context;
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
        currentUser = userUtils.getCurrentUser();
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new FlexibleAdapter<>(abstractFlexibleItemList, context, false);
        adapter.addListener(this);
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {

        String queryString;
        if (query != null && query.length() > 1) {
            queryString = String.valueOf(query.subSequence(1, query.length()));
        } else {
            queryString = "";
        }

        adapter.setFilter(queryString);
        ncApi.getMentionAutocompleteSuggestions(ApiUtils.getCredentials(currentUser.getUsername(), currentUser
                        .getToken()), ApiUtils.getUrlForMentionSuggestions(currentUser.getBaseUrl(), roomToken),
                queryString, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(3)
                .subscribe(new Observer<MentionOverall>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MentionOverall mentionOverall) {
                        List<Mention> mentionsList = mentionOverall.getOcs().getData();

                        if (mentionsList.size() == 0) {
                            adapter.clear();
                        } else {
                            List<AbstractFlexibleItem> internalAbstractFlexibleItemList = new ArrayList<>();
                            for (Mention mention : mentionsList) {
                                internalAbstractFlexibleItemList.add(
                                        new MentionAutocompleteItem(mention.getId(),
                                                mention.getLabel(), mention.getSource(),
                                                currentUser));
                            }

                            if (adapter.getItemCount() != 0) {
                                adapter.clear();
                            }

                            adapter.updateDataSet(internalAbstractFlexibleItemList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.clear();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public boolean onItemClick(View view, int position) {
        Mention mention = new Mention();
        MentionAutocompleteItem mentionAutocompleteItem = (MentionAutocompleteItem) adapter.getItem(position);
        if (mentionAutocompleteItem != null) {
            mention.setId(mentionAutocompleteItem.getObjectId());
            mention.setLabel(mentionAutocompleteItem.getDisplayName());
            mention.setSource(mentionAutocompleteItem.getSource());
            dispatchClick(mention);
        }
        return true;
    }
}
