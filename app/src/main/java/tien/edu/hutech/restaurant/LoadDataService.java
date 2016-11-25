package tien.edu.hutech.restaurant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LoadDataService extends Service {

    public LoadDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
