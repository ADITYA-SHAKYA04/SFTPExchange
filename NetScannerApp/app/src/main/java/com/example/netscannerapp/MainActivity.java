package com.example.netscannerapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {
    private Button scanButton;
    private TextView statusText;
    private RecyclerView deviceList;
    private DeviceAdapter deviceAdapter;
    private List<DeviceInfo> devices = new ArrayList<>();
    private ExecutorService executor = Executors.newFixedThreadPool(32);
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int[] COMMON_PORTS = {21, 22, 23, 80, 443, 445, 3389};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = findViewById(R.id.scanButton);
        statusText = findViewById(R.id.statusText);
        deviceList = findViewById(R.id.deviceList);
        deviceAdapter = new DeviceAdapter(devices);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setAdapter(deviceAdapter);
        scanButton.setOnClickListener(v -> scanNetwork());
    }
    private void scanNetwork() {
        devices.clear();
        deviceAdapter.notifyDataSetChanged();
        statusText.setText(getString(R.string.scanning));
        executor.execute(() -> {
            String localIp = getLocalIp();
            if (localIp == null) {
                postStatus("No network");
                return;
            }
            String subnet = localIp.substring(0, localIp.lastIndexOf('.')+1);
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 1; i < 255; i++) {
                String host = subnet + i;
                futures.add(executor.submit(() -> {
                    try {
                        InetAddress addr = InetAddress.getByName(host);
                        if (addr.isReachable(200)) {
                            DeviceInfo info = new DeviceInfo();
                            info.ip = host;
                            info.hostname = addr.getCanonicalHostName();
                            info.openPorts = scanPorts(host);
                            info.vulnerabilities = getVulns(info.openPorts);
                            addDevice(info);
                        }
                    } catch (IOException ignored) {}
                }));
            }
            for (Future<?> f : futures) try { f.get(); } catch (Exception ignored) {}
            postStatus("Scan complete");
        });
    }
    private List<Integer> scanPorts(String host) {
        List<Integer> open = new ArrayList<>();
        for (int port : COMMON_PORTS) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, port), 100);
                open.add(port);
            } catch (IOException ignored) {}
        }
        return open;
    }
    private List<String> getVulns(List<Integer> ports) {
        List<String> vulns = new ArrayList<>();
        if (ports.contains(23)) vulns.add("Telnet open");
        if (ports.contains(21)) vulns.add("FTP open");
        if (ports.contains(445)) vulns.add("SMB open");
        if (ports.contains(3389)) vulns.add("RDP open");
        return vulns;
    }
    private void addDevice(DeviceInfo info) {
        mainHandler.post(() -> {
            devices.add(info);
            deviceAdapter.notifyItemInserted(devices.size()-1);
        });
    }
    private void postStatus(String msg) {
        mainHandler.post(() -> statusText.setText(msg));
    }
    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
    static class DeviceInfo {
        String ip;
        String hostname;
        List<Integer> openPorts = new ArrayList<>();
        List<String> vulnerabilities = new ArrayList<>();
    }
    static class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
        private List<DeviceInfo> devices;
        DeviceAdapter(List<DeviceInfo> devices) { this.devices = devices; }
        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(24, 24, 24, 24);
            return new ViewHolder(tv);
        }
        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            DeviceInfo d = devices.get(position);
            String info = d.ip + (TextUtils.isEmpty(d.hostname) ? "" : (" ("+d.hostname+")"));
            info += "\nOpen: " + d.openPorts;
            if (!d.vulnerabilities.isEmpty()) info += "\nVuln: " + d.vulnerabilities;
            holder.textView.setText(info);
        }
        @Override public int getItemCount() { return devices.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(android.view.View itemView) { super(itemView); textView = (TextView)itemView; }
        }
    }
}
