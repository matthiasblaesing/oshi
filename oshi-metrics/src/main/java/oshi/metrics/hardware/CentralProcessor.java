/**
 *
 * Copyright 2013, Somete Group, LLC. All rights reserved.
 *
 * $LastChangedDate$
 * $LastChangedBy$
 * $Revision$
 *
 */
package oshi.metrics.hardware;

// TODO: implements oshi.hardware.CentralProcessor
public class CentralProcessor {
    // private static final long serialVersionUID = 1L;
    private static final oshi.hardware.CentralProcessor CPU = HardwareAbstractionLayer.HAL.getProcessor();

    public int physicalPackageCount;
    public int physicalProcessorCount;
    public int logicalProcessorCount;
    public String name;


    /**
     * @return Returns the physicalPackageCount.
     */
    public int getPhysicalPackageCount() {
        physicalPackageCount = CPU.getPhysicalPackageCount();
        return physicalPackageCount;
    }

    /**
     * @return Returns the physicalProcessorCount.
     */
    public int getPhysicalProcessorCount() {
        physicalProcessorCount = CPU.getPhysicalProcessorCount();
        return physicalProcessorCount;
    }

    /**
     * @return Returns the logicalProcessorCount.
     */
    public int getLogicalProcessorCount() {
        logicalProcessorCount = CPU.getLogicalProcessorCount();
        return logicalProcessorCount;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        name = CPU.getName();
        return name;
    }

}
