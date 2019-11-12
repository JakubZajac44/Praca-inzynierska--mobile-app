package jakub.zajac.com.inzv2.personalisation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jakub.zajac.com.inzv2.R;

/**
 * Created by Jakub on 20.12.2017.
 */

public class LoginView extends Fragment  implements View.OnClickListener{

    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private AuthorizationPresenter authorizationPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate( R.layout.login_fragment, container, false);
        loginText = (EditText) view.findViewById(R.id.loginInputText);
        passwordText = (EditText) view.findViewById(R.id.loginInputPassword);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        return view;
    }

    public void setAuthorizationPresenter(AuthorizationPresenter authorizationPresenter) {
        this.authorizationPresenter = authorizationPresenter;
    }

    public void showToast(String message)
    {
        Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==loginButton.getId())
        {
            authorizationPresenter.loginUser(loginText.getText().toString(),passwordText.getText().toString());
        }
    }
}
