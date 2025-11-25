# AndroidAppExample

After receiving a license

Replace your email:

MainActivity/
private void initializeMudra()
    {
        Mudra.getInstance().requestAccessPermissions(this);
        Mudra.getInstance().getLicenseForEmailFromCloud("------@--------", (success, errorResult) -> {
            if( success ) {
                Log.d(TAG , "licenses set successfully.");
            } else {
                Log.d(TAG , "failed to set licenses : " + errorResult +".");
            }
        });
        Mudra.getInstance().setCoreLoggingSeverity(LoggingSeverity.Error);
    }