package pl.sviete.dom.devices.net;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import pl.sviete.dom.devices.net.Models.DeviceInfo;

import java.net.InetAddress;

public class NsdController {

    private boolean mInitialized = false;
    private String TAG = "NsdController";
    private String SERVICE_TYPE = "_http._tcp.";
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private DeviceInfoListener mDeviceInfoListener;

    public void Start() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void Stop() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void AddListener(DeviceInfoListener listener){
        mDeviceInfoListener = listener;
    }

    public void initializeDiscoveryListener(Activity activity) {
        //if (mInitialized) return;

        Context context = activity.getApplicationContext();
        mNsdManager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);

        // Instantiate a new DiscoveryListener
        mDiscoveryListener  = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                }  else {
                    mNsdManager.resolveService(service, new MyResolveListener());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);

                if (mDeviceInfoListener != null) {
                    DeviceInfo devInfo = new DeviceInfo(service.getServiceName());
                    mDeviceInfoListener.LostDevice(devInfo);
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
        mInitialized = true;
    }

    private class MyResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed: " + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

            if (mDeviceInfoListener != null) {
                InetAddress host = serviceInfo.getHost();
                DeviceInfo devInfo = new DeviceInfo(serviceInfo.getServiceName(), host.getHostName());
                mDeviceInfoListener.NewDevice(devInfo);
            }
        }
    }

    /*public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                //mService = serviceInfo;
                //int port = mService.getPort();
                //InetAddress host = mService.getHost();
            }
        };
    }*/
}
