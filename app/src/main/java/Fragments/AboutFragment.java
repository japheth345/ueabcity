package Fragments;

import android.os.Bundle;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.japho.ueab.root.R;

public class AboutFragment extends Fragment {
    private TextView repoLink;

    public AboutFragment() {
        // Required empty default constructor
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        repoLink = (TextView) view.findViewById(R.id.repo_link);
        repoLink.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
