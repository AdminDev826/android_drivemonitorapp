package dashboard;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lineztech.farhan.vehicaltarckingapp.R;

/**
 * Created by Dev on 2/28/2017.
 */
public class SosFragment extends Fragment {

    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null) return view;
        view = inflater.inflate(R.layout.sos_layout, container, false);
        initView();
        return view;
    }

    private void initView() {
        context = getActivity();
    }
}
