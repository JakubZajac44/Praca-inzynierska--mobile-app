package jakub.zajac.com.inzv2.personalisation;

import android.content.Context;
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

public class RegistrationView extends Fragment implements View.OnClickListener{

    private EditText loginText;
    private EditText passwordText;
    private Button registrationButton;
    private Context context;
    private AuthorizationPresenter authorizationPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate( R.layout.registration_fragment, container, false);
        loginText = (EditText) view.findViewById(R.id.loginRegistrationInputText);
        passwordText = (EditText) view.findViewById(R.id.loginRegistrationInputPassword);
        registrationButton = (Button) view.findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(this);
//        context = this.getContext();
        return view;
    }



    public void showToast(String message)
    {
        Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {

       if(view.getId()==registrationButton.getId())
       {
            authorizationPresenter.registerUser(loginText.getText().toString(),passwordText.getText().toString());
       }

    }

    public void setAuthorizationPresenter(AuthorizationPresenter authorizationPresenter) {
        this.authorizationPresenter = authorizationPresenter;
    }

}
