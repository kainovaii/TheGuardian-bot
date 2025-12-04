package fr.kainovaii.guardian.utils;

public class PerspectiveConfig
{
    private final double alertThreshold;
    private final double muteThreshold;
    public PerspectiveConfig( double alertThreshold, double muteThreshold)
    {
        this.alertThreshold = alertThreshold;
        this.muteThreshold = muteThreshold;
    }

    public double getAlertThreshold() { return alertThreshold; }
    public double getMuteThreshold() { return muteThreshold; }
}