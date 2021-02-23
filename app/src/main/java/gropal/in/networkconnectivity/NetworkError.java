package gropal.in.networkconnectivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import gropal.in.R;
import gropal.in.SplashActivity;


public class NetworkError extends AppCompatActivity {
    ImageView mCheckConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_no_internet_connection);
        mCheckConnection = (ImageView) findViewById(R.id.no_internet_connection);
        mCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.connectionChecking(getApplicationContext())) {
                    Intent intent = new Intent(NetworkError.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(NetworkError.this, NoInternetConnection.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
