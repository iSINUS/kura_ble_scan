package org.eclipse.kura.example.beacon.scanner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.bluetooth.le.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeaconScannerExample {

    private static final Logger logger = LoggerFactory.getLogger(BeaconScannerExample.class);

    private static final String adapterName = "hci0";
    // Internal State
    private BluetoothLeService bluetoothService;
    private BluetoothLeAdapter bluetoothAdapter;

    // Services
    public void setBluetoothService(BluetoothLeService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public void unsetBluetoothService(BluetoothLeService bluetoothService) {
        this.bluetoothService = null;
    }

    protected void activate(ComponentContext context, Map<String, Object> properties) {
        logger.info("Activating Bluetooth Beacon Scanner example...");

        setup();

        logger.info("Activating Bluetooth Beacon Scanner example...Done");

    }

    protected void deactivate(ComponentContext context) {

        logger.debug("Deactivating Beacon Scanner Example...");

        releaseResources();

        logger.debug("Deactivating Beacon Scanner Example... Done.");
    }

    
    private void setup() {

        this.bluetoothAdapter = this.bluetoothService.getAdapter(this.adapterName);
        if (this.bluetoothAdapter != null) {
        	if (!this.bluetoothAdapter.isPowered()) {
        		this.bluetoothAdapter.setPowered(true);
        	}
        	
        	if (this.bluetoothAdapter.isDiscovering()) {
        	    try {
        	        this.bluetoothAdapter.stopDiscovery();
        	    } catch (KuraException e) {
        	        logger.error("Failed to stop discovery", e);
        	    }
        	}
        	Future<List<BluetoothLeDevice>> future = this.bluetoothAdapter.findDevices(10);
        	try {
        	    List<BluetoothLeDevice> devices = future.get();
        	    for(BluetoothLeDevice device : devices) {
        	    	logger.info(device.getAddress());
        	    }
        	    
        	} catch (InterruptedException | ExecutionException e) {
        	    logger.error("Scan for devices failed", e);
        	}
        }

    }

    private void releaseResources() {
        if (this.bluetoothAdapter != null) {
        	if (this.bluetoothAdapter.isDiscovering()) {
        	    try {
        	        this.bluetoothAdapter.stopDiscovery();
        	    } catch (KuraException e) {
        	        logger.error("Failed to stop discovery", e);
        	    }
        	}
            this.bluetoothAdapter = null;
        }

    }

}
