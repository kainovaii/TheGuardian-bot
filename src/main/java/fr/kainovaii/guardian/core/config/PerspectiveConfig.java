package fr.kainovaii.guardian.core.config;

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
    public double getPenaltyThreshold() { return muteThreshold; }
}