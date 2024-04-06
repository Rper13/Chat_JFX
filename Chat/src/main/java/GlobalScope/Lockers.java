package GlobalScope;

public class Lockers {

    public static final Object login_lock = new Object();

    public static void syncLoginResponse(String clientResp, String servResp, Object locker){
        synchronized (locker) {
            clientResp = servResp;
            locker.notifyAll();
        }
    }

}
